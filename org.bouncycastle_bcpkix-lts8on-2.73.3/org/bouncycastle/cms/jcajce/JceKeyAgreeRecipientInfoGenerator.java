/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier
 *  org.bouncycastle.asn1.cms.OriginatorPublicKey
 *  org.bouncycastle.asn1.cms.RecipientEncryptedKey
 *  org.bouncycastle.asn1.cms.RecipientKeyIdentifier
 *  org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec
 *  org.bouncycastle.jcajce.spec.MQVParameterSpec
 *  org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.KeyMaterialGenerator;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.RFC5753KeyMaterialGenerator;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Arrays;

public class JceKeyAgreeRecipientInfoGenerator
extends KeyAgreeRecipientInfoGenerator {
    private SecretKeySizeProvider keySizeProvider = new DefaultSecretKeySizeProvider();
    private List recipientIDs = new ArrayList();
    private List recipientKeys = new ArrayList();
    private PublicKey senderPublicKey;
    private PrivateKey senderPrivateKey;
    private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    private SecureRandom random;
    private KeyPair ephemeralKP;
    private byte[] userKeyingMaterial;
    private static KeyMaterialGenerator ecc_cms_Generator = new RFC5753KeyMaterialGenerator();

    public JceKeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier keyAgreementOID, PrivateKey senderPrivateKey, PublicKey senderPublicKey, ASN1ObjectIdentifier keyEncryptionOID) {
        super(keyAgreementOID, SubjectPublicKeyInfo.getInstance((Object)senderPublicKey.getEncoded()), keyEncryptionOID);
        this.senderPublicKey = senderPublicKey;
        this.senderPrivateKey = CMSUtils.cleanPrivateKey(senderPrivateKey);
    }

    public JceKeyAgreeRecipientInfoGenerator setUserKeyingMaterial(byte[] userKeyingMaterial) {
        this.userKeyingMaterial = Arrays.clone((byte[])userKeyingMaterial);
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator addRecipient(X509Certificate recipientCert) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(CMSUtils.getIssuerAndSerialNumber(recipientCert)));
        this.recipientKeys.add(recipientCert.getPublicKey());
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator addRecipient(byte[] subjectKeyID, PublicKey publicKey) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(new RecipientKeyIdentifier(subjectKeyID)));
        this.recipientKeys.add(publicKey);
        return this;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier keyAgreeAlgorithm, AlgorithmIdentifier keyEncryptionAlgorithm, GenericKey contentEncryptionKey) throws CMSException {
        if (this.recipientIDs.isEmpty()) {
            throw new CMSException("No recipients associated with generator - use addRecipient()");
        }
        this.init(keyAgreeAlgorithm.getAlgorithm());
        PrivateKey senderPrivateKey = this.senderPrivateKey;
        ASN1ObjectIdentifier keyAgreementOID = keyAgreeAlgorithm.getAlgorithm();
        ASN1EncodableVector recipientEncryptedKeys = new ASN1EncodableVector();
        for (int i = 0; i != this.recipientIDs.size(); ++i) {
            PublicKey recipientPublicKey = (PublicKey)this.recipientKeys.get(i);
            KeyAgreeRecipientIdentifier karId = (KeyAgreeRecipientIdentifier)this.recipientIDs.get(i);
            try {
                DEROctetString encryptedKey;
                MQVParameterSpec agreementParamSpec;
                ASN1ObjectIdentifier keyEncAlg = keyEncryptionAlgorithm.getAlgorithm();
                if (CMSUtils.isMQV(keyAgreementOID)) {
                    agreementParamSpec = new MQVParameterSpec(this.ephemeralKP, recipientPublicKey, this.userKeyingMaterial);
                } else if (CMSUtils.isEC(keyAgreementOID)) {
                    byte[] ukmKeyingMaterial = ecc_cms_Generator.generateKDFMaterial(keyEncryptionAlgorithm, this.keySizeProvider.getKeySize(keyEncAlg), this.userKeyingMaterial);
                    agreementParamSpec = new UserKeyingMaterialSpec(ukmKeyingMaterial);
                } else if (CMSUtils.isRFC2631(keyAgreementOID)) {
                    if (this.userKeyingMaterial != null) {
                        agreementParamSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                    } else {
                        if (keyAgreementOID.equals((ASN1Primitive)PKCSObjectIdentifiers.id_alg_SSDH)) {
                            throw new CMSException("User keying material must be set for static keys.");
                        }
                        agreementParamSpec = null;
                    }
                } else {
                    if (!CMSUtils.isGOST(keyAgreementOID)) throw new CMSException("Unknown key agreement algorithm: " + keyAgreementOID);
                    if (this.userKeyingMaterial == null) throw new CMSException("User keying material must be set for static keys.");
                    agreementParamSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                }
                KeyAgreement keyAgreement = this.helper.createKeyAgreement(keyAgreementOID);
                keyAgreement.init(senderPrivateKey, (AlgorithmParameterSpec)agreementParamSpec, this.random);
                keyAgreement.doPhase(recipientPublicKey, true);
                SecretKey keyEncryptionKey = keyAgreement.generateSecret(keyEncAlg.getId());
                Cipher keyEncryptionCipher = this.helper.createCipher(keyEncAlg);
                if (keyEncAlg.equals((ASN1Primitive)CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || keyEncAlg.equals((ASN1Primitive)CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    keyEncryptionCipher.init(3, (Key)keyEncryptionKey, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, this.userKeyingMaterial));
                    byte[] encKeyBytes = keyEncryptionCipher.wrap(this.helper.getJceKey(contentEncryptionKey));
                    Gost2814789EncryptedKey encKey = new Gost2814789EncryptedKey(Arrays.copyOfRange((byte[])encKeyBytes, (int)0, (int)(encKeyBytes.length - 4)), Arrays.copyOfRange((byte[])encKeyBytes, (int)(encKeyBytes.length - 4), (int)encKeyBytes.length));
                    encryptedKey = new DEROctetString(encKey.getEncoded("DER"));
                } else {
                    keyEncryptionCipher.init(3, (Key)keyEncryptionKey, this.random);
                    byte[] encryptedKeyBytes = keyEncryptionCipher.wrap(this.helper.getJceKey(contentEncryptionKey));
                    encryptedKey = new DEROctetString(encryptedKeyBytes);
                }
                recipientEncryptedKeys.add((ASN1Encodable)new RecipientEncryptedKey(karId, (ASN1OctetString)encryptedKey));
                continue;
            }
            catch (GeneralSecurityException e) {
                throw new CMSException("cannot perform agreement step: " + e.getMessage(), e);
            }
            catch (IOException e) {
                throw new CMSException("unable to encode wrapped key: " + e.getMessage(), e);
            }
        }
        return new DERSequence(recipientEncryptedKeys);
    }

    @Override
    protected byte[] getUserKeyingMaterial(AlgorithmIdentifier keyAgreeAlg) throws CMSException {
        this.init(keyAgreeAlg.getAlgorithm());
        if (this.ephemeralKP != null) {
            OriginatorPublicKey originatorPublicKey = this.createOriginatorPublicKey(SubjectPublicKeyInfo.getInstance((Object)this.ephemeralKP.getPublic().getEncoded()));
            try {
                if (this.userKeyingMaterial != null) {
                    return new MQVuserKeyingMaterial(originatorPublicKey, (ASN1OctetString)new DEROctetString(this.userKeyingMaterial)).getEncoded();
                }
                return new MQVuserKeyingMaterial(originatorPublicKey, null).getEncoded();
            }
            catch (IOException e) {
                throw new CMSException("unable to encode user keying material: " + e.getMessage(), e);
            }
        }
        return this.userKeyingMaterial;
    }

    private void init(ASN1ObjectIdentifier keyAgreementOID) throws CMSException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        if (CMSUtils.isMQV(keyAgreementOID) && this.ephemeralKP == null) {
            try {
                SubjectPublicKeyInfo pubInfo = SubjectPublicKeyInfo.getInstance((Object)this.senderPublicKey.getEncoded());
                AlgorithmParameters ecAlgParams = this.helper.createAlgorithmParameters(keyAgreementOID);
                ecAlgParams.init(pubInfo.getAlgorithm().getParameters().toASN1Primitive().getEncoded());
                KeyPairGenerator ephemKPG = this.helper.createKeyPairGenerator(keyAgreementOID);
                ephemKPG.initialize(ecAlgParams.getParameterSpec(AlgorithmParameterSpec.class), this.random);
                this.ephemeralKP = ephemKPG.generateKeyPair();
            }
            catch (Exception e) {
                throw new CMSException("cannot determine MQV ephemeral key pair parameters from public key: " + e, e);
            }
        }
    }
}

