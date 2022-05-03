package io.github.sornerol.pdb4j.model;

import io.github.sornerol.pdb4j.model.appinfo.AppInfo;
import io.github.sornerol.pdb4j.model.record.PdbRecord;
import io.github.sornerol.pdb4j.model.sortinfo.SortInfo;
import io.github.sornerol.pdb4j.util.PalmDateUtil;
import io.github.sornerol.pdb4j.util.PalmStringUtil;
import io.github.sornerol.pdb4j.util.PdbDatabaseConstants;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Data
public class PdbDatabase<R extends PdbRecord, A extends AppInfo, S extends SortInfo> {
    private boolean useUnixEpochTime;
    private String name;
    private short fileAttributes;
    private short version;
    private Calendar creationTime;
    private Calendar modificationTime;
    private Calendar backupTime;
    private int modificationNumber;  //see https://web.archive.org/web/20090315213538/http://membres.lycos.fr/microfirst/palm/pdb.html
    private String databaseType;
    private String creatorId;
    private int uniqueIdSeed;  // see https://web.archive.org/web/20090315213538/http://membres.lycos.fr/microfirst/palm/pdb.html, note B
    private int nextRecordList;  // see https://web.archive.org/web/20090315213538/http://membres.lycos.fr/microfirst/palm/pdb.html
    private A appInfo;
    private S sortInfo;
    private List<R> records;

    public PdbDatabase() {
        useUnixEpochTime = false;
        records = new ArrayList<>();
    }

    public short getNumberOfRecords() {
        if (records == null) {
            return 0;
        }

        return (short) records.size();
    }

    /**
     * Write the PDB database to the filesystem
     *
     * @param filePath Path to write the file to
     */
    public void writeToFile(String filePath) throws IOException {
        File file = new File(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(toByteArray());
        }
    }

    /**
     * Convert the PDB database to a raw byte array
     *
     * @return byte array representation of the PDB database
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(headerToByteArray());
        outputStream.write(recordHeadersToByteArray());
        if (getAppInfoOffset() > 0) {
            outputStream.write(appInfo.toBytes());
        }
        if (getSortInfoOffset() > 0) {
            outputStream.write(sortInfo.toBytes());
        }
        for (R record : records) {
            outputStream.write(record.toBytes());
        }
        return outputStream.toByteArray();
    }

    public int getAppInfoOffset() {
        if (appInfo == null || appInfo.toBytes().length == 0) {
            return 0;
        }
        return PdbDatabaseConstants.FILE_HEADER_LENGTH_BYTES + getRecordHeadersSize();
    }

    public int getSortInfoOffset() {
        if (sortInfo == null || sortInfo.toBytes().length == 0) {
            return 0;
        }

        int appInfoOffset = getAppInfoOffset();
        int appInfoSize = (appInfoOffset > 0) ? appInfo.toBytes().length : 0;
        int lastUsedOffset = (appInfoOffset > 0) ? appInfoOffset : PdbDatabaseConstants.FILE_HEADER_LENGTH_BYTES;

        return lastUsedOffset + appInfoSize;
    }

    private byte[] headerToByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(PdbDatabaseConstants.FILE_HEADER_LENGTH_BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(fileNameToByteArray());
        buffer.putShort(fileAttributes);
        buffer.putShort(version);
        buffer.putInt(PalmDateUtil.pdbTimestampFromCalendar(creationTime, useUnixEpochTime));
        buffer.putInt(PalmDateUtil.pdbTimestampFromCalendar(modificationTime, useUnixEpochTime));
        buffer.putInt(PalmDateUtil.pdbTimestampFromCalendar(backupTime, useUnixEpochTime));
        buffer.putInt(modificationNumber);
        buffer.putInt(getAppInfoOffset());
        buffer.putInt(getSortInfoOffset());
        buffer.put(PalmStringUtil.unicodeToPalm(databaseType).getBytes(StandardCharsets.ISO_8859_1));
        buffer.put(PalmStringUtil.unicodeToPalm(creatorId).getBytes(StandardCharsets.ISO_8859_1));
        buffer.putInt(uniqueIdSeed);
        buffer.putInt(nextRecordList);
        buffer.putShort(getNumberOfRecords());
        return buffer.array();
    }

    private byte[] fileNameToByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(PdbDatabaseConstants.NAME_LENGTH_BYTES);
        buffer.put(PalmStringUtil.unicodeToPalm(name).getBytes(StandardCharsets.ISO_8859_1));
        return buffer.array();
    }

    private byte[] recordHeadersToByteArray() {
        int appInfoOffset = getAppInfoOffset();
        int appInfoSize = (appInfoOffset > 0) ? appInfo.toBytes().length : 0;

        int sortInfoOffset = getSortInfoOffset();
        int sortInfoSize = (sortInfoOffset > 0) ? sortInfo.toBytes().length : 0;

        int nextOffset;
        if (sortInfoOffset > 0) {
            nextOffset = sortInfoOffset + sortInfoSize;
        } else if (appInfoOffset > 0) {
            nextOffset = appInfoOffset + appInfoSize;
        } else {
            nextOffset = PdbDatabaseConstants.FILE_HEADER_LENGTH_BYTES + getRecordHeadersSize();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(getRecordHeadersSize());
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        for (R record : records) {
            byteBuffer.putInt(nextOffset);
            byteBuffer.put(record.getAttributes());
            byte[] uniqueId = {0, 0, 0};
            byteBuffer.put(uniqueId);
            nextOffset += record.toBytes().length;
        }
        return byteBuffer.array();
    }

    private int getRecordHeadersSize() {
        return getNumberOfRecords() * 8;
    }
}
