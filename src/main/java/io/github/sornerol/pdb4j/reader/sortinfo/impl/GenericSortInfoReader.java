package io.github.sornerol.pdb4j.reader.sortinfo.impl;

import io.github.sornerol.pdb4j.model.sortinfo.impl.GenericSortInfo;
import io.github.sornerol.pdb4j.reader.sortinfo.SortInfoReader;

public class GenericSortInfoReader implements SortInfoReader<GenericSortInfo> {
    public GenericSortInfo read(byte[] data) {
        return new GenericSortInfo(data);
    }
}
