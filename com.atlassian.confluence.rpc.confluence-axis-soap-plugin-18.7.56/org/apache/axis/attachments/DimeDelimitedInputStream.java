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
import org.apache.axis.attachments.DimeBodyPart;
import org.apache.axis.attachments.DimeTypeNameFormat;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class DimeDelimitedInputStream
extends FilterInputStream {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$DimeDelimitedInputStream == null ? (class$org$apache$axis$attachments$DimeDelimitedInputStream = DimeDelimitedInputStream.class$("org.apache.axis.attachments.DimeDelimitedInputStream")) : class$org$apache$axis$attachments$DimeDelimitedInputStream).getName());
    InputStream is = null;
    volatile boolean closed = true;
    boolean theEnd = false;
    boolean moreChunks = false;
    boolean MB = false;
    boolean ME = false;
    DimeTypeNameFormat tnf = null;
    String type = null;
    String id = null;
    long recordLength = 0L;
    long bytesRead = 0L;
    int dataPadLength = 0;
    private static byte[] trash = new byte[4];
    protected int streamNo = 0;
    protected IOException streamInError = null;
    protected static int streamCount = 0;
    static boolean isDebugEnabled = false;
    static /* synthetic */ Class class$org$apache$axis$attachments$DimeDelimitedInputStream;

    protected static synchronized int newStreamNo() {
        log.debug((Object)Messages.getMessage("streamNo", "" + (streamCount + 1)));
        return ++streamCount;
    }

    synchronized DimeDelimitedInputStream getNextStream() throws IOException {
        if (null != this.streamInError) {
            throw this.streamInError;
        }
        if (this.theEnd) {
            return null;
        }
        if (this.bytesRead < this.recordLength || this.moreChunks) {
            throw new RuntimeException(Messages.getMessage("attach.dimeReadFullyError"));
        }
        this.dataPadLength -= this.readPad(this.dataPadLength);
        return new DimeDelimitedInputStream(this.is);
    }

    DimeDelimitedInputStream(InputStream is) throws IOException {
        super(null);
        isDebugEnabled = log.isDebugEnabled();
        this.streamNo = DimeDelimitedInputStream.newStreamNo();
        this.closed = false;
        this.is = is;
        this.readHeader(false);
    }

    private final int readPad(int size) throws IOException {
        if (0 == size) {
            return 0;
        }
        int read = this.readFromStream(trash, 0, size);
        if (size != read) {
            this.streamInError = new IOException(Messages.getMessage("attach.dimeNotPaddedCorrectly"));
            throw this.streamInError;
        }
        return read;
    }

    private final int readFromStream(byte[] b) throws IOException {
        return this.readFromStream(b, 0, b.length);
    }

    private final int readFromStream(byte[] b, int start, int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        int br = 0;
        int brTotal = 0;
        do {
            try {
                br = this.is.read(b, brTotal + start, length - brTotal);
            }
            catch (IOException e) {
                this.streamInError = e;
                throw e;
            }
            if (br <= 0) continue;
            brTotal += br;
        } while (br > -1 && brTotal < length);
        return br > -1 ? brTotal : br;
    }

    public String getContentId() {
        return this.id;
    }

    public DimeTypeNameFormat getDimeTypeNameFormat() {
        return this.tnf;
    }

    public String getType() {
        return this.type;
    }

    public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            this.dataPadLength -= this.readPad(this.dataPadLength);
            throw new IOException(Messages.getMessage("streamClosed"));
        }
        return this._read(b, off, len);
    }

    protected int _read(byte[] b, int off, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException(Messages.getMessage("attach.readLengthError", "" + len));
        }
        if (off < 0) {
            throw new IllegalArgumentException(Messages.getMessage("attach.readOffsetError", "" + off));
        }
        if (b == null) {
            throw new IllegalArgumentException(Messages.getMessage("attach.readArrayNullError"));
        }
        if (b.length < off + len) {
            throw new IllegalArgumentException(Messages.getMessage("attach.readArraySizeError", "" + b.length, "" + len, "" + off));
        }
        if (null != this.streamInError) {
            throw this.streamInError;
        }
        if (0 == len) {
            return 0;
        }
        if (this.recordLength == 0L && this.bytesRead == 0L && !this.moreChunks) {
            ++this.bytesRead;
            if (this.ME) {
                this.finalClose();
            }
            return 0;
        }
        if (this.bytesRead >= this.recordLength && !this.moreChunks) {
            this.dataPadLength -= this.readPad(this.dataPadLength);
            if (this.ME) {
                this.finalClose();
            }
            return -1;
        }
        int totalbytesread = 0;
        int bytes2read = 0;
        do {
            if (this.bytesRead >= this.recordLength && this.moreChunks) {
                this.readHeader(true);
            }
            bytes2read = (int)Math.min(this.recordLength - this.bytesRead, (long)len - (long)totalbytesread);
            bytes2read = (int)Math.min(this.recordLength - this.bytesRead, (long)len - (long)totalbytesread);
            try {
                bytes2read = this.is.read(b, off + totalbytesread, bytes2read);
            }
            catch (IOException e) {
                this.streamInError = e;
                throw e;
            }
            if (0 >= bytes2read) continue;
            totalbytesread += bytes2read;
            this.bytesRead += (long)bytes2read;
        } while (bytes2read > -1 && totalbytesread < len && (this.bytesRead < this.recordLength || this.moreChunks));
        if (0 > bytes2read) {
            if (this.moreChunks) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError0"));
                throw this.streamInError;
            }
            if (this.bytesRead < this.recordLength) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError1", "" + (this.recordLength - this.bytesRead)));
                throw this.streamInError;
            }
            if (!this.ME) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError0"));
                throw this.streamInError;
            }
            this.dataPadLength = 0;
        } else if (this.bytesRead >= this.recordLength) {
            try {
                this.dataPadLength -= this.readPad(this.dataPadLength);
            }
            catch (IOException e) {
                if (!this.ME) {
                    throw e;
                }
                this.dataPadLength = 0;
                this.streamInError = null;
            }
        }
        if (this.bytesRead >= this.recordLength && this.ME) {
            this.finalClose();
        }
        return totalbytesread >= 0 ? totalbytesread : -1;
    }

    void readHeader(boolean isChunk) throws IOException {
        int pad;
        byte[] header;
        this.bytesRead = 0L;
        if (isChunk) {
            if (!this.moreChunks) {
                throw new RuntimeException(Messages.getMessage("attach.DimeStreamError2"));
            }
            this.dataPadLength -= this.readPad(this.dataPadLength);
        }
        if ((header = new byte[12]).length != this.readFromStream(header)) {
            this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError3", "" + header.length));
            throw this.streamInError;
        }
        byte version = (byte)(header[0] >>> 3 & 0x1F);
        if (version > 1) {
            this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError4", "" + version, "1"));
            throw this.streamInError;
        }
        this.MB = 0 != (4 & header[0]);
        this.ME = 0 != (2 & header[0]);
        boolean bl = this.moreChunks = 0 != (1 & header[0]);
        if (!isChunk) {
            this.tnf = DimeTypeNameFormat.parseByte((byte)(header[1] >>> 4 & 0xF));
        }
        int optionsLength = header[2] << 8 & 0xFF00 | header[3];
        int idLength = header[4] << 8 & 0xFF00 | header[5];
        int typeLength = header[6] << 8 & 0xFF00 | header[7];
        this.recordLength = (long)header[8] << 24 & 0xFF000000L | (long)header[9] << 16 & 0xFF0000L | (long)header[10] << 8 & 0xFF00L | (long)header[11] & 0xFFL;
        if (0 != optionsLength) {
            byte[] optBytes = new byte[optionsLength];
            if (optionsLength != this.readFromStream(optBytes)) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError5", "" + optionsLength));
                throw this.streamInError;
            }
            optBytes = null;
            pad = DimeBodyPart.dimePadding(optionsLength);
            if (pad != this.readFromStream(header, 0, pad)) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError7"));
                throw this.streamInError;
            }
        }
        if (0 < idLength) {
            byte[] idBytes = new byte[idLength];
            if (idLength != this.readFromStream(idBytes)) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError8"));
                throw this.streamInError;
            }
            if (idLength != 0 && !isChunk) {
                this.id = new String(idBytes);
            }
            if ((pad = DimeBodyPart.dimePadding(idLength)) != this.readFromStream(header, 0, pad)) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError9"));
                throw this.streamInError;
            }
        }
        if (0 < typeLength) {
            byte[] typeBytes = new byte[typeLength];
            if (typeLength != this.readFromStream(typeBytes)) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError10"));
                throw this.streamInError;
            }
            if (typeLength != 0 && !isChunk) {
                this.type = new String(typeBytes);
            }
            if ((pad = DimeBodyPart.dimePadding(typeLength)) != this.readFromStream(header, 0, pad)) {
                this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError11"));
                throw this.streamInError;
            }
        }
        log.debug((Object)("MB:" + this.MB + ", ME:" + this.ME + ", CF:" + this.moreChunks + "Option length:" + optionsLength + ", ID length:" + idLength + ", typeLength:" + typeLength + ", TYPE_T:" + this.tnf));
        log.debug((Object)("id:\"" + this.id + "\""));
        log.debug((Object)("type:\"" + this.type + "\""));
        log.debug((Object)("recordlength:\"" + this.recordLength + "\""));
        this.dataPadLength = DimeBodyPart.dimePadding(this.recordLength);
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public int read() throws IOException {
        byte[] b = new byte[1];
        int read = this.read(b, 0, 1);
        if (read < 0) {
            return -1;
        }
        return b[0] & 0xFF;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        DimeDelimitedInputStream dimeDelimitedInputStream = this;
        synchronized (dimeDelimitedInputStream) {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        log.debug((Object)Messages.getMessage("bStreamClosed", "" + this.streamNo));
        if (this.bytesRead < this.recordLength || this.moreChunks) {
            byte[] readrest = new byte[16384];
            int bread = 0;
            while ((bread = this._read(readrest, 0, readrest.length)) > -1) {
            }
        }
        this.dataPadLength -= this.readPad(this.dataPadLength);
    }

    public void mark(int readlimit) {
    }

    public void reset() throws IOException {
        this.streamInError = new IOException(Messages.getMessage("attach.bounday.mns"));
        throw this.streamInError;
    }

    public boolean markSupported() {
        return false;
    }

    public synchronized int available() throws IOException {
        if (null != this.streamInError) {
            throw this.streamInError;
        }
        int chunkAvail = (int)Math.min(Integer.MAX_VALUE, this.recordLength - this.bytesRead);
        int streamAvail = 0;
        try {
            streamAvail = this.is.available();
        }
        catch (IOException e) {
            this.streamInError = e;
            throw e;
        }
        if (chunkAvail == 0 && this.moreChunks && 12 + this.dataPadLength <= streamAvail) {
            this.dataPadLength -= this.readPad(this.dataPadLength);
            this.readHeader(true);
            return this.available();
        }
        return Math.min(streamAvail, chunkAvail);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalClose() throws IOException {
        try {
            this.theEnd = true;
            if (null != this.is) {
                this.is.close();
            }
        }
        finally {
            this.is = null;
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

