/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.events;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.events.WhitelistRuleEvent;

public class WhitelistRuleRemovedEvent
extends WhitelistRuleEvent {
    public WhitelistRuleRemovedEvent(WhitelistRule whitelistRule) {
        super(whitelistRule);
    }
}

