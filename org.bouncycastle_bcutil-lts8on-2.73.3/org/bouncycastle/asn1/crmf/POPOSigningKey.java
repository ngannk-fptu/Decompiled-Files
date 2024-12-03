/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.POPOSigningKeyInput;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class POPOSigningKey
extends ASN1Object {
    private POPOSigningKeyInput poposkInput;
    private AlgorithmIdentifier algorithmIdentifier;
    private ASN1BitString signature;

    private POPOSigningKey(ASN1Sequence seq) {
        int index = 0;
        if (seq.getObjectAt(index) instanceof ASN1TaggedObject) {
            ASN1TaggedObject tagObj = (ASN1TaggedObject)seq.getObjectAt(index++);
            this.poposkInput = POPOSigningKeyInput.getInstance(ASN1Util.getContextBaseUniversal((ASN1TaggedObject)tagObj, (int)0, (boolean)false, (int)16));
        }
        this.algorithmIdentifier = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(index++));
        this.signature = ASN1BitString.getInstance((Object)seq.getObjectAt(index));
    }

    public static POPOSigningKey getInstance(Object o) {
        if (o instanceof POPOSigningKey) {
            return (POPOSigningKey)((Object)o);
        }
        if (o != null) {
            return new POPOSigningKey(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static POPOSigningKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return POPOSigningKey.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public POPOSigningKey(POPOSigningKeyInput poposkIn, AlgorithmIdentifier aid, ASN1BitString signature) {
        this.poposkInput = poposkIn;
        this.algorithmIdentifier = aid;
        this.signature = signature;
    }

    public POPOSigningKeyInput getPoposkInput() {
        return this.poposkInput;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }

    public ASN1BitString getSignature() {
        return this.signature;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.poposkInput != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.poposkInput));
        }
        v.add((ASN1Encodable)this.algorithmIdentifier);
        v.add((ASN1Encodable)this.signature);
        return new DERSequence(v);
    }
}

