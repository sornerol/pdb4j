package io.github.sornerol.pdb4j.reader.sortinfo;

import io.github.sornerol.pdb4j.model.sortinfo.SortInfo;

public interface SortInfoReader<T extends SortInfo> {
    T read(byte[] data);
}
