/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;

public class ObjectStore
extends ASN1Object {
    private final ASN1Encodable storeData;
    private final ObjectStoreIntegrityCheck integrityCheck;

    public ObjectStore(ObjectStoreData objectStoreData, ObjectStoreIntegrityCheck integrityCheck) {
        this.storeData = objectStoreData;
        this.integrityCheck = integrityCheck;
    }

    public ObjectStore(EncryptedObjectStoreData encryptedObjectStoreData, ObjectStoreIntegrityCheck integrityCheck) {
        this.storeData = encryptedObjectStoreData;
        this.integrityCheck = integrityCheck;
    }

    private ObjectStore(ASN1Sequence seq) {
        ASN1Sequence seqData;
        if (seq.size() != 2) {
            throw new IllegalArgumentException("malformed sequence");
        }
        ASN1Encodable sData = seq.getObjectAt(0);
        this.storeData = sData instanceof EncryptedObjectStoreData ? sData : (sData instanceof ObjectStoreData ? sData : ((seqData = ASN1Sequence.getInstance(sData)).size() == 2 ? EncryptedObjectStoreData.getInstance(seqData) : ObjectStoreData.getInstance(seqData)));
        this.integrityCheck = ObjectStoreIntegrityCheck.getInstance(seq.getObjectAt(1));
    }

    public static ObjectStore getInstance(Object o) {
        if (o instanceof ObjectStore) {
            return (ObjectStore)o;
        }
        if (o != null) {
            return new ObjectStore(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ObjectStoreIntegrityCheck getIntegrityCheck() {
        return this.integrityCheck;
    }

    public ASN1Encodable getStoreData() {
        return this.storeData;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.storeData);
        v.add(this.integrityCheck);
        return new DERSequence(v);
    }
}

