/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

public final class Predictor {
    private Predictor() {
    }

    static void decodePredictorRow(int predictor, int colors, int bitsPerComponent, int columns, byte[] actline, byte[] lastline) {
        if (predictor == 1) {
            return;
        }
        int bitsPerPixel = colors * bitsPerComponent;
        int bytesPerPixel = (bitsPerPixel + 7) / 8;
        int rowlength = actline.length;
        switch (predictor) {
            case 2: {
                if (bitsPerComponent == 8) {
                    for (int p = bytesPerPixel; p < rowlength; ++p) {
                        int sub = actline[p] & 0xFF;
                        int left = actline[p - bytesPerPixel] & 0xFF;
                        actline[p] = (byte)(sub + left);
                    }
                } else if (bitsPerComponent == 16) {
                    for (int p = bytesPerPixel; p < rowlength - 1; p += 2) {
                        int sub = ((actline[p] & 0xFF) << 8) + (actline[p + 1] & 0xFF);
                        int left = ((actline[p - bytesPerPixel] & 0xFF) << 8) + (actline[p - bytesPerPixel + 1] & 0xFF);
                        actline[p] = (byte)(sub + left >> 8 & 0xFF);
                        actline[p + 1] = (byte)(sub + left & 0xFF);
                    }
                } else if (bitsPerComponent == 1 && colors == 1) {
                    for (int p = 0; p < rowlength; ++p) {
                        for (int bit = 7; bit >= 0; --bit) {
                            int sub = actline[p] >> bit & 1;
                            if (p == 0 && bit == 7) continue;
                            int left = bit == 7 ? actline[p - 1] & 1 : actline[p] >> bit + 1 & 1;
                            if ((sub + left & 1) == 0) {
                                int n = p;
                                actline[n] = (byte)(actline[n] & ~(1 << bit));
                                continue;
                            }
                            int n = p;
                            actline[n] = (byte)(actline[n] | 1 << bit);
                        }
                    }
                } else {
                    int elements = columns * colors;
                    for (int p = colors; p < elements; ++p) {
                        int bytePosSub = p * bitsPerComponent / 8;
                        int bitPosSub = 8 - p * bitsPerComponent % 8 - bitsPerComponent;
                        int bytePosLeft = (p - colors) * bitsPerComponent / 8;
                        int bitPosLeft = 8 - (p - colors) * bitsPerComponent % 8 - bitsPerComponent;
                        int sub = Predictor.getBitSeq(actline[bytePosSub], bitPosSub, bitsPerComponent);
                        int left = Predictor.getBitSeq(actline[bytePosLeft], bitPosLeft, bitsPerComponent);
                        actline[bytePosSub] = (byte)Predictor.calcSetBitSeq(actline[bytePosSub], bitPosSub, bitsPerComponent, sub + left);
                    }
                }
                break;
            }
            case 10: {
                break;
            }
            case 11: {
                for (int p = bytesPerPixel; p < rowlength; ++p) {
                    byte sub = actline[p];
                    byte left = actline[p - bytesPerPixel];
                    actline[p] = (byte)(sub + left);
                }
                break;
            }
            case 12: {
                for (int p = 0; p < rowlength; ++p) {
                    int up = actline[p] & 0xFF;
                    int prior = lastline[p] & 0xFF;
                    actline[p] = (byte)(up + prior & 0xFF);
                }
                break;
            }
            case 13: {
                for (int p = 0; p < rowlength; ++p) {
                    int avg = actline[p] & 0xFF;
                    int left = p - bytesPerPixel >= 0 ? actline[p - bytesPerPixel] & 0xFF : 0;
                    int up = lastline[p] & 0xFF;
                    actline[p] = (byte)(avg + (left + up) / 2 & 0xFF);
                }
                break;
            }
            case 14: {
                for (int p = 0; p < rowlength; ++p) {
                    int paeth = actline[p] & 0xFF;
                    int a = p - bytesPerPixel >= 0 ? actline[p - bytesPerPixel] & 0xFF : 0;
                    int b = lastline[p] & 0xFF;
                    int c = p - bytesPerPixel >= 0 ? lastline[p - bytesPerPixel] & 0xFF : 0;
                    int value = a + b - c;
                    int absa = Math.abs(value - a);
                    int absb = Math.abs(value - b);
                    int absc = Math.abs(value - c);
                    actline[p] = absa <= absb && absa <= absc ? (byte)(paeth + a & 0xFF) : (absb <= absc ? (byte)(paeth + b & 0xFF) : (byte)(paeth + c & 0xFF));
                }
                break;
            }
        }
    }

