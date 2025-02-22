/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedValue
extends ASN1Object {
    private AlgorithmIdentifier intendedAlg;
    private AlgorithmIdentifier symmAlg;
    private ASN1BitString encSymmKey;
    private AlgorithmIdentifier keyAlg;
    private ASN1OctetString valueHint;
    private ASN1BitString encValue;

    private EncryptedValue(ASN1Sequence seq) {
        int index = 0;
        while (seq.getObjectAt(index) instanceof ASN1TaggedObject) {
            ASN1TaggedObject tObj = (ASN1TaggedObject)seq.getObjectAt(index);
            switch (tObj.getTagNo()) {
                case 0: {
                    this.intendedAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    break;
                }
                case 1: {
                    this.symmAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    break;
                }
                case 2: {
                    this.encSymmKey = ASN1BitString.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    break;
                }
                case 3: {
                    this.keyAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    break;
                }
                case 4: {
                    this.valueHint = ASN1OctetString.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered: " + tObj.getTagNo());
                }
            }
            ++index;
        }
        this.encValue = ASN1BitString.getInstance((Object)seq.getObjectAt(index));
    }

    public static EncryptedValue getInstance(Object o) {
        if (o instanceof EncryptedValue) {
            return (EncryptedValue)((Object)o);
        }
        if (o != null) {
            return new EncryptedValue(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public EncryptedValue(AlgorithmIdentifier intendedAlg, AlgorithmIdentifier symmAlg, ASN1BitString encSymmKey, AlgorithmIdentifier keyAlg, ASN1OctetString valueHint, ASN1BitString encValue) {
        if (encValue == null) {
            throw new IllegalArgumentException("'encValue' cannot be null");
        }
        this.intendedAlg = intendedAlg;
        this.symmAlg = symmAlg;
        this.encSymmKey = encSymmKey;
        this.keyAlg = keyAlg;
        this.valueHint = valueHint;
        this.encValue = encValue;
    }

    public AlgorithmIdentifier getIntendedAlg() {
        return this.intendedAlg;
    }

    public AlgorithmIdentifier getSymmAlg() {
        return this.symmAlg;
    }

    public ASN1BitString getEncSymmKey() {
        return this.encSymmKey;
    }

    public AlgorithmIdentifier getKeyAlg() {
        return this.keyAlg;
    }

    public ASN1OctetString getValueHint() {
        return this.valueHint;
    }

    public ASN1BitString getEncValue() {
        return this.encValue;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(6);
        this.addOptional(v, 0, (ASN1Encodable)this.intendedAlg);
        this.addOptional(v, 1, (ASN1Encodable)this.symmAlg);
        this.addOptional(v, 2, (ASN1Encodable)this.encSymmKey);
        this.addOptional(v, 3, (ASN1Encodable)this.keyAlg);
        this.addOptional(v, 4, (ASN1Encodable)this.valueHint);
        v.add((ASN1Encodable)this.encValue);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, tagNo, obj));
        }
    }
}

