/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.util.ClassLoaderUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class FileEntitiesCache {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$FileEntitiesCache == null ? (class$com$opensymphony$user$provider$file$FileEntitiesCache = FileEntitiesCache.class$("com.opensymphony.user.provider.file.FileEntitiesCache")) : class$com$opensymphony$user$provider$file$FileEntitiesCache));
    protected String storeFile;
    protected String storeFileType;
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$FileEntitiesCache;

    public FileEntitiesCache(String storeFile, String storeFileType) {
        this.storeFile = storeFile;
        this.storeFileType = storeFileType;
        if (storeFile == null) {
            log.fatal((Object)"property storeFile must be specified");
        }
        if (storeFileType == null) {
            log.fatal((Object)"property storeFileType must be specified; one of (file,resource)");
        }
        if (!storeFileType.equalsIgnoreCase("file") && !storeFileType.equalsIgnoreCase("resource")) {
            log.fatal((Object)"property storeFileType must be one of (file,resource)");
        }
    }

    public abstract boolean load();

    public abstract boolean store();

    protected InputStream getInputStreamFromStoreFile() throws IOException {
        if (this.storeFileType.equalsIgnoreCase("file")) {
            return new FileInputStream(this.storeFile);
        }
        if (this.storeFileType.equalsIgnoreCase("resource")) {
            return ClassLoaderUtil.getResourceAsStream((String)this.storeFile, this.getClass());
        }
        log.fatal((Object)"property storeFileType must be one of (file,resource)");
        return null;
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

