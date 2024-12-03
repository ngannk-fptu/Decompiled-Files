/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8
 */
package org.bouncycastle.its;

import org.bouncycastle.its.ETSIKeyWrapper;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class ETSIRecipientInfoBuilder {
    private final ETSIKeyWrapper keyWrapper;
    private final byte[] recipientID;

    public ETSIRecipientInfoBuilder(ETSIKeyWrapper keyWrapper, byte[] recipientID) {
        this.keyWrapper = keyWrapper;
        this.recipientID = recipientID;
    }

    public RecipientInfo build(byte[] secretKey) {
        try {
            return RecipientInfo.certRecipInfo((PKRecipientInfo)PKRecipientInfo.builder().setRecipientId(new HashedId8(this.recipientID)).setEncKey(this.keyWrapper.wrap(secretKey)).createPKRecipientInfo());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}

