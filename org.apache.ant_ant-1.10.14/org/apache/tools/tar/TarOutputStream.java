/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.tar;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.tar.TarBuffer;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;

public class TarOutputStream
extends FilterOutputStream {
    public static final int LONGFILE_ERROR = 0;
    public static final int LONGFILE_TRUNCATE = 1;
    public static final int LONGFILE_GNU = 2;
    public static final int LONGFILE_POSIX = 3;
    public static final int BIGNUMBER_ERROR = 0;
    public static final int BIGNUMBER_STAR = 1;
    public static final int BIGNUMBER_POSIX = 2;
    protected boolean debug;
    protected long currSize;
    protected String currName;
    protected long currBytes;
    protected byte[] oneBuf;
    protected byte[] recordBuf;
    protected int assemLen;
    protected byte[] assemBuf;
    protected TarBuffer buffer;
    protected int longFileMode = 0;
    private int bigNumberMode = 0;
    private boolean closed = false;
    private boolean haveUnclosedEntry = false;
    private boolean finished = false;
    private final ZipEncoding encoding;
    private boolean addPaxHeadersForNonAsciiNames = false;
    private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");

    public TarOutputStream(OutputStream os) {
        this(os, 10240, 512);
    }

    public TarOutputStream(OutputStream os, String encoding) {
        this(os, 10240, 512, encoding);
    }

    public TarOutputStream(OutputStream os, int blockSize) {
        this(os, blockSize, 512);
    }

    public TarOutputStream(OutputStream os, int blockSize, String encoding) {
        this(os, blockSize, 512, encoding);
    }

    public TarOutputStream(OutputStream os, int blockSize, int recordSize) {
        this(os, blockSize, recordSize, null);
    }

    public TarOutputStream(OutputStream os, int blockSize, int recordSize, String encoding) {
        super(os);
        this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.buffer = new TarBuffer(os, blockSize, recordSize);
        this.debug = false;
        this.assemLen = 0;
        this.assemBuf = new byte[recordSize];
        this.recordBuf = new byte[recordSize];
        this.oneBuf = new byte[1];
    }

    public void setLongFileMode(int longFileMode) {
        this.longFileMode = longFileMode;
    }

    public void setBigNumberMode(int bigNumberMode) {
        this.bigNumberMode = bigNumberMode;
    }

    public void setAddPaxHeadersForNonAsciiNames(boolean b) {
        this.addPaxHeadersForNonAsciiNames = b;
    }

    public void setDebug(boolean debugF) {
        this.debug = debugF;
    }

    public void setBufferDebug(boolean debug) {
        this.buffer.setDebug(debug);
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.haveUnclosedEntry) {
            throw new IOException("This archives contains unclosed entries.");
        }
        this.writeEOFRecord();
        this.writeEOFRecord();
        this.buffer.flushBlock();
        this.finished = true;
    }

    @Override
    public void close() throws IOException {
        if (!this.finished) {
            this.finish();
        }
        if (!this.closed) {
            this.buffer.close();
            this.out.close();
            this.closed = true;
        }
    }

    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }

    public void putNextEntry(TarEntry entry) throws IOException {
        boolean paxHeaderContainsLinkPath;
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        HashMap<String, String> paxHeaders = new HashMap<String, String>();
        String entryName = entry.getName();
        boolean paxHeaderContainsPath = this.handleLongName(entry, entryName, paxHeaders, "path", (byte)76, "file name");
        String linkName = entry.getLinkName();
        boolean bl = paxHeaderContainsLinkPath = linkName != null && !linkName.isEmpty() && this.handleLongName(entry, linkName, paxHeaders, "linkpath", (byte)75, "link name");
        if (this.bigNumberMode == 2) {
            this.addPaxHeadersForBigNumbers(paxHeaders, entry);
        } else if (this.bigNumberMode != 1) {
            this.failForBigNumbers(entry);
        }
        if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsPath && !ASCII.canEncode(entryName)) {
            paxHeaders.put("path", entryName);
        }
        if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsLinkPath && (entry.isLink() || entry.isSymbolicLink()) && !ASCII.canEncode(linkName)) {
            paxHeaders.put("linkpath", linkName);
        }
        if (paxHeaders.size() > 0) {
            this.writePaxHeaders(entry, entryName, paxHeaders);
        }
        entry.writeEntryHeader(this.recordBuf, this.encoding, this.bigNumberMode == 1);
        this.buffer.writeRecord(this.recordBuf);
        this.currBytes = 0L;
        this.currSize = entry.isDirectory() ? 0L : entry.getSize();
        this.currName = entryName;
        this.haveUnclosedEntry = true;
    }

    public void closeEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (!this.haveUnclosedEntry) {
            throw new IOException("No current entry to close");
        }
        if (this.assemLen > 0) {
            for (int i = this.assemLen; i < this.assemBuf.length; ++i) {
                this.assemBuf[i] = 0;
            }
            this.buffer.writeRecord(this.assemBuf);
            this.currBytes += (long)this.assemLen;
            this.assemLen = 0;
        }
        if (this.currBytes < this.currSize) {
            throw new IOException("entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
        }
        this.haveUnclosedEntry = false;
    }

    @Override
    public void write(int b) throws IOException {
        this.oneBuf[0] = (byte)b;
        this.write(this.oneBuf, 0, 1);
    }

    @Override
    public void write(byte[] wBuf) throws IOException {
        this.write(wBuf, 0, wBuf.length);
    }

    @Override
    public void write(byte[] wBuf, int wOffset, int numToWrite) throws IOException {
        if (this.currBytes + (long)numToWrite > this.currSize) {
            throw new IOException("request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
        }
        if (this.assemLen > 0) {
            if (this.assemLen + numToWrite >= this.recordBuf.length) {
                int aLen = this.recordBuf.length - this.assemLen;
                System.arraycopy(this.assemBuf, 0, this.recordBuf, 0, this.assemLen);
                System.arraycopy(wBuf, wOffset, this.recordBuf, this.assemLen, aLen);
                this.buffer.writeRecord(this.recordBuf);
                this.currBytes += (long)this.recordBuf.length;
                wOffset += aLen;
                numToWrite -= aLen;
                this.assemLen = 0;
            } else {
                System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
                wOffset += numToWrite;
                this.assemLen += numToWrite;
                numToWrite = 0;
            }
        }
        while (numToWrite > 0) {
            if (numToWrite < this.recordBuf.length) {
                System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
                this.assemLen += numToWrite;
                break;
            }
            this.buffer.writeRecord(wBuf, wOffset);
            int num = this.recordBuf.length;
            this.currBytes += (long)num;
            numToWrite -= num;
            wOffset += num;
        }
    }

    void writePaxHeaders(TarEntry entry, String entryName, Map<String, String> headers) throws IOException {
        String name = "./PaxHeaders.X/" + this.stripTo7Bits(entryName);
        if (name.length() >= 100) {
            name = name.substring(0, 99);
        }
        while (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        TarEntry pex = new TarEntry(name, 120);
        this.transferModTime(entry, pex);
        StringWriter w = new StringWriter();
        for (Map.Entry<String, String> h : headers.entrySet()) {
            String key = h.getKey();
            String value = h.getValue();
            int len = key.length() + value.length() + 3 + 2;
            String line = len + " " + key + "=" + value + "\n";
            int actualLength = line.getBytes(StandardCharsets.UTF_8).length;
            while (len != actualLength) {
                len = actualLength;
                line = len + " " + key + "=" + value + "\n";
                actualLength = line.getBytes(StandardCharsets.UTF_8).length;
            }
            w.write(line);
        }
        byte[] data = w.toString().getBytes(StandardCharsets.UTF_8);
        pex.setSize(data.length);
        this.putNextEntry(pex);
        this.write(data);
        this.closeEntry();
    }

    private String stripTo7Bits(String name) {
        StringBuilder result = new StringBuilder(name.length());
        for (char ch : name.toCharArray()) {
            char stripped = (char)(ch & 0x7F);
            if (stripped == '\u0000') continue;
            result.append(stripped);
        }
        return result.toString();
    }

    private void writeEOFRecord() throws IOException {
        Arrays.fill(this.recordBuf, (byte)0);
        this.buffer.writeRecord(this.recordBuf);
    }

    private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarEntry entry) {
        this.addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 0x1FFFFFFFFL);
        this.addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), 0x1FFFFFL);
        this.addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000L, 0x1FFFFFFFFL);
        this.addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), 0x1FFFFFL);
        this.addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", entry.getDevMajor(), 0x1FFFFFL);
        this.addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", entry.getDevMinor(), 0x1FFFFFL);
        this.failForBigNumber("mode", entry.getMode(), 0x1FFFFFL);
    }

    private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue) {
        if (value < 0L || value > maxValue) {
            paxHeaders.put(header, String.valueOf(value));
        }
    }

    private void failForBigNumbers(TarEntry entry) {
        this.failForBigNumber("entry size", entry.getSize(), 0x1FFFFFFFFL);
        this.failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), 0x1FFFFFL);
        this.failForBigNumber("last modification time", entry.getModTime().getTime() / 1000L, 0x1FFFFFFFFL);
        this.failForBigNumber("user id", entry.getLongUserId(), 0x1FFFFFL);
        this.failForBigNumber("mode", entry.getMode(), 0x1FFFFFL);
        this.failForBigNumber("major device number", entry.getDevMajor(), 0x1FFFFFL);
        this.failForBigNumber("minor device number", entry.getDevMinor(), 0x1FFFFFL);
    }

    private void failForBigNumber(String field, long value, long maxValue) {
        this.failForBigNumber(field, value, maxValue, "");
    }

    private void failForBigNumberWithPosixMessage(String field, long value, long maxValue) {
        this.failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
    }

    private void failForBigNumber(String field, long value, long maxValue, String additionalMsg) {
        if (value < 0L || value > maxValue) {
            throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )");
        }
    }

    private boolean handleLongName(TarEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName) throws IOException {
        ByteBuffer encodedName = this.encoding.encode(name);
        int len = encodedName.limit() - encodedName.position();
        if (len >= 100) {
            if (this.longFileMode == 3) {
                paxHeaders.put(paxHeaderName, name);
                return true;
            }
            if (this.longFileMode == 2) {
                TarEntry longLinkEntry = new TarEntry("././@LongLink", linkType);
                longLinkEntry.setSize(len + 1);
                this.transferModTime(entry, longLinkEntry);
                this.putNextEntry(longLinkEntry);
                this.write(encodedName.array(), encodedName.arrayOffset(), len);
                this.write(0);
                this.closeEntry();
            } else if (this.longFileMode != 1) {
                throw new RuntimeException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
            }
        }
        return false;
    }

    private void transferModTime(TarEntry from, TarEntry to) {
        Date fromModTime = from.getModTime();
        long fromModTimeSeconds = fromModTime.getTime() / 1000L;
        if (fromModTimeSeconds < 0L || fromModTimeSeconds > 0x1FFFFFFFFL) {
            fromModTime = new Date(0L);
        }
        to.setModTime(fromModTime);
    }
}

