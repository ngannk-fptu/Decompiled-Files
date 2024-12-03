/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io.ole2;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.io.LittleEndianDataInputStream;
import com.twelvemonkeys.io.LittleEndianRandomAccessFile;
import com.twelvemonkeys.io.MemoryCacheSeekableStream;
import com.twelvemonkeys.io.Seekable;
import com.twelvemonkeys.io.SeekableInputStream;
import com.twelvemonkeys.io.ole2.CorruptDocumentException;
import com.twelvemonkeys.io.ole2.Entry;
import com.twelvemonkeys.io.ole2.SIdChain;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import javax.imageio.stream.ImageInputStream;

public final class CompoundDocument
implements AutoCloseable {
    static final byte[] MAGIC = new byte[]{-48, -49, 17, -32, -95, -79, 26, -31};
    private static final int FREE_SID = -1;
    private static final int END_OF_CHAIN_SID = -2;
    private static final int SAT_SECTOR_SID = -3;
    private static final int MSAT_SECTOR_SID = -4;
    public static final int HEADER_SIZE = 512;
    public static final long EPOCH_OFFSET = -11644477200000L;
    private final DataInput input;
    private UUID uUID;
    private int sectorSize;
    private int shortSectorSize;
    private int directorySId;
    private int minStreamSize;
    private int shortSATSId;
    private int shortSATSize;
    private int[] masterSAT;
    private int[] SAT;
    private int[] shortSAT;
    private Entry rootEntry;
    private SIdChain shortStreamSIdChain;
    private SIdChain directorySIdChain;

    public CompoundDocument(File file) throws IOException {
        this.input = new LittleEndianRandomAccessFile(FileUtil.resolve(file), "r");
        this.readHeader();
    }

    public CompoundDocument(InputStream inputStream) throws IOException {
        this(new MemoryCacheSeekableStream(inputStream));
    }

    CompoundDocument(SeekableInputStream seekableInputStream) throws IOException {
        this.input = new SeekableLittleEndianDataInputStream(seekableInputStream);
        this.readHeader();
    }

    public CompoundDocument(ImageInputStream imageInputStream) throws IOException {
        this.input = (DataInput)Validate.notNull((Object)imageInputStream, (String)"input");
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        this.readHeader();
    }

    @Override
    public void close() throws IOException {
        if (this.input instanceof RandomAccessFile) {
            ((RandomAccessFile)this.input).close();
        } else if (this.input instanceof LittleEndianRandomAccessFile) {
            ((LittleEndianRandomAccessFile)this.input).close();
        }
    }

    public static boolean canRead(DataInput dataInput) {
        return CompoundDocument.canRead(dataInput, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean canRead(DataInput dataInput, boolean bl) {
        long l;
        block26: {
            l = -1L;
            if (bl) {
                try {
                    if (dataInput instanceof InputStream && ((InputStream)((Object)dataInput)).markSupported()) {
                        ((InputStream)((Object)dataInput)).mark(8);
                        break block26;
                    }
                    if (dataInput instanceof ImageInputStream) {
                        ((ImageInputStream)dataInput).mark();
                        break block26;
                    }
                    if (dataInput instanceof RandomAccessFile) {
                        l = ((RandomAccessFile)dataInput).getFilePointer();
                        break block26;
                    }
                    if (dataInput instanceof LittleEndianRandomAccessFile) {
                        l = ((LittleEndianRandomAccessFile)dataInput).getFilePointer();
                        break block26;
                    }
                    return false;
                }
                catch (IOException iOException) {
                    return false;
                }
            }
        }
        try {
            byte[] byArray = new byte[8];
            dataInput.readFully(byArray);
            boolean bl2 = Arrays.equals(byArray, MAGIC);
            return bl2;
        }
        catch (IOException iOException) {
        }
        finally {
            if (bl) {
                try {
                    if (dataInput instanceof InputStream && ((InputStream)((Object)dataInput)).markSupported()) {
                        ((InputStream)((Object)dataInput)).reset();
                    } else if (dataInput instanceof ImageInputStream) {
                        ((ImageInputStream)dataInput).reset();
                    } else if (dataInput instanceof RandomAccessFile) {
                        ((RandomAccessFile)dataInput).seek(l);
                    } else if (dataInput instanceof LittleEndianRandomAccessFile) {
                        ((LittleEndianRandomAccessFile)dataInput).seek(l);
                    }
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
        }
        return false;
    }

    private void readHeader() throws IOException {
        int n;
        if (this.masterSAT != null) {
            return;
        }
        if (!CompoundDocument.canRead(this.input, false)) {
            throw new CorruptDocumentException("Not an OLE 2 Compound Document");
        }
        this.uUID = new UUID(this.input.readLong(), this.input.readLong());
        this.input.readUnsignedShort();
        this.input.readUnsignedShort();
        int n2 = this.input.readUnsignedShort();
        if (n2 == 65535) {
            throw new CorruptDocumentException("Cannot read big endian OLE 2 Compound Documents");
        }
        if (n2 != 65534) {
            throw new CorruptDocumentException(String.format("Unknown byte order marker: 0x%04x, expected 0xfffe or 0xffff", n2));
        }
        this.sectorSize = 1 << this.input.readUnsignedShort();
        this.shortSectorSize = 1 << this.input.readUnsignedShort();
        if (this.skipBytesFully(10) != 10) {
            throw new CorruptDocumentException();
        }
        int n3 = this.input.readInt();
        this.directorySId = this.input.readInt();
        if (this.skipBytesFully(4) != 4) {
            throw new CorruptDocumentException();
        }
        this.minStreamSize = this.input.readInt();
        this.shortSATSId = this.input.readInt();
        this.shortSATSize = this.input.readInt();
        int n4 = this.input.readInt();
        int n5 = this.input.readInt();
        this.masterSAT = new int[n3];
        int n6 = Math.min(n3, 109);
        for (n = 0; n < n6; ++n) {
            this.masterSAT[n] = this.input.readInt();
        }
        if (n4 == -2) {
            n = 436 - n3 * 4;
            if (this.skipBytesFully(n) != n) {
                throw new CorruptDocumentException();
            }
        } else {
            this.seekToSId(n4, -1L);
            n = n6;
            for (int i = 0; i < n5; ++i) {
                int n7;
                block5: for (n7 = 0; n7 < 127; ++n7) {
                    int n8 = this.input.readInt();
                    switch (n8) {
                        case -1: {
                            continue block5;
                        }
                        default: {
                            this.masterSAT[n++] = n8;
                        }
                    }
                }
                n7 = this.input.readInt();
                if (n7 == -2) break;
                this.seekToSId(n7, -1L);
            }
        }
    }

    private int skipBytesFully(int n) throws IOException {
        int n2;
        int n3;
        for (n2 = n; n2 > 0 && (n3 = this.input.skipBytes(n)) > 0; n2 -= n3) {
        }
        return n - n2;
    }

    private void readSAT() throws IOException {
        int n;
        int n2;
        int n3;
        if (this.SAT != null) {
            return;
        }
        int n4 = this.sectorSize / 4;
        this.SAT = new int[this.masterSAT.length * n4];
        for (int i = 0; i < this.masterSAT.length; ++i) {
            this.seekToSId(this.masterSAT[i], -1L);
            for (n3 = 0; n3 < n4; ++n3) {
                n2 = this.input.readInt();
                n = n3 + i * n4;
                this.SAT[n] = n2;
            }
        }
        SIdChain sIdChain = this.getSIdChain(this.shortSATSId, -1L);
        this.shortSAT = new int[this.shortSATSize * n4];
        for (n3 = 0; n3 < this.shortSATSize; ++n3) {
            this.seekToSId(sIdChain.get(n3), -1L);
            for (n2 = 0; n2 < n4; ++n2) {
                n = this.input.readInt();
                int n5 = n2 + n3 * n4;
                this.shortSAT[n5] = n;
            }
        }
    }

    private SIdChain getSIdChain(int n, long l) throws IOException {
        SIdChain sIdChain = new SIdChain();
        int[] nArray = this.isShortStream(l) ? this.shortSAT : this.SAT;
        int n2 = n;
        while (n2 != -2 && n2 != -1) {
            sIdChain.addSID(n2);
            n2 = nArray[n2];
        }
        return sIdChain;
    }

    private boolean isShortStream(long l) {
        return l != -1L && l < (long)this.minStreamSize;
    }

    private void seekToSId(int n, long l) throws IOException {
        long l2;
        if (this.isShortStream(l)) {
            Entry entry = this.getRootEntry();
            if (this.shortStreamSIdChain == null) {
                this.shortStreamSIdChain = this.getSIdChain(entry.startSId, entry.streamSize);
            }
            int n2 = this.sectorSize / this.shortSectorSize;
            int n3 = n / n2;
            int n4 = n - n3 * n2;
            l2 = 512L + (long)this.shortStreamSIdChain.get(n3) * (long)this.sectorSize + (long)n4 * (long)this.shortSectorSize;
        } else {
            l2 = 512L + (long)n * (long)this.sectorSize;
        }
        if (this.input instanceof LittleEndianRandomAccessFile) {
            ((LittleEndianRandomAccessFile)this.input).seek(l2);
        } else if (this.input instanceof ImageInputStream) {
            ((ImageInputStream)this.input).seek(l2);
        } else {
            ((SeekableLittleEndianDataInputStream)this.input).seek(l2);
        }
    }

    private void seekToDId(int n) throws IOException {
        if (this.directorySIdChain == null) {
            this.directorySIdChain = this.getSIdChain(this.directorySId, -1L);
        }
        int n2 = this.sectorSize / 128;
        int n3 = n / n2;
        int n4 = n - n3 * n2;
        int n5 = this.directorySIdChain.get(n3);
        this.seekToSId(n5, -1L);
        if (this.input instanceof LittleEndianRandomAccessFile) {
            LittleEndianRandomAccessFile littleEndianRandomAccessFile = (LittleEndianRandomAccessFile)this.input;
            littleEndianRandomAccessFile.seek(littleEndianRandomAccessFile.getFilePointer() + (long)(n4 * 128));
        } else if (this.input instanceof ImageInputStream) {
            ImageInputStream imageInputStream = (ImageInputStream)this.input;
            imageInputStream.seek(imageInputStream.getStreamPosition() + (long)(n4 * 128));
        } else {
            SeekableLittleEndianDataInputStream seekableLittleEndianDataInputStream = (SeekableLittleEndianDataInputStream)this.input;
            seekableLittleEndianDataInputStream.seek(seekableLittleEndianDataInputStream.getStreamPosition() + (long)(n4 * 128));
        }
    }

    SeekableInputStream getInputStreamForSId(int n, int n2) throws IOException {
        SIdChain sIdChain = this.getSIdChain(n, n2);
        int n3 = n2 < this.minStreamSize ? this.shortSectorSize : this.sectorSize;
        return new MemoryCacheSeekableStream(new Stream(sIdChain, n2, n3, this));
    }

    private InputStream getDirectoryStreamForDId(int n) throws IOException {
        byte[] byArray = new byte[128];
        this.seekToDId(n);
        this.input.readFully(byArray);
        return new ByteArrayInputStream(byArray);
    }

    Entry getEntry(int n, Entry entry) throws IOException {
        Entry entry2 = Entry.readEntry(new LittleEndianDataInputStream(this.getDirectoryStreamForDId(n)));
        entry2.parent = entry;
        entry2.document = this;
        return entry2;
    }

    SortedSet<Entry> getEntries(int n, Entry entry) throws IOException {
        return this.getEntriesRecursive(n, entry, new TreeSet<Entry>());
    }

    private SortedSet<Entry> getEntriesRecursive(int n, Entry entry, SortedSet<Entry> sortedSet) throws IOException {
        Entry entry2 = this.getEntry(n, entry);
        if (!sortedSet.add(entry2)) {
            throw new CorruptDocumentException("Cyclic chain reference for entry: " + n);
        }
        if (entry2.prevDId != -1) {
            this.getEntriesRecursive(entry2.prevDId, entry, sortedSet);
        }
        if (entry2.nextDId != -1) {
            this.getEntriesRecursive(entry2.nextDId, entry, sortedSet);
        }
        return sortedSet;
    }

    Entry getEntry(String string) throws IOException {
        String string2;
        String[] stringArray;
        if (StringUtil.isEmpty((String)string) || !string.startsWith("/")) {
            throw new IllegalArgumentException("Path must be absolute, and contain a valid path: " + string);
        }
        Entry entry = this.getRootEntry();
        if (string.equals("/")) {
            return entry;
        }
        String[] stringArray2 = stringArray = StringUtil.toStringArray((String)string, (String)"/");
        int n = stringArray2.length;
        for (int i = 0; i < n && (entry = entry.getChildEntry(string2 = stringArray2[i])) != null; ++i) {
        }
        return entry;
    }

    public Entry getRootEntry() throws IOException {
        if (this.rootEntry == null) {
            this.readSAT();
            this.rootEntry = this.getEntry(0, null);
            if (this.rootEntry.type != 5) {
                throw new CorruptDocumentException("Invalid root storage type: " + this.rootEntry.type);
            }
        }
        return this.rootEntry;
    }

    public String toString() {
        return String.format("%s[uuid: %s, sector size: %d/%d bytes, directory SID: %d, master SAT: %s entries]", this.getClass().getSimpleName(), this.uUID, this.sectorSize, this.shortSectorSize, this.directorySId, this.masterSAT.length);
    }

    public static long toJavaTimeInMillis(long l) {
        if (l == 0L) {
            return 0L;
        }
        return (l >> 1) / 5000L + -11644477200000L;
    }

    static class SeekableLittleEndianDataInputStream
    extends LittleEndianDataInputStream
    implements Seekable {
        private final SeekableInputStream seekable;

        public SeekableLittleEndianDataInputStream(SeekableInputStream seekableInputStream) {
            super(seekableInputStream);
            this.seekable = seekableInputStream;
        }

        @Override
        public void seek(long l) throws IOException {
            this.seekable.seek(l);
        }

        @Override
        public boolean isCachedFile() {
            return this.seekable.isCachedFile();
        }

        @Override
        public boolean isCachedMemory() {
            return this.seekable.isCachedMemory();
        }

        @Override
        public boolean isCached() {
            return this.seekable.isCached();
        }

        @Override
        public long getStreamPosition() throws IOException {
            return this.seekable.getStreamPosition();
        }

        @Override
        public long getFlushedPosition() throws IOException {
            return this.seekable.getFlushedPosition();
        }

        @Override
        public void flushBefore(long l) throws IOException {
            this.seekable.flushBefore(l);
        }

        @Override
        public void flush() throws IOException {
            this.seekable.flush();
        }

        @Override
        public void reset() throws IOException {
            this.seekable.reset();
        }

        @Override
        public void mark() {
            this.seekable.mark();
        }
    }

    static class Stream
    extends InputStream {
        private final SIdChain chain;
        private final CompoundDocument document;
        private final long length;
        private long streamPos;
        private int nextSectorPos;
        private byte[] buffer;
        private int bufferPos;

        public Stream(SIdChain sIdChain, int n, int n2, CompoundDocument compoundDocument) {
            this.chain = sIdChain;
            this.length = n;
            this.buffer = new byte[n2];
            this.bufferPos = this.buffer.length;
            this.document = compoundDocument;
        }

        @Override
        public int available() throws IOException {
            return (int)Math.min((long)(this.buffer.length - this.bufferPos), this.length - this.streamPos);
        }

        @Override
        public int read() throws IOException {
            if (this.available() <= 0 && !this.fillBuffer()) {
                return -1;
            }
            ++this.streamPos;
            return this.buffer[this.bufferPos++] & 0xFF;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean fillBuffer() throws IOException {
            if (this.streamPos < this.length && this.nextSectorPos < this.chain.length()) {
                CompoundDocument compoundDocument = this.document;
                synchronized (compoundDocument) {
                    this.document.seekToSId(this.chain.get(this.nextSectorPos), this.length);
                    this.document.input.readFully(this.buffer);
                }
                ++this.nextSectorPos;
                this.bufferPos = 0;
                return true;
            }
            return false;
        }

        @Override
        public int read(byte[] byArray, int n, int n2) throws IOException {
            if (this.available() <= 0 && !this.fillBuffer()) {
                return -1;
            }
            int n3 = Math.min(n2, this.available());
            System.arraycopy(this.buffer, this.bufferPos, byArray, n, n3);
            this.bufferPos += n3;
            this.streamPos += (long)n3;
            return n3;
        }

        @Override
        public void close() throws IOException {
            this.buffer = null;
        }
    }
}

