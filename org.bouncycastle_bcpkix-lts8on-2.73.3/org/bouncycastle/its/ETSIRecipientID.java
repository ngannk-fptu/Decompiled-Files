/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.its;

import org.bouncycastle.its.ETSIRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class ETSIRecipientID
implements Selector<ETSIRecipientInfo> {
    private final HashedId8 id;

    public ETSIRecipientID(byte[] id) {
        this(new HashedId8(id));
    }

    public ETSIRecipientID(HashedId8 id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ETSIRecipientID that = (ETSIRecipientID)o;
        return this.id != null ? this.id.equals((Object)that.id) : that.id == null;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public boolean match(ETSIRecipientInfo obj) {
        if (obj.getRecipientInfo().getChoice() == 2) {
            PKRecipientInfo objPkInfo = PKRecipientInfo.getInstance((Object)obj.getRecipientInfo().getRecipientInfo());
            return Arrays.areEqual((byte[])objPkInfo.getRecipientId().getHashBytes(), (byte[])this.id.getHashBytes());
        }
        return false;
    }

    public Object clone() {
        return this;
    }
}

