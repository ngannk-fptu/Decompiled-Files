/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class CertTemplate
extends ASN1Object {
    private ASN1Sequence seq;
    private ASN1Integer version;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signingAlg;
    private X500Name issuer;
    private OptionalValidity validity;
    private X500Name subject;
    private SubjectPublicKeyInfo publicKey;
    private ASN1BitString issuerUID;
    private ASN1BitString subjectUID;
    private Extensions extensions;

    private CertTemplate(ASN1Sequence seq) {
        this.seq = seq;
        Enumeration en = seq.getObjects();
        block12: while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject)en.nextElement();
            switch (tObj.getTagNo()) {
                case 0: {
                    this.version = ASN1Integer.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
                case 1: {
                    this.serialNumber = ASN1Integer.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
                case 2: {
                    this.signingAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
                case 3: {
                    this.issuer = X500Name.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block12;
                }
                case 4: {
                    this.validity = OptionalValidity.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)tObj, (boolean)false));
                    continue block12;
                }
                case 5: {
                    this.subject = X500Name.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                    continue block12;
                }
                case 6: {
                    this.publicKey = SubjectPublicKeyInfo.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
                case 7: {
                    this.issuerUID = ASN1BitString.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
                case 8: {
                    this.subjectUID = ASN1BitString.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
                case 9: {
                    this.extensions = Extensions.getInstance((ASN1TaggedObject)tObj, (boolean)false);
                    continue block12;
                }
            }
            throw new IllegalArgumentException("unknown tag: " + tObj.getTagNo());
        }
    }

    public static CertTemplate getInstance(Object o) {
        if (o instanceof CertTemplate) {
            return (CertTemplate)((Object)o);
        }
        if (o != null) {
            return new CertTemplate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public int getVersion() {
        if (this.version != null) {
            return this.version.intValueExact();
        }
        return -1;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSigningAlg() {
        return this.signingAlg;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public OptionalValidity getValidity() {
        return this.validity;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getPublicKey() {
        return this.publicKey;
    }

    public ASN1BitString getIssuerUID() {
        return this.issuerUID;
    }

    public ASN1BitString getSubjectUID() {
        return this.subjectUID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}

