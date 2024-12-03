/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;

public class ASN1Absent
extends ASN1Primitive {
    public static final ASN1Absent INSTANCE = new ASN1Absent();

    private ASN1Absent() {
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        return 0;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        return o == this;
    }
}

