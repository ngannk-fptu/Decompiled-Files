/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.index;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Index
implements Iterable<byte[]> {
    static final int LEAF = 0;
    static final int INDEX = 1;
    static final int SIGNATURE = 0;
    static final int MAGIC = 1229735000;
    static final int KEYSIZE = 4;
    FileChannel file;
    final int pageSize = 4096;
    final int keySize;
    final int valueSize = 8;
    final int capacity;
    public Page root;
    final LinkedHashMap<Integer, Page> cache = new LinkedHashMap();
    final MappedByteBuffer settings;
    private int nextPage;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Index(File file, int keySize) throws IOException {
        this.capacity = 4092 / (keySize + 8);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        this.file = raf.getChannel();
        this.settings = this.file.map(FileChannel.MapMode.READ_WRITE, 0L, 4096L);
        if (this.file.size() == 4096L) {
            this.keySize = keySize;
            this.settings.putInt(0, 1229735000);
            this.settings.putInt(4, keySize);
            this.nextPage = 1;
            this.root = this.allocate(true);
            this.root.n = 1;
            this.root.set(0, new byte[4], 0L);
            this.root.write();
        } else {
            if (this.settings.getInt(0) != 1229735000) {
                throw new IllegalStateException("No Index file, magic is not 1229735000");
            }
            this.keySize = this.settings.getInt(4);
            if (keySize != 0 && this.keySize != keySize) {
                throw new IllegalStateException("Invalid key size for Index file. The file is " + this.keySize + " and was expected to be " + this.keySize);
            }
            this.root = this.getPage(1);
            this.nextPage = (int)(this.file.size() / 4096L);
        }
    }

    public void insert(byte[] k, long v) throws Exception {
        this.root.insert(k, v);
    }

    public long search(byte[] k) throws Exception {
        return this.root.search(k);
    }

    Page allocate(boolean leaf) throws IOException {
        Page page = new Page(this.nextPage++, leaf);
        this.cache.put(page.number, page);
        return page;
    }

    Page getPage(int number) throws IOException {
        Page page = this.cache.get(number);
        if (page == null) {
            page = new Page(number);
            this.cache.put(number, page);
        }
        return page;
    }

    public String toString() {
        return this.root.toString();
    }

    public void close() throws IOException {
        this.file.close();
        this.cache.clear();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return this.root.iterator();
    }

    class Page {
        static final int TYPE_OFFSET = 0;
        static final int COUNT_OFFSET = 2;
        static final int START_OFFSET = 4;
        final int number;
        boolean leaf;
        final MappedByteBuffer buffer;
        int n = 0;
        boolean dirty;

        Page(int number) throws IOException {
            this.number = number;
            this.buffer = Index.this.file.map(FileChannel.MapMode.READ_WRITE, (long)number * 4096L, 4096L);
            this.n = this.buffer.getShort(2);
            short type = this.buffer.getShort(0);
            this.leaf = type != 0;
        }

        Page(int number, boolean leaf) throws IOException {
            this.number = number;
            this.leaf = leaf;
            this.n = 0;
            this.buffer = Index.this.file.map(FileChannel.MapMode.READ_WRITE, (long)number * 4096L, 4096L);
        }

        Iterator<byte[]> iterator() {
            return new Iterator<byte[]>(){
                Iterator<byte[]> i;
                int rover = 0;

                @Override
                public byte[] next() {
                    if (Page.this.leaf) {
                        return Page.this.k(this.rover++);
                    }
                    return this.i.next();
                }

                @Override
                public boolean hasNext() {
                    try {
                        if (Page.this.leaf) {
                            return this.rover < Page.this.n;
                        }
                        while (this.i == null || !this.i.hasNext()) {
                            int c = (int)Page.this.c(this.rover++);
                            this.i = Index.this.getPage(c).iterator();
                        }
                        return this.i.hasNext();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        void write() throws IOException {
            this.buffer.putShort(2, (short)this.n);
            this.buffer.put(0, (byte)(this.leaf ? 1 : 0));
            this.buffer.force();
        }

        int compare(byte[] key, int i) {
            int index = this.pos(i);
            int j = 0;
            while (j < Index.this.keySize) {
                int b;
                int a = 0;
                if (j < key.length) {
                    a = key[j] & 0xFF;
                }
                if (a != (b = this.buffer.get(index) & 0xFF)) {
                    return a > b ? 1 : -1;
                }
                ++j;
                ++index;
            }
            return 0;
        }

        int pos(int i) {
            return 4 + this.size(i);
        }

        int size(int n) {
            return n * (Index.this.keySize + 8);
        }

        void copyFrom(Page page, int start, int length) {
            this.copy(page.buffer, this.pos(start), this.buffer, this.pos(0), this.size(length));
        }

        void copy(ByteBuffer src, int srcPos, ByteBuffer dst, int dstPos, int length) {
            if (srcPos < dstPos) {
                while (length-- > 0) {
                    dst.put(dstPos + length, src.get(srcPos + length));
                }
            } else {
                while (length-- > 0) {
                    dst.put(dstPos++, src.get(srcPos++));
                }
            }
        }

        long search(byte[] k) throws Exception {
            int i;
            int cmp = 0;
            for (i = this.n - 1; i >= 0 && (cmp = this.compare(k, i)) < 0; --i) {
            }
            if (this.leaf) {
                if (cmp != 0) {
                    return -1L;
                }
                return this.c(i);
            }
            long value = this.c(i);
            Page child = Index.this.getPage((int)value);
            return child.search(k);
        }

        void insert(byte[] k, long v) throws IOException {
            if (this.n == Index.this.capacity) {
                int t = Index.this.capacity / 2;
                Page left = Index.this.allocate(this.leaf);
                Page right = Index.this.allocate(this.leaf);
                left.copyFrom(this, 0, t);
                left.n = t;
                right.copyFrom(this, t, Index.this.capacity - t);
                right.n = Index.this.capacity - t;
                this.leaf = false;
                this.set(0, left.k(0), left.number);
                this.set(1, right.k(0), right.number);
                this.n = 2;
                left.write();
                right.write();
            }
            this.insertNonFull(k, v);
        }

        byte[] k(int i) {
            this.buffer.position(this.pos(i));
            byte[] key = new byte[Index.this.keySize];
            this.buffer.get(key);
            return key;
        }

        long c(int i) {
            if (i < 0) {
                System.err.println("Arghhh");
            }
            int index = this.pos(i) + Index.this.keySize;
            return this.buffer.getLong(index);
        }

        void set(int i, byte[] k, long v) {
            int index = this.pos(i);
            for (int j = 0; j < Index.this.keySize; ++j) {
                byte a = 0;
                if (j < k.length) {
                    a = k[j];
                }
                this.buffer.put(index + j, a);
            }
            this.buffer.putLong(index + Index.this.keySize, v);
        }

        void insertNonFull(byte[] k, long v) throws IOException {
            int i;
            int cmp = 0;
            for (i = this.n - 1; i >= 0 && (cmp = this.compare(k, i)) < 0; --i) {
            }
            if (this.leaf) {
                if (cmp != 0 && ++i != this.n) {
                    this.copy(this.buffer, this.pos(i), this.buffer, this.pos(i + 1), this.size(this.n - i));
                }
                this.set(i, k, v);
                ++this.n;
                this.dirty = true;
            } else {
                long value = this.c(i);
                Page child = Index.this.getPage((int)value);
                if (child.n == Index.this.capacity) {
                    Page left = child;
                    int t = Index.this.capacity / 2;
                    Page right = Index.this.allocate(child.leaf);
                    right.copyFrom(left, t, Index.this.capacity - t);
                    right.n = Index.this.capacity - t;
                    left.n = t;
                    if (++i < this.n) {
                        this.copy(this.buffer, this.pos(i), this.buffer, this.pos(i + 1), this.size(this.n - i));
                    }
                    this.set(i, right.k(0), right.number);
                    ++this.n;
                    assert (i < this.n);
                    child = right.compare(k, 0) >= 0 ? right : left;
                    left.dirty = true;
                    right.dirty = true;
                    this.dirty = true;
                }
                child.insertNonFull(k, v);
            }
            this.write();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            try {
                this.toString(sb, "");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        public void toString(StringBuilder sb, String indent) throws IOException {
            for (int i = 0; i < this.n; ++i) {
                sb.append(String.format("%s %02d:%02d %20s %s %d%n", indent, this.number, i, this.hex(this.k(i), 0, 4), this.leaf ? "==" : "->", this.c(i)));
                if (this.leaf) continue;
                long c = this.c(i);
                Page sub = Index.this.getPage((int)c);
                sub.toString(sb, indent + " ");
            }
        }

        private String hex(byte[] k, int i, int j) {
            StringBuilder sb = new StringBuilder();
            while (i < j) {
                int b = 0xFF & k[i];
                sb.append(this.nibble(b >> 4));
                sb.append(this.nibble(b));
                ++i;
            }
            return sb.toString();
        }

        private char nibble(int i) {
            return (char)((i &= 0xF) >= 10 ? i + 65 - 10 : i + 48);
        }
    }
}

