/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Generator;

public abstract class BERGenerator
extends ASN1Generator {
    private boolean _tagged = false;
    private boolean _isExplicit;
    private int _tagNo;

    protected BERGenerator(OutputStream out) {
        super(out);
    }

    protected BERGenerator(OutputStream out, int tagNo, boolean isExplicit) {
        super(out);
        this._tagged = true;
        this._isExplicit = isExplicit;
        this._tagNo = tagNo;
    }

    @Override
    public OutputStream getRawOutputStream() {
        return this._out;
    }

    private void writeHdr(int tag) throws IOException {
        this._out.write(tag);
        this._out.write(128);
    }

    protected void writeBERHeader(int tag) throws IOException {
        if (this._tagged) {
            int tagNum = this._tagNo | 0x80;
            if (this._isExplicit) {
                this.writeHdr(tagNum | 0x20);
                this.writeHdr(tag);
            } else if ((tag & 0x20) != 0) {
                this.writeHdr(tagNum | 0x20);
            } else {
                this.writeHdr(tagNum);
            }
        } else {
            this.writeHdr(tag);
        }
    }

    protected void writeBEREnd() throws IOException {
        this._out.write(0);
        this._out.write(0);
        if (this._tagged && this._isExplicit) {
            this._out.write(0);
            this._out.write(0);
        }
    }
}

