/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.plugins.tiff.CCITTFaxDecoderStream;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.OutputStream;

final class CCITTFaxEncoderStream
extends OutputStream {
    private int currentBufferLength = 0;
    private final byte[] inputBuffer;
    private final int inputBufferLength;
    private int columns;
    private int rows;
    private int[] changesCurrentRow;
    private int[] changesReferenceRow;
    private int currentRow = 0;
    private int changesCurrentRowLength = 0;
    private int changesReferenceRowLength = 0;
    private byte outputBuffer = 0;
    private byte outputBufferBitLength = 0;
    private int type;
    private int fillOrder;
    private boolean optionG32D;
    private boolean optionG3Fill;
    private boolean optionUncompressed;
    private OutputStream stream;
    public static final Code[] WHITE_TERMINATING_CODES;
    public static final Code[] WHITE_NONTERMINATING_CODES;
    public static final Code[] BLACK_TERMINATING_CODES;
    public static final Code[] BLACK_NONTERMINATING_CODES;

    public CCITTFaxEncoderStream(OutputStream outputStream, int n, int n2, int n3, int n4, long l) {
        this.stream = outputStream;
        this.type = n3;
        this.columns = n;
        this.rows = n2;
        this.fillOrder = n4;
        this.changesReferenceRow = new int[n];
        this.changesCurrentRow = new int[n];
        switch (n3) {
            case 3: {
                this.optionG32D = (l & 1L) != 0L;
                this.optionG3Fill = (l & 4L) != 0L;
                this.optionUncompressed = (l & 2L) != 0L;
                break;
            }
            case 4: {
                this.optionUncompressed = (l & 2L) != 0L;
            }
        }
        this.inputBufferLength = (n + 7) / 8;
        this.inputBuffer = new byte[this.inputBufferLength];
        Validate.isTrue((!this.optionUncompressed ? 1 : 0) != 0, (Object)this.optionUncompressed, (String)"CCITT GROUP 3/4 OPTION UNCOMPRESSED is not supported");
    }

    @Override
    public void write(int n) throws IOException {
        this.inputBuffer[this.currentBufferLength] = (byte)n;
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
        int[] nArray = this.changesReferenceRow;
        this.changesReferenceRow = this.changesCurrentRow;
        this.changesCurrentRow = nArray;
        this.changesReferenceRowLength = this.changesCurrentRowLength;
        this.changesCurrentRowLength = 0;
        boolean bl = true;
        for (int i = 0; i < this.columns; ++i) {
            int n = i / 8;
            int n2 = i % 8;
            if ((this.inputBuffer[n] >> 7 - n2 & 1) == 1 != bl) continue;
            this.changesCurrentRow[this.changesCurrentRowLength] = i;
            ++this.changesCurrentRowLength;
            bl = !bl;
        }
        switch (this.type) {
            case 2: {
                this.encodeRowType2();
                break;
            }
            case 3: {
                this.encodeRowType4();
                break;
            }
            case 4: {
                this.encodeRowType6();
            }
        }
        if (this.currentRow == this.rows) {
            if (this.type == 4) {
                this.writeEOL();
                this.writeEOL();
            }
            this.fill();
        }
    }

    private void encodeRowType2() throws IOException {
        this.encode1D();
        this.fill();
    }

    private void encodeRowType4() throws IOException {
        this.writeEOL();
        if (this.optionG32D) {
            if (this.changesReferenceRowLength == 0) {
                this.write(1, 1);
                this.encode1D();
            } else {
                this.write(0, 1);
                this.encode2D();
            }
        } else {
            this.encode1D();
        }
        if (this.optionG3Fill) {
            this.fill();
        }
    }

    private void encodeRowType6() throws IOException {
        this.encode2D();
    }

    private void encode1D() throws IOException {
        int n;
        boolean bl = true;
        for (int i = 0; i < this.columns; i += n) {
            int[] nArray = this.getNextChanges(i, bl);
            n = nArray[0] - i;
            this.writeRun(n, bl);
            bl = !bl;
        }
    }

    private int[] getNextChanges(int n, boolean bl) {
        int[] nArray = new int[]{this.columns, this.columns};
        for (int i = 0; i < this.changesCurrentRowLength; ++i) {
            if (n >= this.changesCurrentRow[i] && (n != 0 || !bl)) continue;
            nArray[0] = this.changesCurrentRow[i];
            if (i + 1 >= this.changesCurrentRowLength) break;
            nArray[1] = this.changesCurrentRow[i + 1];
            break;
        }
        return nArray;
    }

    private void writeRun(int n, boolean bl) throws IOException {
        Code[] codeArray;
        int n2 = n / 64;
        Code[] codeArray2 = codeArray = bl ? WHITE_NONTERMINATING_CODES : BLACK_NONTERMINATING_CODES;
        while (n2 > 0) {
            if (n2 >= codeArray.length) {
                this.write(codeArray[codeArray.length - 1].code, codeArray[codeArray.length - 1].length);
                n2 -= codeArray.length;
                continue;
            }
            this.write(codeArray[n2 - 1].code, codeArray[n2 - 1].length);
            n2 = 0;
        }
        Code code = bl ? WHITE_TERMINATING_CODES[n % 64] : BLACK_TERMINATING_CODES[n % 64];
        this.write(code.code, code.length);
    }

    private void encode2D() throws IOException {
        boolean bl = true;
        int n = 0;
        while (n < this.columns) {
            int[] nArray = this.getNextChanges(n, bl);
            int[] nArray2 = this.getNextRefChanges(n, bl);
            int n2 = nArray[0] - nArray2[0];
            if (nArray[0] > nArray2[1]) {
                this.write(1, 4);
                n = nArray2[1];
                continue;
            }
            if (n2 > 3 || n2 < -3) {
                this.write(1, 3);
                this.writeRun(nArray[0] - n, bl);
                this.writeRun(nArray[1] - nArray[0], !bl);
                n = nArray[1];
                continue;
            }
            switch (n2) {
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
                }
            }
            bl = !bl;
            n = nArray2[0] + n2;
        }
    }

    private int[] getNextRefChanges(int n, boolean bl) {
        int n2;
        int[] nArray = new int[]{this.columns, this.columns};
        int n3 = n2 = bl ? 0 : 1;
        while (n2 < this.changesReferenceRowLength) {
            if (this.changesReferenceRow[n2] > n || n == 0 && n2 == 0) {
                nArray[0] = this.changesReferenceRow[n2];
                if (n2 + 1 >= this.changesReferenceRowLength) break;
                nArray[1] = this.changesReferenceRow[n2 + 1];
                break;
            }
            n2 += 2;
        }
        return nArray;
    }

    private void write(int n, int n2) throws IOException {
        for (int i = 0; i < n2; ++i) {
            boolean bl;
            boolean bl2 = bl = (n >> n2 - i - 1 & 1) == 1;
            this.outputBuffer = this.fillOrder == 1 ? (byte)(this.outputBuffer | (bl ? 1 << 7 - this.outputBufferBitLength % 8 : 0)) : (byte)(this.outputBuffer | (bl ? 1 << this.outputBufferBitLength % 8 : 0));
            this.outputBufferBitLength = (byte)(this.outputBufferBitLength + 1);
            if (this.outputBufferBitLength != 8) continue;
            this.stream.write(this.outputBuffer);
            this.clearOutputBuffer();
        }
    }

    private void writeEOL() throws IOException {
        if (this.optionG3Fill) {
            while (this.outputBufferBitLength != 4) {
                this.write(0, 1);
            }
        }
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
        short s;
        short s2;
        int n;
        int n2;
        int n3;
        WHITE_TERMINATING_CODES = new Code[64];
        WHITE_NONTERMINATING_CODES = new Code[40];
        for (n3 = 0; n3 < CCITTFaxDecoderStream.WHITE_CODES.length; ++n3) {
            n2 = n3 + 4;
            for (n = 0; n < CCITTFaxDecoderStream.WHITE_CODES[n3].length; ++n) {
                s2 = CCITTFaxDecoderStream.WHITE_RUN_LENGTHS[n3][n];
                s = CCITTFaxDecoderStream.WHITE_CODES[n3][n];
                if (s2 < 64) {
                    CCITTFaxEncoderStream.WHITE_TERMINATING_CODES[s2] = new Code(s, n2);
                    continue;
                }
                CCITTFaxEncoderStream.WHITE_NONTERMINATING_CODES[s2 / 64 - 1] = new Code(s, n2);
            }
        }
        BLACK_TERMINATING_CODES = new Code[64];
        BLACK_NONTERMINATING_CODES = new Code[40];
        for (n3 = 0; n3 < CCITTFaxDecoderStream.BLACK_CODES.length; ++n3) {
            n2 = n3 + 2;
            for (n = 0; n < CCITTFaxDecoderStream.BLACK_CODES[n3].length; ++n) {
                s2 = CCITTFaxDecoderStream.BLACK_RUN_LENGTHS[n3][n];
                s = CCITTFaxDecoderStream.BLACK_CODES[n3][n];
                if (s2 < 64) {
                    CCITTFaxEncoderStream.BLACK_TERMINATING_CODES[s2] = new Code(s, n2);
                    continue;
                }
                CCITTFaxEncoderStream.BLACK_NONTERMINATING_CODES[s2 / 64 - 1] = new Code(s, n2);
            }
        }
    }

    public static class Code {
        final int code;
        final int length;

        private Code(int n, int n2) {
            this.code = n;
            this.length = n2;
        }
    }
}

