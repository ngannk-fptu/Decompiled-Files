/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.io.MacOutputStream
 */
package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JceKEKRecipient;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceKEKAuthenticatedRecipient
extends JceKEKRecipient {
    public JceKEKAuthenticatedRecipient(SecretKey recipientKey) {
        super(recipientKey);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentMacAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        final Key secretKey = this.extractSecretKey(keyEncryptionAlgorithm, contentMacAlgorithm, encryptedContentEncryptionKey);
        final Mac dataMac = this.contentHelper.createContentMac(secretKey, contentMacAlgorithm);
        return new RecipientOperator(new MacCalculator(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentMacAlgorithm;
            }

            @Override
            public GenericKey getKey() {
                return new JceGenericKey(contentMacAlgorithm, secretKey);
            }

            @Override
            public OutputStream getOutputStream() {
                return new MacOutputStream(dataMac);
            }

            @Override
            public byte[] getMac() {
                return dataMac.doFinal();
            }
        });
    }
}

