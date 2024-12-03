/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import org.springframework.util.Assert;
import org.springframework.vault.authentication.AppIdUserIdMechanism;

public class StaticUserId
implements AppIdUserIdMechanism {
    private final String userId;

    public StaticUserId(String userId) {
        Assert.hasText((String)userId, (String)"UserId must not be empty");
        this.userId = userId;
    }

    @Override
    public String createUserId() {
        return this.userId;
    }
}

