package io.github.sornerol.pdb4j.reader.appinfo.impl;

import io.github.sornerol.pdb4j.model.appinfo.impl.GenericAppInfo;
import io.github.sornerol.pdb4j.reader.appinfo.AppInfoReader;

public class GenericAppInfoReader implements AppInfoReader<GenericAppInfo> {
    public GenericAppInfo read(byte[] data) {
        return new GenericAppInfo(data);
    }
}
