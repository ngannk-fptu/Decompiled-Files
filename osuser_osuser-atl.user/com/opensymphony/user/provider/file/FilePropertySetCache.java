/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileEntitiesCache;
import java.util.HashMap;
import java.util.Map;

abstract class FilePropertySetCache
extends FileEntitiesCache {
    protected Map propertySets = new HashMap();

    protected FilePropertySetCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
    }
}

