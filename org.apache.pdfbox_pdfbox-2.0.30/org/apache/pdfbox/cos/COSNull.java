/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.ICOSVisitor;

public final class COSNull
extends COSBase {
    public static final byte[] NULL_BYTES = new byte[]{110, 117, 108, 108};
    public static final COSNull NULL = new COSNull();

    private COSNull() {
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromNull(this);
    }

    public void writePDF(OutputStream output) throws IOException {
        output.write(NULL_BYTES);
    }

    public String toString() {
        return "COSNull{}";
    }
}

