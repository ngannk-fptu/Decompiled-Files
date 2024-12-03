/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.crmf.AttributeTypeAndValue
 *  org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers
 *  org.bouncycastle.asn1.crmf.CertReqMsg
 *  org.bouncycastle.asn1.crmf.CertTemplate
 *  org.bouncycastle.asn1.crmf.Controls
 *  org.bouncycastle.asn1.crmf.PKIArchiveOptions
 *  org.bouncycastle.asn1.crmf.PKMACValue
 *  org.bouncycastle.asn1.crmf.POPOSigningKey
 *  org.bouncycastle.asn1.crmf.POPOSigningKeyInput
 *  org.bouncycastle.asn1.crmf.ProofOfPossession
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cert.crmf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.Controls;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.POPOSigningKeyInput;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.crmf.AuthenticatorControl;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.CRMFUtil;
import org.bouncycastle.cert.crmf.Control;
import org.bouncycastle.cert.crmf.PKIArchiveControl;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.cert.crmf.PKMACValueVerifier;
import org.bouncycastle.cert.crmf.RegTokenControl;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;

public class CertificateRequestMessage
implements Encodable {
    public static final int popRaVerified = 0;
    public static final int popSigningKey = 1;
    public static final int popKeyEncipherment = 2;
    public static final int popKeyAgreement = 3;
    private final CertReqMsg certReqMsg;
    private final Controls controls;

    private static CertReqMsg parseBytes(byte[] encoding) throws IOException {
        try {
            return CertReqMsg.getInstance((Object)encoding);
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public CertificateRequestMessage(byte[] certReqMsg) throws IOException {
        this(CertificateRequestMessage.parseBytes(certReqMsg));
    }

    public CertificateRequestMessage(CertReqMsg certReqMsg) {
        this.certReqMsg = certReqMsg;
        this.controls = certReqMsg.getCertReq().getControls();
    }

    public CertReqMsg toASN1Structure() {
        return this.certReqMsg;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqMsg.getCertReq().getCertReqId();
    }

    public CertTemplate getCertTemplate() {
        return this.certReqMsg.getCertReq().getCertTemplate();
    }

    public boolean hasControls() {
        return this.controls != null;
    }

    public boolean hasControl(ASN1ObjectIdentifier type) {
        return this.findControl(type) != null;
    }

    public Control getControl(ASN1ObjectIdentifier type) {
        AttributeTypeAndValue found = this.findControl(type);
        if (found != null) {
            if (found.getType().equals((ASN1Primitive)CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions)) {
                return new PKIArchiveControl(PKIArchiveOptions.getInstance((Object)found.getValue()));
            }
            if (found.getType().equals((ASN1Primitive)CRMFObjectIdentifiers.id_regCtrl_regToken)) {
                return new RegTokenControl(ASN1UTF8String.getInstance((Object)found.getValue()));
            }
            if (found.getType().equals((ASN1Primitive)CRMFObjectIdentifiers.id_regCtrl_authenticator)) {
                return new AuthenticatorControl(ASN1UTF8String.getInstance((Object)found.getValue()));
            }
        }
        return null;
    }

    private AttributeTypeAndValue findControl(ASN1ObjectIdentifier type) {
        if (this.controls == null) {
            return null;
        }
        AttributeTypeAndValue[] tAndVs = this.controls.toAttributeTypeAndValueArray();
        AttributeTypeAndValue found = null;
        for (int i = 0; i != tAndVs.length; ++i) {
            if (!tAndVs[i].getType().equals((ASN1Primitive)type)) continue;
            found = tAndVs[i];
            break;
        }
        return found;
    }

    public boolean hasProofOfPossession() {
        return this.certReqMsg.getPop() != null;
    }

    public int getProofOfPossessionType() {
        return this.certReqMsg.getPop().getType();
    }

    public boolean hasSigningKeyProofOfPossessionWithPKMAC() {
        ProofOfPossession pop = this.certReqMsg.getPop();
        if (pop.getType() != 1) {
            return false;
        }
        POPOSigningKey popoSign = POPOSigningKey.getInstance((Object)pop.getObject());
        return popoSign.getPoposkInput().getPublicKeyMAC() != null;
    }

    public boolean isValidSigningKeyPOP(ContentVerifierProvider verifierProvider) throws CRMFException, IllegalStateException {
        ProofOfPossession pop = this.certReqMsg.getPop();
        if (pop.getType() != 1) {
            throw new IllegalStateException("not Signing Key type of proof of possession");
        }
        POPOSigningKey popoSign = POPOSigningKey.getInstance((Object)pop.getObject());
        if (popoSign.getPoposkInput() != null && popoSign.getPoposkInput().getPublicKeyMAC() != null) {
            throw new IllegalStateException("verification requires password check");
        }
        return this.verifySignature(verifierProvider, popoSign);
    }

    public boolean isValidSigningKeyPOP(ContentVerifierProvider verifierProvider, PKMACBuilder macBuilder, char[] password) throws CRMFException, IllegalStateException {
        ProofOfPossession pop = this.certReqMsg.getPop();
        if (pop.getType() != 1) {
            throw new IllegalStateException("not Signing Key type of proof of possession");
        }
        POPOSigningKey popoSign = POPOSigningKey.getInstance((Object)pop.getObject());
        if (popoSign.getPoposkInput() == null || popoSign.getPoposkInput().getSender() != null) {
            throw new IllegalStateException("no PKMAC present in proof of possession");
        }
        PKMACValueVerifier macVerifier = new PKMACValueVerifier(macBuilder);
        PKMACValue pkMAC = popoSign.getPoposkInput().getPublicKeyMAC();
        return macVerifier.isValid(pkMAC, password, this.getCertTemplate().getPublicKey()) && this.verifySignature(verifierProvider, popoSign);
    }

    private boolean verifySignature(ContentVerifierProvider verifierProvider, POPOSigningKey popoSign) throws CRMFException {
        ContentVerifier verifier;
        try {
            verifier = verifierProvider.get(popoSign.getAlgorithmIdentifier());
        }
        catch (OperatorCreationException e) {
            throw new CRMFException("unable to create verifier: " + e.getMessage(), e);
        }
        POPOSigningKeyInput obj = popoSign.getPoposkInput();
        if (obj == null) {
            obj = this.certReqMsg.getCertReq();
        }
        CRMFUtil.derEncodeToStream((ASN1Object)obj, verifier.getOutputStream());
        return verifier.verify(popoSign.getSignature().getOctets());
    }

    public byte[] getEncoded() throws IOException {
        return this.certReqMsg.getEncoded();
    }
}

