/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.whitelist.AccessManager
 *  com.atlassian.plugins.whitelist.InboundWhitelist
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.whitelist.AccessManager;
import com.atlassian.plugins.whitelist.InboundWhitelist;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.core.AbstractWhitelist;
import com.atlassian.plugins.whitelist.core.WhitelistInput;
import com.atlassian.plugins.whitelist.core.matcher.SelfUrlMatcher;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InboundWhitelistImpl
extends AbstractWhitelist
implements InboundWhitelist {
    private static final Logger logger = LoggerFactory.getLogger(InboundWhitelistImpl.class);

    public InboundWhitelistImpl(WhitelistOnOffSwitch whitelistOnOffSwitch, WhitelistService whitelistService, SelfUrlMatcher selfUrlMatcher, ReadOnlyApplicationLinkService applicationLinkService, AccessManager accessManager) {
        super(whitelistOnOffSwitch, whitelistService, selfUrlMatcher, applicationLinkService, accessManager);
    }

    @Override
    public boolean isAllowed(URI uri) {
        boolean allowed = super.isAllowed(uri);
        if (!allowed) {
            logger.info("No inbound rule found matching URI: " + uri);
        }
        return allowed;
    }

    @Override
    protected boolean matches(WhitelistRule whitelistRule, WhitelistInput whitelistInput) {
        return whitelistRule.isAllowInbound() && super.matches(whitelistRule, whitelistInput);
    }
}

