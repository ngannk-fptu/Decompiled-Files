/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.KEMRecipientInfo
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey
 *  org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport
 *  org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec
 *  org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.util.Arrays;

public abstract class JceKeyTransRecipient
implements KeyTransRecipient {
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected Map extraMappings;
    protected boolean validateKeySize;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKeyTransRecipient(PrivateKey recipientKey) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.extraMappings = new HashMap();
        this.validateKeySize = false;
        this.recipientKey = CMSUtils.cleanPrivateKey(recipientKey);
    }

    public JceKeyTransRecipient setProvider(Provider provider) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyTransRecipient setProvider(String providerName) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    public JceKeyTransRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKeyTransRecipient setMustProduceEncodableUnwrappedKey(boolean unwrappedKeyMustBeEncodable) {
        this.unwrappedKeyMustBeEncodable = unwrappedKeyMustBeEncodable;
        return this;
    }

    public JceKeyTransRecipient setContentProvider(String providerName) {
        this.contentHelper = CMSUtils.createContentHelper(providerName);
        return this;
    }

    public JceKeyTransRecipient setKeySizeValidation(boolean doValidate) {
        this.validateKeySize = doValidate;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedEncryptionKey) throws CMSException {
        if (CMSUtils.isGOST(keyEncryptionAlgorithm.getAlgorithm())) {
            try {
                GostR3410KeyTransport transport = GostR3410KeyTransport.getInstance((Object)encryptedEncryptionKey);
                GostR3410TransportParameters transParams = transport.getTransportParameters();
                KeyFactory keyFactory = this.helper.createKeyFactory(keyEncryptionAlgorithm.getAlgorithm());
                PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(transParams.getEphemeralPublicKey().getEncoded()));
                KeyAgreement agreement = this.helper.createKeyAgreement(keyEncryptionAlgorithm.getAlgorithm());
                agreement.init((Key)this.recipientKey, (AlgorithmParameterSpec)new UserKeyingMaterialSpec(transParams.getUkm()));
                agreement.doPhase(pubKey, true);
                SecretKey key = agreement.generateSecret(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId());
                Cipher keyCipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
                keyCipher.init(4, (Key)key, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(transParams.getEncryptionParamSet(), transParams.getUkm()));
                Gost2814789EncryptedKey encKey = transport.getSessionEncryptedKey();
                return keyCipher.unwrap(Arrays.concatenate((byte[])encKey.getEncryptedKey(), (byte[])encKey.getMacKey()), this.helper.getBaseCipherName(encryptedKeyAlgorithm.getAlgorithm()), 3);
            }
            catch (Exception e) {
                throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
            }
        }
        if (CMSObjectIdentifiers.id_ori_kem.equals((ASN1Primitive)keyEncryptionAlgorithm.getAlgorithm())) {
            KEMRecipientInfo gktParams = KEMRecipientInfo.getInstance((Object)keyEncryptionAlgorithm.getParameters());
            JceAsymmetricKeyUnwrapper unwrapper = this.helper.createAsymmetricUnwrapper(gktParams.getKem(), this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
            if (!this.extraMappings.isEmpty()) {
                for (ASN1ObjectIdentifier algorithm : this.extraMappings.keySet()) {
                    unwrapper.setAlgorithmMapping(algorithm, (String)this.extraMappings.get(algorithm));
                }
            }
            try {
                Key key = this.helper.getJceKey(encryptedKeyAlgorithm.getAlgorithm(), unwrapper.generateUnwrappedKey(encryptedKeyAlgorithm, encryptedEncryptionKey));
                if (this.validateKeySize) {
                    this.helper.keySizeCheck(encryptedKeyAlgorithm, key);
                }
                return key;
            }
            catch (OperatorException e) {
                throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
            }
        }
        JceAsymmetricKeyUnwrapper unwrapper = this.helper.createAsymmetricUnwrapper(keyEncryptionAlgorithm, this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
        if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier algorithm : this.extraMappings.keySet()) {
                unwrapper.setAlgorithmMapping(algorithm, (String)this.extraMappings.get(algorithm));
            }
        }
        try {
            Key key = this.helper.getJceKey(encryptedKeyAlgorithm.getAlgorithm(), unwrapper.generateUnwrappedKey(encryptedKeyAlgorithm, encryptedEncryptionKey));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(encryptedKeyAlgorithm, key);
            }
            return key;
        }
        catch (OperatorException e) {
            throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
        }
    }
}

