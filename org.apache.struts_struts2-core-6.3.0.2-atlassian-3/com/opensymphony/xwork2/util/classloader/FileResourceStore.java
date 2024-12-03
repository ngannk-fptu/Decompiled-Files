/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.classloader.AbstractResourceStore;
import java.io.File;
import java.io.FileInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileResourceStore
extends AbstractResourceStore {
    private static final Logger LOG = LogManager.getLogger(FileResourceStore.class);

    public FileResourceStore(File file) {
        super(file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] read(String pResourceName) {
        FileInputStream fis = null;
        try {
            File file = this.getFile(pResourceName);
            byte[] data = new byte[(int)file.length()];
            fis = new FileInputStream(file);
            fis.read(data);
            byte[] byArray = data;
            this.closeQuietly(fis);
            return byArray;
        }
        catch (Exception e) {
            LOG.debug("Unable to read file [{}]", (Object)pResourceName, (Object)e);
            byte[] byArray = null;
            return byArray;
        }
        finally {
            this.closeQuietly(fis);
        }
    }

    private File getFile(String pResourceName) {
        String fileName = pResourceName.replace('/', File.separatorChar);
        return new File(this.file, fileName);
    }
}

