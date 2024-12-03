/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.eac.CVCertificate
 *  org.bouncycastle.asn1.eac.CertificateBody
 *  org.bouncycastle.asn1.eac.CertificateHolderAuthorization
 *  org.bouncycastle.asn1.eac.CertificateHolderReference
 *  org.bouncycastle.asn1.eac.CertificationAuthorityReference
 *  org.bouncycastle.asn1.eac.PackedDate
 *  org.bouncycastle.asn1.eac.PublicKeyDataObject
 */
package org.bouncycastle.eac;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.eac.CVCertificate;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;
import org.bouncycastle.asn1.eac.CertificateHolderReference;
import org.bouncycastle.asn1.eac.CertificationAuthorityReference;
import org.bouncycastle.asn1.eac.PackedDate;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.EACCertificateHolder;
import org.bouncycastle.eac.EACException;
import org.bouncycastle.eac.operator.EACSigner;

public class EACCertificateBuilder {
    private static final byte[] ZeroArray = new byte[]{0};
    private PublicKeyDataObject publicKey;
    private CertificateHolderAuthorization certificateHolderAuthorization;
    private PackedDate certificateEffectiveDate;
    private PackedDate certificateExpirationDate;
    private CertificateHolderReference certificateHolderReference;
    private CertificationAuthorityReference certificationAuthorityReference;

    public EACCertificateBuilder(CertificationAuthorityReference certificationAuthorityReference, PublicKeyDataObject publicKey, CertificateHolderReference certificateHolderReference, CertificateHolderAuthorization certificateHolderAuthorization, PackedDate certificateEffectiveDate, PackedDate certificateExpirationDate) {
        this.certificationAuthorityReference = certificationAuthorityReference;
        this.publicKey = publicKey;
        this.certificateHolderReference = certificateHolderReference;
        this.certificateHolderAuthorization = certificateHolderAuthorization;
        this.certificateEffectiveDate = certificateEffectiveDate;
        this.certificateExpirationDate = certificateExpirationDate;
    }

    private CertificateBody buildBody() {
        DERTaggedObject certificateProfileIdentifier = new DERTaggedObject(false, 64, 41, (ASN1Encodable)new DEROctetString(ZeroArray));
        CertificateBody body = new CertificateBody((ASN1TaggedObject)certificateProfileIdentifier, this.certificationAuthorityReference, this.publicKey, this.certificateHolderReference, this.certificateHolderAuthorization, this.certificateEffectiveDate, this.certificateExpirationDate);
        return body;
    }

    public EACCertificateHolder build(EACSigner signer) throws EACException {
        try {
            CertificateBody body = this.buildBody();
            OutputStream vOut = signer.getOutputStream();
            vOut.write(body.getEncoded("DER"));
            vOut.close();
            return new EACCertificateHolder(new CVCertificate(body, signer.getSignature()));
        }
        catch (Exception e) {
            throw new EACException("unable to process signature: " + e.getMessage(), e);
        }
    }
}

