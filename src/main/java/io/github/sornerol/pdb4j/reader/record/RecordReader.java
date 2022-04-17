package io.github.sornerol.pdb4j.reader.record;

import io.github.sornerol.pdb4j.model.record.impl.AbstractPdbRecord;

/**
 * A RecordReader reads individual record data and stores it in an {@link AbstractPdbRecord}.
 * @param <T> The type of records produced by the RecordReader
 */
public interface RecordReader <T extends AbstractPdbRecord> {
    T read(byte[] data);
    T read(byte attributes, byte[] data);
}
