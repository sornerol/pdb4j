package io.github.sornerol.pdb4j.util;

public class PdbDatabaseConstants {
    public static final int FILE_HEADER_LENGTH_BYTES = 78;

    // File offsets in bytes
    public static final int NAME_OFFSET = 0;
    public static final int NAME_LENGTH_BYTES = 32;
    public static final int FILE_ATTRIBUTES_OFFSET = 32;
    public static final int VERSION_OFFSET = 34;
    public static final int CREATION_TIME_OFFSET = 36;
    public static final int MODIFICATION_TIME_OFFSET = 40;
    public static final int BACKUP_TIME_OFFSET = 44;
    public static final int MODIFICATION_NUMBER_OFFSET = 48;
    public static final int APP_INFO_OFFSET = 52;
    public static final int SORT_INFO_OFFSET = 56;
    public static final int DATABASE_TYPE_OFFSET = 60;
    public static final int CREATOR_ID_OFFSET = 64;
    public static final int UNIQUE_ID_SEED_OFFSET = 68;
    public static final int NEXT_RECORD_LIST_OFFSET = 72;
    public static final int NUMBER_OF_RECORDS_OFFSET = 76;
    public static final int RECORD_HEADERS_OFFSET = 78;

    public static final int RECORD_HEADER_SIZE_BYTES = 8;

    public static final int PALM_EPOCH_YEAR = 1904;
    public static final int UNIX_EPOCH_YEAR = 1970;
}
