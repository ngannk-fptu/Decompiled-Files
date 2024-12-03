/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 */
package org.apache.http.impl.client.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.Resource;

@Contract(threading=ThreadingBehavior.SAFE)
public class FileResource
implements Resource {
    private static final long serialVersionUID = 4132244415919043397L;
    private final File file;
    private volatile boolean disposed;

    public FileResource(File file) {
        this.file = file;
        this.disposed = false;
    }

    synchronized File getFile() {
        return this.file;
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public synchronized long length() {
        return this.file.length();
    }

    @Override
    public synchronized void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        this.file.delete();
    }
}

