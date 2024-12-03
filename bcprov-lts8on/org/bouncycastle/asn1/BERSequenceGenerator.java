/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERGenerator;

public class BERSequenceGenerator
extends BERGenerator {
    public BERSequenceGenerator(OutputStream out) throws IOException {
        super(out);
        this.writeBERHeader(48);
    }

    public BERSequenceGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
        this.writeBERHeader(48);
    }

    public void addObject(ASN1Encodable object) throws IOException {
        object.toASN1Primitive().encodeTo(this._out);
    }

    public void addObject(ASN1Primitive primitive) throws IOException {
        primitive.encodeTo(this._out);
    }

    public void close() throws IOException {
        this.writeBEREnd();
    }
}

