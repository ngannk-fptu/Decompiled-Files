/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

final class CCITTFaxDecoderStream
extends FilterInputStream {
    private final int columns;
    private final byte[] decodedRow;
    private final boolean optionG32D;
    private final boolean optionG3Fill;
    private final boolean optionUncompressed;
    private final boolean optionByteAligned;
    private final int type;
    private int decodedLength;
    private int decodedPos;
    private int[] changesReferenceRow;
    private int[] changesCurrentRow;
    private int changesReferenceRowCount;
    private int changesCurrentRowCount;
    private int lastChangingElement = 0;
    int buffer = -1;
    int bufferPos = -1;
    static final short[][] BLACK_CODES;
    static final short[][] BLACK_RUN_LENGTHS;
    public static final short[][] WHITE_CODES;
    public static final short[][] WHITE_RUN_LENGTHS;
    static final Node EOL;
    static final Node FILL;
    static final Tree blackRunTree;
    static final Tree whiteRunTree;
    static final Tree eolOnlyTree;
    static final Tree codeTree;
    static final int VALUE_EOL = -2000;
    static final int VALUE_FILL = -1000;
    static final int VALUE_PASSMODE = -3000;
    static final int VALUE_HMODE = -4000;

    public CCITTFaxDecoderStream(InputStream inputStream, int n, int n2, long l, boolean bl) {
        super((InputStream)Validate.notNull((Object)inputStream, (String)"stream"));
        this.columns = (Integer)Validate.isTrue((n > 0 ? 1 : 0) != 0, (Object)n, (String)"width must be greater than 0");
        this.type = (Integer)Validate.isTrue((n2 == 2 || n2 == 3 || n2 == 4 ? 1 : 0) != 0, (Object)n2, (String)"Only CCITT Modified Huffman RLE compression (2), CCITT T4 (3) or CCITT T6 (4) supported: %s");
        this.decodedRow = new byte[(n + 7) / 8];
        this.changesReferenceRow = new int[n + 2];
        this.changesCurrentRow = new int[n + 2];
        switch (n2) {
            case 2: {
                this.optionByteAligned = bl;
                this.optionG32D = false;
                this.optionG3Fill = false;
                this.optionUncompressed = false;
                break;
            }
            case 3: {
                this.optionByteAligned = bl;
                this.optionG32D = (l & 1L) != 0L;
                this.optionG3Fill = (l & 4L) != 0L;
                this.optionUncompressed = (l & 2L) != 0L;
                break;
            }
            case 4: {
                this.optionByteAligned = bl;
                this.optionG32D = false;
                this.optionG3Fill = false;
                this.optionUncompressed = (l & 2L) != 0L;
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        Validate.isTrue((!this.optionUncompressed ? 1 : 0) != 0, (Object)this.optionUncompressed, (String)"CCITT GROUP 3/4 OPTION UNCOMPRESSED is not supported");
    }

    public CCITTFaxDecoderStream(InputStream inputStream, int n, int n2, long l) {
        this(inputStream, n, n2, l, n2 == 2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int findCompressionType(int n, InputStream inputStream) throws IOException {
        if (n == 3 && inputStream.markSupported()) {
            int n2 = 512;
            try {
                int n3;
                inputStream.mark(n2);
                int n4 = inputStream.read();
                int n5 = inputStream.read();
                if (n5 == -1) {
                    int n6 = n;
                    return n6;
                }
                if (n4 == 0 && ((byte)n5 >> 4 == 1 || (byte)n5 == 1)) {
                    int n7 = n;
                    return n7;
                }
                short s = (short)(((byte)n4 << 8) + (byte)n5 >> 4);
                int n8 = n2 * 8;
                int n9 = n5;
                byte by = (byte)n9;
                for (n3 = 12; n3 < n8; ++n3) {
                    if (n3 % 8 == 0) {
                        n9 = inputStream.read();
                        if (n9 == -1) {
                            int n10 = 2;
                            return n10;
                        }
                        by = (byte)n9;
                    }
                    if (((s = (short)((s << 1) + (by >> 7 - n3 % 8 & 1))) & 0xFFF) != 1) continue;
                    int n11 = 3;
                    return n11;
                }
                n3 = 2;
                return n3;
            }
            finally {
                inputStream.reset();
            }
        }
        return n;
    }

    private void fetch() throws IOException {
        if (this.decodedPos >= this.decodedLength) {
            this.decodedLength = 0;
            try {
                this.decodeRow();
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                throw new IOException("Malformed CCITT stream", arrayIndexOutOfBoundsException);
            }
            catch (EOFException eOFException) {
                if (this.decodedLength != 0) {
                    throw eOFException;
                }
                this.decodedLength = -1;
            }
            this.decodedPos = 0;
        }
    }

    private void decode1D() throws IOException {
        int n = 0;
        boolean bl = true;
        this.changesCurrentRowCount = 0;
        do {
            int n2 = bl ? this.decodeRun(whiteRunTree) : this.decodeRun(blackRunTree);
            this.changesCurrentRow[this.changesCurrentRowCount++] = n += n2;
            boolean bl2 = bl = !bl;
        } while (n < this.columns);
    }

    private void decode2D() throws IOException {
        this.changesReferenceRowCount = this.changesCurrentRowCount;
        int[] nArray = this.changesCurrentRow;
        this.changesCurrentRow = this.changesReferenceRow;
        this.changesReferenceRow = nArray;
        boolean bl = true;
        int n = 0;
        this.changesCurrentRowCount = 0;
        block4: while (n < this.columns) {
            Node node = CCITTFaxDecoderStream.codeTree.root;
            while ((node = node.walk(this.readBit())) != null) {
                if (!node.isLeaf) continue;
                switch (node.value) {
                    case -4000: {
                        int n2 = this.decodeRun(bl ? whiteRunTree : blackRunTree);
                        this.changesCurrentRow[this.changesCurrentRowCount++] = n += n2;
                        n2 = this.decodeRun(bl ? blackRunTree : whiteRunTree);
                        this.changesCurrentRow[this.changesCurrentRowCount++] = n += n2;
                        continue block4;
                    }
                    case -3000: {
                        int n3 = this.getNextChangingElement(n, bl) + 1;
                        if (n3 >= this.changesReferenceRowCount) {
                            n = this.columns;
                            continue block4;
                        }
                        n = this.changesReferenceRow[n3];
                        continue block4;
                    }
                }
                int n4 = this.getNextChangingElement(n, bl);
                n = n4 >= this.changesReferenceRowCount || n4 == -1 ? this.columns + node.value : this.changesReferenceRow[n4] + node.value;
                this.changesCurrentRow[this.changesCurrentRowCount] = n;
                ++this.changesCurrentRowCount;
                bl = !bl;
                continue block4;
            }
        }
    }

    private int getNextChangingElement(int n, boolean bl) {
        int n2 = (this.lastChangingElement & 0xFFFFFFFE) + (bl ? 0 : 1);
        if (n2 > 2) {
            n2 -= 2;
        }
        if (n == 0) {
            return n2;
        }
        for (int i = n2; i < this.changesReferenceRowCount; i += 2) {
            if (n >= this.changesReferenceRow[i]) continue;
            this.lastChangingElement = i;
            return i;
        }
        return -1;
    }

    private void decodeRowType2() throws IOException {
        if (this.optionByteAligned) {
            this.resetBuffer();
        }
        this.decode1D();
    }

    private void decodeRowType4() throws IOException {
        if (this.optionByteAligned) {
            this.resetBuffer();
        }
        block0: while (true) {
            Node node = CCITTFaxDecoderStream.eolOnlyTree.root;
            do {
                if ((node = node.walk(this.readBit())) == null) continue block0;
            } while (!node.isLeaf);
            break;
        }
        if (!this.optionG32D || this.readBit()) {
            this.decode1D();
        } else {
            this.decode2D();
        }
    }

    private void decodeRowType6() throws IOException {
        if (this.optionByteAligned) {
            this.resetBuffer();
        }
        this.decode2D();
    }

    private void decodeRow() throws IOException {
        switch (this.type) {
            case 2: {
                this.decodeRowType2();
                break;
            }
            case 3: {
                this.decodeRowType4();
                break;
            }
            case 4: {
                this.decodeRowType6();
            }
        }
        int n = 0;
        boolean bl = true;
        this.lastChangingElement = 0;
        for (int i = 0; i <= this.changesCurrentRowCount; ++i) {
            int n2 = this.columns;
            if (i != this.changesCurrentRowCount) {
                n2 = this.changesCurrentRow[i];
            }
            if (n2 > this.columns) {
                n2 = this.columns;
            }
            int n3 = n / 8;
            while (n % 8 != 0 && n2 - n > 0) {
                int n4 = n3;
                this.decodedRow[n4] = (byte)(this.decodedRow[n4] | (bl ? 0 : 1 << 7 - n % 8));
                ++n;
            }
            if (n % 8 == 0) {
                n3 = n / 8;
                byte by = (byte)(bl ? 0 : 255);
                while (n2 - n > 7) {
                    this.decodedRow[n3] = by;
                    n += 8;
                    ++n3;
                }
            }
            while (n2 - n > 0) {
                if (n % 8 == 0) {
                    this.decodedRow[n3] = 0;
                }
                int n5 = n3;
                this.decodedRow[n5] = (byte)(this.decodedRow[n5] | (bl ? 0 : 1 << 7 - n % 8));
                ++n;
            }
            bl = !bl;
        }
        if (n != this.columns) {
            throw new IOException("Sum of run-lengths does not equal scan line width: " + n + " > " + this.columns);
        }
        this.decodedLength = (n + 7) / 8;
    }

    private int decodeRun(Tree tree) throws IOException {
        int n = 0;
        Node node = tree.root;
        while (true) {
            boolean bl;
            if ((node = node.walk(bl = this.readBit())) == null) {
                throw new IOException("Unknown code in Huffman RLE stream");
            }
            if (!node.isLeaf) continue;
            n += node.value;
            if (node.value < 64) break;
            node = tree.root;
        }
        if (node.value >= 0) {
            return n;
        }
        return this.columns;
    }

    private void resetBuffer() throws IOException {
        this.bufferPos = -1;
    }

    private boolean readBit() throws IOException {
        if (this.bufferPos > 7 || this.bufferPos < 0) {
            this.buffer = this.in.read();
            if (this.buffer == -1) {
                throw new EOFException("Unexpected end of Huffman RLE stream");
            }
            this.bufferPos = 0;
        }
        boolean bl = (this.buffer & 0x80) != 0;
        this.buffer <<= 1;
        ++this.bufferPos;
        return bl;
    }

    @Override
    public int read() throws IOException {
        if (this.decodedLength < 0) {
            return 0;
        }
        if (this.decodedPos >= this.decodedLength) {
            this.fetch();
            if (this.decodedLength < 0) {
                return 0;
            }
        }
        return this.decodedRow[this.decodedPos++] & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.decodedLength < 0) {
            Arrays.fill(byArray, n, n + n2, (byte)0);
            return n2;
        }
        if (this.decodedPos >= this.decodedLength) {
            this.fetch();
            if (this.decodedLength < 0) {
                Arrays.fill(byArray, n, n + n2, (byte)0);
                return n2;
            }
        }
        int n3 = Math.min(this.decodedLength - this.decodedPos, n2);
        System.arraycopy(this.decodedRow, this.decodedPos, byArray, n, n3);
        this.decodedPos += n3;
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        if (this.decodedLength < 0) {
            return -1L;
        }
        if (this.decodedPos >= this.decodedLength) {
            this.fetch();
            if (this.decodedLength < 0) {
                return -1L;
            }
        }
        int n = (int)Math.min((long)(this.decodedLength - this.decodedPos), l);
        this.decodedPos += n;
        return n;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    static {
        int n;
        int n2;
        BLACK_CODES = new short[][]{{2, 3}, {2, 3}, {2, 3}, {3}, {4, 5}, {4, 5, 7}, {4, 7}, {24}, {23, 24, 55, 8, 15}, {23, 24, 40, 55, 103, 104, 108, 8, 12, 13}, {18, 19, 20, 21, 22, 23, 28, 29, 30, 31, 36, 39, 40, 43, 44, 51, 52, 53, 55, 56, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 200, 201, 202, 203, 204, 205, 210, 211, 212, 213, 214, 215, 218, 219}, {74, 75, 76, 77, 82, 83, 84, 85, 90, 91, 100, 101, 108, 109, 114, 115, 116, 117, 118, 119}};
        BLACK_RUN_LENGTHS = new short[][]{{3, 2}, {1, 4}, {6, 5}, {7}, {9, 8}, {10, 11, 12}, {13, 14}, {15}, {16, 17, 0, 18, 64}, {24, 25, 23, 22, 19, 20, 21, 1792, 1856, 1920}, {1984, 2048, 2112, 2176, 2240, 2304, 2368, 2432, 2496, 2560, 52, 55, 56, 59, 60, 320, 384, 448, 53, 54, 50, 51, 44, 45, 46, 47, 57, 58, 61, 256, 48, 49, 62, 63, 30, 31, 32, 33, 40, 41, 128, 192, 26, 27, 28, 29, 34, 35, 36, 37, 38, 39, 42, 43}, {640, 704, 768, 832, 1280, 1344, 1408, 1472, 1536, 1600, 1664, 1728, 512, 576, 896, 960, 1024, 1088, 1152, 1216}};
        WHITE_CODES = new short[][]{{7, 8, 11, 12, 14, 15}, {18, 19, 20, 27, 7, 8}, {23, 24, 42, 43, 3, 52, 53, 7, 8}, {19, 23, 24, 36, 39, 40, 43, 3, 55, 4, 8, 12}, {18, 19, 20, 21, 22, 23, 26, 27, 2, 36, 37, 40, 41, 42, 43, 44, 45, 3, 50, 51, 52, 53, 54, 55, 4, 74, 75, 5, 82, 83, 84, 85, 88, 89, 90, 91, 100, 101, 103, 104, 10, 11}, {152, 153, 154, 155, 204, 205, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219}, new short[0], {8, 12, 13}, {18, 19, 20, 21, 22, 23, 28, 29, 30, 31}};
        WHITE_RUN_LENGTHS = new short[][]{{2, 3, 4, 5, 6, 7}, {128, 8, 9, 64, 10, 11}, {192, 1664, 16, 17, 13, 14, 15, 1, 12}, {26, 21, 28, 27, 18, 24, 25, 22, 256, 23, 20, 19}, {33, 34, 35, 36, 37, 38, 31, 32, 29, 53, 54, 39, 40, 41, 42, 43, 44, 30, 61, 62, 63, 0, 320, 384, 45, 59, 60, 46, 49, 50, 51, 52, 55, 56, 57, 58, 448, 512, 640, 576, 47, 48}, {1472, 1536, 1600, 1728, 704, 768, 832, 896, 960, 1024, 1088, 1152, 1216, 1280, 1344, 1408}, new short[0], {1792, 1856, 1920}, {1984, 2048, 2112, 2176, 2240, 2304, 2368, 2432, 2496, 2560}};
        EOL = new Node();
        CCITTFaxDecoderStream.EOL.isLeaf = true;
        CCITTFaxDecoderStream.EOL.value = -2000;
        FILL = new Node();
        CCITTFaxDecoderStream.FILL.value = -1000;
        CCITTFaxDecoderStream.FILL.left = FILL;
        CCITTFaxDecoderStream.FILL.right = EOL;
        eolOnlyTree = new Tree();
        try {
            eolOnlyTree.fill(12, 0, FILL);
            eolOnlyTree.fill(12, 1, EOL);
        }
        catch (IOException iOException) {
            throw new AssertionError((Object)iOException);
        }
        blackRunTree = new Tree();
        try {
            for (n2 = 0; n2 < BLACK_CODES.length; ++n2) {
                for (n = 0; n < BLACK_CODES[n2].length; ++n) {
                    blackRunTree.fill(n2 + 2, (int)BLACK_CODES[n2][n], BLACK_RUN_LENGTHS[n2][n]);
                }
            }
            blackRunTree.fill(12, 0, FILL);
            blackRunTree.fill(12, 1, EOL);
        }
        catch (IOException iOException) {
            throw new AssertionError((Object)iOException);
        }
        whiteRunTree = new Tree();
        try {
            for (n2 = 0; n2 < WHITE_CODES.length; ++n2) {
                for (n = 0; n < WHITE_CODES[n2].length; ++n) {
                    whiteRunTree.fill(n2 + 4, (int)WHITE_CODES[n2][n], WHITE_RUN_LENGTHS[n2][n]);
                }
            }
            whiteRunTree.fill(12, 0, FILL);
            whiteRunTree.fill(12, 1, EOL);
        }
        catch (IOException iOException) {
            throw new AssertionError((Object)iOException);
        }
        codeTree = new Tree();
        try {
            codeTree.fill(4, 1, -3000);
            codeTree.fill(3, 1, -4000);
            codeTree.fill(1, 1, 0);
            codeTree.fill(3, 3, 1);
            codeTree.fill(6, 3, 2);
            codeTree.fill(7, 3, 3);
            codeTree.fill(3, 2, -1);
            codeTree.fill(6, 2, -2);
            codeTree.fill(7, 2, -3);
        }
        catch (IOException iOException) {
            throw new AssertionError((Object)iOException);
        }
    }

    private static final class Tree {
        final Node root = new Node();

        private Tree() {
        }

        void fill(int n, int n2, int n3) throws IOException {
            Node node = this.root;
            for (int i = 0; i < n; ++i) {
                int n4 = n - 1 - i;
                boolean bl = (n2 >> n4 & 1) == 1;
                Node node2 = node.walk(bl);
                if (node2 == null) {
                    node2 = new Node();
                    if (i == n - 1) {
                        node2.value = n3;
                        node2.isLeaf = true;
                    }
                    if (n2 == 0) {
                        node2.canBeFill = true;
                    }
                    node.set(bl, node2);
                } else if (node2.isLeaf) {
                    throw new IOException("node is leaf, no other following");
                }
                node = node2;
            }
        }

        void fill(int n, int n2, Node node) throws IOException {
            Node node2 = this.root;
            for (int i = 0; i < n; ++i) {
                int n3 = n - 1 - i;
                boolean bl = (n2 >> n3 & 1) == 1;
                Node node3 = node2.walk(bl);
                if (node3 == null) {
                    node3 = i == n - 1 ? node : new Node();
                    if (n2 == 0) {
                        node3.canBeFill = true;
                    }
                    node2.set(bl, node3);
                } else if (node3.isLeaf) {
                    throw new IOException("node is leaf, no other following");
                }
                node2 = node3;
            }
        }
    }

    private static final class Node {
        Node left;
        Node right;
        int value;
        boolean canBeFill = false;
        boolean isLeaf = false;

        private Node() {
        }

        void set(boolean bl, Node node) {
            if (!bl) {
                this.left = node;
            } else {
                this.right = node;
            }
        }

        Node walk(boolean bl) {
            return bl ? this.right : this.left;
        }

        public String toString() {
            return "[leaf=" + this.isLeaf + ", value=" + this.value + ", canBeFill=" + this.canBeFill + "]";
        }
    }
}

