package io.github.sornerol.pdb4j.model.record;

import lombok.Data;

@Data
public class AbstractPdbRecord {
    private Integer offset;
    private byte attributes;
    private Integer uniqueId;
}
