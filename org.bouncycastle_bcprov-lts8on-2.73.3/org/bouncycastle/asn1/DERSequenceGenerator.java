/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERGenerator;

public class DERSequenceGenerator
extends DERGenerator {
    private final ByteArrayOutputStream _bOut = new ByteArrayOutputStream();

    public DERSequenceGenerator(OutputStream out) throws IOException {
        super(out);
    }

    public DERSequenceGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
    }

    public void addObject(ASN1Encodable object) throws IOException {
        object.toASN1Primitive().encodeTo(this._bOut, "DER");
    }

    public void addObject(ASN1Primitive primitive) throws IOException {
        primitive.encodeTo(this._bOut, "DER");
    }

    @Override
    public OutputStream getRawOutputStream() {
        return this._bOut;
    }

    public void close() throws IOException {
        this.writeDEREncoded(48, this._bOut.toByteArray());
    }
}

