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
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;
import org.bouncycastle.asn1.eac.CertificateHolderReference;
import org.bouncycastle.asn1.eac.CertificationAuthorityReference;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.asn1.eac.PackedDate;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;

public class CertificateBody
extends ASN1Object {
    private ASN1TaggedObject certificateProfileIdentifier;
    private ASN1TaggedObject certificationAuthorityReference;
    private PublicKeyDataObject publicKey;
    private ASN1TaggedObject certificateHolderReference;
    private CertificateHolderAuthorization certificateHolderAuthorization;
    private ASN1TaggedObject certificateEffectiveDate;
    private ASN1TaggedObject certificateExpirationDate;
    private int certificateType = 0;
    private static final int CPI = 1;
    private static final int CAR = 2;
    private static final int PK = 4;
    private static final int CHR = 8;
    private static final int CHA = 16;
    private static final int CEfD = 32;
    private static final int CExD = 64;
    public static final int profileType = 127;
    public static final int requestType = 13;

    private void setIso7816CertificateBody(ASN1TaggedObject appSpe) throws IOException {
        if (!appSpe.hasTag(64, 78)) {
            throw new IOException("Bad tag : not an iso7816 CERTIFICATE_CONTENT_TEMPLATE");
        }
        ASN1Sequence content = ASN1Sequence.getInstance((Object)appSpe.getBaseUniversal(false, 16));
        Enumeration objs = content.getObjects();
        block9: while (objs.hasMoreElements()) {
            ASN1TaggedObject aSpe = ASN1TaggedObject.getInstance(objs.nextElement(), (int)64);
            switch (aSpe.getTagNo()) {
                case 41: {
                    this.setCertificateProfileIdentifier(aSpe);
                    continue block9;
                }
                case 2: {
                    this.setCertificationAuthorityReference(aSpe);
                    continue block9;
                }
                case 73: {
                    this.setPublicKey(PublicKeyDataObject.getInstance(aSpe.getBaseUniversal(false, 16)));
                    continue block9;
                }
                case 32: {
                    this.setCertificateHolderReference(aSpe);
                    continue block9;
                }
                case 76: {
                    this.setCertificateHolderAuthorization(new CertificateHolderAuthorization(aSpe));
                    continue block9;
                }
                case 37: {
                    this.setCertificateEffectiveDate(aSpe);
                    continue block9;
                }
                case 36: {
                    this.setCertificateExpirationDate(aSpe);
                    continue block9;
                }
            }
            this.certificateType = 0;
            throw new IOException("Not a valid iso7816 ASN1TaggedObject tag " + aSpe.getTagNo());
        }
    }

    public CertificateBody(ASN1TaggedObject certificateProfileIdentifier, CertificationAuthorityReference certificationAuthorityReference, PublicKeyDataObject publicKey, CertificateHolderReference certificateHolderReference, CertificateHolderAuthorization certificateHolderAuthorization, PackedDate certificateEffectiveDate, PackedDate certificateExpirationDate) {
        this.setCertificateProfileIdentifier(certificateProfileIdentifier);
        this.setCertificationAuthorityReference(EACTagged.create(2, certificationAuthorityReference.getEncoded()));
        this.setPublicKey(publicKey);
        this.setCertificateHolderReference(EACTagged.create(32, certificateHolderReference.getEncoded()));
        this.setCertificateHolderAuthorization(certificateHolderAuthorization);
        this.setCertificateEffectiveDate(EACTagged.create(37, certificateEffectiveDate.getEncoding()));
        this.setCertificateExpirationDate(EACTagged.create(36, certificateExpirationDate.getEncoding()));
    }

    private CertificateBody(ASN1TaggedObject obj) throws IOException {
        this.setIso7816CertificateBody(obj);
    }

    private ASN1Primitive profileToASN1Object() throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        v.add((ASN1Encodable)this.certificateProfileIdentifier);
        v.add((ASN1Encodable)this.certificationAuthorityReference);
        v.add((ASN1Encodable)EACTagged.create(73, this.publicKey));
        v.add((ASN1Encodable)this.certificateHolderReference);
        v.add((ASN1Encodable)this.certificateHolderAuthorization);
        v.add((ASN1Encodable)this.certificateEffectiveDate);
        v.add((ASN1Encodable)this.certificateExpirationDate);
        return EACTagged.create(78, (ASN1Sequence)new DERSequence(v));
    }

    private void setCertificateProfileIdentifier(ASN1TaggedObject certificateProfileIdentifier) throws IllegalArgumentException {
        if (certificateProfileIdentifier.hasTag(64, 41)) {
            this.certificateProfileIdentifier = certificateProfileIdentifier;
            this.certificateType |= 1;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.INTERCHANGE_PROFILE tag :" + certificateProfileIdentifier.getTagNo());
        }
    }

    private void setCertificateHolderReference(ASN1TaggedObject certificateHolderReference) throws IllegalArgumentException {
        if (certificateHolderReference.hasTag(64, 32)) {
            this.certificateHolderReference = certificateHolderReference;
            this.certificateType |= 8;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.CARDHOLDER_NAME tag");
        }
    }

    private void setCertificationAuthorityReference(ASN1TaggedObject certificationAuthorityReference) throws IllegalArgumentException {
        if (certificationAuthorityReference.hasTag(64, 2)) {
            this.certificationAuthorityReference = certificationAuthorityReference;
            this.certificateType |= 2;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.ISSUER_IDENTIFICATION_NUMBER tag");
        }
    }

    private void setPublicKey(PublicKeyDataObject publicKey) {
        this.publicKey = PublicKeyDataObject.getInstance((Object)publicKey);
        this.certificateType |= 4;
    }

    private ASN1Primitive requestToASN1Object() throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.certificateProfileIdentifier);
        v.add((ASN1Encodable)EACTagged.create(73, this.publicKey));
        v.add((ASN1Encodable)this.certificateHolderReference);
        return EACTagged.create(78, (ASN1Sequence)new DERSequence(v));
    }

    public ASN1Primitive toASN1Primitive() {
        try {
            if (this.certificateType == 127) {
                return this.profileToASN1Object();
            }
            if (this.certificateType == 13) {
                return this.requestToASN1Object();
            }
        }
        catch (IOException e) {
            return null;
        }
        return null;
    }

    public int getCertificateType() {
        return this.certificateType;
    }

    public static CertificateBody getInstance(Object obj) throws IOException {
        if (obj instanceof CertificateBody) {
            return (CertificateBody)((Object)obj);
        }
        if (obj != null) {
            return new CertificateBody(ASN1TaggedObject.getInstance((Object)obj, (int)64));
        }
        return null;
    }

    public PackedDate getCertificateEffectiveDate() {
        if ((this.certificateType & 0x20) == 32) {
            return new PackedDate(ASN1OctetString.getInstance((Object)this.certificateEffectiveDate.getBaseUniversal(false, 4)).getOctets());
        }
        return null;
    }

    private void setCertificateEffectiveDate(ASN1TaggedObject ced) throws IllegalArgumentException {
        if (ced.hasTag(64, 37)) {
            this.certificateEffectiveDate = ced;
            this.certificateType |= 0x20;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EFFECTIVE_DATE tag :" + ced.getTagNo());
        }
    }

    public PackedDate getCertificateExpirationDate() throws IOException {
        if ((this.certificateType & 0x40) == 64) {
            return new PackedDate(ASN1OctetString.getInstance((Object)this.certificateEffectiveDate.getBaseUniversal(false, 4)).getOctets());
        }
        throw new IOException("certificate Expiration Date not set");
    }

    private void setCertificateExpirationDate(ASN1TaggedObject ced) throws IllegalArgumentException {
        if (ced.hasTag(64, 36)) {
            this.certificateExpirationDate = ced;
            this.certificateType |= 0x40;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EXPIRATION_DATE tag");
        }
    }

    public CertificateHolderAuthorization getCertificateHolderAuthorization() throws IOException {
        if ((this.certificateType & 0x10) == 16) {
            return this.certificateHolderAuthorization;
        }
        throw new IOException("Certificate Holder Authorisation not set");
    }

    private void setCertificateHolderAuthorization(CertificateHolderAuthorization cha) {
        this.certificateHolderAuthorization = cha;
        this.certificateType |= 0x10;
    }

    public CertificateHolderReference getCertificateHolderReference() {
        return new CertificateHolderReference(ASN1OctetString.getInstance((Object)this.certificateHolderReference.getBaseUniversal(false, 4)).getOctets());
    }

    public ASN1TaggedObject getCertificateProfileIdentifier() {
        return this.certificateProfileIdentifier;
    }

    public CertificationAuthorityReference getCertificationAuthorityReference() throws IOException {
        if ((this.certificateType & 2) == 2) {
            return new CertificationAuthorityReference(ASN1OctetString.getInstance((Object)this.certificationAuthorityReference.getBaseUniversal(false, 4)).getOctets());
        }
        throw new IOException("Certification authority reference not set");
    }

    public PublicKeyDataObject getPublicKey() {
        return this.publicKey;
    }
}

