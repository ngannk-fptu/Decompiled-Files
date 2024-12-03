/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decode.Predictor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LZWDecode {
    ByteBuffer buf;
    int bytepos;
    int bitpos;
    byte[][] dict = new byte[4096][];
    int dictlen = 0;
    int bitspercode = 9;
    static int STOP = 257;
    static int CLEARDICT = 256;

    private LZWDecode(ByteBuffer buf) throws PDFParseException {
        for (int i = 0; i < 256; ++i) {
            this.dict[i] = new byte[1];
            this.dict[i][0] = (byte)i;
        }
        this.dictlen = 258;
        this.bitspercode = 9;
        this.buf = buf;
        this.bytepos = 0;
        this.bitpos = 0;
    }

    private void resetDict() {
        this.dictlen = 258;
        this.bitspercode = 9;
    }

    private int nextCode() {
        int fillbits = this.bitspercode;
        int value = 0;
        if (this.bytepos >= this.buf.limit() - 1) {
            return -1;
        }
        while (fillbits > 0) {
            byte nextbits = this.buf.get(this.bytepos);
            int bitsfromhere = 8 - this.bitpos;
            if (bitsfromhere > fillbits) {
                bitsfromhere = fillbits;
            }
            value |= (nextbits >> 8 - this.bitpos - bitsfromhere & 255 >> 8 - bitsfromhere) << fillbits - bitsfromhere;
            fillbits -= bitsfromhere;
            this.bitpos += bitsfromhere;
            if (this.bitpos < 8) continue;
            this.bitpos = 0;
            ++this.bytepos;
        }
        return value;
    }

    private ByteBuffer decode() throws PDFParseException {
        int cW = CLEARDICT;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            byte[] p;
            int pW = cW;
            cW = this.nextCode();
            if (cW == -1) {
                throw new PDFParseException("Missed the stop code in LZWDecode!");
            }
            if (cW == STOP) break;
            if (cW == CLEARDICT) {
                this.resetDict();
                continue;
            }
            if (pW == CLEARDICT) {
                baos.write(this.dict[cW], 0, this.dict[cW].length);
                continue;
            }
            if (cW < this.dictlen) {
                baos.write(this.dict[cW], 0, this.dict[cW].length);
                p = new byte[this.dict[pW].length + 1];
                System.arraycopy(this.dict[pW], 0, p, 0, this.dict[pW].length);
                p[this.dict[pW].length] = this.dict[cW][0];
                this.dict[this.dictlen++] = p;
            } else {
                p = new byte[this.dict[pW].length + 1];
                System.arraycopy(this.dict[pW], 0, p, 0, this.dict[pW].length);
                p[this.dict[pW].length] = p[0];
                baos.write(p, 0, p.length);
                this.dict[this.dictlen++] = p;
            }
            if (this.dictlen < (1 << this.bitspercode) - 1 || this.bitspercode >= 12) continue;
            ++this.bitspercode;
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public static ByteBuffer decode(ByteBuffer buf, PDFObject params) throws IOException {
        Predictor predictor;
        LZWDecode me = new LZWDecode(buf);
        ByteBuffer outBytes = me.decode();
        if (params != null && params.getDictionary().containsKey("Predictor") && (predictor = Predictor.getPredictor(params)) != null) {
            outBytes = predictor.unpredict(outBytes);
        }
        return outBytes;
    }
}

