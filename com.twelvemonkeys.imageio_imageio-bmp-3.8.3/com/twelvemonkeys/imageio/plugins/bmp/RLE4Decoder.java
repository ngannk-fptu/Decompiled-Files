/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.AbstractRLEDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

final class RLE4Decoder
extends AbstractRLEDecoder {
    static final int[] BIT_MASKS = new int[]{240, 15};
    static final int[] BIT_SHIFTS = new int[]{4, 0};

    public RLE4Decoder(int n) {
        super(n, 4);
    }

    @Override
    protected void decodeRow(InputStream inputStream) throws IOException {
        Arrays.fill(this.row, (byte)0);
        int n = 0;
        int n2 = 0;
        while (this.srcY >= 0) {
            block14: {
                int n3;
                int n4;
                int n5;
                block13: {
                    n5 = inputStream.read();
                    n4 = RLE4Decoder.checkEOF(inputStream.read());
                    if (n5 != 0) break block13;
                    switch (n4) {
                        case 0: {
                            if (this.srcX != 0) {
                                this.srcX = this.row.length * 2;
                                break;
                            }
                            break block14;
                        }
                        case 1: {
                            this.srcX = this.row.length * 2;
                            this.srcY = -1;
                            break;
                        }
                        case 2: {
                            n = this.srcX + inputStream.read();
                            n2 = this.srcY + RLE4Decoder.checkEOF(inputStream.read());
                            this.srcX = this.row.length * 2;
                            break;
                        }
                        default: {
                            n3 = (n4 + 1) / 2 % 2 != 0 ? 1 : 0;
                            int n6 = 0;
                            for (int i = 0; i < n4 && this.srcX / 2 < this.row.length; ++i) {
                                if (i % 2 == 0) {
                                    n6 = RLE4Decoder.checkEOF(inputStream.read());
                                }
                                int n7 = this.srcX / 2;
                                this.row[n7] = (byte)(this.row[n7] | (byte)((n6 & BIT_MASKS[i % 2]) >> BIT_SHIFTS[i % 2] << BIT_SHIFTS[this.srcX % 2]));
                                ++this.srcX;
                            }
                            if (n3 != 0) {
                                RLE4Decoder.checkEOF(inputStream.read());
                                break;
                            }
                            break block14;
                        }
                    }
                    break block14;
                }
                for (n3 = 0; n3 < n5 && this.srcX / 2 < this.row.length; ++n3) {
                    int n8 = this.srcX / 2;
                    this.row[n8] = (byte)(this.row[n8] | (byte)((n4 & BIT_MASKS[n3 % 2]) >> BIT_SHIFTS[n3 % 2] << BIT_SHIFTS[this.srcX % 2]));
                    ++this.srcX;
                }
            }
            if (this.srcX < this.row.length * 2) continue;
            if (n != 0 || n2 != 0) {
                this.srcX = n;
                if (n2 != this.srcY) {
                    this.srcY = n2;
                    break;
                }
                n = 0;
                n2 = 0;
                continue;
            }
            if (this.srcY == -1) break;
            this.srcX = 0;
            ++this.srcY;
            break;
        }
    }
}

