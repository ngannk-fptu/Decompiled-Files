/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.tar;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.tar.TarArchiveSparseEntry;
import org.apache.tools.tar.TarBuffer;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;

public class TarInputStream
extends FilterInputStream {
    private static final int SMALL_BUFFER_SIZE = 256;
    private static final int BUFFER_SIZE = 8192;
    private static final int LARGE_BUFFER_SIZE = 32768;
    private static final int BYTE_MASK = 255;
    private final byte[] SKIP_BUF = new byte[8192];
    private final byte[] SMALL_BUF = new byte[256];
    protected boolean debug;
    protected boolean hasHitEOF;
    protected long entrySize;
    protected long entryOffset;
    protected byte[] readBuf;
    protected TarBuffer buffer;
    protected TarEntry currEntry;
    protected byte[] oneBuf;
    private final ZipEncoding encoding;

    public TarInputStream(InputStream is) {
        this(is, 10240, 512);
    }

    public TarInputStream(InputStream is, String encoding) {
        this(is, 10240, 512, encoding);
    }

    public TarInputStream(InputStream is, int blockSize) {
        this(is, blockSize, 512);
    }

    public TarInputStream(InputStream is, int blockSize, String encoding) {
        this(is, blockSize, 512, encoding);
    }

    public TarInputStream(InputStream is, int blockSize, int recordSize) {
        this(is, blockSize, recordSize, null);
    }

    public TarInputStream(InputStream is, int blockSize, int recordSize, String encoding) {
        super(is);
        this.buffer = new TarBuffer(is, blockSize, recordSize);
        this.readBuf = null;
        this.oneBuf = new byte[1];
        this.debug = false;
        this.hasHitEOF = false;
        this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        this.buffer.setDebug(debug);
    }

    @Override
    public void close() throws IOException {
        this.buffer.close();
    }

    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }

    @Override
    public int available() throws IOException {
        if (this.isDirectory()) {
            return 0;
        }
        if (this.entrySize - this.entryOffset > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)(this.entrySize - this.entryOffset);
    }

    @Override
    public long skip(long numToSkip) throws IOException {
        int realSkip;
        long skip;
        int numRead;
        if (numToSkip <= 0L || this.isDirectory()) {
            return 0L;
        }
        for (skip = numToSkip; skip > 0L && (numRead = this.read(this.SKIP_BUF, 0, realSkip = (int)(skip > (long)this.SKIP_BUF.length ? (long)this.SKIP_BUF.length : skip))) != -1; skip -= (long)numRead) {
        }
        return numToSkip - skip;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int markLimit) {
    }

    @Override
    public void reset() {
    }

    public TarEntry getNextEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            long numToSkip = this.entrySize - this.entryOffset;
            if (this.debug) {
                System.err.println("TarInputStream: SKIP currENTRY '" + this.currEntry.getName() + "' SZ " + this.entrySize + " OFF " + this.entryOffset + "  skipping " + numToSkip + " bytes");
            }
            while (numToSkip > 0L) {
                long skipped = this.skip(numToSkip);
                if (skipped <= 0L) {
                    throw new IOException("failed to skip current tar entry");
                }
                numToSkip -= skipped;
            }
            this.readBuf = null;
        }
        byte[] headerBuf = this.getRecord();
        if (this.hasHitEOF) {
            this.currEntry = null;
            return null;
        }
        try {
            this.currEntry = new TarEntry(headerBuf, this.encoding);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
        if (this.debug) {
            System.err.println("TarInputStream: SET CURRENTRY '" + this.currEntry.getName() + "' size = " + this.currEntry.getSize());
        }
        this.entryOffset = 0L;
        this.entrySize = this.currEntry.getSize();
        if (this.currEntry.isGNULongLinkEntry()) {
            byte[] longLinkData = this.getLongNameData();
            if (longLinkData == null) {
                return null;
            }
            this.currEntry.setLinkName(this.encoding.decode(longLinkData));
        }
        if (this.currEntry.isGNULongNameEntry()) {
            byte[] longNameData = this.getLongNameData();
            if (longNameData == null) {
                return null;
            }
            this.currEntry.setName(this.encoding.decode(longNameData));
        }
        if (this.currEntry.isPaxHeader()) {
            this.paxHeaders();
        }
        if (this.currEntry.isGNUSparse()) {
            this.readGNUSparse();
        }
        this.entrySize = this.currEntry.getSize();
        return this.currEntry;
    }

    protected byte[] getLongNameData() throws IOException {
        ByteArrayOutputStream longName = new ByteArrayOutputStream();
        int length = 0;
        while ((length = this.read(this.SMALL_BUF)) >= 0) {
            longName.write(this.SMALL_BUF, 0, length);
        }
        this.getNextEntry();
        if (this.currEntry == null) {
            return null;
        }
        byte[] longNameData = longName.toByteArray();
        for (length = longNameData.length; length > 0 && longNameData[length - 1] == 0; --length) {
        }
        if (length != longNameData.length) {
            byte[] l = new byte[length];
            System.arraycopy(longNameData, 0, l, 0, length);
            longNameData = l;
        }
        return longNameData;
    }

    private byte[] getRecord() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        byte[] headerBuf = this.buffer.readRecord();
        if (headerBuf == null) {
            if (this.debug) {
                System.err.println("READ NULL RECORD");
            }
            this.hasHitEOF = true;
        } else if (this.buffer.isEOFRecord(headerBuf)) {
            if (this.debug) {
                System.err.println("READ EOF RECORD");
            }
            this.hasHitEOF = true;
        }
        return this.hasHitEOF ? null : headerBuf;
    }

    private void paxHeaders() throws IOException {
        Map<String, String> headers = this.parsePaxHeaders(this);
        this.getNextEntry();
        this.applyPaxHeadersToCurrentEntry(headers);
    }

    Map<String, String> parsePaxHeaders(InputStream i) throws IOException {
        int ch;
        HashMap<String, String> headers = new HashMap<String, String>();
        block0: do {
            int len = 0;
            int read = 0;
            while ((ch = i.read()) != -1) {
                ++read;
                if (ch == 32) {
                    ByteArrayOutputStream coll = new ByteArrayOutputStream();
                    while ((ch = i.read()) != -1) {
                        ++read;
                        if (ch == 61) {
                            int got;
                            String keyword = coll.toString("UTF-8");
                            int restLen = len - read;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            for (got = 0; got < restLen && (ch = i.read()) != -1; ++got) {
                                bos.write((byte)ch);
                            }
                            bos.close();
                            if (got != restLen) {
                                throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
                            }
                            byte[] rest = bos.toByteArray();
                            String value = new String(rest, 0, restLen - 1, StandardCharsets.UTF_8);
                            headers.put(keyword, value);
                            continue block0;
                        }
                        coll.write((byte)ch);
                    }
                    continue block0;
                }
                len *= 10;
                len += ch - 48;
            }
        } while (ch != -1);
        return headers;
    }

    private void applyPaxHeadersToCurrentEntry(Map<String, String> headers) {
        headers.forEach((key, val) -> {
            switch (key) {
                case "path": {
                    this.currEntry.setName((String)val);
                    break;
                }
                case "linkpath": {
                    this.currEntry.setLinkName((String)val);
                    break;
                }
                case "gid": {
                    this.currEntry.setGroupId(Long.parseLong(val));
                    break;
                }
                case "gname": {
                    this.currEntry.setGroupName((String)val);
                    break;
                }
                case "uid": {
                    this.currEntry.setUserId(Long.parseLong(val));
                    break;
                }
                case "uname": {
                    this.currEntry.setUserName((String)val);
                    break;
                }
                case "size": {
                    this.currEntry.setSize(Long.parseLong(val));
                    break;
                }
                case "mtime": {
                    this.currEntry.setModTime((long)(Double.parseDouble(val) * 1000.0));
                    break;
                }
                case "SCHILY.devminor": {
                    this.currEntry.setDevMinor(Integer.parseInt(val));
                    break;
                }
                case "SCHILY.devmajor": {
                    this.currEntry.setDevMajor(Integer.parseInt(val));
                }
            }
        });
    }

    private void readGNUSparse() throws IOException {
        if (this.currEntry.isExtended()) {
            byte[] headerBuf;
            TarArchiveSparseEntry entry;
            do {
                headerBuf = this.getRecord();
                if (!this.hasHitEOF) continue;
                this.currEntry = null;
                break;
            } while ((entry = new TarArchiveSparseEntry(headerBuf)).isExtended());
        }
    }

    @Override
    public int read() throws IOException {
        int num = this.read(this.oneBuf, 0, 1);
        return num == -1 ? -1 : this.oneBuf[0] & 0xFF;
    }

    @Override
    public int read(byte[] buf, int offset, int numToRead) throws IOException {
        int totalRead = 0;
        if (this.entryOffset >= this.entrySize || this.isDirectory()) {
            return -1;
        }
        if ((long)numToRead + this.entryOffset > this.entrySize) {
            numToRead = (int)(this.entrySize - this.entryOffset);
        }
        if (this.readBuf != null) {
            int sz = numToRead > this.readBuf.length ? this.readBuf.length : numToRead;
            System.arraycopy(this.readBuf, 0, buf, offset, sz);
            if (sz >= this.readBuf.length) {
                this.readBuf = null;
            } else {
                int newLen = this.readBuf.length - sz;
                byte[] newBuf = new byte[newLen];
                System.arraycopy(this.readBuf, sz, newBuf, 0, newLen);
                this.readBuf = newBuf;
            }
            totalRead += sz;
            numToRead -= sz;
            offset += sz;
        }
        while (numToRead > 0) {
            byte[] rec = this.buffer.readRecord();
            if (rec == null) {
                throw new IOException("unexpected EOF with " + numToRead + " bytes unread");
            }
            int recLen = rec.length;
            int sz = numToRead;
            if (recLen > sz) {
                System.arraycopy(rec, 0, buf, offset, sz);
                this.readBuf = new byte[recLen - sz];
                System.arraycopy(rec, sz, this.readBuf, 0, recLen - sz);
            } else {
                sz = recLen;
                System.arraycopy(rec, 0, buf, offset, recLen);
            }
            totalRead += sz;
            numToRead -= sz;
            offset += sz;
        }
        this.entryOffset += (long)totalRead;
        return totalRead;
    }

    public void copyEntryContents(OutputStream out) throws IOException {
        int numRead;
        byte[] buf = new byte[32768];
        while ((numRead = this.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, numRead);
        }
    }

    public boolean canReadEntryData(TarEntry te) {
        return !te.isGNUSparse();
    }

    private boolean isDirectory() {
        return this.currEntry != null && this.currEntry.isDirectory();
    }
}

