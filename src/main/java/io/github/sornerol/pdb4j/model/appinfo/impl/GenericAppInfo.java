package io.github.sornerol.pdb4j.model.appinfo.impl;

import io.github.sornerol.pdb4j.model.appinfo.AppInfo;
import lombok.Data;

@Data
public class GenericAppInfo implements AppInfo {
    private byte[] data;

    public GenericAppInfo(byte[] data) {
        this.data = data;
    }

    public GenericAppInfo() {}

    @Override
    public byte[] toBytes() {
        return data;
    }
}
