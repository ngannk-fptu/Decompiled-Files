/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileEntitiesCache;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class FileGroupsCache
extends FileEntitiesCache {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$FileGroupsCache == null ? (class$com$opensymphony$user$provider$file$FileGroupsCache = FileGroupsCache.class$("com.opensymphony.user.provider.file.FileGroupsCache")) : class$com$opensymphony$user$provider$file$FileGroupsCache));
    public Map groups = new HashMap();
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$FileGroupsCache;

    public FileGroupsCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

