/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

public class SuballocatedByteVector {
    protected int m_blocksize;
    protected int m_numblocks = 32;
    protected byte[][] m_map;
    protected int m_firstFree = 0;
    protected byte[] m_map0;

    public SuballocatedByteVector() {
        this(2048);
    }

    public SuballocatedByteVector(int blocksize) {
        this.m_blocksize = blocksize;
        this.m_map0 = new byte[blocksize];
        this.m_map = new byte[this.m_numblocks][];
        this.m_map[0] = this.m_map0;
    }

    public SuballocatedByteVector(int blocksize, int increaseSize) {
        this(blocksize);
    }

    public int size() {
        return this.m_firstFree;
    }

    private void setSize(int sz) {
        if (this.m_firstFree < sz) {
            this.m_firstFree = sz;
        }
    }

    public void addElement(byte value) {
        if (this.m_firstFree < this.m_blocksize) {
            this.m_map0[this.m_firstFree++] = value;
        } else {
            byte[] block;
            int index = this.m_firstFree / this.m_blocksize;
            int offset = this.m_firstFree % this.m_blocksize;
            ++this.m_firstFree;
            if (index >= this.m_map.length) {
                int newsize = index + this.m_numblocks;
                byte[][] newMap = new byte[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            if (null == (block = this.m_map[index])) {
                this.m_map[index] = new byte[this.m_blocksize];
                block = this.m_map[index];
            }
            block[offset] = value;
        }
    }

    private void addElements(byte value, int numberOfElements) {
        if (this.m_firstFree + numberOfElements < this.m_blocksize) {
            for (int i = 0; i < numberOfElements; ++i) {
                this.m_map0[this.m_firstFree++] = value;
            }
        } else {
            int index = this.m_firstFree / this.m_blocksize;
            int offset = this.m_firstFree % this.m_blocksize;
            this.m_firstFree += numberOfElements;
            while (numberOfElements > 0) {
                byte[] block;
                if (index >= this.m_map.length) {
                    int newsize = index + this.m_numblocks;
                    byte[][] newMap = new byte[newsize][];
                    System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                    this.m_map = newMap;
                }
                if (null == (block = this.m_map[index])) {
                    this.m_map[index] = new byte[this.m_blocksize];
                    block = this.m_map[index];
                }
                int copied = this.m_blocksize - offset < numberOfElements ? this.m_blocksize - offset : numberOfElements;
                numberOfElements -= copied;
                while (copied-- > 0) {
                    block[offset++] = value;
                }
                ++index;
                offset = 0;
            }
        }
    }

    private void addElements(int numberOfElements) {
        int newlen = this.m_firstFree + numberOfElements;
        if (newlen > this.m_blocksize) {
            int index = this.m_firstFree % this.m_blocksize;
            int newindex = (this.m_firstFree + numberOfElements) % this.m_blocksize;
            for (int i = index + 1; i <= newindex; ++i) {
                this.m_map[i] = new byte[this.m_blocksize];
            }
        }
        this.m_firstFree = newlen;
    }

    private void insertElementAt(byte value, int at) {
        if (at == this.m_firstFree) {
            this.addElement(value);
        } else if (at > this.m_firstFree) {
            byte[] block;
            int index = at / this.m_blocksize;
            if (index >= this.m_map.length) {
                int newsize = index + this.m_numblocks;
                byte[][] newMap = new byte[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            if (null == (block = this.m_map[index])) {
                this.m_map[index] = new byte[this.m_blocksize];
                block = this.m_map[index];
            }
            int offset = at % this.m_blocksize;
            block[offset] = value;
            this.m_firstFree = offset + 1;
        } else {
            int maxindex = this.m_firstFree + 1 / this.m_blocksize;
            ++this.m_firstFree;
            int offset = at % this.m_blocksize;
            for (int index = at / this.m_blocksize; index <= maxindex; ++index) {
                byte push;
                int copylen = this.m_blocksize - offset - 1;
                byte[] block = this.m_map[index];
                if (null == block) {
                    push = 0;
                    this.m_map[index] = new byte[this.m_blocksize];
                    block = this.m_map[index];
                } else {
                    push = block[this.m_blocksize - 1];
                    System.arraycopy(block, offset, block, offset + 1, copylen);
                }
                block[offset] = value;
                value = push;
                offset = 0;
            }
        }
    }

    public void removeAllElements() {
        this.m_firstFree = 0;
    }

    private boolean removeElement(byte s) {
        int at = this.indexOf(s, 0);
        if (at < 0) {
            return false;
        }
        this.removeElementAt(at);
        return true;
    }

    private void removeElementAt(int at) {
        if (at < this.m_firstFree) {
            int maxindex = this.m_firstFree / this.m_blocksize;
            int offset = at % this.m_blocksize;
            for (int index = at / this.m_blocksize; index <= maxindex; ++index) {
                int copylen = this.m_blocksize - offset - 1;
                byte[] block = this.m_map[index];
                if (null == block) {
                    this.m_map[index] = new byte[this.m_blocksize];
                    block = this.m_map[index];
                } else {
                    System.arraycopy(block, offset + 1, block, offset, copylen);
                }
                if (index < maxindex) {
                    byte[] next = this.m_map[index + 1];
                    if (next != null) {
                        block[this.m_blocksize - 1] = next != null ? next[0] : (byte)0;
                    }
                } else {
                    block[this.m_blocksize - 1] = 0;
                }
                offset = 0;
            }
        }
        --this.m_firstFree;
    }

    public void setElementAt(byte value, int at) {
        byte[] block;
        if (at < this.m_blocksize) {
            this.m_map0[at] = value;
            return;
        }
        int index = at / this.m_blocksize;
        int offset = at % this.m_blocksize;
        if (index >= this.m_map.length) {
            int newsize = index + this.m_numblocks;
            byte[][] newMap = new byte[newsize][];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
            this.m_map = newMap;
        }
        if (null == (block = this.m_map[index])) {
            this.m_map[index] = new byte[this.m_blocksize];
            block = this.m_map[index];
        }
        block[offset] = value;
        if (at >= this.m_firstFree) {
            this.m_firstFree = at + 1;
        }
    }

    public byte elementAt(int i) {
        if (i < this.m_blocksize) {
            return this.m_map0[i];
        }
        return this.m_map[i / this.m_blocksize][i % this.m_blocksize];
    }

    private boolean contains(byte s) {
        return this.indexOf(s, 0) >= 0;
    }

    public int indexOf(byte elem, int index) {
        byte[] block;
        if (index >= this.m_firstFree) {
            return -1;
        }
        int boffset = index % this.m_blocksize;
        int maxindex = this.m_firstFree / this.m_blocksize;
        for (int bindex = index / this.m_blocksize; bindex < maxindex; ++bindex) {
            block = this.m_map[bindex];
            if (block != null) {
                for (int offset = boffset; offset < this.m_blocksize; ++offset) {
                    if (block[offset] != elem) continue;
                    return offset + bindex * this.m_blocksize;
                }
            }
            boffset = 0;
        }
        int maxoffset = this.m_firstFree % this.m_blocksize;
        block = this.m_map[maxindex];
        for (int offset = boffset; offset < maxoffset; ++offset) {
            if (block[offset] != elem) continue;
            return offset + maxindex * this.m_blocksize;
        }
        return -1;
    }

    public int indexOf(byte elem) {
        return this.indexOf(elem, 0);
    }

    private int lastIndexOf(byte elem) {
        int boffset = this.m_firstFree % this.m_blocksize;
        for (int index = this.m_firstFree / this.m_blocksize; index >= 0; --index) {
            byte[] block = this.m_map[index];
            if (block != null) {
                for (int offset = boffset; offset >= 0; --offset) {
                    if (block[offset] != elem) continue;
                    return offset + index * this.m_blocksize;
                }
            }
            boffset = 0;
        }
        return -1;
    }
}

