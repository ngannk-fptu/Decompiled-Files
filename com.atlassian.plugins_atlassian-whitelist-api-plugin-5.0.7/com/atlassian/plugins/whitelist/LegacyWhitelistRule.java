/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.Immutable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.google.common.base.Preconditions;
import javax.annotation.concurrent.Immutable;
import org.apache.commons.lang3.StringUtils;

@Immutable
@PublicApi
public final class LegacyWhitelistRule
implements WhitelistRule {
    private final String expression;
    private final WhitelistType type;

    public LegacyWhitelistRule(String acceptRule) {
        Preconditions.checkNotNull((Object)acceptRule, (Object)"acceptRule");
        if (acceptRule.startsWith("=")) {
            this.expression = StringUtils.removeStart((String)acceptRule, (String)"=");
            this.type = WhitelistType.EXACT_URL;
        } else if (acceptRule.startsWith("/")) {
            this.expression = StringUtils.removeStart((String)acceptRule, (String)"/");
            this.type = WhitelistType.REGULAR_EXPRESSION;
        } else {
            this.expression = acceptRule;
            this.type = WhitelistType.WILDCARD_EXPRESSION;
        }
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public WhitelistType getType() {
        return this.type;
    }

    @Override
    public boolean isAllowInbound() {
        return false;
    }

    @Override
    public boolean isAuthenticationRequired() {
        return false;
    }

    public String toString() {
        return "LegacyWhitelistRule{expression='" + this.expression + '\'' + ", type=" + (Object)((Object)this.type) + '}';
    }
}

