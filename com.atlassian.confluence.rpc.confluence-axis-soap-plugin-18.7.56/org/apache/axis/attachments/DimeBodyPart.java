/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import org.apache.axis.attachments.DimeTypeNameFormat;
import org.apache.axis.attachments.DynamicContentDataHandler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class DimeBodyPart {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$DimeBodyPart == null ? (class$org$apache$axis$attachments$DimeBodyPart = DimeBodyPart.class$("org.apache.axis.attachments.DimeBodyPart")) : class$org$apache$axis$attachments$DimeBodyPart).getName());
    protected Object data = null;
    protected DimeTypeNameFormat dtnf = null;
    protected byte[] type = null;
    protected byte[] id = null;
    static final byte POSITION_FIRST = 4;
    static final byte POSITION_LAST = 2;
    private static final byte CHUNK = 1;
    private static final byte CHUNK_NEXT = 2;
    private static int MAX_TYPE_LENGTH = 65535;
    private static int MAX_ID_LENGTH = 65535;
    static final long MAX_DWORD = 0xFFFFFFFFL;
    private static final byte[] pad = new byte[4];
    static final byte CURRENT_OPT_T = 0;
    static /* synthetic */ Class class$org$apache$axis$attachments$DimeBodyPart;

    protected DimeBodyPart() {
    }

    public DimeBodyPart(byte[] data, DimeTypeNameFormat format, String type, String id) {
        byte[] byArray = new byte[data.length];
        this.data = byArray;
        System.arraycopy(data, 0, byArray, 0, data.length);
        this.dtnf = format;
        this.type = type.getBytes();
        if (this.type.length > MAX_TYPE_LENGTH) {
            throw new IllegalArgumentException(Messages.getMessage("attach.dimetypeexceedsmax", "" + this.type.length, "" + MAX_TYPE_LENGTH));
        }
        this.id = id.getBytes();
        if (this.id.length > MAX_ID_LENGTH) {
            throw new IllegalArgumentException(Messages.getMessage("attach.dimelengthexceedsmax", "" + this.id.length, "" + MAX_ID_LENGTH));
        }
    }

    public DimeBodyPart(DataHandler dh, DimeTypeNameFormat format, String type, String id) {
        this.data = dh;
        this.dtnf = format;
        if (type == null || type.length() == 0) {
            type = "application/octet-stream";
        }
        this.type = type.getBytes();
        if (this.type.length > MAX_TYPE_LENGTH) {
            throw new IllegalArgumentException(Messages.getMessage("attach.dimetypeexceedsmax", "" + this.type.length, "" + MAX_TYPE_LENGTH));
        }
        this.id = id.getBytes();
        if (this.id.length > MAX_ID_LENGTH) {
            throw new IllegalArgumentException(Messages.getMessage("attach.dimelengthexceedsmax", "" + this.id.length, "" + MAX_ID_LENGTH));
        }
    }

    public DimeBodyPart(DataHandler dh, String id) {
        this(dh, DimeTypeNameFormat.MIME, dh.getContentType(), id);
        StringTokenizer st;
        String t;
        String ct = dh.getContentType();
        if (ct != null && (ct = ct.trim()).toLowerCase().startsWith("application/uri") && (t = (st = new StringTokenizer(ct, " \t;")).nextToken(" \t;")).equalsIgnoreCase("application/uri")) {
            while (st.hasMoreTokens()) {
                t = st.nextToken(" \t;");
                if (t.equalsIgnoreCase("uri")) {
                    t = st.nextToken("=");
                    if (t != null) {
                        if ((t = t.trim()).startsWith("\"")) {
                            t = t.substring(1);
                        }
                        if (t.endsWith("\"")) {
                            t = t.substring(0, t.length() - 1);
                        }
                        this.type = t.getBytes();
                        this.dtnf = DimeTypeNameFormat.URI;
                    }
                    return;
                }
                if (t.equalsIgnoreCase("uri=")) {
                    t = st.nextToken(" \t;");
                    if (null == t || t.length() == 0) continue;
                    if ((t = t.trim()).startsWith("\"")) {
                        t = t.substring(1);
                    }
                    if (t.endsWith("\"")) {
                        t = t.substring(0, t.length() - 1);
                    }
                    this.type = t.getBytes();
                    this.dtnf = DimeTypeNameFormat.URI;
                    return;
                }
                if (!t.toLowerCase().startsWith("uri=") || -1 == t.indexOf(61) || (t = t.substring(t.indexOf(61)).trim()).length() == 0) continue;
                if ((t = t.trim()).startsWith("\"")) {
                    t = t.substring(1);
                }
                if (t.endsWith("\"")) {
                    t = t.substring(0, t.length() - 1);
                }
                this.type = t.getBytes();
                this.dtnf = DimeTypeNameFormat.URI;
                return;
            }
        }
    }

    void write(OutputStream os, byte position, long maxchunk) throws IOException {
        if (maxchunk < 1L) {
            throw new IllegalArgumentException(Messages.getMessage("attach.dimeMaxChunkSize0", "" + maxchunk));
        }
        if (maxchunk > 0xFFFFFFFFL) {
            throw new IllegalArgumentException(Messages.getMessage("attach.dimeMaxChunkSize1", "" + maxchunk));
        }
        if (this.data instanceof byte[]) {
            this.send(os, position, (byte[])this.data, maxchunk);
        } else if (this.data instanceof DynamicContentDataHandler) {
            this.send(os, position, (DynamicContentDataHandler)((Object)this.data), maxchunk);
        } else if (this.data instanceof DataHandler) {
            DataSource source = ((DataHandler)this.data).getDataSource();
            DynamicContentDataHandler dh2 = new DynamicContentDataHandler(source);
            this.send(os, position, dh2, maxchunk);
        }
    }

    void write(OutputStream os, byte position) throws IOException {
        this.write(os, position, 0xFFFFFFFFL);
    }

    void send(OutputStream os, byte position, byte[] data, long maxchunk) throws IOException {
        this.send(os, position, data, 0, data.length, maxchunk);
    }

    void send(OutputStream os, byte position, byte[] data, int offset, int length, long maxchunk) throws IOException {
        int sendlength;
        int chunknext = 0;
        do {
            this.sendChunk(os, position, data, offset, sendlength, (byte)(((sendlength = (int)Math.min(maxchunk, (long)(length - offset))) < length - offset ? 1 : 0) | chunknext));
            chunknext = 2;
        } while ((offset += sendlength) < length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void send(OutputStream os, byte position, DataHandler dh, long maxchunk) throws IOException {
        InputStream in = null;
        try {
            int bytesread;
            long dataSize = this.getDataSize();
            in = dh.getInputStream();
            byte[] readbuf = new byte[65536];
            this.sendHeader(os, position, dataSize, (byte)0);
            long totalsent = 0L;
            do {
                if ((bytesread = in.read(readbuf)) <= 0) continue;
                os.write(readbuf, 0, bytesread);
                totalsent += (long)bytesread;
            } while (bytesread > -1);
            os.write(pad, 0, DimeBodyPart.dimePadding(totalsent));
            Object var14_10 = null;
            if (in == null) return;
        }
        catch (Throwable throwable) {
            Object var14_11 = null;
            if (in == null) throw throwable;
            try {
                in.close();
                throw throwable;
            }
            catch (IOException e) {
                // empty catch block
            }
            throw throwable;
        }
        try {
            in.close();
            return;
        }
        catch (IOException e) {}
    }

    void send(OutputStream os, byte position, DynamicContentDataHandler dh, long maxchunk) throws IOException {
        BufferedInputStream in = new BufferedInputStream(dh.getInputStream());
        int myChunkSize = dh.getChunkSize();
        byte[] buffer1 = new byte[myChunkSize];
        byte[] buffer2 = new byte[myChunkSize];
        int bytesRead1 = 0;
        int bytesRead2 = 0;
        bytesRead1 = in.read(buffer1);
        if (bytesRead1 < 0) {
            this.sendHeader(os, position, 0L, (byte)0);
            os.write(pad, 0, DimeBodyPart.dimePadding(0L));
            return;
        }
        do {
            if ((bytesRead2 = in.read(buffer2)) < 0) {
                this.sendChunk(os, position, buffer1, 0, bytesRead1, (byte)0);
                break;
            }
            this.sendChunk(os, position, buffer1, 0, bytesRead1, (byte)1);
            System.arraycopy(buffer2, 0, buffer1, 0, myChunkSize);
            bytesRead1 = bytesRead2;
        } while (bytesRead2 > 0);
    }

    protected void sendChunk(OutputStream os, byte position, byte[] data, byte chunk) throws IOException {
        this.sendChunk(os, position, data, 0, data.length, chunk);
    }

    protected void sendChunk(OutputStream os, byte position, byte[] data, int offset, int length, byte chunk) throws IOException {
        this.sendHeader(os, position, length, chunk);
        os.write(data, offset, length);
        os.write(pad, 0, DimeBodyPart.dimePadding(length));
    }

    protected void sendHeader(OutputStream os, byte position, long length, byte chunk) throws IOException {
        byte[] fixedHeader = new byte[12];
        fixedHeader[0] = 8;
        fixedHeader[0] = (byte)(fixedHeader[0] | (byte)(position & 6 & ((chunk & 1) != 0 ? -3 : -1) & ((chunk & 2) != 0 ? -5 : -1)));
        fixedHeader[0] = (byte)(fixedHeader[0] | chunk & 1);
        if ((chunk & 2) == 0) {
            fixedHeader[1] = (byte)(this.dtnf.toByte() << 4 & 0xF0);
        }
        fixedHeader[1] = (byte)(fixedHeader[1] | 0);
        fixedHeader[2] = 0;
        fixedHeader[3] = 0;
        if ((chunk & 2) == 0) {
            fixedHeader[4] = (byte)(this.id.length >>> 8 & 0xFF);
            fixedHeader[5] = (byte)(this.id.length & 0xFF);
        }
        if ((chunk & 2) == 0) {
            fixedHeader[6] = (byte)(this.type.length >>> 8 & 0xFF);
            fixedHeader[7] = (byte)(this.type.length & 0xFF);
        }
        fixedHeader[8] = (byte)(length >>> 24 & 0xFFL);
        fixedHeader[9] = (byte)(length >>> 16 & 0xFFL);
        fixedHeader[10] = (byte)(length >>> 8 & 0xFFL);
        fixedHeader[11] = (byte)(length & 0xFFL);
        os.write(fixedHeader);
        if ((chunk & 2) == 0) {
            os.write(this.id);
            os.write(pad, 0, DimeBodyPart.dimePadding(this.id.length));
        }
        if ((chunk & 2) == 0) {
            os.write(this.type);
            os.write(pad, 0, DimeBodyPart.dimePadding(this.type.length));
        }
    }

    static final int dimePadding(long l) {
        return (int)(4L - (l & 3L) & 3L);
    }

    long getTransmissionSize(long chunkSize) {
        long size = 0L;
        size += (long)this.id.length;
        size += (long)DimeBodyPart.dimePadding(this.id.length);
        size += (long)this.type.length;
        size += (long)DimeBodyPart.dimePadding(this.type.length);
        long dataSize = this.getDataSize();
        if (0L == dataSize) {
            size += 12L;
        } else {
            long fullChunks = dataSize / chunkSize;
            long lastChunkSize = dataSize % chunkSize;
            if (0L != lastChunkSize) {
                size += 12L;
            }
            size += 12L * fullChunks;
            size += fullChunks * (long)DimeBodyPart.dimePadding(chunkSize);
            size += (long)DimeBodyPart.dimePadding(lastChunkSize);
            size += dataSize;
        }
        return size;
    }

    long getTransmissionSize() {
        return this.getTransmissionSize(0xFFFFFFFFL);
    }

    protected long getDataSize() {
        if (this.data instanceof byte[]) {
            return ((byte[])this.data).length;
        }
        if (this.data instanceof DataHandler) {
            return this.getDataSize((DataHandler)this.data);
        }
        return -1L;
    }

    protected long getDataSize(DataHandler dh) {
        long dataSize = -1L;
        try {
            DataSource ds = dh.getDataSource();
            if (ds instanceof FileDataSource) {
                FileDataSource fdh = (FileDataSource)ds;
                File df = fdh.getFile();
                if (!df.exists()) {
                    throw new RuntimeException(Messages.getMessage("noFile", df.getAbsolutePath()));
                }
                dataSize = df.length();
            } else {
                int bytesread;
                dataSize = 0L;
                InputStream in = ds.getInputStream();
                byte[] readbuf = new byte[65536];
                do {
                    if ((bytesread = in.read(readbuf)) <= 0) continue;
                    dataSize += (long)bytesread;
                } while (bytesread > -1);
                if (in.markSupported()) {
                    in.reset();
                } else {
                    in.close();
                }
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
        return dataSize;
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

