/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.hwpf.usermodel.ObjectsPool;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.util.Internal;

@Internal
public class ObjectPoolImpl
implements ObjectsPool {
    private DirectoryEntry _objectPool;

    public ObjectPoolImpl(DirectoryEntry _objectPool) {
        this._objectPool = _objectPool;
    }

    @Override
    public Entry getObjectById(String objId) {
        if (this._objectPool == null) {
            return null;
        }
        try {
            return this._objectPool.getEntry(objId);
        }
        catch (FileNotFoundException exc) {
            return null;
        }
    }

    @Internal
    public void writeTo(DirectoryEntry directoryEntry) throws IOException {
        if (this._objectPool != null) {
            EntryUtils.copyNodeRecursively(this._objectPool, directoryEntry);
        }
    }
}

