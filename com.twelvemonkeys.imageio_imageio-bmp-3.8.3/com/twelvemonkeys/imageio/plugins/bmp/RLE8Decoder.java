/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.AbstractRLEDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

final class RLE8Decoder
extends AbstractRLEDecoder {
    public RLE8Decoder(int n) {
        super(n, 8);
    }

    @Override
    protected void decodeRow(InputStream inputStream) throws IOException {
        int n = 0;
        int n2 = 0;
        while (this.srcY >= 0) {
            block13: {
                byte by;
                int n3;
                int n4;
                block12: {
                    n4 = inputStream.read();
                    n3 = RLE8Decoder.checkEOF(inputStream.read());
                    if (n4 != 0) break block12;
                    switch (n3) {
                        case 0: {
                            if (this.srcX != 0) {
                                Arrays.fill(this.row, this.srcX, this.row.length, (byte)0);
                                this.srcX = this.row.length;
                                break;
                            }
                            break block13;
                        }
                        case 1: {
                            Arrays.fill(this.row, this.srcX, this.row.length, (byte)0);
                            this.srcX = this.row.length;
                            this.srcY = -1;
                            break;
                        }
                        case 2: {
                            n = this.srcX + inputStream.read();
                            n2 = this.srcY + RLE8Decoder.checkEOF(inputStream.read());
                            Arrays.fill(this.row, this.srcX, n, (byte)0);
                            this.srcX = this.row.length;
                            break;
                        }
                        default: {
                            byte by2 = by = n3 % 2 != 0 ? (byte)1 : 0;
                            while (n3-- > 0 && this.srcX < this.row.length) {
                                this.row[this.srcX++] = (byte)RLE8Decoder.checkEOF(inputStream.read());
                            }
                            if (by != 0) {
                                RLE8Decoder.checkEOF(inputStream.read());
                                break;
                            }
                            break block13;
                        }
                    }
                    break block13;
                }
                by = (byte)n3;
                while (n4-- > 0 && this.srcX < this.row.length) {
                    this.row[this.srcX++] = by;
                }
            }
            if (this.srcX < this.row.length) continue;
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

