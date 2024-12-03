/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ConcurrentOperationMap
 *  com.atlassian.util.concurrent.ConcurrentOperationMapImpl
 *  com.google.common.base.Throwables
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion;

import com.atlassian.util.concurrent.ConcurrentOperationMap;
import com.atlassian.util.concurrent.ConcurrentOperationMapImpl;
import com.benryan.conversion.ConversionCache;
import com.google.common.base.Throwables;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBackedCache
implements ConversionCache {
    private static Logger log = LoggerFactory.getLogger(FileBackedCache.class);
    private final ConcurrentOperationMap<String, Object> operationMap = new ConcurrentOperationMapImpl();
    private final File _cacheDir;

    public FileBackedCache(String path) throws IOException {
        this._cacheDir = new File(path);
        if (!this._cacheDir.exists() && !this._cacheDir.mkdirs()) {
            throw new IOException("The specified path: " + path + " doesn't exist and we are unable to create it.");
        }
        if (!this._cacheDir.canRead() || !this._cacheDir.canWrite()) {
            throw new IOException("Confluence doesn't have read/write access to the specified cache directory:" + path + ".");
        }
    }

    @Override
    public Object get(Object key) {
        String name = key.toString();
        try {
            return this.operationMap.runOperation((Object)name, () -> this.loadFile(name));
        }
        catch (ExecutionException e) {
            throw Throwables.propagate((Throwable)e.getCause());
        }
    }

    private Object loadFile(String name) {
        Object inObj = null;
        File child = new File(this._cacheDir, name);
        if (child.exists() && child.canRead()) {
            try (FileInputStream fileIn = new FileInputStream(child);
                 BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
                 ObjectInputStream in = new ObjectInputStream(bufferIn);){
                inObj = in.readObject();
            }
            catch (Exception e) {
                log.warn("Failed to read file " + child + ": " + e.getMessage(), (Throwable)e);
            }
        }
        return inObj;
    }

    @Override
    public void put(Object key, Object val) {
        String name = key.toString();
        if (val instanceof Serializable) {
            File file = new File(this._cacheDir, name);
            File tmpFile = new File(this._cacheDir, name + ".part");
            try (FileOutputStream fileOut = new FileOutputStream(tmpFile);
                 BufferedOutputStream bufferOut = new BufferedOutputStream(fileOut);
                 ObjectOutputStream out = new ObjectOutputStream(bufferOut);){
                out.writeObject(val);
            }
            catch (Exception e) {
                log.warn("Failed to write file " + file + ": " + e.getMessage(), (Throwable)e);
            }
            try {
                if (file.exists()) {
                    file.delete();
                }
                FileUtils.moveFile((File)tmpFile, (File)file);
            }
            catch (IOException e) {
                log.warn("Failed to rename file " + tmpFile + " to " + file + ": " + e.getMessage(), (Throwable)e);
            }
        }
    }
}

