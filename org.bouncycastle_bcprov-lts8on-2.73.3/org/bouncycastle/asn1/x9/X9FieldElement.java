/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.math.ec.ECFieldElement;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X9FieldElement
extends ASN1Object {
    protected ECFieldElement f;
    private static X9IntegerConverter converter = new X9IntegerConverter();

    public X9FieldElement(ECFieldElement f) {
        this.f = f;
    }

    public ECFieldElement getValue() {
        return this.f;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        int byteCount = converter.getByteLength(this.f);
        byte[] paddedBigInteger = converter.integerToBytes(this.f.toBigInteger(), byteCount);
        return new DEROctetString(paddedBigInteger);
    }
}