    static int calculateRowLength(int colors, int bitsPerComponent, int columns) {
        int bitsPerPixel = colors * bitsPerComponent;
        return (columns * bitsPerPixel + 7) / 8;
    }

    static int getBitSeq(int by, int startBit, int bitSize) {
        int mask = (1 << bitSize) - 1;
        return by >>> startBit & mask;
    }

    static int calcSetBitSeq(int by, int startBit, int bitSize, int val) {
        int mask = (1 << bitSize) - 1;
        int truncatedVal = val & mask;
        mask = ~(mask << startBit);
        return by & mask | truncatedVal << startBit;
    }

    static OutputStream wrapPredictor(OutputStream out, COSDictionary decodeParams) {
        int predictor = decodeParams.getInt(COSName.PREDICTOR);
        if (predictor > 1) {
            int colors = Math.min(decodeParams.getInt(COSName.COLORS, 1), 32);
            int bitsPerPixel = decodeParams.getInt(COSName.BITS_PER_COMPONENT, 8);
            int columns = decodeParams.getInt(COSName.COLUMNS, 1);
            return new PredictorOutputStream(out, predictor, colors, bitsPerPixel, columns);
        }
        return out;
    }

    private static final class PredictorOutputStream
    extends FilterOutputStream {
        private int predictor;
        private final int colors;
        private final int bitsPerComponent;
        private final int columns;
        private final int rowLength;
        private final boolean predictorPerRow;
        private byte[] currentRow;
        private byte[] lastRow;
        private int currentRowData = 0;
        private boolean predictorRead = false;

        PredictorOutputStream(OutputStream out, int predictor, int colors, int bitsPerComponent, int columns) {
            super(out);
            this.predictor = predictor;
            this.colors = colors;
            this.bitsPerComponent = bitsPerComponent;
            this.columns = columns;
            this.rowLength = Predictor.calculateRowLength(colors, bitsPerComponent, columns);
            this.predictorPerRow = predictor >= 10;
            this.currentRow = new byte[this.rowLength];
            this.lastRow = new byte[this.rowLength];
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this.write(bytes, 0, bytes.length);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            int currentOffset = off;
            int maxOffset = currentOffset + len;
            while (currentOffset < maxOffset) {
                if (this.predictorPerRow && this.currentRowData == 0 && !this.predictorRead) {
                    this.predictor = bytes[currentOffset] + 10;
                    ++currentOffset;
                    this.predictorRead = true;
                    continue;
                }
                int toRead = Math.min(this.rowLength - this.currentRowData, maxOffset - currentOffset);
                System.arraycopy(bytes, currentOffset, this.currentRow, this.currentRowData, toRead);
                this.currentRowData += toRead;
                currentOffset += toRead;
                if (this.currentRowData != this.currentRow.length) continue;
                this.decodeAndWriteRow();
            }
        }

        private void decodeAndWriteRow() throws IOException {
            Predictor.decodePredictorRow(this.predictor, this.colors, this.bitsPerComponent, this.columns, this.currentRow, this.lastRow);
            this.out.write(this.currentRow);
            this.flipRows();
        }

        private void flipRows() {
            byte[] temp = this.lastRow;
            this.lastRow = this.currentRow;
            this.currentRow = temp;
            this.currentRowData = 0;
            this.predictorRead = false;
        }

        @Override
        public void flush() throws IOException {
            if (this.currentRowData > 0) {
                Arrays.fill(this.currentRow, this.currentRowData, this.rowLength, (byte)0);
                this.decodeAndWriteRow();
            }
            super.flush();
        }

        @Override
        public void write(int i) throws IOException {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}

