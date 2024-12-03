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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JcePasswordRecipient;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JcePasswordAuthenticatedRecipient
extends JcePasswordRecipient {
    public JcePasswordAuthenticatedRecipient(char[] password) {
        super(password);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentMacAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        final Key secretKey = this.extractSecretKey(keyEncryptionAlgorithm, contentMacAlgorithm, derivedKey, encryptedContentEncryptionKey);
        final Mac dataMac = this.helper.createContentMac(secretKey, contentMacAlgorithm);
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

