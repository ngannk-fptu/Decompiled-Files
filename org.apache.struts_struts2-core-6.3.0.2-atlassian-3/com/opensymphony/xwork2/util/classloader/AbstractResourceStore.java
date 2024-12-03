/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.classloader.JarResourceStore;
import com.opensymphony.xwork2.util.classloader.ResourceStore;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourceStore
implements ResourceStore {
    private static final Logger log = LogManager.getLogger(JarResourceStore.class);
    protected final File file;

    public AbstractResourceStore(File file) {
        this.file = file;
    }

    protected void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (IOException e) {
            log.error("Unable to close file input stream", (Throwable)e);
        }
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData) {
    }

    public String toString() {
        return this.getClass().getName() + this.file.toString();
    }
}

