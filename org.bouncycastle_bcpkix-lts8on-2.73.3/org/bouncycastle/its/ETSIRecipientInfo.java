/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext
 *  org.bouncycastle.oer.its.ieee1609dot2.EncryptedData
 *  org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey
 *  org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its;

import org.bouncycastle.its.operator.ETSIDataDecryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

public class ETSIRecipientInfo {
    private final RecipientInfo recipientInfo;
    private final EncryptedData encryptedData;

    public ETSIRecipientInfo(EncryptedData encryptedData, RecipientInfo recipientInfo) {
        this.recipientInfo = recipientInfo;
        this.encryptedData = encryptedData;
    }

    public ETSIRecipientInfo(RecipientInfo recipientInfo) {
        this.recipientInfo = recipientInfo;
        this.encryptedData = null;
    }

    public RecipientInfo getRecipientInfo() {
        return this.recipientInfo;
    }

    public EncryptedData getEncryptedData() {
        return this.encryptedData;
    }

    public byte[] getContent(ETSIDataDecryptor ddec) {
        if (0 != this.encryptedData.getCiphertext().getChoice()) {
            throw new IllegalArgumentException("Encrypted data is no AES 128 CCM");
        }
        AesCcmCiphertext act = AesCcmCiphertext.getInstance((Object)this.encryptedData.getCiphertext().getSymmetricCiphertext());
        PKRecipientInfo pkRecipientInfo = PKRecipientInfo.getInstance((Object)this.recipientInfo.getRecipientInfo());
        EncryptedDataEncryptionKey edec = pkRecipientInfo.getEncKey();
        EciesP256EncryptedKey key = EciesP256EncryptedKey.getInstance((Object)edec.getEncryptedDataEncryptionKey());
        EccP256CurvePoint point = EccP256CurvePoint.getInstance((Object)key.getV());
        byte[] wrappedKey = Arrays.concatenate((byte[])point.getEncodedPoint(), (byte[])key.getC().getOctets(), (byte[])key.getT().getOctets());
        return ddec.decrypt(wrappedKey, act.getCcmCiphertext().getContent(), act.getNonce().getOctets());
    }
}

