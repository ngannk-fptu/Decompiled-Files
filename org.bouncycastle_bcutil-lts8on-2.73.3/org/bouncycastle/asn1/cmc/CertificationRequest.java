/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertificationRequest
extends ASN1Object {
    private static final ASN1Integer ZERO = new ASN1Integer(0L);
    private final CertificationRequestInfo certificationRequestInfo;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final ASN1BitString signature;

    public CertificationRequest(X500Name subject, AlgorithmIdentifier subjectPublicAlgorithm, ASN1BitString subjectPublicKey, ASN1Set attributes, AlgorithmIdentifier signatureAlgorithm, ASN1BitString signature) {
        this.certificationRequestInfo = new CertificationRequestInfo(subject, subjectPublicAlgorithm, subjectPublicKey, attributes);
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = signature;
    }

    private CertificationRequest(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.certificationRequestInfo = new CertificationRequestInfo(ASN1Sequence.getInstance((Object)seq.getObjectAt(0)));
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.signature = ASN1BitString.getInstance((Object)seq.getObjectAt(2));
    }

    public static CertificationRequest getInstance(Object o) {
        if (o instanceof CertificationRequest) {
            return (CertificationRequest)((Object)o);
        }
        if (o != null) {
            return new CertificationRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public BigInteger getVersion() {
        return this.certificationRequestInfo.getVersion().getValue();
    }

    public X500Name getSubject() {
        return this.certificationRequestInfo.getSubject();
    }

    public ASN1Set getAttributes() {
        return this.certificationRequestInfo.getAttributes();
    }

    public AlgorithmIdentifier getSubjectPublicKeyAlgorithm() {
        return AlgorithmIdentifier.getInstance((Object)this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(0));
    }

    public ASN1BitString getSubjectPublicKey() {
        return ASN1BitString.getInstance((Object)this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(1));
    }

    public ASN1Primitive parsePublicKey() throws IOException {
        return ASN1Primitive.fromByteArray((byte[])this.getSubjectPublicKey().getOctets());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public ASN1BitString getSignature() {
        return this.signature;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.certificationRequestInfo);
        v.add((ASN1Encodable)this.signatureAlgorithm);
        v.add((ASN1Encodable)this.signature);
        return new DERSequence(v);
    }

    private static class CertificationRequestInfo
    extends ASN1Object {
        private final ASN1Integer version;
        private final X500Name subject;
        private final ASN1Sequence subjectPublicKeyInfo;
        private final ASN1Set attributes;

        private CertificationRequestInfo(ASN1Sequence seq) {
            if (seq.size() != 4) {
                throw new IllegalArgumentException("incorrect sequence size for CertificationRequestInfo");
            }
            this.version = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
            this.subject = X500Name.getInstance((Object)seq.getObjectAt(1));
            this.subjectPublicKeyInfo = ASN1Sequence.getInstance((Object)seq.getObjectAt(2));
            if (this.subjectPublicKeyInfo.size() != 2) {
                throw new IllegalArgumentException("incorrect subjectPublicKeyInfo size for CertificationRequestInfo");
            }
            ASN1TaggedObject tagobj = (ASN1TaggedObject)seq.getObjectAt(3);
            if (tagobj.getTagNo() != 0) {
                throw new IllegalArgumentException("incorrect tag number on attributes for CertificationRequestInfo");
            }
            this.attributes = ASN1Set.getInstance((ASN1TaggedObject)tagobj, (boolean)false);
        }

        private CertificationRequestInfo(X500Name subject, AlgorithmIdentifier algorithm, ASN1BitString subjectPublicKey, ASN1Set attributes) {
            this.version = ZERO;
            this.subject = subject;
            this.subjectPublicKeyInfo = new DERSequence(new ASN1Encodable[]{algorithm, subjectPublicKey});
            this.attributes = attributes;
        }

        private ASN1Integer getVersion() {
            return this.version;
        }

        private X500Name getSubject() {
            return this.subject;
        }

        private ASN1Sequence getSubjectPublicKeyInfo() {
            return this.subjectPublicKeyInfo;
        }

        private ASN1Set getAttributes() {
            return this.attributes;
        }

        public ASN1Primitive toASN1Primitive() {
            ASN1EncodableVector v = new ASN1EncodableVector(4);
            v.add((ASN1Encodable)this.version);
            v.add((ASN1Encodable)this.subject);
            v.add((ASN1Encodable)this.subjectPublicKeyInfo);
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.attributes));
            return new DERSequence(v);
        }
    }
}

