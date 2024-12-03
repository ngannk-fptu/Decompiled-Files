/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.util.Assert;
import org.springframework.vault.authentication.AppIdUserIdMechanism;

public class StaticUserId
implements AppIdUserIdMechanism {
    private final String userId;

    public StaticUserId(String userId) {
        Assert.hasText(userId, "UserId must not be empty");
        this.userId = userId;
    }

    @Override
    public String createUserId() {
        return this.userId;
    }
}

