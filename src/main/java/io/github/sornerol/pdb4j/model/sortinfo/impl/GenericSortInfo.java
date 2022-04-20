package io.github.sornerol.pdb4j.model.sortinfo.impl;

import io.github.sornerol.pdb4j.model.sortinfo.SortInfo;
import lombok.Data;

/**
 *
 */
@Data
public class GenericSortInfo implements SortInfo {
    private byte[] data;

    public GenericSortInfo(byte[] data) {
        this.data = data;
    }

    public GenericSortInfo() {}

    @Override
    public byte[] toBytes() {
        return data;
    }
}
