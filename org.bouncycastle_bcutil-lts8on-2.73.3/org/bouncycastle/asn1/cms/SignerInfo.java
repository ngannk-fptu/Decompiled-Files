/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SignerInfo
extends ASN1Object {
    private ASN1Integer version;
    private SignerIdentifier sid;
    private AlgorithmIdentifier digAlgorithm;
    private ASN1Set authenticatedAttributes;
    private AlgorithmIdentifier digEncryptionAlgorithm;
    private ASN1OctetString encryptedDigest;
    private ASN1Set unauthenticatedAttributes;

    public static SignerInfo getInstance(Object o) throws IllegalArgumentException {
        if (o instanceof SignerInfo) {
            return (SignerInfo)((Object)o);
        }
        if (o != null) {
            return new SignerInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public SignerInfo(SignerIdentifier sid, AlgorithmIdentifier digAlgorithm, ASN1Set authenticatedAttributes, AlgorithmIdentifier digEncryptionAlgorithm, ASN1OctetString encryptedDigest, ASN1Set unauthenticatedAttributes) {
        this.version = sid.isTagged() ? new ASN1Integer(3L) : new ASN1Integer(1L);
        this.sid = sid;
        this.digAlgorithm = digAlgorithm;
        this.authenticatedAttributes = authenticatedAttributes;
        this.digEncryptionAlgorithm = digEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = unauthenticatedAttributes;
    }

    public SignerInfo(SignerIdentifier sid, AlgorithmIdentifier digAlgorithm, Attributes authenticatedAttributes, AlgorithmIdentifier digEncryptionAlgorithm, ASN1OctetString encryptedDigest, Attributes unauthenticatedAttributes) {
        this.version = sid.isTagged() ? new ASN1Integer(3L) : new ASN1Integer(1L);
        this.sid = sid;
        this.digAlgorithm = digAlgorithm;
        this.authenticatedAttributes = ASN1Set.getInstance((Object)((Object)authenticatedAttributes));
        this.digEncryptionAlgorithm = digEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = ASN1Set.getInstance((Object)((Object)unauthenticatedAttributes));
    }

    private SignerInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.version = (ASN1Integer)e.nextElement();
        this.sid = SignerIdentifier.getInstance(e.nextElement());
        this.digAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        Object obj = e.nextElement();
        if (obj instanceof ASN1TaggedObject) {
            this.authenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)obj), (boolean)false);
            this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        } else {
            this.authenticatedAttributes = null;
            this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(obj);
        }
        this.encryptedDigest = DEROctetString.getInstance(e.nextElement());
        this.unauthenticatedAttributes = e.hasMoreElements() ? ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)e.nextElement()), (boolean)false) : null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public SignerIdentifier getSID() {
        return this.sid;
    }

    public ASN1Set getAuthenticatedAttributes() {
        return this.authenticatedAttributes;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digAlgorithm;
    }

    public ASN1OctetString getEncryptedDigest() {
        return this.encryptedDigest;
    }

    public AlgorithmIdentifier getDigestEncryptionAlgorithm() {
        return this.digEncryptionAlgorithm;
    }

    public ASN1Set getUnauthenticatedAttributes() {
        return this.unauthenticatedAttributes;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.sid);
        v.add((ASN1Encodable)this.digAlgorithm);
        if (this.authenticatedAttributes != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.authenticatedAttributes));
        }
        v.add((ASN1Encodable)this.digEncryptionAlgorithm);
        v.add((ASN1Encodable)this.encryptedDigest);
        if (this.unauthenticatedAttributes != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.unauthenticatedAttributes));
        }
        return new DERSequence(v);
    }
}

