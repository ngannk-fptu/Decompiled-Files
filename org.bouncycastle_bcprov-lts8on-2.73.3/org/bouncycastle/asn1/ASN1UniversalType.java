/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Tag;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Type;
import org.bouncycastle.asn1.DEROctetString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
abstract class ASN1UniversalType
extends ASN1Type {
    final ASN1Tag tag;

    ASN1UniversalType(Class javaClass, int tagNumber) {
        super(javaClass);
        this.tag = ASN1Tag.create(0, tagNumber);
    }

    final ASN1Primitive checkedCast(ASN1Primitive primitive) {
        if (this.javaClass.isInstance(primitive)) {
            return primitive;
        }
        throw new IllegalStateException("unexpected object: " + primitive.getClass().getName());
    }

    ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
        throw new IllegalStateException("unexpected implicit primitive encoding");
    }

    ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
        throw new IllegalStateException("unexpected implicit constructed encoding");
    }

    final ASN1Primitive fromByteArray(byte[] bytes) throws IOException {
        return this.checkedCast(ASN1Primitive.fromByteArray(bytes));
    }

    final ASN1Primitive getContextInstance(ASN1TaggedObject taggedObject, boolean declaredExplicit) {
        if (128 != taggedObject.getTagClass()) {
            throw new IllegalStateException("this method only valid for CONTEXT_SPECIFIC tags");
        }
        return this.checkedCast(taggedObject.getBaseUniversal(declaredExplicit, this));
    }

    final ASN1Tag getTag() {
        return this.tag;
    }
}

