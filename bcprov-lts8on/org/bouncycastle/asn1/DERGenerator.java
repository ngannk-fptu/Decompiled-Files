/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Generator;

public abstract class DERGenerator
extends ASN1Generator {
    private boolean _tagged = false;
    private boolean _isExplicit;
    private int _tagNo;

    protected DERGenerator(OutputStream out) {
        super(out);
    }

    public DERGenerator(OutputStream out, int tagNo, boolean isExplicit) {
        super(out);
        this._tagged = true;
        this._isExplicit = isExplicit;
        this._tagNo = tagNo;
    }

    private void writeLength(OutputStream out, int length) throws IOException {
        if (length > 127) {
            int size = 1;
            int val = length;
            while ((val >>>= 8) != 0) {
                ++size;
            }
            out.write((byte)(size | 0x80));
            for (int i = (size - 1) * 8; i >= 0; i -= 8) {
                out.write((byte)(length >> i));
            }
        } else {
            out.write((byte)length);
        }
    }

    void writeDEREncoded(OutputStream out, int tag, byte[] bytes) throws IOException {
        out.write(tag);
        this.writeLength(out, bytes.length);
        out.write(bytes);
    }

    void writeDEREncoded(int tag, byte[] bytes) throws IOException {
        if (this._tagged) {
            int tagNum = this._tagNo | 0x80;
            if (this._isExplicit) {
                int newTag = this._tagNo | 0x20 | 0x80;
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                this.writeDEREncoded(bOut, tag, bytes);
                this.writeDEREncoded(this._out, newTag, bOut.toByteArray());
            } else if ((tag & 0x20) != 0) {
                this.writeDEREncoded(this._out, tagNum | 0x20, bytes);
            } else {
                this.writeDEREncoded(this._out, tagNum, bytes);
            }
        } else {
            this.writeDEREncoded(this._out, tag, bytes);
        }
    }
}

