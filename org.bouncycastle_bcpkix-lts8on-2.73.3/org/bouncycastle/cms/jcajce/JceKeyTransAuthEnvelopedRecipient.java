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
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputAEADDecryptor;

public class JceKeyTransAuthEnvelopedRecipient
extends JceKeyTransRecipient {
    public JceKeyTransAuthEnvelopedRecipient(PrivateKey recipientKey) {
        super(recipientKey);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        Key secretKey = this.extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, encryptedContentEncryptionKey);
        final Cipher dataCipher = this.contentHelper.createContentCipher(secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputAEADDecryptor(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override
            public InputStream getInputStream(InputStream dataIn) {
                return new CipherInputStream(dataIn, dataCipher);
            }

            @Override
            public OutputStream getAADStream() {
                return new AADStream(dataCipher);
            }

            @Override
            public byte[] getMAC() {
                return new byte[0];
            }
        });
    }

    private static class AADStream
    extends OutputStream {
        private Cipher cipher;
        private byte[] oneByte = new byte[1];

        public AADStream(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            this.cipher.updateAAD(buf, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            this.oneByte[0] = (byte)b;
            this.cipher.updateAAD(this.oneByte);
        }
    }
}

