package io.github.sornerol.pdb4j.reader.record;

import io.github.sornerol.pdb4j.model.record.PdbRecord;

/**
 * A RecordReader reads individual record data and stores it in an {@link PdbRecord}.
 * @param <T> The type of records produced by the RecordReader
 */
public interface RecordReader <T extends PdbRecord> {
    T read(byte[] data);
    T read(byte attributes, byte[] data);
}
