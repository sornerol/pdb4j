package io.github.sornerol.pdb4j.reader;

import io.github.sornerol.pdb4j.exception.PdbReaderException;
import io.github.sornerol.pdb4j.model.PdbDatabase;
import io.github.sornerol.pdb4j.model.record.impl.AbstractPdbRecord;
import io.github.sornerol.pdb4j.reader.record.RecordReader;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

import static io.github.sornerol.pdb4j.util.PdbDatabaseConstants.*;

/**
 * Reads a PDB database from either a file or byte array into a {@link PdbDatabase}.
 * @param <T> Class to use for storing database records.
 */
@Slf4j
public class PdbReader<T extends AbstractPdbRecord> {

    private final byte[] fileData;

    /**
     * The {@link RecordReader} to use to interpret data from individual records in the PDB database.
     */
    @Setter
    private RecordReader<T> recordReader;

    /**
     * Create a new PdbReader to read in the provided file.
     * @param file
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
     * @param fileData A PDB database as a byte array.
     */
    public PdbReader(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Reads the PDB file data into a {@link PdbDatabase} object.
     * @return the imported {@link PdbDatabase}.
     * @throws UnsupportedEncodingException
     * @throws PdbReaderException if a {@link RecordReader} hasn't been supplied.
     */
    public PdbDatabase<T> read() throws UnsupportedEncodingException, PdbReaderException {
        if (recordReader == null) {
            throw new PdbReaderException("No RecordReader supplied.");
        }

        PdbDatabase<T> database = new PdbDatabase<T>();
        database.setName(getNullTerminatedString(Arrays.copyOfRange(fileData, NAME_OFFSET, NAME_LENGTH_BYTES)));
        database.setFileAttributes(getShort(fileData, FILE_ATTRIBUTES_OFFSET));
        database.setVersion(getShort(fileData, VERSION_OFFSET));
        database.setCreationTime(dateFromPdbTime(getInt(fileData, CREATION_TIME_OFFSET)));
        database.setModificationTime(dateFromPdbTime(getInt(fileData, MODIFICATION_TIME_OFFSET)));
        database.setBackupTime(dateFromPdbTime(getInt(fileData, BACKUP_TIME_OFFSET)));
        database.setModificationNumber(getInt(fileData, MODIFICATION_NUMBER_OFFSET));
        database.setAppInfoOffset(getInt(fileData, APP_INFO_OFFSET));
        database.setSortInfoOffset(getInt(fileData, SORT_INFO_OFFSET));
        database.setDatabaseType(new String(fileData, DATABASE_TYPE_OFFSET, 4, CHARSET));
        database.setCreatorId(new String(fileData, CREATOR_ID_OFFSET, 4, CHARSET));
        database.setUniqueIdSeed(getInt(fileData, UNIQUE_ID_SEED_OFFSET));
        database.setNextRecordList(getInt(fileData, NEXT_RECORD_LIST_OFFSET));
        database.setNumberOfRecords(getShort(fileData, NUMBER_OF_RECORDS_OFFSET));
        Queue<RecordHeader> recordHeaders = readRecordHeaders(database.getNumberOfRecords());
        database.setRecords(readRecords(recordHeaders));
        return database;
    }

    private String getNullTerminatedString(byte[] array) throws UnsupportedEncodingException {
        int position = 0;
        while (position < array.length) {
            if (array[position] == 0) {
                break;
            }
            position++;
        }
        log.debug("File name length is " + position + " byte(s).");
        return new String(array, 0, position, CHARSET);
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

    private Calendar dateFromPdbTime(int pdbTime) {

        /*
          Some Palm applications use the Unix epoch base instead of the Palm epoch base.
          If the highest bit is not set in the timestamp, we can assume this is the case, since otherwise
          the date would be a date well before the PDB format was created.
        */
        int epochYear = ((pdbTime & 0x80000000) == 0) ? UNIX_EPOCH_YEAR : PALM_EPOCH_YEAR;
        log.debug("Epoch year detected as " + epochYear + ".");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.clear();
        calendar.set(epochYear, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + (Integer.toUnsignedLong(pdbTime) * 1000));
        return calendar;
    }

    private Queue<RecordHeader> readRecordHeaders(int numberOfRecords) {
        Queue<RecordHeader> recordHeaders = new LinkedList<>();
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

    private List<T> readRecords(Queue<RecordHeader> recordHeaders) {
        List<T> records = new ArrayList<>();
        while (recordHeaders.peek() != null) {
            RecordHeader currentRecord = recordHeaders.poll();
            RecordHeader nextRecord = recordHeaders.peek();
            int nextOffset = nextRecord != null ? nextRecord.offset : fileData.length;
            log.debug("Reading record at offset " +
                    currentRecord.offset +
                    " (size: " + (nextOffset - currentRecord.offset) + ").");
            byte[] recordData = Arrays.copyOfRange(fileData, currentRecord.offset, nextOffset);
            records.add((T) recordReader.read(currentRecord.attributes, recordData));
        }
        return records;
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
