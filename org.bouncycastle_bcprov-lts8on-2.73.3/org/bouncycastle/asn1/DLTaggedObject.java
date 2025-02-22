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
import org.bouncycastle.asn1.DLSequence;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLTaggedObject
extends ASN1TaggedObject {
    public DLTaggedObject(int tagNo, ASN1Encodable encodable) {
        super(true, tagNo, encodable);
    }

    public DLTaggedObject(int tagClass, int tagNo, ASN1Encodable encodable) {
        super(true, tagClass, tagNo, encodable);
    }

    public DLTaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        super(explicit, tagNo, obj);
    }

    public DLTaggedObject(boolean explicit, int tagClass, int tagNo, ASN1Encodable obj) {
        super(explicit, tagClass, tagNo, obj);
    }

    DLTaggedObject(int explicitness, int tagClass, int tagNo, ASN1Encodable obj) {
        super(explicitness, tagClass, tagNo, obj);
    }

    @Override
    boolean encodeConstructed() {
        return this.isExplicit() || this.obj.toASN1Primitive().toDLObject().encodeConstructed();
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        ASN1Primitive primitive = this.obj.toASN1Primitive().toDLObject();
        boolean explicit = this.isExplicit();
        int length = primitive.encodedLength(explicit);
        if (explicit) {
            length += ASN1OutputStream.getLengthOfDL(length);
        }
        return length += withTag ? ASN1OutputStream.getLengthOfIdentifier(this.tagNo) : 0;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        ASN1Primitive primitive = this.obj.toASN1Primitive().toDLObject();
        boolean explicit = this.isExplicit();
        if (withTag) {
            int flags = this.tagClass;
            if (explicit || primitive.encodeConstructed()) {
                flags |= 0x20;
            }
            out.writeIdentifier(true, flags, this.tagNo);
        }
        if (explicit) {
            out.writeDL(primitive.encodedLength(true));
        }
        primitive.encode(out.getDLSubStream(), explicit);
    }

    @Override
    ASN1Sequence rebuildConstructed(ASN1Primitive primitive) {
        return new DLSequence(primitive);
    }

    @Override
    ASN1TaggedObject replaceTag(int tagClass, int tagNo) {
        return new DLTaggedObject(this.explicitness, tagClass, tagNo, this.obj);
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

