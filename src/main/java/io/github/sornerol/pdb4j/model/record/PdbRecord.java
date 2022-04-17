package io.github.sornerol.pdb4j.model.record;

/**
 * A single record in a PDB database.
 */
public interface PdbRecord {
    boolean isSecret();
    boolean isBusy();
    boolean isDirty();
    boolean isDelete();
    int getCategoryValue();
}
