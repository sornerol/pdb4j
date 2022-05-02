package io.github.sornerol.pdb4j.model;

import io.github.sornerol.pdb4j.model.appinfo.AppInfo;
import io.github.sornerol.pdb4j.model.record.PdbRecord;
import io.github.sornerol.pdb4j.model.sortinfo.SortInfo;
import io.github.sornerol.pdb4j.util.PalmDateUtil;
import io.github.sornerol.pdb4j.util.PalmStringUtil;
import io.github.sornerol.pdb4j.util.PdbDatabaseConstants;
import lombok.Data;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
public class PdbDatabase<T extends PdbRecord, R extends AppInfo, S extends SortInfo> {
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
    private R appInfo;
    private S sortInfo;
    private List<T> records;

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
     * @param filePath Path to write the file to
     */
    public void writeToFile(String filePath) throws IOException {
        File file = new File(filePath);
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(toByteArray());
        }
    }

    /**
     * Convert the PDB database to a raw byte array
     * @return byte array representation of the PDB database
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //TODO: Calculate appInfoOffset and sortInfoOffset
        outputStream.write(headerToByteArray());
        return null;
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

    public int getAppInfoOffset() {

        return 0;
    }

    public int getSortInfoOffset() {

        return 0;
    }
}
