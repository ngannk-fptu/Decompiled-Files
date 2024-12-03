/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.util;

import java.io.BufferedWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DetachableInputStream
extends FilterInputStream {
    private static final Log log = LogFactory.getLog(DetachableInputStream.class);
    private long count = 0L;
    BAAInputStream localStream = null;
    boolean isClosed = false;

    public DetachableInputStream(InputStream in) {
        super(in);
    }

    public long length() throws IOException {
        if (this.localStream == null) {
            this.detach();
        }
        return this.count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void detach() throws IOException {
        if (this.localStream == null && !this.isClosed) {
            BAAOutputStream baaos = new BAAOutputStream();
            try {
                BufferUtils.inputStream2OutputStream(this.in, baaos);
                super.close();
            }
            catch (Throwable t) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("detach caught exception.  Processing continues:" + t));
                    log.debug((Object)("  " + DetachableInputStream.stackToString(t)));
                }
            }
            finally {
                this.in = null;
            }
            this.localStream = new BAAInputStream(baaos.buffers(), baaos.length());
            if (log.isDebugEnabled()) {
                log.debug((Object)("The local stream built from the detached stream has a length of:" + baaos.length()));
            }
            this.count += (long)baaos.length();
        }
    }

    public int available() throws IOException {
        if (this.localStream != null) {
            return this.localStream.available();
        }
        return super.available();
    }

    public void close() throws IOException {
        this.isClosed = true;
        if (this.localStream != null) {
            this.localStream.close();
        } else {
            super.close();
        }
    }

    public boolean markSupported() {
        return false;
    }

    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    public int read() throws IOException {
        if (this.localStream == null) {
            int rc = super.read();
            if (rc != -1) {
                ++this.count;
            }
            return rc;
        }
        return this.localStream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.localStream == null) {
            int rc = super.read(b, off, len);
            if (rc > 0) {
                this.count += (long)rc;
            }
            return rc;
        }
        return this.localStream.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        if (this.localStream == null) {
            int rc = super.read(b);
            if (rc > 0) {
                this.count += (long)rc;
            }
            return rc;
        }
        return this.localStream.read(b);
    }

    public synchronized void reset() throws IOException {
        throw new IOException();
    }

    public long skip(long n) throws IOException {
        if (this.localStream == null) {
            long rc = super.skip(n);
            if (rc > 0L) {
                this.count += rc;
            }
            return rc;
        }
        return this.localStream.skip(n);
    }

    private static String stackToString(Throwable e) {
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        PrintWriter pw = new PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        return text;
    }
}

