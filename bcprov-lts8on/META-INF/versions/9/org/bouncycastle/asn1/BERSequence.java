/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERBitString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BERSequence
extends ASN1Sequence {
    public BERSequence() {
    }

    public BERSequence(ASN1Encodable element) {
        super(element);
    }

    public BERSequence(ASN1EncodableVector elementVector) {
        super(elementVector);
    }

    public BERSequence(ASN1Encodable[] elements) {
        super(elements);
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        int totalLength = withTag ? 4 : 3;
        int count = this.elements.length;
        for (int i = 0; i < count; ++i) {
            ASN1Primitive p = this.elements[i].toASN1Primitive();
            totalLength += p.encodedLength(true);
        }
        return totalLength;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingIL(withTag, 48, this.elements);
    }

    @Override
    ASN1BitString toASN1BitString() {
        return new BERBitString(this.getConstructedBitStrings());
    }

    @Override
    ASN1External toASN1External() {
        return ((ASN1Sequence)this.toDLObject()).toASN1External();
    }

    @Override
    ASN1OctetString toASN1OctetString() {
        return new BEROctetString(this.getConstructedOctetStrings());
    }

    @Override
    ASN1Set toASN1Set() {
        return new BERSet(false, this.toArrayInternal());
    }
}

