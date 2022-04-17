package io.github.sornerol.pdb4j.model.record;

/**
 * A single record in a PDB database.
 */
public interface PdbRecord {
    /**
     * Is the record secret
     * @return true if the secret flag is set in the record attributes
     */
    boolean isSecret();

    /**
     * Is the record busy
     * @return true if the busy flag is set in the record attributes
     */
    boolean isBusy();

    /**
     * Is the record dirty
     * @return true if the dirty flag is set in the record attributes
     */
    boolean isDirty();

    /**
     * Is the record deleted
     * @return true if the delete flag is set in the record attributes
     */
    boolean isDelete();

    /**
     * Get the record's category value
     * @return the category value (the lowest four bits in the record's attribute byte)
     */
    int getCategoryValue();
}
