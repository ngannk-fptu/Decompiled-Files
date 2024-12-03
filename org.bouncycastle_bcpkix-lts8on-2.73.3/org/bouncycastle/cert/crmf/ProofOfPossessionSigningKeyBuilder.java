/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.crmf.CertRequest
 *  org.bouncycastle.asn1.crmf.PKMACValue
 *  org.bouncycastle.asn1.crmf.POPOSigningKey
 *  org.bouncycastle.asn1.crmf.POPOSigningKeyInput
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.POPOSigningKeyInput;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.CRMFUtil;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.cert.crmf.PKMACValueGenerator;
import org.bouncycastle.operator.ContentSigner;

public class ProofOfPossessionSigningKeyBuilder {
    private CertRequest certRequest;
    private SubjectPublicKeyInfo pubKeyInfo;
    private GeneralName name;
    private PKMACValue publicKeyMAC;

    public ProofOfPossessionSigningKeyBuilder(CertRequest certRequest) {
        this.certRequest = certRequest;
    }

    public ProofOfPossessionSigningKeyBuilder(SubjectPublicKeyInfo pubKeyInfo) {
        this.pubKeyInfo = pubKeyInfo;
    }

    public ProofOfPossessionSigningKeyBuilder setSender(GeneralName name) {
        this.name = name;
        return this;
    }

    public ProofOfPossessionSigningKeyBuilder setPublicKeyMac(PKMACBuilder builder, char[] password) throws CRMFException {
        this.publicKeyMAC = PKMACValueGenerator.generate(builder, password, this.pubKeyInfo);
        return this;
    }

    public POPOSigningKey build(ContentSigner signer) {
        POPOSigningKeyInput popo;
        if (this.name != null && this.publicKeyMAC != null) {
            throw new IllegalStateException("name and publicKeyMAC cannot both be set.");
        }
        if (this.certRequest != null) {
            popo = null;
            CRMFUtil.derEncodeToStream((ASN1Object)this.certRequest, signer.getOutputStream());
        } else if (this.name != null) {
            popo = new POPOSigningKeyInput(this.name, this.pubKeyInfo);
            CRMFUtil.derEncodeToStream((ASN1Object)popo, signer.getOutputStream());
        } else {
            popo = new POPOSigningKeyInput(this.publicKeyMAC, this.pubKeyInfo);
            CRMFUtil.derEncodeToStream((ASN1Object)popo, signer.getOutputStream());
        }
        return new POPOSigningKey(popo, signer.getAlgorithmIdentifier(), (ASN1BitString)new DERBitString(signer.getSignature()));
    }
}

