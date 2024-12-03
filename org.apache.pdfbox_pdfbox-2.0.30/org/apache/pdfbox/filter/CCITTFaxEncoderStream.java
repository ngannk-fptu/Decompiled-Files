/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.pdfbox.filter.CCITTFaxDecoderStream;

final class CCITTFaxEncoderStream
extends OutputStream {
    private int currentBufferLength = 0;
    private final byte[] inputBuffer;
    private final int inputBufferLength;
    private final int columns;
    private final int rows;
    private int[] changesCurrentRow;
    private int[] changesReferenceRow;
    private int currentRow = 0;
    private int changesCurrentRowLength = 0;
    private int changesReferenceRowLength = 0;
    private byte outputBuffer = 0;
    private byte outputBufferBitLength = 0;
    private final int fillOrder;
    private final OutputStream stream;
    private static final Code[] WHITE_TERMINATING_CODES;
    private static final Code[] WHITE_NONTERMINATING_CODES;
    private static final Code[] BLACK_TERMINATING_CODES;
    private static final Code[] BLACK_NONTERMINATING_CODES;

    CCITTFaxEncoderStream(OutputStream stream, int columns, int rows, int fillOrder) {
        this.stream = stream;
        this.columns = columns;
        this.rows = rows;
        this.fillOrder = fillOrder;
        this.changesReferenceRow = new int[columns];
        this.changesCurrentRow = new int[columns];
        this.inputBufferLength = (columns + 7) / 8;
        this.inputBuffer = new byte[this.inputBufferLength];
    }

    @Override
    public void write(int b) throws IOException {
        this.inputBuffer[this.currentBufferLength] = (byte)b;
        ++this.currentBufferLength;
        if (this.currentBufferLength == this.inputBufferLength) {
            this.encodeRow();
            this.currentBufferLength = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    private void encodeRow() throws IOException {
        ++this.currentRow;
        int[] tmp = this.changesReferenceRow;
        this.changesReferenceRow = this.changesCurrentRow;
        this.changesCurrentRow = tmp;
        this.changesReferenceRowLength = this.changesCurrentRowLength;
        this.changesCurrentRowLength = 0;
        boolean white = true;
        for (int index = 0; index < this.columns; ++index) {
            int byteIndex = index / 8;
            int bit = index % 8;
            if ((this.inputBuffer[byteIndex] >> 7 - bit & 1) == 1 != white) continue;
            this.changesCurrentRow[this.changesCurrentRowLength] = index;
            ++this.changesCurrentRowLength;
            white = !white;
        }
        this.encodeRowType6();
        if (this.currentRow == this.rows) {
            this.writeEOL();
            this.writeEOL();
            this.fill();
        }
    }

    private void encodeRowType6() throws IOException {
        this.encode2D();
    }

    private int[] getNextChanges(int pos, boolean white) {
        int[] result = new int[]{this.columns, this.columns};
        for (int i = 0; i < this.changesCurrentRowLength; ++i) {
            if (pos >= this.changesCurrentRow[i] && (pos != 0 || !white)) continue;
            result[0] = this.changesCurrentRow[i];
            if (i + 1 >= this.changesCurrentRowLength) break;
            result[1] = this.changesCurrentRow[i + 1];
            break;
        }
        return result;
    }

    private void writeRun(int runLength, boolean white) throws IOException {
        Code[] codes;
        int nonterm = runLength / 64;
        Code[] codeArray = codes = white ? WHITE_NONTERMINATING_CODES : BLACK_NONTERMINATING_CODES;
        while (nonterm > 0) {
            if (nonterm >= codes.length) {
                this.write(codes[codes.length - 1].code, codes[codes.length - 1].length);
                nonterm -= codes.length;
                continue;
            }
            this.write(codes[nonterm - 1].code, codes[nonterm - 1].length);
            nonterm = 0;
        }
        Code c = white ? WHITE_TERMINATING_CODES[runLength % 64] : BLACK_TERMINATING_CODES[runLength % 64];
        this.write(c.code, c.length);
    }

    private void encode2D() throws IOException {
        boolean white = true;
        int index = 0;
        while (index < this.columns) {
            int[] nextChanges = this.getNextChanges(index, white);
            int[] nextRefs = this.getNextRefChanges(index, white);
            int difference = nextChanges[0] - nextRefs[0];
            if (nextChanges[0] > nextRefs[1]) {
                this.write(1, 4);
                index = nextRefs[1];
                continue;
            }
            if (difference > 3 || difference < -3) {
                this.write(1, 3);
                this.writeRun(nextChanges[0] - index, white);
                this.writeRun(nextChanges[1] - nextChanges[0], !white);
                index = nextChanges[1];
                continue;
            }
            switch (difference) {
                case 0: {
                    this.write(1, 1);
                    break;
                }
                case 1: {
                    this.write(3, 3);
                    break;
                }
                case 2: {
                    this.write(3, 6);
                    break;
                }
                case 3: {
                    this.write(3, 7);
                    break;
                }
                case -1: {
                    this.write(2, 3);
                    break;
                }
                case -2: {
                    this.write(2, 6);
                    break;
                }
                case -3: {
                    this.write(2, 7);
                    break;
                }
            }
            white = !white;
            index = nextRefs[0] + difference;
        }
    }

    private int[] getNextRefChanges(int a0, boolean white) {
        int i;
        int[] result = new int[]{this.columns, this.columns};
        int n = i = white ? 0 : 1;
        while (i < this.changesReferenceRowLength) {
            if (this.changesReferenceRow[i] > a0 || a0 == 0 && i == 0) {
                result[0] = this.changesReferenceRow[i];
                if (i + 1 >= this.changesReferenceRowLength) break;
                result[1] = this.changesReferenceRow[i + 1];
                break;
            }
            i += 2;
        }
        return result;
    }

    private void write(int code, int codeLength) throws IOException {
        for (int i = 0; i < codeLength; ++i) {
            boolean codeBit;
            boolean bl = codeBit = (code >> codeLength - i - 1 & 1) == 1;
            this.outputBuffer = this.fillOrder == 1 ? (byte)(this.outputBuffer | (codeBit ? 1 << 7 - this.outputBufferBitLength % 8 : 0)) : (byte)(this.outputBuffer | (codeBit ? 1 << this.outputBufferBitLength % 8 : 0));
            this.outputBufferBitLength = (byte)(this.outputBufferBitLength + 1);
            if (this.outputBufferBitLength != 8) continue;
            this.stream.write(this.outputBuffer);
            this.clearOutputBuffer();
        }
    }

    private void writeEOL() throws IOException {
        this.write(1, 12);
    }

    private void fill() throws IOException {
        if (this.outputBufferBitLength != 0) {
            this.stream.write(this.outputBuffer);
        }
        this.clearOutputBuffer();
    }

    private void clearOutputBuffer() {
        this.outputBuffer = 0;
        this.outputBufferBitLength = 0;
    }

    static {
        short code;
        short value;
        int j;
        int bitLength;
        int i;
        WHITE_TERMINATING_CODES = new Code[64];
        WHITE_NONTERMINATING_CODES = new Code[40];
        for (i = 0; i < CCITTFaxDecoderStream.WHITE_CODES.length; ++i) {
            bitLength = i + 4;
            for (j = 0; j < CCITTFaxDecoderStream.WHITE_CODES[i].length; ++j) {
                value = CCITTFaxDecoderStream.WHITE_RUN_LENGTHS[i][j];
                code = CCITTFaxDecoderStream.WHITE_CODES[i][j];
                if (value < 64) {
                    CCITTFaxEncoderStream.WHITE_TERMINATING_CODES[value] = new Code(code, bitLength);
                    continue;
                }
                CCITTFaxEncoderStream.WHITE_NONTERMINATING_CODES[value / 64 - 1] = new Code(code, bitLength);
            }
        }
        BLACK_TERMINATING_CODES = new Code[64];
        BLACK_NONTERMINATING_CODES = new Code[40];
        for (i = 0; i < CCITTFaxDecoderStream.BLACK_CODES.length; ++i) {
            bitLength = i + 2;
            for (j = 0; j < CCITTFaxDecoderStream.BLACK_CODES[i].length; ++j) {
                value = CCITTFaxDecoderStream.BLACK_RUN_LENGTHS[i][j];
                code = CCITTFaxDecoderStream.BLACK_CODES[i][j];
                if (value < 64) {
                    CCITTFaxEncoderStream.BLACK_TERMINATING_CODES[value] = new Code(code, bitLength);
                    continue;
                }
                CCITTFaxEncoderStream.BLACK_NONTERMINATING_CODES[value / 64 - 1] = new Code(code, bitLength);
            }
        }
    }

    private static class Code {
        final int code;
        final int length;

        private Code(int code, int length) {
            this.code = code;
            this.length = length;
        }
    }
}

