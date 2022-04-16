package io.github.sornerol.pdb4j.model.record;

import lombok.Data;

@Data
public class GenericPdbRecord extends AbstractPdbRecord {

    private byte[] data;

    public GenericPdbRecord() {
        super();
    }
}
