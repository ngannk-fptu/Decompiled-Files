/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.cafs;

import aQute.lib.index.Index;
import aQute.lib.io.IO;
import aQute.libg.cryptography.SHA1;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class CAFS
implements Closeable,
Iterable<SHA1> {
    static final byte[] CAFS;
    static final byte[] CAFE;
    static final String INDEXFILE = "index.idx";
    static final String STOREFILE = "store.cafs";
    static final String ALGORITHM = "SHA-1";
    static final int KEYLENGTH = 20;
    static final int HEADERLENGTH = 38;
    final File home;
    Index index;
    RandomAccessFile store;
    FileChannel channel;

    public CAFS(File home, boolean create) throws Exception {
        this.home = home;
        if (!home.isDirectory()) {
            if (create) {
                IO.mkdirs(home);
            } else {
                throw new IllegalArgumentException("CAFS requires a directory with create=false");
            }
        }
        this.index = new Index(new File(home, INDEXFILE), 20);
        this.store = new RandomAccessFile(new File(home, STOREFILE), "rw");
        this.channel = this.store.getChannel();
        if (this.store.length() < 256L) {
            if (create) {
                this.store.write(CAFS);
                for (int i = 1; i < 64; ++i) {
                    this.store.writeInt(0);
                }
            } else {
                throw new IllegalArgumentException("Invalid store file, length is too short " + this.store);
            }
            this.channel.force(true);
            System.err.println(this.store.length());
        }
        this.store.seek(0L);
        if (!this.verifySignature(this.store, CAFS)) {
            throw new IllegalArgumentException("Not a valid signature: CAFS at start of file");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SHA1 write(InputStream in) throws Exception {
        Deflater deflater = new Deflater();
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        DigestInputStream din = new DigestInputStream(in, md);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DeflaterOutputStream dout = new DeflaterOutputStream((OutputStream)bout, deflater);
        IO.copy((InputStream)din, (OutputStream)dout);
        RandomAccessFile randomAccessFile = this.store;
        synchronized (randomAccessFile) {
            SHA1 sHA1;
            block9: {
                SHA1 sha1 = new SHA1(md.digest());
                long search = this.index.search(sha1.digest());
                if (search > 0L) {
                    return sha1;
                }
                byte[] compressed = bout.toByteArray();
                FileLock lock = null;
                try {
                    long insertPoint;
                    int recordLength = compressed.length + 38;
                    while (true) {
                        insertPoint = this.store.length();
                        lock = this.channel.lock(insertPoint, recordLength, false);
                        if (this.store.length() == insertPoint) break;
                        lock.release();
                    }
                    int totalLength = deflater.getTotalIn();
                    this.store.seek(insertPoint);
                    this.update(sha1.digest(), compressed, totalLength);
                    this.index.insert(sha1.digest(), insertPoint);
                    sHA1 = sha1;
                    if (lock == null) break block9;
                }
                catch (Throwable throwable) {
                    if (lock != null) {
                        lock.release();
                    }
                    throw throwable;
                }
                lock.release();
            }
            return sHA1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InputStream read(SHA1 sha1) throws Exception {
        RandomAccessFile randomAccessFile = this.store;
        synchronized (randomAccessFile) {
            long offset = this.index.search(sha1.digest());
            if (offset < 0L) {
                return null;
            }
            this.store.seek(offset);
            if (!this.verifySignature(this.store, CAFE)) {
                throw new IllegalArgumentException("No signature");
            }
            int flags = this.store.readInt();
            int compressedLength = this.store.readInt();
            int uncompressedLength = this.store.readInt();
            byte[] readSha1 = new byte[20];
            this.store.read(readSha1);
            SHA1 rsha1 = new SHA1(readSha1);
            if (!sha1.equals(rsha1)) {
                throw new IOException("SHA1 read and asked mismatch: " + sha1 + " " + rsha1);
            }
            short crc = this.store.readShort();
            if (crc != this.checksum(flags, compressedLength, uncompressedLength, readSha1)) {
                throw new IllegalArgumentException("Invalid header checksum: " + sha1);
            }
            byte[] buffer = new byte[compressedLength];
            this.store.readFully(buffer);
            return this.getSha1Stream(sha1, buffer, uncompressedLength);
        }
    }

    public boolean exists(byte[] sha1) throws Exception {
        return this.index.search(sha1) >= 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reindex() throws Exception {
        long length;
        RandomAccessFile randomAccessFile = this.store;
        synchronized (randomAccessFile) {
            length = this.store.length();
            if (length < 256L) {
                throw new IllegalArgumentException("Store file is too small, need to be at least 256 bytes: " + this.store);
            }
        }
        try (RandomAccessFile in = new RandomAccessFile(new File(this.home, STOREFILE), "r");){
            byte[] signature = new byte[4];
            in.readFully(signature);
            if (!Arrays.equals(CAFS, signature)) {
                throw new IllegalArgumentException("Store file does not start with CAFS: " + in);
            }
            in.seek(256L);
            File ixf = new File(this.home, "index.new");
            Index index = new Index(ixf, 20);
            while (in.getFilePointer() < length) {
                long entry = in.getFilePointer();
                SHA1 sha1 = this.verifyEntry(in);
                index.insert(sha1.digest(), entry);
            }
            RandomAccessFile randomAccessFile2 = this.store;
            synchronized (randomAccessFile2) {
                index.close();
                File indexFile = new File(this.home, INDEXFILE);
                IO.rename(ixf, indexFile);
                this.index = new Index(indexFile, 20);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        RandomAccessFile randomAccessFile = this.store;
        synchronized (randomAccessFile) {
            try {
                this.store.close();
            }
            finally {
                this.index.close();
            }
        }
    }

    private SHA1 verifyEntry(RandomAccessFile in) throws IOException, NoSuchAlgorithmException {
        byte[] signature = new byte[4];
        in.readFully(signature);
        if (!Arrays.equals(CAFE, signature)) {
            throw new IllegalArgumentException("File is corrupted: " + in);
        }
        in.readInt();
        int compressedSize = in.readInt();
        int uncompressedSize = in.readInt();
        byte[] key = new byte[20];
        in.readFully(key);
        SHA1 sha1 = new SHA1(key);
        byte[] buffer = new byte[compressedSize];
        in.readFully(buffer);
        try (InputStream xin = this.getSha1Stream(sha1, buffer, uncompressedSize);){
            xin.skip(uncompressedSize);
        }
        return sha1;
    }

    private boolean verifySignature(DataInput din, byte[] org) throws IOException {
        byte[] read = new byte[org.length];
        din.readFully(read);
        return Arrays.equals(read, org);
    }

    private InputStream getSha1Stream(final SHA1 sha1, byte[] buffer, final int total) throws NoSuchAlgorithmException {
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        InflaterInputStream iin = new InflaterInputStream(in){
            int count;
            final MessageDigest digestx;
            final AtomicBoolean calculated;
            {
                super(x0);
                this.count = 0;
                this.digestx = MessageDigest.getInstance(aQute.libg.cafs.CAFS.ALGORITHM);
                this.calculated = new AtomicBoolean();
            }

            @Override
            public int read(byte[] data, int offset, int length) throws IOException {
                int size = super.read(data, offset, length);
                if (size <= 0) {
                    this.eof();
                } else {
                    this.count += size;
                    this.digestx.update(data, offset, size);
                }
                return size;
            }

            @Override
            public int read() throws IOException {
                int c = super.read();
                if (c < 0) {
                    this.eof();
                } else {
                    ++this.count;
                    this.digestx.update((byte)c);
                }
                return c;
            }

            void eof() throws IOException {
                if (this.calculated.getAndSet(true)) {
                    return;
                }
                if (this.count != total) {
                    throw new IOException("Counts do not match. Expected to read: " + total + " Actually read: " + this.count);
                }
                SHA1 calculatedSha1 = new SHA1(this.digestx.digest());
                if (!sha1.equals(calculatedSha1)) {
                    throw new IOException("SHA1 caclulated and asked mismatch, asked: " + sha1 + ", \nfound: " + calculatedSha1);
                }
            }

            @Override
            public void close() throws IOException {
                this.eof();
                super.close();
            }
        };
        return iin;
    }

    private void update(byte[] sha1, byte[] compressed, int totalLength) throws IOException {
        this.store.write(CAFE);
        this.store.writeInt(0);
        this.store.writeInt(compressed.length);
        this.store.writeInt(totalLength);
        this.store.write(sha1);
        this.store.writeShort(this.checksum(0, compressed.length, totalLength, sha1));
        this.store.write(compressed);
        this.channel.force(false);
    }

    short checksum(int flags, int compressedLength, int totalLength, byte[] sha1) {
        CRC32 crc = new CRC32();
        crc.update(flags);
        crc.update(flags >> 8);
        crc.update(flags >> 16);
        crc.update(flags >> 24);
        crc.update(compressedLength);
        crc.update(compressedLength >> 8);
        crc.update(compressedLength >> 16);
        crc.update(compressedLength >> 24);
        crc.update(totalLength);
        crc.update(totalLength >> 8);
        crc.update(totalLength >> 16);
        crc.update(totalLength >> 24);
        crc.update(sha1);
        return (short)crc.getValue();
    }

    @Override
    public Iterator<SHA1> iterator() {
        return new Iterator<SHA1>(){
            long position = 256L;

            @Override
            public boolean hasNext() {
                RandomAccessFile randomAccessFile = CAFS.this.store;
                synchronized (randomAccessFile) {
                    try {
                        return this.position < CAFS.this.store.length();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public SHA1 next() {
                RandomAccessFile randomAccessFile = CAFS.this.store;
                synchronized (randomAccessFile) {
                    try {
                        CAFS.this.store.seek(this.position);
                        byte[] signature = new byte[4];
                        CAFS.this.store.readFully(signature);
                        if (!Arrays.equals(CAFE, signature)) {
                            throw new IllegalArgumentException("No signature");
                        }
                        int flags = CAFS.this.store.readInt();
                        int compressedLength = CAFS.this.store.readInt();
                        int totalLength = CAFS.this.store.readInt();
                        byte[] sha1 = new byte[20];
                        CAFS.this.store.readFully(sha1);
                        short crc = CAFS.this.store.readShort();
                        if (crc != CAFS.this.checksum(flags, compressedLength, totalLength, sha1)) {
                            throw new IllegalArgumentException("Header checksum fails");
                        }
                        this.position += (long)(38 + compressedLength);
                        return new SHA1(sha1);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remvoe not supported, CAFS is write once");
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() throws IOException {
        RandomAccessFile randomAccessFile = this.store;
        synchronized (randomAccessFile) {
            return this.store.getFilePointer() <= 256L;
        }
    }

    static {
        try {
            CAFS = "CAFS".getBytes(StandardCharsets.UTF_8);
            CAFE = "CAFE".getBytes(StandardCharsets.UTF_8);
        }
        catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

