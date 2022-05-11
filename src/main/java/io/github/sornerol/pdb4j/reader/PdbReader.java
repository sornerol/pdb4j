package io.github.sornerol.pdb4j.reader;

import io.github.sornerol.pdb4j.model.PdbDatabase;
import io.github.sornerol.pdb4j.model.appinfo.AppInfo;
import io.github.sornerol.pdb4j.model.record.PdbRecord;
import io.github.sornerol.pdb4j.model.sortinfo.SortInfo;
import io.github.sornerol.pdb4j.reader.appinfo.AppInfoReader;
import io.github.sornerol.pdb4j.reader.record.RecordReader;
import io.github.sornerol.pdb4j.reader.sortinfo.SortInfoReader;
import io.github.sornerol.pdb4j.util.PalmDateUtil;
import io.github.sornerol.pdb4j.util.PalmStringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.github.sornerol.pdb4j.util.PdbDatabaseConstants.*;

/**
 * Reads a PDB database from either a file or byte array into a {@link PdbDatabase}.
 *
 * @param <R>
 * @param <A>
 * @param <S>
 */
@Slf4j
public class PdbReader<R extends PdbRecord, A extends AppInfo, S extends SortInfo> {

    private final byte[] fileData;

    private int appInfoOffset;
    private int sortInfoOffset;
    private int numberOfRecords;
    /**
     * The {@link RecordReader} to use to interpret data from individual records in the PDB database.
     */
    @Setter
    private RecordReader<R> recordReader;

    /**
     * The {@link AppInfoReader} to use to interpret the file's app info area (if the file contains one).
     */
    @Setter
    private AppInfoReader<A> appInfoReader;

    /**
     * The {@link SortInfoReader} to use to interpret the file's sort info area (if the file contains one).
     */
    @Setter
    private SortInfoReader<S> sortInfoReader;

