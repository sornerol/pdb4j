package io.github.sornerol.pdb4j.model.record.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenericPdbRecord extends AbstractPdbRecord {

    private byte[] data;

    public GenericPdbRecord(byte attributes, byte[] data) {
        super(attributes);
        this.data = data;
    }

    public GenericPdbRecord(byte attributes) {
        super(attributes);
    }

    public GenericPdbRecord(byte[] data) {
        this.data = data;
    }

    public GenericPdbRecord() {
    }
}
