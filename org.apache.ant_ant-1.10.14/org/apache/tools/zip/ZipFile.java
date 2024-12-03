/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import org.apache.tools.zip.GeneralPurposeBit;
import org.apache.tools.zip.Zip64ExtendedInformationExtraField;
import org.apache.tools.zip.ZipEightByteInteger;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipLong;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.zip.ZipShort;
import org.apache.tools.zip.ZipUtil;

public class ZipFile
implements Closeable {
    private static final int HASH_SIZE = 509;
    static final int NIBLET_MASK = 15;
    static final int BYTE_SHIFT = 8;
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private final List<ZipEntry> entries = new LinkedList<ZipEntry>();
    private final Map<String, LinkedList<ZipEntry>> nameMap = new HashMap<String, LinkedList<ZipEntry>>(509);
    private final String encoding;
    private final ZipEncoding zipEncoding;
    private final String archiveName;
    private final RandomAccessFile archive;
    private final boolean useUnicodeExtraFields;
    private volatile boolean closed;
    private final byte[] DWORD_BUF = new byte[8];
    private final byte[] WORD_BUF = new byte[4];
    private final byte[] CFH_BUF = new byte[42];
    private final byte[] SHORT_BUF = new byte[2];
    private static final int CFH_LEN = 42;
    private static final long CFH_SIG = ZipLong.getValue(ZipOutputStream.CFH_SIG);
    private static final int MIN_EOCD_SIZE = 22;
    private static final int MAX_EOCD_SIZE = 65557;
    private static final int CFD_LOCATOR_OFFSET = 16;
    private static final int ZIP64_EOCDL_LENGTH = 20;
    private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
    private final Comparator<ZipEntry> OFFSET_COMPARATOR = (e1, e2) -> {
        Entry ent2;
        if (e1 == e2) {
            return 0;
        }
        Entry ent1 = e1 instanceof Entry ? (Entry)e1 : null;
        Entry entry = ent2 = e2 instanceof Entry ? (Entry)e2 : null;
        if (ent1 == null) {
            return 1;
        }
        if (ent2 == null) {
            return -1;
        }
        long val = ent1.getOffsetEntry().headerOffset - ent2.getOffsetEntry().headerOffset;
        return val == 0L ? 0 : (val < 0L ? -1 : 1);
    };

    public ZipFile(File f) throws IOException {
        this(f, null);
    }

    public ZipFile(String name) throws IOException {
        this(new File(name), null);
    }

    public ZipFile(String name, String encoding) throws IOException {
        this(new File(name), encoding, true);
    }

    public ZipFile(File f, String encoding) throws IOException {
        this(f, encoding, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZipFile(File f, String encoding, boolean useUnicodeExtraFields) throws IOException {
        this.archiveName = f.getAbsolutePath();
        this.encoding = encoding;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.useUnicodeExtraFields = useUnicodeExtraFields;
        this.archive = new RandomAccessFile(f, "r");
        boolean success = false;
        try {
            Map<ZipEntry, NameAndComment> entriesWithoutUTF8Flag = this.populateFromCentralDirectory();
            this.resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
            success = true;
            boolean bl = this.closed = !success;
        }
        catch (Throwable throwable) {
            boolean bl = this.closed = !success;
            if (!success) {
                try {
                    this.archive.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            throw throwable;
        }
        if (!success) {
            try {
                this.archive.close();
            }
            catch (IOException iOException) {}
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.archive.close();
    }

    public static void closeQuietly(ZipFile zipfile) {
        if (zipfile != null) {
            try {
                zipfile.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public Enumeration<ZipEntry> getEntries() {
        return Collections.enumeration(this.entries);
    }

    public Enumeration<ZipEntry> getEntriesInPhysicalOrder() {
        return this.entries.stream().sorted(this.OFFSET_COMPARATOR).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::enumeration));
    }

    public ZipEntry getEntry(String name) {
        LinkedList<ZipEntry> entriesOfThatName = this.nameMap.get(name);
        return entriesOfThatName != null ? entriesOfThatName.getFirst() : null;
    }

    public Iterable<ZipEntry> getEntries(String name) {
        List<ZipEntry> entriesOfThatName = (List<ZipEntry>)this.nameMap.get(name);
        return entriesOfThatName != null ? entriesOfThatName : Collections.emptyList();
    }

    public Iterable<ZipEntry> getEntriesInPhysicalOrder(String name) {
        if (this.nameMap.containsKey(name)) {
            return this.nameMap.get(name).stream().sorted(this.OFFSET_COMPARATOR).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean canReadEntryData(ZipEntry ze) {
        return ZipUtil.canHandleEntryData(ze);
    }

    public InputStream getInputStream(ZipEntry ze) throws IOException, ZipException {
        if (!(ze instanceof Entry)) {
            return null;
        }
        OffsetEntry offsetEntry = ((Entry)ze).getOffsetEntry();
        ZipUtil.checkRequestedFeatures(ze);
        long start = offsetEntry.dataOffset;
        BoundedInputStream bis = new BoundedInputStream(start, ze.getCompressedSize());
        switch (ze.getMethod()) {
            case 0: {
                return bis;
            }
            case 8: {
                bis.addDummy();
                final Inflater inflater = new Inflater(true);
                return new InflaterInputStream(bis, inflater){

                    @Override
                    public void close() throws IOException {
                        super.close();
                        inflater.end();
                    }
                };
            }
        }
        throw new ZipException("Found unsupported compression method " + ze.getMethod());
    }

    public String getName() {
        return this.archiveName;
    }

    protected void finalize() throws Throwable {
        try {
            if (!this.closed) {
                System.err.printf("Cleaning up unclosed %s for archive %s%n", this.getClass().getSimpleName(), this.archiveName);
                this.close();
            }
        }
        finally {
            super.finalize();
        }
    }

    private Map<ZipEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
        HashMap<ZipEntry, NameAndComment> noUTF8Flag = new HashMap<ZipEntry, NameAndComment>();
        this.positionAtCentralDirectory();
        this.archive.readFully(this.WORD_BUF);
        long sig = ZipLong.getValue(this.WORD_BUF);
        if (sig != CFH_SIG && this.startsWithLocalFileHeader()) {
            throw new IOException("central directory is empty, can't expand corrupt archive.");
        }
        while (sig == CFH_SIG) {
            this.readCentralDirectoryEntry(noUTF8Flag);
            this.archive.readFully(this.WORD_BUF);
            sig = ZipLong.getValue(this.WORD_BUF);
        }
        return noUTF8Flag;
    }

    private void readCentralDirectoryEntry(Map<ZipEntry, NameAndComment> noUTF8Flag) throws IOException {
        this.archive.readFully(this.CFH_BUF);
        int off = 0;
        OffsetEntry offset = new OffsetEntry();
        Entry ze = new Entry(offset);
        int versionMadeBy = ZipShort.getValue(this.CFH_BUF, off);
        off += 2;
        ze.setPlatform(versionMadeBy >> 8 & 0xF);
        GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.CFH_BUF, off += 2);
        boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
        ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        ze.setGeneralPurposeBit(gpFlag);
        ze.setMethod(ZipShort.getValue(this.CFH_BUF, off += 2));
        long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.CFH_BUF, off += 2));
        ze.setTime(time);
        ze.setCrc(ZipLong.getValue(this.CFH_BUF, off += 4));
        ze.setCompressedSize(ZipLong.getValue(this.CFH_BUF, off += 4));
        ze.setSize(ZipLong.getValue(this.CFH_BUF, off += 4));
        int fileNameLen = ZipShort.getValue(this.CFH_BUF, off += 4);
        int extraLen = ZipShort.getValue(this.CFH_BUF, off += 2);
        int commentLen = ZipShort.getValue(this.CFH_BUF, off += 2);
        int diskStart = ZipShort.getValue(this.CFH_BUF, off += 2);
        ze.setInternalAttributes(ZipShort.getValue(this.CFH_BUF, off += 2));
        ze.setExternalAttributes(ZipLong.getValue(this.CFH_BUF, off += 2));
        off += 4;
        if (this.archive.length() - this.archive.getFilePointer() < (long)fileNameLen) {
            throw new EOFException();
        }
        byte[] fileName = new byte[fileNameLen];
        this.archive.readFully(fileName);
        ze.setName(entryEncoding.decode(fileName), fileName);
        offset.headerOffset = ZipLong.getValue(this.CFH_BUF, off);
        this.entries.add(ze);
        if (this.archive.length() - this.archive.getFilePointer() < (long)extraLen) {
            throw new EOFException();
        }
        byte[] cdExtraData = new byte[extraLen];
        this.archive.readFully(cdExtraData);
        ze.setCentralDirectoryExtra(cdExtraData);
        this.setSizesAndOffsetFromZip64Extra(ze, offset, diskStart);
        if (this.archive.length() - this.archive.getFilePointer() < (long)commentLen) {
            throw new EOFException();
        }
        byte[] comment = new byte[commentLen];
        this.archive.readFully(comment);
        ze.setComment(entryEncoding.decode(comment));
        if (!hasUTF8Flag && this.useUnicodeExtraFields) {
            noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
        }
    }

    private void setSizesAndOffsetFromZip64Extra(ZipEntry ze, OffsetEntry offset, int diskStart) throws IOException {
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (z64 != null) {
            boolean hasUncompressedSize = ze.getSize() == 0xFFFFFFFFL;
            boolean hasCompressedSize = ze.getCompressedSize() == 0xFFFFFFFFL;
            boolean hasRelativeHeaderOffset = offset.headerOffset == 0xFFFFFFFFL;
            z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, diskStart == 65535);
            if (hasUncompressedSize) {
                ze.setSize(z64.getSize().getLongValue());
            } else if (hasCompressedSize) {
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            }
            if (hasCompressedSize) {
                ze.setCompressedSize(z64.getCompressedSize().getLongValue());
            } else if (hasUncompressedSize) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
            }
            if (hasRelativeHeaderOffset) {
                offset.headerOffset = z64.getRelativeHeaderOffset().getLongValue();
            }
        }
    }

    private void positionAtCentralDirectory() throws IOException {
        boolean searchedForZip64EOCD;
        this.positionAtEndOfCentralDirectoryRecord();
        boolean found = false;
        boolean bl = searchedForZip64EOCD = this.archive.getFilePointer() > 20L;
        if (searchedForZip64EOCD) {
            this.archive.seek(this.archive.getFilePointer() - 20L);
            this.archive.readFully(this.WORD_BUF);
            found = Arrays.equals(ZipOutputStream.ZIP64_EOCD_LOC_SIG, this.WORD_BUF);
        }
        if (!found) {
            if (searchedForZip64EOCD) {
                this.skipBytes(16);
            }
            this.positionAtCentralDirectory32();
        } else {
            this.positionAtCentralDirectory64();
        }
    }

    private void positionAtCentralDirectory64() throws IOException {
        this.skipBytes(4);
        this.archive.readFully(this.DWORD_BUF);
        this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
        this.archive.readFully(this.WORD_BUF);
        if (!Arrays.equals(this.WORD_BUF, ZipOutputStream.ZIP64_EOCD_SIG)) {
            throw new ZipException("archive's ZIP64 end of central directory locator is corrupt.");
        }
        this.skipBytes(44);
        this.archive.readFully(this.DWORD_BUF);
        this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
    }

    private void positionAtCentralDirectory32() throws IOException {
        this.skipBytes(16);
        this.archive.readFully(this.WORD_BUF);
        this.archive.seek(ZipLong.getValue(this.WORD_BUF));
    }

    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        boolean found = this.tryToLocateSignature(22L, 65557L, ZipOutputStream.EOCD_SIG);
        if (!found) {
            throw new ZipException("archive is not a ZIP archive");
        }
    }

    private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig) throws IOException {
        long off;
        boolean found = false;
        long stopSearching = Math.max(0L, this.archive.length() - maxDistanceFromEnd);
        if (off >= 0L) {
            for (off = this.archive.length() - minDistanceFromEnd; off >= stopSearching; --off) {
                this.archive.seek(off);
                int curr = this.archive.read();
                if (curr == -1) break;
                if (curr != sig[0] || (curr = this.archive.read()) != sig[1] || (curr = this.archive.read()) != sig[2] || (curr = this.archive.read()) != sig[3]) continue;
                found = true;
                break;
            }
        }
        if (found) {
            this.archive.seek(off);
        }
        return found;
    }

    private void skipBytes(int count) throws IOException {
        int skippedNow;
        for (int totalSkipped = 0; totalSkipped < count; totalSkipped += skippedNow) {
            skippedNow = this.archive.skipBytes(count - totalSkipped);
            if (skippedNow > 0) continue;
            throw new EOFException();
        }
    }

    private void resolveLocalFileHeaderData(Map<ZipEntry, NameAndComment> entriesWithoutUTF8Flag) throws IOException {
        for (ZipEntry zipEntry : this.entries) {
            int skipped;
            Entry ze = (Entry)zipEntry;
            OffsetEntry offsetEntry = ze.getOffsetEntry();
            long offset = offsetEntry.headerOffset;
            this.archive.seek(offset + 26L);
            this.archive.readFully(this.SHORT_BUF);
            int fileNameLen = ZipShort.getValue(this.SHORT_BUF);
            this.archive.readFully(this.SHORT_BUF);
            int extraFieldLen = ZipShort.getValue(this.SHORT_BUF);
            for (int lenToSkip = fileNameLen; lenToSkip > 0; lenToSkip -= skipped) {
                skipped = this.archive.skipBytes(lenToSkip);
                if (skipped > 0) continue;
                throw new IOException("failed to skip file name in local file header");
            }
            if (this.archive.length() - this.archive.getFilePointer() < (long)extraFieldLen) {
                throw new EOFException();
            }
            byte[] localExtraData = new byte[extraFieldLen];
            this.archive.readFully(localExtraData);
            try {
                ze.setExtra(localExtraData);
            }
            catch (RuntimeException ex) {
                ZipException z = new ZipException("Invalid extra data in entry " + ze.getName());
                z.initCause(ex);
                throw z;
            }
            offsetEntry.dataOffset = offset + 26L + 2L + 2L + (long)fileNameLen + (long)extraFieldLen;
            if (entriesWithoutUTF8Flag.containsKey(ze)) {
                NameAndComment nc = entriesWithoutUTF8Flag.get(ze);
                ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
            }
            String name = ze.getName();
            LinkedList entriesOfThatName = this.nameMap.computeIfAbsent(name, k -> new LinkedList());
            entriesOfThatName.addLast(ze);
        }
    }

    private boolean startsWithLocalFileHeader() throws IOException {
        this.archive.seek(0L);
        this.archive.readFully(this.WORD_BUF);
        return Arrays.equals(this.WORD_BUF, ZipOutputStream.LFH_SIG);
    }

    private static class Entry
    extends ZipEntry {
        private final OffsetEntry offsetEntry;

        Entry(OffsetEntry offset) {
            this.offsetEntry = offset;
        }

        OffsetEntry getOffsetEntry() {
            return this.offsetEntry;
        }

        @Override
        public int hashCode() {
            return 3 * super.hashCode() + (int)(this.offsetEntry.headerOffset % Integer.MAX_VALUE);
        }

        @Override
        public boolean equals(Object other) {
            if (super.equals(other)) {
                Entry otherEntry = (Entry)other;
                return this.offsetEntry.headerOffset == otherEntry.offsetEntry.headerOffset && this.offsetEntry.dataOffset == otherEntry.offsetEntry.dataOffset;
            }
            return false;
        }
    }

    private static final class OffsetEntry {
        private long headerOffset = -1L;
        private long dataOffset = -1L;

        private OffsetEntry() {
        }
    }

    private class BoundedInputStream
    extends InputStream {
        private long remaining;
        private long loc;
        private boolean addDummyByte = false;

        BoundedInputStream(long start, long remaining) {
            this.remaining = remaining;
            this.loc = start;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int read() throws IOException {
            if (this.remaining-- <= 0L) {
                if (this.addDummyByte) {
                    this.addDummyByte = false;
                    return 0;
                }
                return -1;
            }
            RandomAccessFile randomAccessFile = ZipFile.this.archive;
            synchronized (randomAccessFile) {
                ZipFile.this.archive.seek(this.loc++);
                return ZipFile.this.archive.read();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int ret;
            if (this.remaining <= 0L) {
                if (this.addDummyByte) {
                    this.addDummyByte = false;
                    b[off] = 0;
                    return 1;
                }
                return -1;
            }
            if (len <= 0) {
                return 0;
            }
            if ((long)len > this.remaining) {
                len = (int)this.remaining;
            }
            RandomAccessFile randomAccessFile = ZipFile.this.archive;
            synchronized (randomAccessFile) {
                ZipFile.this.archive.seek(this.loc);
                ret = ZipFile.this.archive.read(b, off, len);
            }
            if (ret > 0) {
                this.loc += (long)ret;
                this.remaining -= (long)ret;
            }
            return ret;
        }

        void addDummy() {
            this.addDummyByte = true;
        }
    }

    private static final class NameAndComment {
        private final byte[] name;
        private final byte[] comment;

        private NameAndComment(byte[] name, byte[] comment) {
            this.name = name;
            this.comment = comment;
        }
    }
}

