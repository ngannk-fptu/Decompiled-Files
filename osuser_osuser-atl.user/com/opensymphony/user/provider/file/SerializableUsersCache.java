/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileUsersCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class SerializableUsersCache
extends FileUsersCache
implements Serializable {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$SerializableUsersCache == null ? (class$com$opensymphony$user$provider$file$SerializableUsersCache = SerializableUsersCache.class$("com.opensymphony.user.provider.file.SerializableUsersCache")) : class$com$opensymphony$user$provider$file$SerializableUsersCache));
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$SerializableUsersCache;

    public SerializableUsersCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
        this.load();
    }

    public boolean load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(this.getInputStreamFromStoreFile());
            SerializableUsersCache u = (SerializableUsersCache)ois.readObject();
            this.users = u.users;
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

