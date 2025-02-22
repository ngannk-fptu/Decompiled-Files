/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Mac;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JceKTSKeyTransRecipient;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceKTSKeyTransAuthenticatedRecipient
extends JceKTSKeyTransRecipient {
    public JceKTSKeyTransAuthenticatedRecipient(PrivateKey privateKey, KeyTransRecipientId keyTransRecipientId) throws IOException {
        super(privateKey, JceKTSKeyTransAuthenticatedRecipient.getPartyVInfoFromRID(keyTransRecipientId));
    }

    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, byte[] byArray) throws CMSException {
        final Key key = this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, byArray);
        final Mac mac = this.contentHelper.createContentMac(key, algorithmIdentifier2);
        return new RecipientOperator(new MacCalculator(){

            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }

            public GenericKey getKey() {
                return new JceGenericKey(algorithmIdentifier2, key);
            }

            public OutputStream getOutputStream() {
                return new MacOutputStream(mac);
            }

            public byte[] getMac() {
                return mac.doFinal();
            }
        });
    }
}

