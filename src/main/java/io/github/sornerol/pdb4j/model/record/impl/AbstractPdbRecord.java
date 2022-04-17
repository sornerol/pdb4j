package io.github.sornerol.pdb4j.model.record.impl;

import io.github.sornerol.pdb4j.model.record.PdbRecord;
import lombok.Data;

/**
 * Base class for representing a PDB database record.
 */
@Data
public abstract class AbstractPdbRecord implements PdbRecord {
    public static final int SECRET_ATTRIBUTE = 0x10;
    public static final int BUSY_ATTRIBUTE = 0x20;
    public static final int DIRTY_ATTRIBUTE = 0x40;
    public static final int DELETE_ATTRIBUTE = 0x80;
    public static final int CATEGORY_MASK = 0x0F;

    private byte attributes;

    public AbstractPdbRecord(byte attributes) {
        this.attributes = attributes;
    }

    public AbstractPdbRecord() {
    }

    @Override
    public boolean isSecret() {
        return (attributes & SECRET_ATTRIBUTE) != 0;
    }

    @Override
    public boolean isBusy() {
        return (attributes & BUSY_ATTRIBUTE) != 0;
    }

    @Override
    public boolean isDirty() {
        return (attributes & DIRTY_ATTRIBUTE) != 0;
    }

    @Override
    public boolean isDelete() {
        return (attributes & DELETE_ATTRIBUTE) != 0;
    }

    public int getCategoryValue() {
        return attributes & CATEGORY_MASK;
    }
}
