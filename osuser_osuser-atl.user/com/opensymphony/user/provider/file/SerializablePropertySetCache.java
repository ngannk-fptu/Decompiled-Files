/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FilePropertySetCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class SerializablePropertySetCache
extends FilePropertySetCache {
    public SerializablePropertySetCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
        this.load();
    }

    public boolean load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(this.getInputStreamFromStoreFile());
            SerializablePropertySetCache ps = (SerializablePropertySetCache)ois.readObject();
            this.propertySets = ps.propertySets;
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
}

