/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.decoders;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.io.StreamReader;
import org.jpedal.jbig2.util.BinaryOperation;

public class MMRDecoder {
    private StreamReader reader;
    private long bufferLength = 0L;
    private long buffer = 0L;
    private long noOfBytesRead = 0L;
    private static MMRDecoder ref;
    public static int ccittEndOfLine;
    public static final int twoDimensionalPass = 0;
    public static final int twoDimensionalHorizontal = 1;
    public static final int twoDimensionalVertical0 = 2;
    public static final int twoDimensionalVerticalR1 = 3;
    public static final int twoDimensionalVerticalL1 = 4;
    public static final int twoDimensionalVerticalR2 = 5;
    public static final int twoDimensionalVerticalL2 = 6;
    public static final int twoDimensionalVerticalR3 = 7;
    public static final int twoDimensionalVerticalL3 = 8;
    private int[][] twoDimensionalTable1 = new int[][]{{-1, -1}, {-1, -1}, {7, 8}, {7, 7}, {6, 6}, {6, 6}, {6, 5}, {6, 5}, {4, 0}, {4, 0}, {4, 0}, {4, 0}, {4, 0}, {4, 0}, {4, 0}, {4, 0}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {3, 3}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}, {1, 2}};
    private int[][] whiteTable1 = new int[][]{{-1, -1}, {12, ccittEndOfLine}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {11, 1792}, {11, 1792}, {12, 1984}, {12, 2048}, {12, 2112}, {12, 2176}, {12, 2240}, {12, 2304}, {11, 1856}, {11, 1856}, {11, 1920}, {11, 1920}, {12, 2368}, {12, 2432}, {12, 2496}, {12, 2560}};
    private int[][] whiteTable2 = new int[][]{{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {8, 29}, {8, 29}, {8, 30}, {8, 30}, {8, 45}, {8, 45}, {8, 46}, {8, 46}, {7, 22}, {7, 22}, {7, 22}, {7, 22}, {7, 23}, {7, 23}, {7, 23}, {7, 23}, {8, 47}, {8, 47}, {8, 48}, {8, 48}, {6, 13}, {6, 13}, {6, 13}, {6, 13}, {6, 13}, {6, 13}, {6, 13}, {6, 13}, {7, 20}, {7, 20}, {7, 20}, {7, 20}, {8, 33}, {8, 33}, {8, 34}, {8, 34}, {8, 35}, {8, 35}, {8, 36}, {8, 36}, {8, 37}, {8, 37}, {8, 38}, {8, 38}, {7, 19}, {7, 19}, {7, 19}, {7, 19}, {8, 31}, {8, 31}, {8, 32}, {8, 32}, {6, 1}, {6, 1}, {6, 1}, {6, 1}, {6, 1}, {6, 1}, {6, 1}, {6, 1}, {6, 12}, {6, 12}, {6, 12}, {6, 12}, {6, 12}, {6, 12}, {6, 12}, {6, 12}, {8, 53}, {8, 53}, {8, 54}, {8, 54}, {7, 26}, {7, 26}, {7, 26}, {7, 26}, {8, 39}, {8, 39}, {8, 40}, {8, 40}, {8, 41}, {8, 41}, {8, 42}, {8, 42}, {8, 43}, {8, 43}, {8, 44}, {8, 44}, {7, 21}, {7, 21}, {7, 21}, {7, 21}, {7, 28}, {7, 28}, {7, 28}, {7, 28}, {8, 61}, {8, 61}, {8, 62}, {8, 62}, {8, 63}, {8, 63}, {8, 0}, {8, 0}, {8, 320}, {8, 320}, {8, 384}, {8, 384}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 10}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {5, 11}, {7, 27}, {7, 27}, {7, 27}, {7, 27}, {8, 59}, {8, 59}, {8, 60}, {8, 60}, {9, 1472}, {9, 1536}, {9, 1600}, {9, 1728}, {7, 18}, {7, 18}, {7, 18}, {7, 18}, {7, 24}, {7, 24}, {7, 24}, {7, 24}, {8, 49}, {8, 49}, {8, 50}, {8, 50}, {8, 51}, {8, 51}, {8, 52}, {8, 52}, {7, 25}, {7, 25}, {7, 25}, {7, 25}, {8, 55}, {8, 55}, {8, 56}, {8, 56}, {8, 57}, {8, 57}, {8, 58}, {8, 58}, {6, 192}, {6, 192}, {6, 192}, {6, 192}, {6, 192}, {6, 192}, {6, 192}, {6, 192}, {6, 1664}, {6, 1664}, {6, 1664}, {6, 1664}, {6, 1664}, {6, 1664}, {6, 1664}, {6, 1664}, {8, 448}, {8, 448}, {8, 512}, {8, 512}, {9, 704}, {9, 768}, {8, 640}, {8, 640}, {8, 576}, {8, 576}, {9, 832}, {9, 896}, {9, 960}, {9, 1024}, {9, 1088}, {9, 1152}, {9, 1216}, {9, 1280}, {9, 1344}, {9, 1408}, {7, 256}, {7, 256}, {7, 256}, {7, 256}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 2}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {4, 3}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 128}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 8}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {5, 9}, {6, 16}, {6, 16}, {6, 16}, {6, 16}, {6, 16}, {6, 16}, {6, 16}, {6, 16}, {6, 17}, {6, 17}, {6, 17}, {6, 17}, {6, 17}, {6, 17}, {6, 17}, {6, 17}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 4}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {6, 14}, {6, 14}, {6, 14}, {6, 14}, {6, 14}, {6, 14}, {6, 14}, {6, 14}, {6, 15}, {6, 15}, {6, 15}, {6, 15}, {6, 15}, {6, 15}, {6, 15}, {6, 15}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {5, 64}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}, {4, 7}};
    int[][] blackTable1 = new int[][]{{-1, -1}, {-1, -1}, {12, ccittEndOfLine}, {12, ccittEndOfLine}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {11, 1792}, {11, 1792}, {11, 1792}, {11, 1792}, {12, 1984}, {12, 1984}, {12, 2048}, {12, 2048}, {12, 2112}, {12, 2112}, {12, 2176}, {12, 2176}, {12, 2240}, {12, 2240}, {12, 2304}, {12, 2304}, {11, 1856}, {11, 1856}, {11, 1856}, {11, 1856}, {11, 1920}, {11, 1920}, {11, 1920}, {11, 1920}, {12, 2368}, {12, 2368}, {12, 2432}, {12, 2432}, {12, 2496}, {12, 2496}, {12, 2560}, {12, 2560}, {10, 18}, {10, 18}, {10, 18}, {10, 18}, {10, 18}, {10, 18}, {10, 18}, {10, 18}, {12, 52}, {12, 52}, {13, 640}, {13, 704}, {13, 768}, {13, 832}, {12, 55}, {12, 55}, {12, 56}, {12, 56}, {13, 1280}, {13, 1344}, {13, 1408}, {13, 1472}, {12, 59}, {12, 59}, {12, 60}, {12, 60}, {13, 1536}, {13, 1600}, {11, 24}, {11, 24}, {11, 24}, {11, 24}, {11, 25}, {11, 25}, {11, 25}, {11, 25}, {13, 1664}, {13, 1728}, {12, 320}, {12, 320}, {12, 384}, {12, 384}, {12, 448}, {12, 448}, {13, 512}, {13, 576}, {12, 53}, {12, 53}, {12, 54}, {12, 54}, {13, 896}, {13, 960}, {13, 1024}, {13, 1088}, {13, 1152}, {13, 1216}, {10, 64}, {10, 64}, {10, 64}, {10, 64}, {10, 64}, {10, 64}, {10, 64}, {10, 64}};
    int[][] blackTable2 = new int[][]{{8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {8, 13}, {11, 23}, {11, 23}, {12, 50}, {12, 51}, {12, 44}, {12, 45}, {12, 46}, {12, 47}, {12, 57}, {12, 58}, {12, 61}, {12, 256}, {10, 16}, {10, 16}, {10, 16}, {10, 16}, {10, 17}, {10, 17}, {10, 17}, {10, 17}, {12, 48}, {12, 49}, {12, 62}, {12, 63}, {12, 30}, {12, 31}, {12, 32}, {12, 33}, {12, 40}, {12, 41}, {11, 22}, {11, 22}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {8, 14}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 10}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {7, 11}, {9, 15}, {9, 15}, {9, 15}, {9, 15}, {9, 15}, {9, 15}, {9, 15}, {9, 15}, {12, 128}, {12, 192}, {12, 26}, {12, 27}, {12, 28}, {12, 29}, {11, 19}, {11, 19}, {11, 20}, {11, 20}, {12, 34}, {12, 35}, {12, 36}, {12, 37}, {12, 38}, {12, 39}, {11, 21}, {11, 21}, {12, 42}, {12, 43}, {10, 0}, {10, 0}, {10, 0}, {10, 0}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}, {7, 12}};
    int[][] blackTable3 = new int[][]{{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {6, 9}, {6, 8}, {5, 7}, {5, 7}, {4, 6}, {4, 6}, {4, 6}, {4, 6}, {4, 5}, {4, 5}, {4, 5}, {4, 5}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 1}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {3, 4}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 3}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}};

    /*
     * Opcode count of 13765 triggered aggressive code reduction.  Override with --aggressivesizethreshold.
     */
    private MMRDecoder() {
    }

    /*
     * Opcode count of 13768 triggered aggressive code reduction.  Override with --aggressivesizethreshold.
     */
    private MMRDecoder(StreamReader streamReader) {
        this.reader = streamReader;
    }

    public static void initiate(StreamReader streamReader) {
        ref = new MMRDecoder(streamReader);
    }

    public static MMRDecoder getInstance() throws JBIG2Exception {
        if (ref == null) {
            throw new JBIG2Exception("JBIG2MMRDecoder is uninitiated. Call JBIG2MMRDecoder.initiate(StreamReader reader) before getInstance()");
        }
        return ref;
    }

    public void reset() {
        this.bufferLength = 0L;
        this.noOfBytesRead = 0L;
        this.buffer = 0L;
    }

    public void skipTo(int n) throws IOException {
        while (this.noOfBytesRead < (long)n) {
            this.reader.readByte();
            ++this.noOfBytesRead;
        }
    }

    public long get24Bits() throws IOException {
        while (this.bufferLength < 24L) {
            this.buffer = BinaryOperation.bit32Shift(this.buffer, 8, 0) | (long)(this.reader.readByte() & 0xFF);
            this.bufferLength += 8L;
            ++this.noOfBytesRead;
        }
        return BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 24L), 1) & 0xFFFFFFL;
    }

    public int get2DCode() throws IOException {
        int[] nArray;
        if (this.bufferLength == 0L) {
            this.buffer = this.reader.readByte() & 0xFF;
            this.bufferLength = 8L;
            ++this.noOfBytesRead;
            int n = (int)(BinaryOperation.bit32Shift(this.buffer, 1, 1) & 0x7FL);
            nArray = this.twoDimensionalTable1[n];
        } else if (this.bufferLength == 8L) {
            int n = (int)(BinaryOperation.bit32Shift(this.buffer, 1, 1) & 0x7FL);
            nArray = this.twoDimensionalTable1[n];
        } else {
            int n = (int)(BinaryOperation.bit32Shift(this.buffer, (int)(7L - this.bufferLength), 0) & 0x7FL);
            nArray = this.twoDimensionalTable1[n];
            if (nArray[0] < 0 || nArray[0] > (int)this.bufferLength) {
                int n2 = this.reader.readByte() & 0xFF;
                long l = BinaryOperation.bit32Shift(this.buffer, 8, 0);
                this.buffer = l | (long)n2;
                this.bufferLength += 8L;
                ++this.noOfBytesRead;
                int n3 = (int)(BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 7L), 1) & 0x7FL);
                nArray = this.twoDimensionalTable1[n3];
            }
        }
        if (nArray[0] < 0) {
            if (JBIG2StreamDecoder.debug) {
                System.out.println("Bad two dim code in JBIG2 MMR stream");
            }
            return 0;
        }
        this.bufferLength -= (long)nArray[0];
        return nArray[1];
    }

    public int getWhiteCode() throws IOException {
        if (this.bufferLength == 0L) {
            this.buffer = this.reader.readByte() & 0xFF;
            this.bufferLength = 8L;
            ++this.noOfBytesRead;
        }
        while (true) {
            int[] nArray;
            long l;
            if (this.bufferLength >= 7L && (BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 7L), 1) & 0x7FL) == 0L) {
                l = this.bufferLength <= 12L ? BinaryOperation.bit32Shift(this.buffer, (int)(12L - this.bufferLength), 0) : BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 12L), 1);
                nArray = this.whiteTable1[(int)(l & 0x1FL)];
            } else {
                l = this.bufferLength <= 9L ? BinaryOperation.bit32Shift(this.buffer, (int)(9L - this.bufferLength), 0) : BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 9L), 1);
                int n = (int)(l & 0x1FFL);
                nArray = n >= 0 ? this.whiteTable2[n] : this.whiteTable2[this.whiteTable2.length + n];
            }
            if (nArray[0] > 0 && nArray[0] <= (int)this.bufferLength) {
                this.bufferLength -= (long)nArray[0];
                return nArray[1];
            }
            if (this.bufferLength >= 12L) break;
            this.buffer = BinaryOperation.bit32Shift(this.buffer, 8, 0) | (long)(this.reader.readByte() & 0xFF);
            this.bufferLength += 8L;
            ++this.noOfBytesRead;
        }
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Bad white code in JBIG2 MMR stream");
        }
        --this.bufferLength;
        return 1;
    }

    public int getBlackCode() throws IOException {
        if (this.bufferLength == 0L) {
            this.buffer = this.reader.readByte() & 0xFF;
            this.bufferLength = 8L;
            ++this.noOfBytesRead;
        }
        while (true) {
            int n;
            int[] nArray;
            long l;
            if (this.bufferLength >= 6L && (BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 6L), 1) & 0x3FL) == 0L) {
                l = this.bufferLength <= 13L ? BinaryOperation.bit32Shift(this.buffer, (int)(13L - this.bufferLength), 0) : BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 13L), 1);
                nArray = this.blackTable1[(int)(l & 0x7FL)];
            } else if (this.bufferLength >= 4L && (this.buffer >> (int)(this.bufferLength - 4L) & 0xFL) == 0L) {
                l = this.bufferLength <= 12L ? BinaryOperation.bit32Shift(this.buffer, (int)(12L - this.bufferLength), 0) : BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 12L), 1);
                n = (int)((l & 0xFFL) - 64L);
                nArray = n >= 0 ? this.blackTable2[n] : this.blackTable1[this.blackTable1.length + n];
            } else {
                l = this.bufferLength <= 6L ? BinaryOperation.bit32Shift(this.buffer, (int)(6L - this.bufferLength), 0) : BinaryOperation.bit32Shift(this.buffer, (int)(this.bufferLength - 6L), 1);
                n = (int)(l & 0x3FL);
                nArray = n >= 0 ? this.blackTable3[n] : this.blackTable2[this.blackTable2.length + n];
            }
            if (nArray[0] > 0 && nArray[0] <= (int)this.bufferLength) {
                this.bufferLength -= (long)nArray[0];
                return nArray[1];
            }
            if (this.bufferLength >= 13L) break;
            this.buffer = BinaryOperation.bit32Shift(this.buffer, 8, 0) | (long)(this.reader.readByte() & 0xFF);
            this.bufferLength += 8L;
            ++this.noOfBytesRead;
        }
        if (JBIG2StreamDecoder.debug) {
            System.out.println("Bad black code in JBIG2 MMR stream");
        }
        --this.bufferLength;
        return 1;
    }

    static {
        ccittEndOfLine = -2;
    }
}

