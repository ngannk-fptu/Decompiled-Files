/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileGroupsCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class SerializableGroupsCache
extends FileGroupsCache
implements Serializable {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$SerializableGroupsCache == null ? (class$com$opensymphony$user$provider$file$SerializableGroupsCache = SerializableGroupsCache.class$("com.opensymphony.user.provider.file.SerializableGroupsCache")) : class$com$opensymphony$user$provider$file$SerializableGroupsCache));
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$SerializableGroupsCache;

    public SerializableGroupsCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
        this.load();
    }

    public boolean load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(this.getInputStreamFromStoreFile());
            SerializableGroupsCache g = (SerializableGroupsCache)ois.readObject();
            this.groups = g.groups;
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)("cannot load from file " + this.storeFile + ". Create a new blank store."), (Throwable)e);
            return false;
        }
    }

    public boolean store() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(this.storeFile));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            fos.close();
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)("cannot store in file " + this.storeFile + "."), (Throwable)e);
            return false;
        }
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

