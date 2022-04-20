package io.github.sornerol.pdb4j.model;

import io.github.sornerol.pdb4j.model.appinfo.AppInfo;
import io.github.sornerol.pdb4j.model.record.PdbRecord;
import io.github.sornerol.pdb4j.model.sortinfo.SortInfo;
import lombok.Data;

import java.util.Calendar;
import java.util.List;

@Data
public class PdbDatabase<T extends PdbRecord, R extends AppInfo, S extends SortInfo> {
    private String name;
    private short fileAttributes;
    private short version;
    private Calendar creationTime;
    private Calendar modificationTime;
    private Calendar backupTime;
    private int modificationNumber;
    private int appInfoOffset;
    private int sortInfoOffset;
    private String databaseType;
    private String creatorId;
    private int uniqueIdSeed;
    private int nextRecordList;
    private short numberOfRecords;
    private R appInfo;
    private S sortInfo;
    private List<T> records;
}
