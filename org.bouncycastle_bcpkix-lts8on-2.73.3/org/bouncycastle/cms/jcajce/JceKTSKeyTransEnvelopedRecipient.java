/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.io.CipherInputStream
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JceKTSKeyTransRecipient;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JceKTSKeyTransEnvelopedRecipient
extends JceKTSKeyTransRecipient {
    public JceKTSKeyTransEnvelopedRecipient(PrivateKey recipientKey, KeyTransRecipientId recipientId) throws IOException {
        super(recipientKey, JceKTSKeyTransEnvelopedRecipient.getPartyVInfoFromRID(recipientId));
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        Key secretKey = this.extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, encryptedContentEncryptionKey);
        final Cipher dataCipher = this.contentHelper.createContentCipher(secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override
            public InputStream getInputStream(InputStream dataIn) {
                return new CipherInputStream(dataIn, dataCipher);
            }
        });
    }
}

