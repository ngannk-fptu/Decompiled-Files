/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CMPObjectIdentifiers
 *  org.bouncycastle.asn1.cmp.PKIBody
 *  org.bouncycastle.asn1.cmp.PKIHeader
 *  org.bouncycastle.asn1.cmp.PKIMessage
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.GeneralPKIMessage;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.util.Arrays;

public class ProtectedPKIMessage {
    private PKIMessage pkiMessage;

    public ProtectedPKIMessage(GeneralPKIMessage pkiMessage) {
        if (!pkiMessage.hasProtection()) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pkiMessage.toASN1Structure();
    }

    ProtectedPKIMessage(PKIMessage pkiMessage) {
        if (pkiMessage.getHeader().getProtectionAlg() == null) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pkiMessage;
    }

    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }

    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }

    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }

    public boolean hasPasswordBasedMacProtection() {
        ASN1ObjectIdentifier procAlg = this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm();
        return procAlg.equals((ASN1Primitive)CMPObjectIdentifiers.passwordBasedMac);
    }

    public AlgorithmIdentifier getProtectionAlgorithm() {
        return this.pkiMessage.getHeader().getProtectionAlg();
    }

    public X509CertificateHolder[] getCertificates() {
        CMPCertificate[] certs = this.pkiMessage.getExtraCerts();
        if (certs == null) {
            return new X509CertificateHolder[0];
        }
        X509CertificateHolder[] res = new X509CertificateHolder[certs.length];
        for (int i = 0; i != certs.length; ++i) {
            res[i] = new X509CertificateHolder(certs[i].getX509v3PKCert());
        }
        return res;
    }

    public boolean verify(ContentVerifierProvider verifierProvider) throws CMPException {
        try {
            ContentVerifier verifier = verifierProvider.get(this.pkiMessage.getHeader().getProtectionAlg());
            return this.verifySignature(this.pkiMessage.getProtection().getBytes(), verifier);
        }
        catch (Exception e) {
            throw new CMPException("unable to verify signature: " + e.getMessage(), e);
        }
    }

    public boolean verify(PBEMacCalculatorProvider pbeMacCalculatorProvider, char[] password) throws CMPException {
        try {
            MacCalculator calculator = pbeMacCalculatorProvider.get(this.pkiMessage.getHeader().getProtectionAlg(), password);
            OutputStream macOut = calculator.getOutputStream();
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add((ASN1Encodable)this.pkiMessage.getHeader());
            v.add((ASN1Encodable)this.pkiMessage.getBody());
            macOut.write(new DERSequence(v).getEncoded("DER"));
            macOut.close();
            return Arrays.constantTimeAreEqual((byte[])calculator.getMac(), (byte[])this.pkiMessage.getProtection().getBytes());
        }
        catch (Exception e) {
            throw new CMPException("unable to verify MAC: " + e.getMessage(), e);
        }
    }

    private boolean verifySignature(byte[] signature, ContentVerifier verifier) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)this.pkiMessage.getHeader());
        v.add((ASN1Encodable)this.pkiMessage.getBody());
        OutputStream sOut = verifier.getOutputStream();
        sOut.write(new DERSequence(v).getEncoded("DER"));
        sOut.close();
        return verifier.verify(signature);
    }
}

