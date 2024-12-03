/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;

public class RecipientEncryptedKey
extends ASN1Object {
    private KeyAgreeRecipientIdentifier identifier;
    private ASN1OctetString encryptedKey;

    private RecipientEncryptedKey(ASN1Sequence seq) {
        this.identifier = KeyAgreeRecipientIdentifier.getInstance(seq.getObjectAt(0));
        this.encryptedKey = (ASN1OctetString)seq.getObjectAt(1);
    }

    public static RecipientEncryptedKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return RecipientEncryptedKey.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static RecipientEncryptedKey getInstance(Object obj) {
        if (obj instanceof RecipientEncryptedKey) {
            return (RecipientEncryptedKey)((Object)obj);
        }
        if (obj != null) {
            return new RecipientEncryptedKey(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public RecipientEncryptedKey(KeyAgreeRecipientIdentifier id, ASN1OctetString encryptedKey) {
        this.identifier = id;
        this.encryptedKey = encryptedKey;
    }

    public KeyAgreeRecipientIdentifier getIdentifier() {
        return this.identifier;
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.identifier);
        v.add((ASN1Encodable)this.encryptedKey);
        return new DERSequence(v);
    }
}

