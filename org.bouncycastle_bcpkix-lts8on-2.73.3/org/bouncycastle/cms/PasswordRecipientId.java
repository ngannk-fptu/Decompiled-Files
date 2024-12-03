/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.cms.PasswordRecipientInformation;
import org.bouncycastle.cms.RecipientId;

public class PasswordRecipientId
extends RecipientId {
    public PasswordRecipientId() {
        super(3);
    }

    public int hashCode() {
        return 3;
    }

    public boolean equals(Object o) {
        return o instanceof PasswordRecipientId;
    }

    @Override
    public Object clone() {
        return new PasswordRecipientId();
    }

    public boolean match(Object obj) {
        return obj instanceof PasswordRecipientInformation;
    }
}

