/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.util.TransientFileFactory;

public class BinaryImpl
implements Binary {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int MAX_BUFFER_SIZE = 65536;
    private final File tmpFile;
    private byte[] buffer = EMPTY_BYTE_ARRAY;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BinaryImpl(InputStream in) throws IOException {
        byte[] spoolBuffer = new byte[8192];
        int len = 0;
        File spoolFile = null;
        try (OutputStream out = null;){
            int read;
            while ((read = in.read(spoolBuffer)) > 0) {
                if (out != null) {
                    out.write(spoolBuffer, 0, read);
                    len += read;
                    continue;
                }
                if (len + read > 65536) {
                    TransientFileFactory fileFactory = TransientFileFactory.getInstance();
                    spoolFile = fileFactory.createTransientFile("bin", null, null);
                    out = new FileOutputStream(spoolFile);
                    out.write(this.buffer, 0, len);
                    out.write(spoolBuffer, 0, read);
                    this.buffer = null;
                    len += read;
                    continue;
                }
                byte[] newBuffer = new byte[len + read];
                System.arraycopy(this.buffer, 0, newBuffer, 0, len);
                System.arraycopy(spoolBuffer, 0, newBuffer, len, read);
                this.buffer = newBuffer;
                len += read;
            }
        }
        this.tmpFile = spoolFile;
    }

    public BinaryImpl(byte[] buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must be non-null");
        }
        this.buffer = buffer;
        this.tmpFile = null;
    }

    @Override
    public InputStream getStream() throws RepositoryException {
        if (this.tmpFile != null) {
            try {
                return new FileInputStream(this.tmpFile);
            }
            catch (FileNotFoundException e) {
                throw new RepositoryException("already disposed");
            }
        }
        return new ByteArrayInputStream(this.buffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(byte[] b, long position) throws IOException, RepositoryException {
        if (this.tmpFile != null) {
            try (RandomAccessFile raf = new RandomAccessFile(this.tmpFile, "r");){
                raf.seek(position);
                int n = raf.read(b);
                return n;
            }
        }
        int length = Math.min(b.length, this.buffer.length - (int)position);
        if (length > 0) {
            System.arraycopy(this.buffer, (int)position, b, 0, length);
            return length;
        }
        return -1;
    }

    @Override
    public long getSize() throws RepositoryException {
        if (this.tmpFile != null) {
            if (this.tmpFile.exists()) {
                return this.tmpFile.length();
            }
            return -1L;
        }
        return this.buffer.length;
    }

    @Override
    public void dispose() {
        if (this.tmpFile != null) {
            this.tmpFile.delete();
        } else {
            this.buffer = EMPTY_BYTE_ARRAY;
        }
    }
}

