/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.BufferedBlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.StreamCipher
 *  org.bouncycastle.crypto.io.CipherInputStream
 *  org.bouncycastle.crypto.params.KeyParameter
 */
package org.bouncycastle.cms.bc;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.bc.BcPasswordRecipient;
import org.bouncycastle.cms.bc.EnvelopedDataHelper;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.InputDecryptor;

public class BcPasswordEnvelopedRecipient
extends BcPasswordRecipient {
    public BcPasswordEnvelopedRecipient(char[] password) {
        super(password);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        KeyParameter secretKey = this.extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, derivedKey, encryptedContentEncryptionKey);
        final Object dataCipher = EnvelopedDataHelper.createContentCipher(false, (CipherParameters)secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override
            public InputStream getInputStream(InputStream dataOut) {
                if (dataCipher instanceof BufferedBlockCipher) {
                    return new CipherInputStream(dataOut, (BufferedBlockCipher)dataCipher);
                }
                return new CipherInputStream(dataOut, (StreamCipher)dataCipher);
            }
        });
    }
}

