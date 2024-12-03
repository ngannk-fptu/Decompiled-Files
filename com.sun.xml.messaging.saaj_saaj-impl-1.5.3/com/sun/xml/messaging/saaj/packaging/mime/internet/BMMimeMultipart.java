/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import javax.activation.DataSource;

public class BMMimeMultipart
extends MimeMultipart {
    private boolean begining = true;
    int[] bcs = new int[256];
    int[] gss = null;
    private static final int BUFFER_SIZE = 4096;
    private byte[] buffer = new byte[4096];
    private byte[] prevBuffer = new byte[4096];
    private BitSet lastPartFound = new BitSet(1);
    private InputStream in = null;
    private String boundary = null;
    int b = 0;
    private boolean lazyAttachments = false;

    public BMMimeMultipart() {
    }

    public BMMimeMultipart(String subtype) {
        super(subtype);
    }

    public BMMimeMultipart(DataSource ds, ContentType ct) throws MessagingException {
        super(ds, ct);
        this.boundary = ct.getParameter("boundary");
    }

    public InputStream initStream() throws MessagingException {
        if (this.in == null) {
            try {
                this.in = this.ds.getInputStream();
                if (!(this.in instanceof ByteArrayInputStream || this.in instanceof BufferedInputStream || this.in instanceof SharedInputStream)) {
                    this.in = new BufferedInputStream(this.in);
                }
            }
            catch (Exception ex) {
                throw new MessagingException("No inputstream from datasource");
            }
            if (!this.in.markSupported()) {
                throw new MessagingException("InputStream does not support Marking");
            }
        }
        return this.in;
    }

    @Override
    protected void parse() throws MessagingException {
        if (this.parsed) {
            return;
        }
        this.initStream();
        SharedInputStream sin = null;
        if (this.in instanceof SharedInputStream) {
            sin = (SharedInputStream)((Object)this.in);
        }
        String bnd = "--" + this.boundary;
        byte[] bndbytes = ASCIIUtility.getBytes(bnd);
        try {
            this.parse(this.in, bndbytes, sin);
        }
        catch (IOException ioex) {
            throw new MessagingException("IO Error", ioex);
        }
        catch (Exception ex) {
            throw new MessagingException("Error", ex);
        }
        this.parsed = true;
    }

    public boolean lastBodyPartFound() {
        return this.lastPartFound.get(0);
    }

    public MimeBodyPart getNextPart(InputStream stream, byte[] pattern, SharedInputStream sin) throws Exception {
        if (!stream.markSupported()) {
            throw new Exception("InputStream does not support Marking");
        }
        if (this.begining) {
            this.compile(pattern);
            if (!this.skipPreamble(stream, pattern, sin)) {
                throw new Exception("Missing Start Boundary, or boundary does not start on a new line");
            }
            this.begining = false;
        }
        if (this.lastBodyPartFound()) {
            throw new Exception("No parts found in Multipart InputStream");
        }
        if (sin != null) {
            long start = sin.getPosition();
            this.b = this.readHeaders(stream);
            if (this.b == -1) {
                throw new Exception("End of Stream encountered while reading part headers");
            }
            long[] v = new long[]{-1L};
            this.b = this.readBody(stream, pattern, v, null, sin);
            if (!ignoreMissingEndBoundary && this.b == -1 && !this.lastBodyPartFound()) {
                throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
            }
            long end = v[0];
            MimeBodyPart mbp = this.createMimeBodyPart(sin.newStream(start, end));
            this.addBodyPart(mbp);
            return mbp;
        }
        InternetHeaders headers = this.createInternetHeaders(stream);
        ByteOutputStream baos = new ByteOutputStream();
        this.b = this.readBody(stream, pattern, null, baos, null);
        if (!ignoreMissingEndBoundary && this.b == -1 && !this.lastBodyPartFound()) {
            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
        }
        MimeBodyPart mbp = this.createMimeBodyPart(headers, baos.getBytes(), baos.getCount());
        this.addBodyPart(mbp);
        return mbp;
    }

    public boolean parse(InputStream stream, byte[] pattern, SharedInputStream sin) throws Exception {
        while (!this.lastPartFound.get(0) && this.b != -1) {
            this.getNextPart(stream, pattern, sin);
        }
        return true;
    }

    private int readHeaders(InputStream is) throws Exception {
        int b = is.read();
        while (b != -1) {
            if (b == 13) {
                b = is.read();
                if (b != 10 || (b = is.read()) != 13 || (b = is.read()) != 10) continue;
                return b;
            }
            b = is.read();
        }
        if (b == -1) {
            throw new Exception("End of inputstream while reading Mime-Part Headers");
        }
        return b;
    }

    private int readBody(InputStream is, byte[] pattern, long[] posVector, ByteOutputStream baos, SharedInputStream sin) throws Exception {
        if (!this.find(is, pattern, posVector, baos, sin)) {
            throw new Exception("Missing boundary delimitier while reading Body Part");
        }
        return this.b;
    }

    private boolean skipPreamble(InputStream is, byte[] pattern, SharedInputStream sin) throws Exception {
        if (!this.find(is, pattern, sin)) {
            return false;
        }
        if (this.lastPartFound.get(0)) {
            throw new Exception("Found closing boundary delimiter while trying to skip preamble");
        }
        return true;
    }

    public int readNext(InputStream is, byte[] buff, int patternLength, BitSet eof, long[] posVector, SharedInputStream sin) throws Exception {
        int bufferLength = is.read(this.buffer, 0, patternLength);
        if (bufferLength == -1) {
            eof.flip(0);
        } else if (bufferLength < patternLength) {
            int i;
            int temp = 0;
            long pos = 0L;
            for (i = bufferLength; i < patternLength; ++i) {
                if (sin != null) {
                    pos = sin.getPosition();
                }
                if ((temp = is.read()) == -1) {
                    eof.flip(0);
                    if (sin == null) break;
                    posVector[0] = pos;
                    break;
                }
                this.buffer[i] = (byte)temp;
            }
            bufferLength = i;
        }
        return bufferLength;
    }

    public boolean find(InputStream is, byte[] pattern, SharedInputStream sin) throws Exception {
        int l = pattern.length;
        int lx = l - 1;
        BitSet eof = new BitSet(1);
        long[] posVector = new long[1];
        while (true) {
            int i;
            is.mark(l);
            this.readNext(is, this.buffer, l, eof, posVector, sin);
            if (eof.get(0)) {
                return false;
            }
            for (i = lx; i >= 0 && this.buffer[i] == pattern[i]; --i) {
            }
            if (i < 0) {
                if (!this.skipLWSPAndCRLF(is)) {
                    throw new Exception("Boundary does not terminate with CRLF");
                }
                return true;
            }
            int s = Math.max(i + 1 - this.bcs[this.buffer[i] & 0x7F], this.gss[i]);
            is.reset();
            is.skip(s);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean find(InputStream is, byte[] pattern, long[] posVector, ByteOutputStream out, SharedInputStream sin) throws Exception {
        int l = pattern.length;
        int lx = l - 1;
        int bufferLength = 0;
        int s = 0;
        long endPos = -1L;
        byte[] tmp = null;
        boolean first = true;
        BitSet eof = new BitSet(1);
        while (true) {
            int i;
            is.mark(l);
            if (!first) {
                tmp = this.prevBuffer;
                this.prevBuffer = this.buffer;
                this.buffer = tmp;
            }
            if (sin != null) {
                endPos = sin.getPosition();
            }
            if ((bufferLength = this.readNext(is, this.buffer, l, eof, posVector, sin)) == -1) {
                this.b = -1;
                if (s != l || sin != null) return true;
                out.write(this.prevBuffer, 0, s);
                return true;
            }
            if (bufferLength < l) {
                if (sin == null) {
                    out.write(this.buffer, 0, bufferLength);
                }
                this.b = -1;
                return true;
            }
            for (i = lx; i >= 0 && this.buffer[i] == pattern[i]; --i) {
            }
            if (i < 0) {
                if (s > 0) {
                    if (s <= 2) {
                        if (s == 2) {
                            if (this.prevBuffer[1] != 10) throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
                            if (this.prevBuffer[0] != 13 && this.prevBuffer[0] != 10) {
                                out.write(this.prevBuffer, 0, 1);
                            }
                            if (sin != null) {
                                posVector[0] = endPos;
                            }
                        } else if (s == 1) {
                            if (this.prevBuffer[0] != 10) {
                                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
                            }
                            if (sin != null) {
                                posVector[0] = endPos;
                            }
                        }
                    } else if (s > 2) {
                        if (this.prevBuffer[s - 2] == 13 && this.prevBuffer[s - 1] == 10) {
                            if (sin != null) {
                                posVector[0] = endPos - 2L;
                            } else {
                                out.write(this.prevBuffer, 0, s - 2);
                            }
                        } else {
                            if (this.prevBuffer[s - 1] != 10) throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
                            if (sin != null) {
                                posVector[0] = endPos - 1L;
                            } else {
                                out.write(this.prevBuffer, 0, s - 1);
                            }
                        }
                    }
                }
                if (this.skipLWSPAndCRLF(is)) return true;
                return true;
            }
            if (s > 0 && sin == null) {
                if (this.prevBuffer[s - 1] == 13) {
                    if (this.buffer[0] == 10) {
                        int j;
                        for (j = lx - 1; j > 0 && this.buffer[j + 1] == pattern[j]; --j) {
                        }
                        if (j == 0) {
                            out.write(this.prevBuffer, 0, s - 1);
                        } else {
                            out.write(this.prevBuffer, 0, s);
                        }
                    } else {
                        out.write(this.prevBuffer, 0, s);
                    }
                } else {
                    out.write(this.prevBuffer, 0, s);
                }
            }
            s = Math.max(i + 1 - this.bcs[this.buffer[i] & 0x7F], this.gss[i]);
            is.reset();
            is.skip(s);
            if (!first) continue;
            first = false;
        }
    }

    private boolean skipLWSPAndCRLF(InputStream is) throws Exception {
        this.b = is.read();
        if (this.b == 10) {
            return true;
        }
        if (this.b == 13) {
            this.b = is.read();
            if (this.b == 13) {
                this.b = is.read();
            }
            if (this.b == 10) {
                return true;
            }
            throw new Exception("transport padding after a Mime Boundary  should end in a CRLF, found CR only");
        }
        if (this.b == 45) {
            this.b = is.read();
            if (this.b != 45) {
                throw new Exception("Unexpected singular '-' character after Mime Boundary");
            }
            this.lastPartFound.flip(0);
            this.b = is.read();
        }
        while (this.b != -1 && (this.b == 32 || this.b == 9)) {
            this.b = is.read();
            if (this.b == 10) {
                return true;
            }
            if (this.b != 13) continue;
            this.b = is.read();
            if (this.b == 13) {
                this.b = is.read();
            }
            if (this.b != 10) continue;
            return true;
        }
        if (this.b == -1) {
            if (!this.lastPartFound.get(0)) {
                throw new Exception("End of Multipart Stream before encountering  closing boundary delimiter");
            }
            return true;
        }
        return false;
    }

    private void compile(byte[] pattern) {
        int i;
        int l = pattern.length;
        for (i = 0; i < l; ++i) {
            this.bcs[pattern[i]] = i + 1;
        }
        this.gss = new int[l];
        block1: for (i = l; i > 0; --i) {
            int j;
            for (j = l - 1; j >= i; --j) {
                if (pattern[j] != pattern[j - i]) continue block1;
                this.gss[j - 1] = i;
            }
            while (j > 0) {
                this.gss[--j] = i;
            }
        }
        this.gss[l - 1] = 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(OutputStream os) throws IOException, MessagingException {
        if (this.in != null) {
            this.contentType.setParameter("boundary", this.boundary);
        }
        String bnd = "--" + this.contentType.getParameter("boundary");
        for (int i = 0; i < this.parts.size(); ++i) {
            OutputUtil.writeln(bnd, os);
            ((MimeBodyPart)this.parts.get(i)).writeTo(os);
            OutputUtil.writeln(os);
        }
        if (this.in != null) {
            OutputUtil.writeln(bnd, os);
            if (os instanceof ByteOutputStream && this.lazyAttachments) {
                ((ByteOutputStream)os).write(this.in);
            } else {
                try (ByteOutputStream baos = null;){
                    baos = new ByteOutputStream(this.in.available());
                    baos.write(this.in);
                    baos.writeTo(os);
                    this.in = baos.newInputStream();
                }
            }
        } else {
            OutputUtil.writeAsAscii(bnd, os);
            OutputUtil.writeAsAscii("--", os);
        }
    }

    public void setInputStream(InputStream is) {
        this.in = is;
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public void setBoundary(String bnd) {
        this.boundary = bnd;
        if (this.contentType != null) {
            this.contentType.setParameter("boundary", bnd);
        }
    }

    public String getBoundary() {
        return this.boundary;
    }

    public boolean isEndOfStream() {
        return this.b == -1;
    }

    public void setLazyAttachments(boolean flag) {
        this.lazyAttachments = flag;
    }
}

