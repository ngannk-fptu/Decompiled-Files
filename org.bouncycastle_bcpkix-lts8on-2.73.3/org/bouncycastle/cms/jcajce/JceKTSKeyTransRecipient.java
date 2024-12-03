/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.encoders.Hex
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.util.encoders.Hex;

public abstract class JceKTSKeyTransRecipient
implements KeyTransRecipient {
    private static final byte[] ANONYMOUS_SENDER = Hex.decode((String)"0c14416e6f6e796d6f75732053656e64657220202020");
    private final byte[] partyVInfo;
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected Map extraMappings;
    protected boolean validateKeySize;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKTSKeyTransRecipient(PrivateKey recipientKey, byte[] partyVInfo) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.extraMappings = new HashMap();
        this.validateKeySize = false;
        this.recipientKey = CMSUtils.cleanPrivateKey(recipientKey);
        this.partyVInfo = partyVInfo;
    }

    public JceKTSKeyTransRecipient setProvider(Provider provider) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKTSKeyTransRecipient setProvider(String providerName) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKTSKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    public JceKTSKeyTransRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKTSKeyTransRecipient setContentProvider(String providerName) {
        this.contentHelper = CMSUtils.createContentHelper(providerName);
        return this;
    }

    public JceKTSKeyTransRecipient setKeySizeValidation(boolean doValidate) {
        this.validateKeySize = doValidate;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedEncryptionKey) throws CMSException {
        JceKTSKeyUnwrapper unwrapper = this.helper.createAsymmetricUnwrapper(keyEncryptionAlgorithm, this.recipientKey, ANONYMOUS_SENDER, this.partyVInfo);
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

    protected static byte[] getPartyVInfoFromRID(KeyTransRecipientId recipientId) throws IOException {
        if (recipientId.getSerialNumber() != null) {
            return new IssuerAndSerialNumber(recipientId.getIssuer(), recipientId.getSerialNumber()).getEncoded("DER");
        }
        return new DEROctetString(recipientId.getSubjectKeyIdentifier()).getEncoded();
    }
}

