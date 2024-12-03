/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipException;
import org.apache.tools.zip.GeneralPurposeBit;
import org.apache.tools.zip.UnicodeCommentExtraField;
import org.apache.tools.zip.UnicodePathExtraField;
import org.apache.tools.zip.Zip64ExtendedInformationExtraField;
import org.apache.tools.zip.Zip64Mode;
import org.apache.tools.zip.Zip64RequiredException;
import org.apache.tools.zip.ZipEightByteInteger;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipLong;
import org.apache.tools.zip.ZipShort;
import org.apache.tools.zip.ZipUtil;

public class ZipOutputStream
extends FilterOutputStream {
    private static final int BUFFER_SIZE = 512;
    private static final int LFH_SIG_OFFSET = 0;
    private static final int LFH_VERSION_NEEDED_OFFSET = 4;
    private static final int LFH_GPB_OFFSET = 6;
    private static final int LFH_METHOD_OFFSET = 8;
    private static final int LFH_TIME_OFFSET = 10;
    private static final int LFH_CRC_OFFSET = 14;
    private static final int LFH_COMPRESSED_SIZE_OFFSET = 18;
    private static final int LFH_ORIGINAL_SIZE_OFFSET = 22;
    private static final int LFH_FILENAME_LENGTH_OFFSET = 26;
    private static final int LFH_EXTRA_LENGTH_OFFSET = 28;
    private static final int LFH_FILENAME_OFFSET = 30;
    private static final int CFH_SIG_OFFSET = 0;
    private static final int CFH_VERSION_MADE_BY_OFFSET = 4;
    private static final int CFH_VERSION_NEEDED_OFFSET = 6;
    private static final int CFH_GPB_OFFSET = 8;
    private static final int CFH_METHOD_OFFSET = 10;
    private static final int CFH_TIME_OFFSET = 12;
    private static final int CFH_CRC_OFFSET = 16;
    private static final int CFH_COMPRESSED_SIZE_OFFSET = 20;
    private static final int CFH_ORIGINAL_SIZE_OFFSET = 24;
    private static final int CFH_FILENAME_LENGTH_OFFSET = 28;
    private static final int CFH_EXTRA_LENGTH_OFFSET = 30;
    private static final int CFH_COMMENT_LENGTH_OFFSET = 32;
    private static final int CFH_DISK_NUMBER_OFFSET = 34;
    private static final int CFH_INTERNAL_ATTRIBUTES_OFFSET = 36;
    private static final int CFH_EXTERNAL_ATTRIBUTES_OFFSET = 38;
    private static final int CFH_LFH_OFFSET = 42;
    private static final int CFH_FILENAME_OFFSET = 46;
    private boolean finished = false;
    private static final int DEFLATER_BLOCK_SIZE = 8192;
    public static final int DEFLATED = 8;
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int STORED = 0;
    static final String DEFAULT_ENCODING = null;
    @Deprecated
    public static final int EFS_FLAG = 2048;
    private static final byte[] EMPTY = new byte[0];
    private CurrentEntry entry;
    private String comment = "";
    private int level = -1;
    private boolean hasCompressionLevelChanged = false;
    private int method = 8;
    private final List<ZipEntry> entries = new LinkedList<ZipEntry>();
    private final CRC32 crc = new CRC32();
    private long written = 0L;
    private long cdOffset = 0L;
    private long cdLength = 0L;
    private static final byte[] ZERO = new byte[]{0, 0};
    private static final byte[] LZERO = new byte[]{0, 0, 0, 0};
    private static final byte[] ONE = ZipLong.getBytes(1L);
    private final Map<ZipEntry, Long> offsets = new HashMap<ZipEntry, Long>();
    private String encoding = null;
    private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
    protected final Deflater def = new Deflater(this.level, true);
    protected byte[] buf = new byte[512];
    private final RandomAccessFile raf;
    private boolean useUTF8Flag = true;
    private boolean fallbackToUTF8 = false;
    private UnicodeExtraFieldPolicy createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
    private boolean hasUsedZip64 = false;
    private Zip64Mode zip64Mode = Zip64Mode.AsNeeded;
    private final Calendar calendarInstance = Calendar.getInstance();
    private final byte[] oneByte = new byte[1];
    protected static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
    protected static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
    protected static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
    protected static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
    static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
    static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);

    public ZipOutputStream(OutputStream out) {
        super(out);
        this.raf = null;
    }

    public ZipOutputStream(File file) throws IOException {
        super(null);
        RandomAccessFile ranf = null;
        try {
            ranf = new RandomAccessFile(file, "rw");
            ranf.setLength(0L);
        }
        catch (IOException e) {
            if (ranf != null) {
                try {
                    ranf.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                ranf = null;
            }
            this.out = Files.newOutputStream(file.toPath(), new OpenOption[0]);
        }
        this.raf = ranf;
    }

    public boolean isSeekable() {
        return this.raf != null;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        if (this.useUTF8Flag && !ZipEncodingHelper.isUTF8(encoding)) {
            this.useUTF8Flag = false;
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setUseLanguageEncodingFlag(boolean b) {
        this.useUTF8Flag = b && ZipEncodingHelper.isUTF8(this.encoding);
    }

    public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b) {
        this.createUnicodeExtraFields = b;
    }

    public void setFallbackToUTF8(boolean b) {
        this.fallbackToUTF8 = b;
    }

    public void setUseZip64(Zip64Mode mode) {
        this.zip64Mode = mode;
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        }
        if (this.entry != null) {
            this.closeEntry();
        }
        this.cdOffset = this.written;
        this.writeCentralDirectoryInChunks();
        this.cdLength = this.written - this.cdOffset;
        this.writeZip64CentralDirectory();
        this.writeCentralDirectoryEnd();
        this.offsets.clear();
        this.entries.clear();
        this.def.end();
        this.finished = true;
    }

    private void writeCentralDirectoryInChunks() throws IOException {
        int NUM_PER_WRITE = 1000;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70000);
        int count = 0;
        for (ZipEntry ze : this.entries) {
            byteArrayOutputStream.write(this.createCentralFileHeader(ze));
            if (++count <= 1000) continue;
            this.writeCounted(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.reset();
            count = 0;
        }
        this.writeCounted(byteArrayOutputStream.toByteArray());
    }

    public void closeEntry() throws IOException {
        this.preClose();
        this.flushDeflater();
        Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
        long bytesWritten = this.written - this.entry.dataStart;
        long realCrc = this.crc.getValue();
        this.crc.reset();
        boolean actuallyNeedsZip64 = this.handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
        this.closeEntry(actuallyNeedsZip64);
    }

    private void closeEntry(boolean actuallyNeedsZip64) throws IOException {
        if (this.raf != null) {
            this.rewriteSizesAndCrc(actuallyNeedsZip64);
        }
        this.writeDataDescriptor(this.entry.entry);
        this.entry = null;
    }

    private void preClose() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (this.entry == null) {
            throw new IOException("No current entry to close");
        }
        if (!this.entry.hasWritten) {
            this.write(EMPTY, 0, 0);
        }
    }

    private void flushDeflater() throws IOException {
        if (this.entry.entry.getMethod() == 8) {
            this.def.finish();
            while (!this.def.finished()) {
                this.deflate();
            }
        }
    }

    private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode) throws ZipException {
        if (this.entry.entry.getMethod() == 8) {
            this.entry.entry.setSize(this.entry.bytesRead);
            this.entry.entry.setCompressedSize(bytesWritten);
            this.entry.entry.setCrc(crc);
            this.def.reset();
        } else if (this.raf == null) {
            if (this.entry.entry.getCrc() != crc) {
                throw new ZipException("bad CRC checksum for entry " + this.entry.entry.getName() + ": " + Long.toHexString(this.entry.entry.getCrc()) + " instead of " + Long.toHexString(crc));
            }
            if (this.entry.entry.getSize() != bytesWritten) {
                throw new ZipException("bad size for entry " + this.entry.entry.getName() + ": " + this.entry.entry.getSize() + " instead of " + bytesWritten);
            }
        } else {
            this.entry.entry.setSize(bytesWritten);
            this.entry.entry.setCompressedSize(bytesWritten);
            this.entry.entry.setCrc(crc);
        }
        return this.checkIfNeedsZip64(effectiveMode);
    }

    private boolean checkIfNeedsZip64(Zip64Mode effectiveMode) throws ZipException {
        boolean actuallyNeedsZip64 = this.isZip64Required(this.entry.entry, effectiveMode);
        if (actuallyNeedsZip64 && effectiveMode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
        return actuallyNeedsZip64;
    }

    private boolean isZip64Required(ZipEntry entry1, Zip64Mode requestedMode) {
        return requestedMode == Zip64Mode.Always || this.isTooLageForZip32(entry1);
    }

    private boolean isTooLageForZip32(ZipEntry zipArchiveEntry) {
        return zipArchiveEntry.getSize() >= 0xFFFFFFFFL || zipArchiveEntry.getCompressedSize() >= 0xFFFFFFFFL;
    }

    private void rewriteSizesAndCrc(boolean actuallyNeedsZip64) throws IOException {
        long save = this.raf.getFilePointer();
        this.raf.seek(this.entry.localDataStart);
        this.writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
        if (!this.hasZip64Extra(this.entry.entry) || !actuallyNeedsZip64) {
            this.writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
            this.writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
        } else {
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
        }
        if (this.hasZip64Extra(this.entry.entry)) {
            this.raf.seek(this.entry.localDataStart + 12L + 4L + (long)this.getName(this.entry.entry).limit() + 4L);
            this.writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
            this.writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
            if (!actuallyNeedsZip64) {
                this.raf.seek(this.entry.localDataStart - 10L);
                this.writeOut(ZipShort.getBytes(10));
                this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
                this.entry.entry.setExtra();
                if (this.entry.causedUseOfZip64) {
                    this.hasUsedZip64 = false;
                }
            }
        }
        this.raf.seek(save);
    }

    public void putNextEntry(ZipEntry archiveEntry) throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        }
        if (this.entry != null) {
            this.closeEntry();
        }
        this.entry = new CurrentEntry(archiveEntry);
        this.entries.add(this.entry.entry);
        this.setDefaults(this.entry.entry);
        Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
        this.validateSizeInformation(effectiveMode);
        if (this.shouldAddZip64Extra(this.entry.entry, effectiveMode)) {
            Zip64ExtendedInformationExtraField z64 = this.getZip64Extra(this.entry.entry);
            ZipEightByteInteger size = ZipEightByteInteger.ZERO;
            ZipEightByteInteger compressedSize = ZipEightByteInteger.ZERO;
            if (this.entry.entry.getMethod() == 0 && this.entry.entry.getSize() != -1L) {
                compressedSize = size = new ZipEightByteInteger(this.entry.entry.getSize());
            }
            z64.setSize(size);
            z64.setCompressedSize(compressedSize);
            this.entry.entry.setExtra();
        }
        if (this.entry.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
            this.def.setLevel(this.level);
            this.hasCompressionLevelChanged = false;
        }
        this.writeLocalFileHeader(this.entry.entry);
    }

    private void setDefaults(ZipEntry entry) {
        if (entry.getMethod() == -1) {
            entry.setMethod(this.method);
        }
        if (entry.getTime() == -1L) {
            entry.setTime(System.currentTimeMillis());
        }
    }

    private void validateSizeInformation(Zip64Mode effectiveMode) throws ZipException {
        if (this.entry.entry.getMethod() == 0 && this.raf == null) {
            if (this.entry.entry.getSize() == -1L) {
                throw new ZipException("uncompressed size is required for STORED method when not writing to a file");
            }
            if (this.entry.entry.getCrc() == -1L) {
                throw new ZipException("crc checksum is required for STORED method when not writing to a file");
            }
            this.entry.entry.setCompressedSize(this.entry.entry.getSize());
        }
        if ((this.entry.entry.getSize() >= 0xFFFFFFFFL || this.entry.entry.getCompressedSize() >= 0xFFFFFFFFL) && effectiveMode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
    }

    private boolean shouldAddZip64Extra(ZipEntry entry, Zip64Mode mode) {
        return mode == Zip64Mode.Always || entry.getSize() >= 0xFFFFFFFFL || entry.getCompressedSize() >= 0xFFFFFFFFL || entry.getSize() == -1L && this.raf != null && mode != Zip64Mode.Never;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLevel(int level) {
        if (level < -1 || level > 9) {
            throw new IllegalArgumentException("Invalid compression level: " + level);
        }
        if (this.level == level) {
            return;
        }
        this.hasCompressionLevelChanged = true;
        this.level = level;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public boolean canWriteEntryData(ZipEntry ae) {
        return ZipUtil.canHandleEntryData(ae);
    }

    @Override
    public void write(int b) throws IOException {
        this.oneByte[0] = (byte)(b & 0xFF);
        this.write(this.oneByte, 0, 1);
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        if (this.entry == null) {
            throw new IllegalStateException("No current entry");
        }
        ZipUtil.checkRequestedFeatures(this.entry.entry);
        this.entry.hasWritten = true;
        if (this.entry.entry.getMethod() == 8) {
            this.writeDeflated(b, offset, length);
        } else {
            this.writeCounted(b, offset, length);
        }
        this.crc.update(b, offset, length);
    }

    private void writeCounted(byte[] data) throws IOException {
        this.writeCounted(data, 0, data.length);
    }

    private void writeCounted(byte[] data, int offset, int length) throws IOException {
        this.writeOut(data, offset, length);
        this.written += (long)length;
    }

    private void writeDeflated(byte[] b, int offset, int length) throws IOException {
        if (length > 0 && !this.def.finished()) {
            this.entry.bytesRead += length;
            if (length <= 8192) {
                this.def.setInput(b, offset, length);
                this.deflateUntilInputIsNeeded();
            } else {
                int fullblocks = length / 8192;
                for (int i = 0; i < fullblocks; ++i) {
                    this.def.setInput(b, offset + i * 8192, 8192);
                    this.deflateUntilInputIsNeeded();
                }
                int done = fullblocks * 8192;
                if (done < length) {
                    this.def.setInput(b, offset + done, length - done);
                    this.deflateUntilInputIsNeeded();
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.finished) {
            this.finish();
        }
        this.destroy();
    }

    @Override
    public void flush() throws IOException {
        if (this.out != null) {
            this.out.flush();
        }
    }

    protected final void deflate() throws IOException {
        int len = this.def.deflate(this.buf, 0, this.buf.length);
        if (len > 0) {
            this.writeCounted(this.buf, 0, len);
        }
    }

    protected void writeLocalFileHeader(ZipEntry ze) throws IOException {
        boolean encodable = this.zipEncoding.canEncode(ze.getName());
        ByteBuffer name = this.getName(ze);
        if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
            this.addUnicodeExtraFields(ze, encodable, name);
        }
        byte[] localHeader = this.createLocalFileHeader(ze, name, encodable);
        long localHeaderStart = this.written;
        this.offsets.put(ze, localHeaderStart);
        this.entry.localDataStart = localHeaderStart + 14L;
        this.writeCounted(localHeader);
        this.entry.dataStart = this.written;
    }

    private byte[] createLocalFileHeader(ZipEntry ze, ByteBuffer name, boolean encodable) {
        byte[] extra = ze.getLocalFileDataExtra();
        int nameLen = name.limit() - name.position();
        int len = 30 + nameLen + extra.length;
        byte[] buf = new byte[len];
        System.arraycopy(LFH_SIG, 0, buf, 0, 4);
        int zipMethod = ze.getMethod();
        ZipShort.putShort(this.versionNeededToExtract(zipMethod, this.hasZip64Extra(ze)), buf, 4);
        GeneralPurposeBit generalPurposeBit = this.getGeneralPurposeBits(zipMethod, !encodable && this.fallbackToUTF8);
        generalPurposeBit.encode(buf, 6);
        ZipShort.putShort(zipMethod, buf, 8);
        ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 10);
        if (zipMethod == 8 || this.raf != null) {
            System.arraycopy(LZERO, 0, buf, 14, 4);
        } else {
            ZipLong.putLong(ze.getCrc(), buf, 14);
        }
        if (this.hasZip64Extra(this.entry.entry)) {
            ZipLong.ZIP64_MAGIC.putLong(buf, 18);
            ZipLong.ZIP64_MAGIC.putLong(buf, 22);
        } else if (zipMethod == 8 || this.raf != null) {
            System.arraycopy(LZERO, 0, buf, 18, 4);
            System.arraycopy(LZERO, 0, buf, 22, 4);
        } else {
            ZipLong.putLong(ze.getSize(), buf, 18);
            ZipLong.putLong(ze.getSize(), buf, 22);
        }
        ZipShort.putShort(nameLen, buf, 26);
        ZipShort.putShort(extra.length, buf, 28);
        System.arraycopy(name.array(), name.arrayOffset(), buf, 30, nameLen);
        System.arraycopy(extra, 0, buf, 30 + nameLen, extra.length);
        return buf;
    }

    private void addUnicodeExtraFields(ZipEntry ze, boolean encodable, ByteBuffer name) throws IOException {
        String comm;
        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !encodable) {
            ze.addExtraField(new UnicodePathExtraField(ze.getName(), name.array(), name.arrayOffset(), name.limit() - name.position()));
        }
        if ((comm = ze.getComment()) == null || comm.isEmpty()) {
            return;
        }
        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !this.zipEncoding.canEncode(comm)) {
            ByteBuffer commentB = this.getEntryEncoding(ze).encode(comm);
            ze.addExtraField(new UnicodeCommentExtraField(comm, commentB.array(), commentB.arrayOffset(), commentB.limit() - commentB.position()));
        }
    }

    protected void writeDataDescriptor(ZipEntry ze) throws IOException {
        if (ze.getMethod() != 8 || this.raf != null) {
            return;
        }
        this.writeCounted(DD_SIG);
        this.writeCounted(ZipLong.getBytes(ze.getCrc()));
        if (!this.hasZip64Extra(ze)) {
            this.writeCounted(ZipLong.getBytes(ze.getCompressedSize()));
            this.writeCounted(ZipLong.getBytes(ze.getSize()));
        } else {
            this.writeCounted(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
            this.writeCounted(ZipEightByteInteger.getBytes(ze.getSize()));
        }
    }

    protected void writeCentralFileHeader(ZipEntry ze) throws IOException {
        byte[] centralFileHeader = this.createCentralFileHeader(ze);
        this.writeCounted(centralFileHeader);
    }

    private byte[] createCentralFileHeader(ZipEntry ze) throws IOException {
        boolean needsZip64Extra;
        long lfhOffset = this.offsets.get(ze);
        boolean bl = needsZip64Extra = this.hasZip64Extra(ze) || ze.getCompressedSize() >= 0xFFFFFFFFL || ze.getSize() >= 0xFFFFFFFFL || lfhOffset >= 0xFFFFFFFFL || this.zip64Mode == Zip64Mode.Always;
        if (needsZip64Extra && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
        }
        this.handleZip64Extra(ze, lfhOffset, needsZip64Extra);
        return this.createCentralFileHeader(ze, this.getName(ze), lfhOffset, needsZip64Extra);
    }

    private byte[] createCentralFileHeader(ZipEntry ze, ByteBuffer name, long lfhOffset, boolean needsZip64Extra) throws IOException {
        byte[] extra = ze.getCentralDirectoryExtra();
        String comm = ze.getComment();
        if (comm == null) {
            comm = "";
        }
        ByteBuffer commentB = this.getEntryEncoding(ze).encode(comm);
        int nameLen = name.limit() - name.position();
        int commentLen = commentB.limit() - commentB.position();
        int len = 46 + nameLen + extra.length + commentLen;
        byte[] buf = new byte[len];
        System.arraycopy(CFH_SIG, 0, buf, 0, 4);
        ZipShort.putShort(ze.getPlatform() << 8 | (!this.hasUsedZip64 ? 20 : 45), buf, 4);
        int zipMethod = ze.getMethod();
        boolean encodable = this.zipEncoding.canEncode(ze.getName());
        ZipShort.putShort(this.versionNeededToExtract(zipMethod, needsZip64Extra), buf, 6);
        this.getGeneralPurposeBits(zipMethod, !encodable && this.fallbackToUTF8).encode(buf, 8);
        ZipShort.putShort(zipMethod, buf, 10);
        ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 12);
        ZipLong.putLong(ze.getCrc(), buf, 16);
        if (ze.getCompressedSize() >= 0xFFFFFFFFL || ze.getSize() >= 0xFFFFFFFFL || this.zip64Mode == Zip64Mode.Always) {
            ZipLong.ZIP64_MAGIC.putLong(buf, 20);
            ZipLong.ZIP64_MAGIC.putLong(buf, 24);
        } else {
            ZipLong.putLong(ze.getCompressedSize(), buf, 20);
            ZipLong.putLong(ze.getSize(), buf, 24);
        }
        ZipShort.putShort(nameLen, buf, 28);
        ZipShort.putShort(extra.length, buf, 30);
        ZipShort.putShort(commentLen, buf, 32);
        System.arraycopy(ZERO, 0, buf, 34, 2);
        ZipShort.putShort(ze.getInternalAttributes(), buf, 36);
        ZipLong.putLong(ze.getExternalAttributes(), buf, 38);
        if (lfhOffset >= 0xFFFFFFFFL || this.zip64Mode == Zip64Mode.Always) {
            ZipLong.putLong(0xFFFFFFFFL, buf, 42);
        } else {
            ZipLong.putLong(Math.min(lfhOffset, 0xFFFFFFFFL), buf, 42);
        }
        System.arraycopy(name.array(), name.arrayOffset(), buf, 46, nameLen);
        int extraStart = 46 + nameLen;
        System.arraycopy(extra, 0, buf, extraStart, extra.length);
        int commentStart = extraStart + extra.length;
        System.arraycopy(commentB.array(), commentB.arrayOffset(), buf, commentStart, commentLen);
        return buf;
    }

    private void handleZip64Extra(ZipEntry ze, long lfhOffset, boolean needsZip64Extra) {
        if (needsZip64Extra) {
            Zip64ExtendedInformationExtraField z64 = this.getZip64Extra(ze);
            if (ze.getCompressedSize() >= 0xFFFFFFFFL || ze.getSize() >= 0xFFFFFFFFL || this.zip64Mode == Zip64Mode.Always) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            } else {
                z64.setCompressedSize(null);
                z64.setSize(null);
            }
            if (lfhOffset >= 0xFFFFFFFFL || this.zip64Mode == Zip64Mode.Always) {
                z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
            }
            ze.setExtra();
        }
    }

    protected void writeCentralDirectoryEnd() throws IOException {
        this.writeCounted(EOCD_SIG);
        this.writeCounted(ZERO);
        this.writeCounted(ZERO);
        int numberOfEntries = this.entries.size();
        if (numberOfEntries > 65535 && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("archive contains more than 65535 entries.");
        }
        if (this.cdOffset > 0xFFFFFFFFL && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
        }
        byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, 65535));
        this.writeCounted(num);
        this.writeCounted(num);
        this.writeCounted(ZipLong.getBytes(Math.min(this.cdLength, 0xFFFFFFFFL)));
        this.writeCounted(ZipLong.getBytes(Math.min(this.cdOffset, 0xFFFFFFFFL)));
        ByteBuffer data = this.zipEncoding.encode(this.comment);
        int dataLen = data.limit() - data.position();
        this.writeCounted(ZipShort.getBytes(dataLen));
        this.writeCounted(data.array(), data.arrayOffset(), dataLen);
    }

    @Deprecated
    protected static ZipLong toDosTime(Date time) {
        return ZipUtil.toDosTime(time);
    }

    @Deprecated
    protected static byte[] toDosTime(long t) {
        return ZipUtil.toDosTime(t);
    }

    protected byte[] getBytes(String name) throws ZipException {
        try {
            ByteBuffer b = ZipEncodingHelper.getZipEncoding(this.encoding).encode(name);
            byte[] result = new byte[b.limit()];
            System.arraycopy(b.array(), b.arrayOffset(), result, 0, result.length);
            return result;
        }
        catch (IOException ex) {
            throw new ZipException("Failed to encode name: " + ex.getMessage());
        }
    }

    protected void writeZip64CentralDirectory() throws IOException {
        if (this.zip64Mode == Zip64Mode.Never) {
            return;
        }
        if (!(this.hasUsedZip64 || this.cdOffset < 0xFFFFFFFFL && this.cdLength < 0xFFFFFFFFL && this.entries.size() < 65535)) {
            this.hasUsedZip64 = true;
        }
        if (!this.hasUsedZip64) {
            return;
        }
        long offset = this.written;
        this.writeOut(ZIP64_EOCD_SIG);
        this.writeOut(ZipEightByteInteger.getBytes(44L));
        this.writeOut(ZipShort.getBytes(45));
        this.writeOut(ZipShort.getBytes(45));
        this.writeOut(LZERO);
        this.writeOut(LZERO);
        byte[] num = ZipEightByteInteger.getBytes(this.entries.size());
        this.writeOut(num);
        this.writeOut(num);
        this.writeOut(ZipEightByteInteger.getBytes(this.cdLength));
        this.writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
        this.writeOut(ZIP64_EOCD_LOC_SIG);
        this.writeOut(LZERO);
        this.writeOut(ZipEightByteInteger.getBytes(offset));
        this.writeOut(ONE);
    }

    protected final void writeOut(byte[] data) throws IOException {
        this.writeOut(data, 0, data.length);
    }

    protected final void writeOut(byte[] data, int offset, int length) throws IOException {
        if (this.raf != null) {
            this.raf.write(data, offset, length);
        } else {
            this.out.write(data, offset, length);
        }
    }

    @Deprecated
    protected static long adjustToLong(int i) {
        return ZipUtil.adjustToLong(i);
    }

    private void deflateUntilInputIsNeeded() throws IOException {
        while (!this.def.needsInput()) {
            this.deflate();
        }
    }

    private GeneralPurposeBit getGeneralPurposeBits(int zipMethod, boolean utfFallback) {
        GeneralPurposeBit b = new GeneralPurposeBit();
        b.useUTF8ForNames(this.useUTF8Flag || utfFallback);
        if (this.isDeflatedToOutputStream(zipMethod)) {
            b.useDataDescriptor(true);
        }
        return b;
    }

    private int versionNeededToExtract(int zipMethod, boolean zip64) {
        if (zip64) {
            return 45;
        }
        return this.isDeflatedToOutputStream(zipMethod) ? 20 : 10;
    }

    private boolean isDeflatedToOutputStream(int zipMethod) {
        return zipMethod == 8 && this.raf == null;
    }

    private Zip64ExtendedInformationExtraField getZip64Extra(ZipEntry ze) {
        if (this.entry != null) {
            this.entry.causedUseOfZip64 = !this.hasUsedZip64;
        }
        this.hasUsedZip64 = true;
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (z64 == null) {
            z64 = new Zip64ExtendedInformationExtraField();
        }
        ze.addAsFirstExtraField(z64);
        return z64;
    }

    private boolean hasZip64Extra(ZipEntry ze) {
        return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) != null;
    }

    private Zip64Mode getEffectiveZip64Mode(ZipEntry ze) {
        if (this.zip64Mode != Zip64Mode.AsNeeded || this.raf != null || ze.getMethod() != 8 || ze.getSize() != -1L) {
            return this.zip64Mode;
        }
        return Zip64Mode.Never;
    }

    private ZipEncoding getEntryEncoding(ZipEntry ze) {
        boolean encodable = this.zipEncoding.canEncode(ze.getName());
        return !encodable && this.fallbackToUTF8 ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
    }

    private ByteBuffer getName(ZipEntry ze) throws IOException {
        return this.getEntryEncoding(ze).encode(ze.getName());
    }

    void destroy() throws IOException {
        if (this.raf != null) {
            this.raf.close();
        }
        if (this.out != null) {
            this.out.close();
        }
    }

    public static final class UnicodeExtraFieldPolicy {
        public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
        public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
        public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
        private final String name;

        private UnicodeExtraFieldPolicy(String n) {
            this.name = n;
        }

        public String toString() {
            return this.name;
        }
    }

    private static final class CurrentEntry {
        private final ZipEntry entry;
        private long localDataStart = 0L;
        private long dataStart = 0L;
        private long bytesRead = 0L;
        private boolean causedUseOfZip64 = false;
        private boolean hasWritten;

        private CurrentEntry(ZipEntry entry) {
            this.entry = entry;
        }
    }
}

