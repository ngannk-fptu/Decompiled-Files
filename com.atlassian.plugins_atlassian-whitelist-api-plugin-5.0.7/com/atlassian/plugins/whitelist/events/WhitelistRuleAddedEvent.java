/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.events;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.events.WhitelistRuleEvent;

public class WhitelistRuleAddedEvent
extends WhitelistRuleEvent {
    public WhitelistRuleAddedEvent(WhitelistRule whitelistRule) {
        super(whitelistRule);
    }
}

