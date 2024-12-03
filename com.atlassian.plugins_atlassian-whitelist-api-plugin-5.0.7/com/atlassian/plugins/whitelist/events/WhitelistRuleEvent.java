/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.whitelist.events;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public abstract class WhitelistRuleEvent {
    @Nonnull
    protected final WhitelistRule whitelistRule;

    public WhitelistRuleEvent(@Nonnull WhitelistRule whitelistRule) {
        this.whitelistRule = (WhitelistRule)Preconditions.checkNotNull((Object)whitelistRule, (Object)"whitelistRule");
    }

    @Nonnull
    public WhitelistRule getWhitelistRule() {
        return this.whitelistRule;
    }
}

