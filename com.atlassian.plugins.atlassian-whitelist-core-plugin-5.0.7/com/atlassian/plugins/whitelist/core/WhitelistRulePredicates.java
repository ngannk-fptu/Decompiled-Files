/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.google.common.base.Preconditions;
import java.util.function.Predicate;

public class WhitelistRulePredicates {
    public static Predicate<WhitelistRule> withId(int id) {
        return input -> {
            Integer whitelistRuleId = input != null ? input.getId() : null;
            return whitelistRuleId != null && id == whitelistRuleId;
        };
    }

    public static Predicate<WhitelistRule> withType(WhitelistType type) {
        Preconditions.checkNotNull((Object)type, (Object)"type");
        return input -> input != null && type == input.getType();
    }

    public static Predicate<WhitelistRule> withExpression(String expression) {
        Preconditions.checkNotNull((Object)expression, (Object)"expression");
        return input -> input != null && expression.equals(input.getExpression());
    }
}

