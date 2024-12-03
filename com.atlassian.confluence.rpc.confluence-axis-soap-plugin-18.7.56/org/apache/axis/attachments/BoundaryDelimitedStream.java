/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.axis.AxisFault;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class BoundaryDelimitedStream
extends FilterInputStream {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$BoundaryDelimitedStream == null ? (class$org$apache$axis$attachments$BoundaryDelimitedStream = BoundaryDelimitedStream.class$("org.apache.axis.attachments.BoundaryDelimitedStream")) : class$org$apache$axis$attachments$BoundaryDelimitedStream).getName());
    protected byte[] boundary = null;
    int boundaryLen = 0;
    int boundaryBufLen = 0;
    InputStream is = null;
    boolean closed = true;
    boolean eos = false;
    boolean theEnd = false;
    int readbufsz = 0;
    byte[] readbuf = null;
    int readBufPos = 0;
    int readBufEnd = 0;
    protected static final int BOUNDARY_NOT_FOUND = Integer.MAX_VALUE;
    int boundaryPos = Integer.MAX_VALUE;
    static int streamCount = 0;
    protected int streamNo = -1;
    static boolean isDebugEnabled = false;
    private int[] skip = null;
    static /* synthetic */ Class class$org$apache$axis$attachments$BoundaryDelimitedStream;

    protected static synchronized int newStreamNo() {
        log.debug((Object)Messages.getMessage("streamNo", "" + (streamCount + 1)));
        return ++streamCount;
    }

    public synchronized BoundaryDelimitedStream getNextStream() throws IOException {
        return this.getNextStream(this.readbufsz);
    }

    protected synchronized BoundaryDelimitedStream getNextStream(int readbufsz) throws IOException {
        BoundaryDelimitedStream ret = null;
        if (!this.theEnd) {
            ret = new BoundaryDelimitedStream(this, readbufsz);
        }
        return ret;
    }

    protected BoundaryDelimitedStream(BoundaryDelimitedStream prev, int readbufsz) throws IOException {
        super(null);
        this.streamNo = BoundaryDelimitedStream.newStreamNo();
        this.boundary = prev.boundary;
        this.boundaryLen = prev.boundaryLen;
        this.boundaryBufLen = prev.boundaryBufLen;
        this.skip = prev.skip;
        this.is = prev.is;
        this.closed = false;
        this.eos = false;
        readbufsz = prev.readbufsz;
        this.readbuf = prev.readbuf;
        this.readBufPos = prev.readBufPos + this.boundaryBufLen;
        this.readBufEnd = prev.readBufEnd;
        this.boundaryPos = this.boundaryPosition(this.readbuf, this.readBufPos, this.readBufEnd);
        prev.theEnd = this.theEnd;
    }

    BoundaryDelimitedStream(InputStream is, byte[] boundary, int readbufsz) throws AxisFault {
        super(null);
        isDebugEnabled = log.isDebugEnabled();
        this.streamNo = BoundaryDelimitedStream.newStreamNo();
        this.closed = false;
        this.is = is;
        this.boundary = new byte[boundary.length];
        System.arraycopy(boundary, 0, this.boundary, 0, boundary.length);
        this.boundaryLen = this.boundary.length;
        this.boundaryBufLen = this.boundaryLen + 2;
        this.readbufsz = Math.max(this.boundaryBufLen * 2, readbufsz);
    }

    private final int readFromStream(byte[] b) throws IOException {
        return this.readFromStream(b, 0, b.length);
    }

    private final int readFromStream(byte[] b, int start, int length) throws IOException {
        int minRead = Math.max(this.boundaryBufLen * 2, length);
        minRead = Math.min(minRead, length - start);
        int br = 0;
        int brTotal = 0;
        do {
            if ((br = this.is.read(b, brTotal + start, length - brTotal)) <= 0) continue;
            brTotal += br;
        } while (br > -1 && brTotal < minRead);
        return brTotal != 0 ? brTotal : br;
    }

    public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException(Messages.getMessage("streamClosed"));
        }
        if (this.eos) {
            return -1;
        }
        if (this.readbuf == null) {
            this.readbuf = new byte[Math.max(len, this.readbufsz)];
            this.readBufEnd = this.readFromStream(this.readbuf);
            if (this.readBufEnd < 0) {
                this.readbuf = null;
                this.closed = true;
                this.finalClose();
                throw new IOException(Messages.getMessage("eosBeforeMarker"));
            }
            this.readBufPos = 0;
            this.boundaryPos = this.boundaryPosition(this.readbuf, 0, this.readBufEnd);
        }
        int bwritten = 0;
        do {
            int bcopy = Math.min(this.readBufEnd - this.readBufPos - this.boundaryBufLen, len - bwritten);
            if ((bcopy = Math.min(bcopy, this.boundaryPos - this.readBufPos)) > 0) {
                System.arraycopy(this.readbuf, this.readBufPos, b, off + bwritten, bcopy);
                bwritten += bcopy;
                this.readBufPos += bcopy;
            }
            if (this.readBufPos == this.boundaryPos) {
                this.eos = true;
                log.debug((Object)Messages.getMessage("atEOS", "" + this.streamNo));
                continue;
            }
            if (bwritten >= len) continue;
            byte[] dstbuf = this.readbuf;
            if (this.readbuf.length < len) {
                dstbuf = new byte[len];
            }
            int movecnt = this.readBufEnd - this.readBufPos;
            System.arraycopy(this.readbuf, this.readBufPos, dstbuf, 0, movecnt);
            int readcnt = this.readFromStream(dstbuf, movecnt, dstbuf.length - movecnt);
            if (readcnt < 0) {
                this.readbuf = null;
                this.closed = true;
                this.finalClose();
                throw new IOException(Messages.getMessage("eosBeforeMarker"));
            }
            this.readBufEnd = readcnt + movecnt;
            this.readbuf = dstbuf;
            this.readBufPos = 0;
            if (Integer.MAX_VALUE != this.boundaryPos) {
                this.boundaryPos -= movecnt;
                continue;
            }
            this.boundaryPos = this.boundaryPosition(this.readbuf, this.readBufPos, this.readBufEnd);
        } while (!this.eos && bwritten < len);
        if (log.isDebugEnabled() && bwritten > 0) {
            byte[] tb = new byte[bwritten];
            System.arraycopy(b, off, tb, 0, bwritten);
            log.debug((Object)Messages.getMessage("readBStream", new String[]{"" + bwritten, "" + this.streamNo, new String(tb)}));
        }
        if (this.eos && this.theEnd) {
            this.readbuf = null;
        }
        return bwritten;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public int read() throws IOException {
        byte[] b = new byte[1];
        int read = this.read(b);
        if (read < 0) {
            return -1;
        }
        return b[0] & 0xFF;
    }

    public synchronized void close() throws IOException {
        if (this.closed) {
            return;
        }
        log.debug((Object)Messages.getMessage("bStreamClosed", "" + this.streamNo));
        this.closed = true;
        if (!this.eos) {
            byte[] readrest = new byte[16384];
            int bread = 0;
            while ((bread = this.read(readrest)) > -1) {
            }
        }
    }

    public void mark(int readlimit) {
    }

    public void reset() throws IOException {
        throw new IOException(Messages.getMessage("attach.bounday.mns"));
    }

    public boolean markSupported() {
        return false;
    }

    public int available() throws IOException {
        int bcopy = this.readBufEnd - this.readBufPos - this.boundaryBufLen;
        bcopy = Math.min(bcopy, this.boundaryPos - this.readBufPos);
        return Math.max(0, bcopy);
    }

    protected int boundaryPosition(byte[] searchbuf, int start, int end) throws IOException {
        int foundAt = this.boundarySearch(searchbuf, start, end);
        if (Integer.MAX_VALUE != foundAt) {
            if (foundAt + this.boundaryLen + 2 > end) {
                foundAt = Integer.MAX_VALUE;
            } else if (searchbuf[foundAt + this.boundaryLen] == 45 && searchbuf[foundAt + this.boundaryLen + 1] == 45) {
                this.finalClose();
            } else if (searchbuf[foundAt + this.boundaryLen] != 13 || searchbuf[foundAt + this.boundaryLen + 1] != 10) {
                foundAt = Integer.MAX_VALUE;
            }
        }
        return foundAt;
    }

    private int boundarySearch(byte[] text, int start, int end) {
        int i = 0;
        int j = 0;
        int k = 0;
        if (this.boundaryLen > end - start) {
            return Integer.MAX_VALUE;
        }
        if (null == this.skip) {
            this.skip = new int[256];
            Arrays.fill(this.skip, this.boundaryLen);
            for (k = 0; k < this.boundaryLen - 1; ++k) {
                this.skip[this.boundary[k]] = this.boundaryLen - k - 1;
            }
        }
        for (k = start + this.boundaryLen - 1; k < end; k += this.skip[text[k] & 0xFF]) {
            try {
                i = k;
                for (j = this.boundaryLen - 1; j >= 0 && text[i] == this.boundary[j]; --j) {
                    --i;
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                StringBuffer sb = new StringBuffer();
                sb.append(">>>" + e);
                sb.append("start=" + start);
                sb.append("k=" + k);
                sb.append("text.length=" + text.length);
                sb.append("i=" + i);
                sb.append("boundary.length=" + this.boundary.length);
                sb.append("j=" + j);
                sb.append("end=" + end);
                log.warn((Object)Messages.getMessage("exception01", sb.toString()));
                throw e;
            }
            if (j != -1) continue;
            return i + 1;
        }
        return Integer.MAX_VALUE;
    }

    protected void finalClose() throws IOException {
        if (this.theEnd) {
            return;
        }
        this.theEnd = true;
        this.is.close();
        this.is = null;
    }

    public static void printarry(byte[] b, int start, int end) {
        if (log.isDebugEnabled()) {
            byte[] tb = new byte[end - start];
            System.arraycopy(b, start, tb, 0, end - start);
            log.debug((Object)("\"" + new String(tb) + "\""));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

