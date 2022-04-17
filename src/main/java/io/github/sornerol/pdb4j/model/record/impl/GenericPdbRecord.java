package io.github.sornerol.pdb4j.model.record.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The most basic way to store a PDB record. The record's data is stored as a byte array.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenericPdbRecord extends AbstractPdbRecord {

    /**
     * The record data as an array of bytes
     */
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
