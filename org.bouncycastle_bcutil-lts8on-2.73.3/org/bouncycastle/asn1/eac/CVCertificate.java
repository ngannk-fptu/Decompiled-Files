/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1ParsingException
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;
import org.bouncycastle.asn1.eac.CertificateHolderReference;
import org.bouncycastle.asn1.eac.CertificationAuthorityReference;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.asn1.eac.Flags;
import org.bouncycastle.asn1.eac.PackedDate;
import org.bouncycastle.util.Arrays;

public class CVCertificate
extends ASN1Object {
    private CertificateBody certificateBody;
    private byte[] signature;
    private int valid;
    private static int bodyValid = 1;
    private static int signValid = 2;

    private void setPrivateData(ASN1TaggedObject appSpe) throws IOException {
        this.valid = 0;
        if (appSpe.hasTag(64, 33)) {
            ASN1Sequence content = ASN1Sequence.getInstance((Object)appSpe.getBaseUniversal(false, 16));
            Enumeration en = content.getObjects();
            while (en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof ASN1TaggedObject) {
                    ASN1TaggedObject aSpe = ASN1TaggedObject.getInstance(obj, (int)64);
                    switch (aSpe.getTagNo()) {
                        case 78: {
                            this.certificateBody = CertificateBody.getInstance(aSpe);
                            this.valid |= bodyValid;
                            break;
                        }
                        case 55: {
                            this.signature = ASN1OctetString.getInstance((Object)aSpe.getBaseUniversal(false, 4)).getOctets();
                            this.valid |= signValid;
                            break;
                        }
                        default: {
                            throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + aSpe.getTagNo());
                        }
                    }
                    continue;
                }
                throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
            }
        } else {
            throw new IOException("not a CARDHOLDER_CERTIFICATE :" + appSpe.getTagNo());
        }
        if (this.valid != (signValid | bodyValid)) {
            throw new IOException("invalid CARDHOLDER_CERTIFICATE :" + appSpe.getTagNo());
        }
    }

    public CVCertificate(ASN1InputStream aIS) throws IOException {
        this.initFrom(aIS);
    }

    private void initFrom(ASN1InputStream aIS) throws IOException {
        ASN1Primitive obj;
        while ((obj = aIS.readObject()) != null) {
            if (obj instanceof ASN1TaggedObject) {
                this.setPrivateData((ASN1TaggedObject)obj);
                continue;
            }
            throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
        }
    }

    private CVCertificate(ASN1TaggedObject appSpe) throws IOException {
        this.setPrivateData(appSpe);
    }

    public CVCertificate(CertificateBody body, byte[] signature) throws IOException {
        this.certificateBody = body;
        this.signature = Arrays.clone((byte[])signature);
        this.valid |= bodyValid;
        this.valid |= signValid;
    }

    public static CVCertificate getInstance(Object obj) {
        if (obj instanceof CVCertificate) {
            return (CVCertificate)((Object)obj);
        }
        if (obj != null) {
            try {
                return new CVCertificate(ASN1TaggedObject.getInstance((Object)obj, (int)64));
            }
            catch (IOException e) {
                throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), (Throwable)e);
            }
        }
        return null;
    }

    public byte[] getSignature() {
        return Arrays.clone((byte[])this.signature);
    }

    public CertificateBody getBody() {
        return this.certificateBody;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.certificateBody);
        v.add((ASN1Encodable)EACTagged.create(55, this.signature));
        return EACTagged.create(33, (ASN1Sequence)new DERSequence(v));
    }

    public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
        CertificateHolderAuthorization cha = this.certificateBody.getCertificateHolderAuthorization();
        return cha.getOid();
    }

    public PackedDate getEffectiveDate() throws IOException {
        return this.certificateBody.getCertificateEffectiveDate();
    }

    public int getCertificateType() {
        return this.certificateBody.getCertificateType();
    }

    public PackedDate getExpirationDate() throws IOException {
        return this.certificateBody.getCertificateExpirationDate();
    }

    public int getRole() throws IOException {
        CertificateHolderAuthorization cha = this.certificateBody.getCertificateHolderAuthorization();
        return cha.getAccessRights();
    }

    public CertificationAuthorityReference getAuthorityReference() throws IOException {
        return this.certificateBody.getCertificationAuthorityReference();
    }

    public CertificateHolderReference getHolderReference() throws IOException {
        return this.certificateBody.getCertificateHolderReference();
    }

    public int getHolderAuthorizationRole() throws IOException {
        int rights = this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
        return rights & 0xC0;
    }

    public Flags getHolderAuthorizationRights() throws IOException {
        return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 0x1F);
    }
}

