/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms;

import org.bouncycastle.cms.KEKRecipientInformation;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.util.Arrays;

public class KEKRecipientId
extends RecipientId {
    private byte[] keyIdentifier;

    public KEKRecipientId(byte[] keyIdentifier) {
        super(1);
        this.keyIdentifier = keyIdentifier;
    }

    public int hashCode() {
        return Arrays.hashCode((byte[])this.keyIdentifier);
    }

    public boolean equals(Object o) {
        if (!(o instanceof KEKRecipientId)) {
            return false;
        }
        KEKRecipientId id = (KEKRecipientId)o;
        return Arrays.areEqual((byte[])this.keyIdentifier, (byte[])id.keyIdentifier);
    }

    public byte[] getKeyIdentifier() {
        return Arrays.clone((byte[])this.keyIdentifier);
    }

    @Override
    public Object clone() {
        return new KEKRecipientId(this.keyIdentifier);
    }

    public boolean match(Object obj) {
        if (obj instanceof byte[]) {
            return Arrays.areEqual((byte[])this.keyIdentifier, (byte[])((byte[])obj));
        }
        if (obj instanceof KEKRecipientInformation) {
            return ((KEKRecipientInformation)obj).getRID().equals(this);
        }
        return false;
    }
}

