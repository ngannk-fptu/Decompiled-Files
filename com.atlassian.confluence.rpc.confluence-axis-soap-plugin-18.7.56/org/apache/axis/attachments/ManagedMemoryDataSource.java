/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.WeakHashMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axis.InternalException;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class ManagedMemoryDataSource
implements DataSource {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$ManagedMemoryDataSource == null ? (class$org$apache$axis$attachments$ManagedMemoryDataSource = ManagedMemoryDataSource.class$("org.apache.axis.attachments.ManagedMemoryDataSource")) : class$org$apache$axis$attachments$ManagedMemoryDataSource).getName());
    protected String contentType = "application/octet-stream";
    InputStream ss = null;
    public static final int MIN_MEMORY_DISK_CACHED = -1;
    public static final int MAX_MEMORY_DISK_CACHED = 16384;
    protected int maxCached = 16384;
    protected File diskCacheFile = null;
    protected WeakHashMap readers = new WeakHashMap();
    protected boolean deleted = false;
    public static final int READ_CHUNK_SZ = 32768;
    protected boolean debugEnabled = false;
    protected LinkedList memorybuflist = new LinkedList();
    protected byte[] currentMemoryBuf = null;
    protected int currentMemoryBufSz = 0;
    protected long totalsz = 0L;
    protected BufferedOutputStream cachediskstream = null;
    protected boolean closed = false;
    protected static Log is_log = LogFactory.getLog((class$org$apache$axis$attachments$ManagedMemoryDataSource$Instream == null ? (class$org$apache$axis$attachments$ManagedMemoryDataSource$Instream = ManagedMemoryDataSource.class$("org.apache.axis.attachments.ManagedMemoryDataSource$Instream")) : class$org$apache$axis$attachments$ManagedMemoryDataSource$Instream).getName());
    static /* synthetic */ Class class$org$apache$axis$attachments$ManagedMemoryDataSource;
    static /* synthetic */ Class class$org$apache$axis$attachments$ManagedMemoryDataSource$Instream;

    protected ManagedMemoryDataSource() {
    }

    public ManagedMemoryDataSource(InputStream ss, int maxCached, String contentType) throws IOException {
        this(ss, maxCached, contentType, false);
    }

    public ManagedMemoryDataSource(InputStream ss, int maxCached, String contentType, boolean readall) throws IOException {
        this.ss = ss instanceof BufferedInputStream ? ss : new BufferedInputStream(ss);
        this.maxCached = maxCached;
        if (null != contentType && contentType.length() != 0) {
            this.contentType = contentType;
        }
        if (maxCached < -1) {
            throw new IllegalArgumentException(Messages.getMessage("badMaxCached", "" + maxCached));
        }
        if (log.isDebugEnabled()) {
            this.debugEnabled = true;
        }
        if (readall) {
            byte[] readbuffer = new byte[32768];
            int read = 0;
            do {
                if ((read = ss.read(readbuffer)) <= 0) continue;
                this.write(readbuffer, read);
            } while (read > -1);
            this.close();
        }
    }

    public String getContentType() {
        return this.contentType;
    }

    public synchronized InputStream getInputStream() throws IOException {
        return new Instream();
    }

    public String getName() {
        String ret = null;
        try {
            this.flushToDisk();
            if (this.diskCacheFile != null) {
                ret = this.diskCacheFile.getAbsolutePath();
            }
        }
        catch (Exception e) {
            this.diskCacheFile = null;
        }
        return ret;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    protected void write(byte[] data) throws IOException {
        this.write(data, data.length);
    }

    protected synchronized void write(byte[] data, int length) throws IOException {
        if (this.closed) {
            throw new IOException(Messages.getMessage("streamClosed"));
        }
        int writesz = length;
        int byteswritten = 0;
        if (null != this.memorybuflist && this.totalsz + (long)writesz > (long)this.maxCached && null == this.cachediskstream) {
            this.flushToDisk();
        }
        if (this.memorybuflist != null) {
            do {
                if (null == this.currentMemoryBuf) {
                    this.currentMemoryBuf = new byte[32768];
                    this.currentMemoryBufSz = 0;
                    this.memorybuflist.add(this.currentMemoryBuf);
                }
                int bytes2write = Math.min(writesz - byteswritten, this.currentMemoryBuf.length - this.currentMemoryBufSz);
                System.arraycopy(data, byteswritten, this.currentMemoryBuf, this.currentMemoryBufSz, bytes2write);
                this.currentMemoryBufSz += bytes2write;
                if ((byteswritten += bytes2write) >= writesz) continue;
                this.currentMemoryBuf = new byte[32768];
                this.currentMemoryBufSz = 0;
                this.memorybuflist.add(this.currentMemoryBuf);
            } while (byteswritten < writesz);
        }
        if (null != this.cachediskstream) {
            this.cachediskstream.write(data, 0, length);
        }
        this.totalsz += (long)writesz;
    }

    protected synchronized void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (null != this.cachediskstream) {
                this.cachediskstream.close();
                this.cachediskstream = null;
            }
            if (null != this.memorybuflist) {
                if (this.currentMemoryBufSz > 0) {
                    byte[] tmp = new byte[this.currentMemoryBufSz];
                    System.arraycopy(this.currentMemoryBuf, 0, tmp, 0, this.currentMemoryBufSz);
                    this.memorybuflist.set(this.memorybuflist.size() - 1, tmp);
                }
                this.currentMemoryBuf = null;
            }
        }
    }

    protected void finalize() throws Throwable {
        if (null != this.cachediskstream) {
            this.cachediskstream.close();
            this.cachediskstream = null;
        }
    }

    protected void flushToDisk() throws IOException, FileNotFoundException {
        LinkedList ml = this.memorybuflist;
        log.debug((Object)Messages.getMessage("maxCached", "" + this.maxCached, "" + this.totalsz));
        if (ml != null && null == this.cachediskstream) {
            try {
                MessageContext mc = MessageContext.getCurrentContext();
                String attdir = mc == null ? null : mc.getStrProp("attachments.directory");
                this.diskCacheFile = File.createTempFile("Axis", ".att", attdir == null ? null : new File(attdir));
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("diskCache", this.diskCacheFile.getAbsolutePath()));
                }
                this.cachediskstream = new BufferedOutputStream(new FileOutputStream(this.diskCacheFile));
                int listsz = ml.size();
                Iterator it = ml.iterator();
                while (it.hasNext()) {
                    byte[] rbuf = (byte[])it.next();
                    int bwrite = listsz-- == 0 ? this.currentMemoryBufSz : rbuf.length;
                    this.cachediskstream.write(rbuf, 0, bwrite);
                    if (!this.closed) continue;
                    this.cachediskstream.close();
                    this.cachediskstream = null;
                }
                this.memorybuflist = null;
            }
            catch (SecurityException se) {
                this.diskCacheFile = null;
                this.cachediskstream = null;
                this.maxCached = Integer.MAX_VALUE;
                log.info((Object)Messages.getMessage("nodisk00"), (Throwable)se);
            }
        }
    }

    public synchronized boolean delete() {
        boolean ret = false;
        this.deleted = true;
        this.memorybuflist = null;
        if (this.diskCacheFile != null) {
            if (this.cachediskstream != null) {
                try {
                    this.cachediskstream.close();
                }
                catch (Exception e) {
                    // empty catch block
                }
                this.cachediskstream = null;
            }
            Object[] array = this.readers.keySet().toArray();
            for (int i = 0; i < array.length; ++i) {
                Instream stream = (Instream)array[i];
                if (null == stream) continue;
                try {
                    stream.close();
                    continue;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            this.readers.clear();
            try {
                this.diskCacheFile.delete();
                ret = true;
            }
            catch (Exception e) {
                this.diskCacheFile.deleteOnExit();
            }
        }
        return ret;
    }

    public static void main(String[] arg) {
        try {
            String readFile = arg[0];
            String writeFile = arg[1];
            FileInputStream ss = new FileInputStream(readFile);
            ManagedMemoryDataSource ms = new ManagedMemoryDataSource(ss, 0x100000, "foo/data", true);
            DataHandler dh = new DataHandler((DataSource)ms);
            InputStream is = dh.getInputStream();
            FileOutputStream fo = new FileOutputStream(writeFile);
            byte[] buf = new byte[512];
            int read = 0;
            do {
                if ((read = is.read(buf)) <= 0) continue;
                fo.write(buf, 0, read);
            } while (read > -1);
            fo.close();
            is.close();
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
    }

    public File getDiskCacheFile() {
        return this.diskCacheFile;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private class Instream
    extends InputStream {
        protected int bread = 0;
        FileInputStream fin = null;
        int currentIndex = 0;
        byte[] currentBuf = null;
        int currentBufPos = 0;
        boolean readClosed = false;

        protected Instream() throws IOException {
            if (ManagedMemoryDataSource.this.deleted) {
                throw new IOException(Messages.getMessage("resourceDeleted"));
            }
            ManagedMemoryDataSource.this.readers.put(this, null);
        }

        public int available() throws IOException {
            if (ManagedMemoryDataSource.this.deleted) {
                throw new IOException(Messages.getMessage("resourceDeleted"));
            }
            if (this.readClosed) {
                throw new IOException(Messages.getMessage("streamClosed"));
            }
            int ret = new Long(ManagedMemoryDataSource.this.totalsz - (long)this.bread).intValue();
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)("available() = " + ret + "."));
            }
            return ret;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int read() throws IOException {
            ManagedMemoryDataSource managedMemoryDataSource = ManagedMemoryDataSource.this;
            synchronized (managedMemoryDataSource) {
                byte[] retb = new byte[1];
                int br = this.read(retb, 0, 1);
                if (br == -1) {
                    return -1;
                }
                return 0xFF & retb[0];
            }
        }

        public boolean markSupported() {
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)"markSupported() = false.");
            }
            return false;
        }

        public void mark(int readlimit) {
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)"mark()");
            }
        }

        public void reset() throws IOException {
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)"reset()");
            }
            throw new IOException(Messages.getMessage("noResetMark"));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public long skip(long skipped) throws IOException {
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)("skip(" + skipped + ")."));
            }
            if (ManagedMemoryDataSource.this.deleted) {
                throw new IOException(Messages.getMessage("resourceDeleted"));
            }
            if (this.readClosed) {
                throw new IOException(Messages.getMessage("streamClosed"));
            }
            if (skipped < 1L) {
                return 0L;
            }
            ManagedMemoryDataSource managedMemoryDataSource = ManagedMemoryDataSource.this;
            synchronized (managedMemoryDataSource) {
                skipped = Math.min(skipped, ManagedMemoryDataSource.this.totalsz - (long)this.bread);
                if (skipped == 0L) {
                    return 0L;
                }
                LinkedList ml = ManagedMemoryDataSource.this.memorybuflist;
                int bwritten = 0;
                if (ml != null) {
                    if (null == this.currentBuf) {
                        this.currentBuf = (byte[])ml.get(this.currentIndex);
                        this.currentBufPos = 0;
                    }
                    do {
                        long bcopy = Math.min((long)(this.currentBuf.length - this.currentBufPos), skipped - (long)bwritten);
                        bwritten = (int)((long)bwritten + bcopy);
                        this.currentBufPos = (int)((long)this.currentBufPos + bcopy);
                        if ((long)bwritten >= skipped) continue;
                        this.currentBuf = (byte[])ml.get(++this.currentIndex);
                        this.currentBufPos = 0;
                    } while ((long)bwritten < skipped);
                }
                if (null != this.fin) {
                    this.fin.skip(skipped);
                }
                this.bread = (int)((long)this.bread + skipped);
            }
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)("skipped " + skipped + "."));
            }
            return skipped;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int read(byte[] b, int off, int len) throws IOException {
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)(this.hashCode() + " read(" + off + ", " + len + ")"));
            }
            if (ManagedMemoryDataSource.this.deleted) {
                throw new IOException(Messages.getMessage("resourceDeleted"));
            }
            if (this.readClosed) {
                throw new IOException(Messages.getMessage("streamClosed"));
            }
            if (b == null) {
                throw new InternalException(Messages.getMessage("nullInput"));
            }
            if (off < 0) {
                throw new IndexOutOfBoundsException(Messages.getMessage("negOffset", "" + off));
            }
            if (len < 0) {
                throw new IndexOutOfBoundsException(Messages.getMessage("length", "" + len));
            }
            if (len + off > b.length) {
                throw new IndexOutOfBoundsException(Messages.getMessage("writeBeyond"));
            }
            if (len == 0) {
                return 0;
            }
            int bwritten = 0;
            ManagedMemoryDataSource managedMemoryDataSource = ManagedMemoryDataSource.this;
            synchronized (managedMemoryDataSource) {
                if ((long)this.bread == ManagedMemoryDataSource.this.totalsz) {
                    return -1;
                }
                LinkedList ml = ManagedMemoryDataSource.this.memorybuflist;
                long longlen = len;
                longlen = Math.min(longlen, ManagedMemoryDataSource.this.totalsz - (long)this.bread);
                len = new Long(longlen).intValue();
                if (ManagedMemoryDataSource.this.debugEnabled) {
                    is_log.debug((Object)("len = " + len));
                }
                if (ml != null) {
                    if (null == this.currentBuf) {
                        this.currentBuf = (byte[])ml.get(this.currentIndex);
                        this.currentBufPos = 0;
                    }
                    do {
                        int bcopy = Math.min(this.currentBuf.length - this.currentBufPos, len - bwritten);
                        System.arraycopy(this.currentBuf, this.currentBufPos, b, off + bwritten, bcopy);
                        this.currentBufPos += bcopy;
                        if ((bwritten += bcopy) >= len) continue;
                        this.currentBuf = (byte[])ml.get(++this.currentIndex);
                        this.currentBufPos = 0;
                    } while (bwritten < len);
                }
                if (bwritten == 0 && null != ManagedMemoryDataSource.this.diskCacheFile) {
                    if (ManagedMemoryDataSource.this.debugEnabled) {
                        is_log.debug((Object)Messages.getMessage("reading", "" + len));
                    }
                    if (null == this.fin) {
                        if (ManagedMemoryDataSource.this.debugEnabled) {
                            is_log.debug((Object)Messages.getMessage("openBread", ManagedMemoryDataSource.this.diskCacheFile.getCanonicalPath()));
                        }
                        if (ManagedMemoryDataSource.this.debugEnabled) {
                            is_log.debug((Object)Messages.getMessage("openBread", "" + this.bread));
                        }
                        this.fin = new FileInputStream(ManagedMemoryDataSource.this.diskCacheFile);
                        if (this.bread > 0) {
                            this.fin.skip(this.bread);
                        }
                    }
                    if (ManagedMemoryDataSource.this.cachediskstream != null) {
                        if (ManagedMemoryDataSource.this.debugEnabled) {
                            is_log.debug((Object)Messages.getMessage("flushing"));
                        }
                        ManagedMemoryDataSource.this.cachediskstream.flush();
                    }
                    if (ManagedMemoryDataSource.this.debugEnabled) {
                        is_log.debug((Object)Messages.getMessage("flushing"));
                        is_log.debug((Object)("len=" + len));
                        is_log.debug((Object)("off=" + off));
                        is_log.debug((Object)("b.length=" + b.length));
                    }
                    bwritten = this.fin.read(b, off, len);
                }
                if (bwritten > 0) {
                    this.bread += bwritten;
                }
            }
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)(this.hashCode() + Messages.getMessage("read", "" + bwritten)));
            }
            return bwritten;
        }

        public synchronized void close() throws IOException {
            if (ManagedMemoryDataSource.this.debugEnabled) {
                is_log.debug((Object)"close()");
            }
            if (!this.readClosed) {
                ManagedMemoryDataSource.this.readers.remove(this);
                this.readClosed = true;
                if (this.fin != null) {
                    this.fin.close();
                }
                this.fin = null;
            }
        }

        protected void finalize() throws Throwable {
            this.close();
        }
    }
}

