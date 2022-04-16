package io.github.sornerol.pdb4j;

import io.github.sornerol.pdb4j.model.PdbDatabase;
import io.github.sornerol.pdb4j.model.record.AbstractPdbRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class PdbReader {

    private static final String CHARSET = "iso-8859-1";

    private final byte[] fileData;

    public PdbReader(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            fileData = new byte[(int) file.length()];
            inputStream.read(fileData);
        }
    }

    public PdbReader(byte[] fileData) {
        this.fileData = fileData;
    }

    public <T extends  AbstractPdbRecord> PdbDatabase<T> read() throws UnsupportedEncodingException {
        PdbDatabase<T> database = new PdbDatabase<T>();
        database.setName(getNullTerminatedString(Arrays.copyOfRange(fileData, 0, 32)));
        database.setFileAttributes(getShort(32));
        database.setVersion(getShort(34));
//        database.setCreationTime(dateFromPdbTime(ByteBuffer.wrap(Arrays.copyOfRange(fileData,36,40)).getInt()));
//        database.setModificationTime(dateFromPdbTime(ByteBuffer.wrap(Arrays.copyOfRange(fileData,40,44)).getInt()));
//        database.setBackupTime(dateFromPdbTime(ByteBuffer.wrap(Arrays.copyOfRange(fileData,44,48)).getInt()));
        database.setModificationNumber(getInt(48));
        database.setAppInfoOffset(getInt(52));
        database.setSortInfoOffset(getInt(56));
        database.setDatabaseType(new String(fileData,60,4, CHARSET));
        database.setCreatorId(new String(fileData,64,4, CHARSET));
        database.setUniqueIdSeed(getInt(68));
        database.setNextRecordList(getInt(72));
        database.setNumberOfRecords(getShort(76));

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
        return new String(array,0, position, CHARSET);
    }

    private short getShort(int offset) {
        return ByteBuffer.wrap(Arrays.copyOfRange(fileData, offset,offset + 2)).getShort();
    }

    private int getInt(int offset) {
        return ByteBuffer.wrap(Arrays.copyOfRange(fileData,offset,offset + 4)).getInt();
    }

    private Calendar dateFromPdbTime(Integer pdbTime) {
        return new Calendar.Builder().build();
    }
}
