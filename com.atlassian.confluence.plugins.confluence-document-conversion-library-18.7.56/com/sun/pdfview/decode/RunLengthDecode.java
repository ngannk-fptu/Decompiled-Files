/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class RunLengthDecode {
    private static final int RUN_LENGTH_EOD = 128;
    private ByteBuffer buf;

    private RunLengthDecode(ByteBuffer buf) {
        this.buf = buf;
    }

    /*
     * Unable to fully structure code
     */
    private ByteBuffer decode() throws PDFParseException {
        this.buf.rewind();
        baos = new ByteArrayOutputStream();
        dupAmount = -1;
        buffer = new byte[128];
        block0: while (true) {
            v0 = this.buf.get();
            dupAmount = v0;
            if (v0 == -1 || dupAmount == 128) break;
            if (dupAmount <= 127) {
                amountToCopy = dupAmount + 1;
                while (true) {
                    if (amountToCopy <= 0) continue block0;
                    this.buf.get(buffer, 0, amountToCopy);
                    baos.write(buffer, 0, amountToCopy);
                }
            }
            dupByte = this.buf.get();
            i = 0;
            while (true) {
                if (i < 257 - (dupAmount & 255)) ** break;
                continue block0;
                baos.write(dupByte);
                ++i;
            }
            break;
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public static ByteBuffer decode(ByteBuffer buf, PDFObject params) throws PDFParseException {
        RunLengthDecode me = new RunLengthDecode(buf);
        return me.decode();
    }
}