    /**
     * Create a new PdbReader to read in the provided file.
     *
     * @param file PDB file to read
     * @throws IOException if the file doesn't exist or there is a problem reading the file
     */
    public PdbReader(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            fileData = new byte[(int) file.length()];
            int bytesRead = inputStream.read(fileData);
            if (bytesRead != file.length()) {
                log.warn("File length is " + file.length() + " byte(s), but read " + bytesRead + " byte(s).");
            }
        }
    }

    /**
     * Create a new PdbReader to read from the provided byte array
     *
     * @param fileData A PDB database as a byte array.
     */
    public PdbReader(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Reads the PDB file data into a {@link PdbDatabase} object.
     *
     * @return the imported {@link PdbDatabase}.
     */
    public PdbDatabase<R, A, S> read() {
        PdbDatabase<R, A, S> database = new PdbDatabase<>();
        readHeader(database);

        Queue<RecordHeader> recordHeaders = readRecordHeaders();
        int firstRecordOffset = 0;
        if (recordHeaders.size() > 0) {
            firstRecordOffset = recordHeaders.peek().offset;
        }
        if (appInfoOffset > 0) {
            readAppInfoArea(database, firstRecordOffset);
        }
        if (sortInfoOffset > 0) {
            readSortInfoArea(database, firstRecordOffset);
        }

        database.setRecords(readRecords(recordHeaders));
        return database;
    }

    private void readHeader(PdbDatabase<R, A, S> database) {
        database.setName(getNullTerminatedString(Arrays.copyOfRange(fileData, NAME_OFFSET, NAME_LENGTH_BYTES)));
        database.setFileAttributes(getShort(fileData, FILE_ATTRIBUTES_OFFSET));
        database.setVersion(getShort(fileData, VERSION_OFFSET));
        database.setCreationTime(PalmDateUtil.calendarFromPdbTime(getInt(fileData, CREATION_TIME_OFFSET)));
        database.setModificationTime(PalmDateUtil.calendarFromPdbTime(getInt(fileData, MODIFICATION_TIME_OFFSET)));
        database.setBackupTime(PalmDateUtil.calendarFromPdbTime(getInt(fileData, BACKUP_TIME_OFFSET)));
        database.setModificationNumber(getInt(fileData, MODIFICATION_NUMBER_OFFSET));
        appInfoOffset = getInt(fileData, APP_INFO_OFFSET);
        sortInfoOffset = getInt(fileData, SORT_INFO_OFFSET);
        database.setDatabaseType(getString(fileData, DATABASE_TYPE_OFFSET, 4));
        database.setCreatorId(getString(fileData, CREATOR_ID_OFFSET, 4));
        database.setUniqueIdSeed(getInt(fileData, UNIQUE_ID_SEED_OFFSET));
        database.setNextRecordList(getInt(fileData, NEXT_RECORD_LIST_OFFSET));
        numberOfRecords = getShort(fileData, NUMBER_OF_RECORDS_OFFSET);
    }

    private String getNullTerminatedString(byte[] array) {
        int length = 0;
        while (length < array.length) {
            if (array[length] == 0) {
                break;
            }
            length++;
        }
        log.debug("File name length is " + length + " byte(s).");
        return getString(array, 0, length);
    }

    private String getString(byte[] array, int offset, int length) {
        return PalmStringUtil.palmToUnicode(new String(array, offset, length, StandardCharsets.ISO_8859_1));
    }

    private short getShort(byte[] array, int offset) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(array, offset, offset + 2));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        return byteBuffer.getShort();
    }

    private int getInt(byte[] array, int offset) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(array, offset, offset + 4));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        return byteBuffer.getInt();
    }

    private Queue<RecordHeader> readRecordHeaders() {
        Queue<RecordHeader> recordHeaders = new LinkedList<>();
        if (numberOfRecords > 0 && recordReader == null) {
            log.warn("PDB database has records, but no RecordReader provided.");
            return recordHeaders;
        }
        int currentOffset = RECORD_HEADERS_OFFSET;
        for (int i = 0; i < numberOfRecords; i++) {
            byte[] recordHeader = Arrays.copyOfRange(fileData, currentOffset, currentOffset + RECORD_HEADER_SIZE_BYTES);
            int recordOffset = getInt(recordHeader, 0);
            byte recordAttributes = recordHeader[4];
            recordHeaders.add(new RecordHeader(recordOffset, recordAttributes));
            currentOffset += RECORD_HEADER_SIZE_BYTES;
        }
        return recordHeaders;
    }

    private List<R> readRecords(Queue<RecordHeader> recordHeaders) {
        List<R> records = new ArrayList<>();
        while (recordHeaders.peek() != null) {
            RecordHeader currentRecord = recordHeaders.poll();
            RecordHeader nextRecord = recordHeaders.peek();
            int nextOffset = nextRecord != null ? nextRecord.offset : fileData.length;
            log.debug("Reading record at offset " + currentRecord.offset + " (size: " + (nextOffset - currentRecord.offset) + ").");
            byte[] recordData = Arrays.copyOfRange(fileData, currentRecord.offset, nextOffset);
            records.add(recordReader.read(currentRecord.attributes, recordData));
        }
        return records;
    }

    private void readAppInfoArea(PdbDatabase<R, A, S> database, int firstRecordOffset) {
        if (appInfoReader == null) {
            log.warn("File has AppInfoOffset, but no AppInfoReader provided.");
            return;
        }
        final int startOffset = appInfoOffset;
        int endOffset = (sortInfoOffset > 0) ? sortInfoOffset : firstRecordOffset;
        if (endOffset == 0) {
            endOffset = fileData.length;
        }
        final byte[] appInfoData = Arrays.copyOfRange(fileData, startOffset, endOffset);
        database.setAppInfo(appInfoReader.read(appInfoData));
    }

    private void readSortInfoArea(PdbDatabase<R, A, S> database, int firstRecordOffset) {
        if (sortInfoReader == null) {
            log.warn("File has SortInfoOffset, but no SortInfoReader provided.");
            return;
        }
        final int startOffset = sortInfoOffset;
        final int endOffset = (firstRecordOffset > 0) ? firstRecordOffset : fileData.length;

        final byte[] sortInfoData = Arrays.copyOfRange(fileData, startOffset, endOffset);
        database.setSortInfo(sortInfoReader.read(sortInfoData));
    }

    private static class RecordHeader {
        int offset;
        byte attributes;

        RecordHeader(int offset, byte attributes) {
            this.offset = offset;
            this.attributes = attributes;
        }
    }
}
