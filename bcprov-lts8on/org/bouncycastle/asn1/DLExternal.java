/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLTaggedObject;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLExternal
extends ASN1External {
    public DLExternal(DLSequence sequence) {
        super(sequence);
    }

    public DLExternal(ASN1ObjectIdentifier directReference, ASN1Integer indirectReference, ASN1Primitive dataValueDescriptor, DERTaggedObject externalData) {
        super(directReference, indirectReference, dataValueDescriptor, externalData);
    }

    public DLExternal(ASN1ObjectIdentifier directReference, ASN1Integer indirectReference, ASN1Primitive dataValueDescriptor, int encoding, ASN1Primitive externalData) {
        super(directReference, indirectReference, dataValueDescriptor, encoding, externalData);
    }

    @Override
    ASN1Sequence buildSequence() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        if (this.directReference != null) {
            v.add(this.directReference);
        }
        if (this.indirectReference != null) {
            v.add(this.indirectReference);
        }
        if (this.dataValueDescriptor != null) {
            v.add(this.dataValueDescriptor.toDLObject());
        }
        v.add(new DLTaggedObject(0 == this.encoding, this.encoding, (ASN1Encodable)this.externalContent));
        return new DLSequence(v);
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

