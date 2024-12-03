/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext
 *  org.bouncycastle.oer.its.ieee1609dot2.EncryptedData
 *  org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.SymmetricCiphertext
 */
package org.bouncycastle.its;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.its.ETSIEncryptedData;
import org.bouncycastle.its.ETSIRecipientInfoBuilder;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SymmetricCiphertext;

public class ETSIEncryptedDataBuilder {
    private final List<ETSIRecipientInfoBuilder> recipientInfoBuilders = new ArrayList<ETSIRecipientInfoBuilder>();

    public void addRecipientInfoBuilder(ETSIRecipientInfoBuilder recipientInfoBuilder) {
        this.recipientInfoBuilders.add(recipientInfoBuilder);
    }

    public ETSIEncryptedData build(ETSIDataEncryptor encryptor, byte[] content) {
        byte[] opaque = encryptor.encrypt(content);
        byte[] key = encryptor.getKey();
        byte[] nonce = encryptor.getNonce();
        SequenceOfRecipientInfo.Builder builder = SequenceOfRecipientInfo.builder();
        for (ETSIRecipientInfoBuilder recipientInfoBuilder : this.recipientInfoBuilders) {
            builder.addRecipients(new RecipientInfo[]{recipientInfoBuilder.build(key)});
        }
        return new ETSIEncryptedData(EncryptedData.builder().setRecipients(builder.createSequenceOfRecipientInfo()).setCiphertext(SymmetricCiphertext.aes128ccm((AesCcmCiphertext)AesCcmCiphertext.builder().setCcmCiphertext(opaque).setNonce(nonce).createAesCcmCiphertext())).createEncryptedData());
    }
}

