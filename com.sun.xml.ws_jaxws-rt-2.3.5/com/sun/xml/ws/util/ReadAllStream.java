/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadAllStream
extends InputStream {
    @NotNull
    private final MemoryStream memStream = new MemoryStream();
    @NotNull
    private final FileStream fileStream = new FileStream();
    private boolean readAll;
    private boolean closed;
    private static final Logger LOGGER = Logger.getLogger(ReadAllStream.class.getName());

    public void readAll(InputStream in, long inMemory) throws IOException {
        assert (!this.readAll);
        this.readAll = true;
        boolean eof = this.memStream.readAll(in, inMemory);
        if (!eof) {
            this.fileStream.readAll(in);
        }
    }

    @Override
    public int read() throws IOException {
        int ch = this.memStream.read();
        if (ch == -1) {
            ch = this.fileStream.read();
        }
        return ch;
    }

    @Override
    public int read(byte[] b, int off, int sz) throws IOException {
        int len = this.memStream.read(b, off, sz);
        if (len == -1) {
            len = this.fileStream.read(b, off, sz);
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.memStream.close();
            this.fileStream.close();
            this.closed = true;
        }
    }

    private static class MemoryStream
    extends InputStream {
        private Chunk head;
        private Chunk tail;
        private int curOff;

        private MemoryStream() {
        }

        private void add(byte[] buf, int len) {
            if (this.tail != null) {
                this.tail = this.tail.createNext(buf, 0, len);
            } else {
                this.head = this.tail = new Chunk(buf, 0, len);
            }
        }

        boolean readAll(InputStream in, long inMemory) throws IOException {
            long total = 0L;
            do {
                byte[] buf = new byte[8192];
                int read = this.fill(in, buf);
                total += (long)read;
                if (read != 0) {
                    this.add(buf, read);
                }
                if (read == buf.length) continue;
                return true;
            } while (total <= inMemory);
            return false;
        }

        private int fill(InputStream in, byte[] buf) throws IOException {
            int total;
            int read;
            for (total = 0; total < buf.length && (read = in.read(buf, total, buf.length - total)) != -1; total += read) {
            }
            return total;
        }

        @Override
        public int read() throws IOException {
            if (!this.fetch()) {
                return -1;
            }
            return this.head.buf[this.curOff++] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int sz) throws IOException {
            if (!this.fetch()) {
                return -1;
            }
            sz = Math.min(sz, this.head.len - (this.curOff - this.head.off));
            System.arraycopy(this.head.buf, this.curOff, b, off, sz);
            this.curOff += sz;
            return sz;
        }

        private boolean fetch() {
            if (this.head == null) {
                return false;
            }
            if (this.curOff == this.head.off + this.head.len) {
                this.head = this.head.next;
                if (this.head == null) {
                    return false;
                }
                this.curOff = this.head.off;
            }
            return true;
        }

        private static final class Chunk {
            Chunk next;
            final byte[] buf;
            final int off;
            final int len;

            public Chunk(byte[] buf, int off, int len) {
                this.buf = buf;
                this.off = off;
                this.len = len;
            }

            public Chunk createNext(byte[] buf, int off, int len) {
                this.next = new Chunk(buf, off, len);
                return this.next;
            }
        }
    }

    private static class FileStream
    extends InputStream {
        @Nullable
        private File tempFile;
        @Nullable
        private FileInputStream fin;

        private FileStream() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void readAll(InputStream in) throws IOException {
            this.tempFile = File.createTempFile("jaxws", ".bin");
            try (FileOutputStream fileOut = new FileOutputStream(this.tempFile);){
                int len;
                byte[] buf = new byte[8192];
                while ((len = in.read(buf)) != -1) {
                    fileOut.write(buf, 0, len);
                }
            }
            this.fin = new FileInputStream(this.tempFile);
        }

        @Override
        public int read() throws IOException {
            return this.fin != null ? this.fin.read() : -1;
        }

        @Override
        public int read(byte[] b, int off, int sz) throws IOException {
            return this.fin != null ? this.fin.read(b, off, sz) : -1;
        }

        @Override
        public void close() throws IOException {
            boolean success;
            if (this.fin != null) {
                this.fin.close();
            }
            if (this.tempFile != null && !(success = this.tempFile.delete())) {
                LOGGER.log(Level.INFO, "File {0} could not be deleted", this.tempFile);
            }
        }
    }
}

