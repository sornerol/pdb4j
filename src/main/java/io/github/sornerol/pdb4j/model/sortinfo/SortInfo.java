package io.github.sornerol.pdb4j.model.sortinfo;

/**
 * Stores the file's sort info area, if provided.
 *
 * Note that Palm OS does not support backup and downloading of the sort info area. PDB4J supports reading/writing
 * the sort info area for completeness, but if you are developing new software for Palm OS, you should probably not
 * use the sort info area to store data.
 */
public interface SortInfo {
    byte[] toBytes();
}
