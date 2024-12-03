/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.util.fs.Revision;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class FileRevision
extends Revision {
    private File file;
    private long lastModified;

    public static Revision build(URL fileUrl) {
        File file;
        try {
            if (fileUrl == null) {
                return null;
            }
            file = new File(fileUrl.toURI());
        }
        catch (URISyntaxException e) {
            file = new File(fileUrl.getPath());
        }
        catch (Throwable t) {
            return null;
        }
        if (file.exists() && file.canRead()) {
            long lastModified = file.lastModified();
            return new FileRevision(file, lastModified);
        }
        return null;
    }

    private FileRevision(File file, long lastUpdated) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.file = file;
        this.lastModified = lastUpdated;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public boolean needsReloading() {
        return this.lastModified < this.file.lastModified();
    }
}

