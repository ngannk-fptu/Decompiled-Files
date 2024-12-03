/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BERTaggedObject
extends ASN1TaggedObject {
    public BERTaggedObject(int tagNo, ASN1Encodable obj) {
        super(true, tagNo, obj);
    }

    public BERTaggedObject(int tagClass, int tagNo, ASN1Encodable obj) {
        super(true, tagClass, tagNo, obj);
    }

    public BERTaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        super(explicit, tagNo, obj);
    }

    public BERTaggedObject(boolean explicit, int tagClass, int tagNo, ASN1Encodable obj) {
        super(explicit, tagClass, tagNo, obj);
    }

    BERTaggedObject(int explicitness, int tagClass, int tagNo, ASN1Encodable obj) {
        super(explicitness, tagClass, tagNo, obj);
    }

    @Override
    boolean encodeConstructed() {
        return this.isExplicit() || this.obj.toASN1Primitive().encodeConstructed();
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        ASN1Primitive primitive = this.obj.toASN1Primitive();
        boolean explicit = this.isExplicit();
        int length = primitive.encodedLength(explicit);
        if (explicit) {
            length += 3;
        }
        return length += withTag ? ASN1OutputStream.getLengthOfIdentifier(this.tagNo) : 0;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        ASN1Primitive primitive = this.obj.toASN1Primitive();
        boolean explicit = this.isExplicit();
        if (withTag) {
            int flags = this.tagClass;
            if (explicit || primitive.encodeConstructed()) {
                flags |= 0x20;
            }
            out.writeIdentifier(true, flags, this.tagNo);
        }
        if (explicit) {
            out.write(128);
            primitive.encode(out, true);
            out.write(0);
            out.write(0);
        } else {
            primitive.encode(out, false);
        }
    }

    @Override
    ASN1Sequence rebuildConstructed(ASN1Primitive primitive) {
        return new BERSequence(primitive);
    }

    @Override
    ASN1TaggedObject replaceTag(int tagClass, int tagNo) {
        return new BERTaggedObject(this.explicitness, tagClass, tagNo, this.obj);
    }
}

