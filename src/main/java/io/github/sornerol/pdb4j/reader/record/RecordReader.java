package io.github.sornerol.pdb4j.reader.record;

import io.github.sornerol.pdb4j.model.record.impl.AbstractPdbRecord;

public interface RecordReader <T extends AbstractPdbRecord> {
    T read(byte[] data);
    T read(byte attributes, byte[] data);
}
