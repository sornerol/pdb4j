package io.github.sornerol.pdb4j.reader.appinfo;

import io.github.sornerol.pdb4j.model.appinfo.AppInfo;

public interface AppInfoReader <T extends AppInfo > {
    T read(byte[] data);
}
