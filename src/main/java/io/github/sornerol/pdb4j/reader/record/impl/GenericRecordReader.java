package io.github.sornerol.pdb4j.reader.record.impl;

import io.github.sornerol.pdb4j.model.record.impl.GenericPdbRecord;
import io.github.sornerol.pdb4j.reader.record.RecordReader;

/**
 * A simple {@link RecordReader} which simply stores the record's data in
 * a {@link GenericPdbRecord} as a raw byte array.
 */
public class GenericRecordReader implements RecordReader<GenericPdbRecord> {
    @Override
    public GenericPdbRecord read(byte[] data) {
        return new GenericPdbRecord(data);
    }

    @Override
    public GenericPdbRecord read(byte attributes, byte[] data) {
        return new GenericPdbRecord(attributes, data);
    }
}
