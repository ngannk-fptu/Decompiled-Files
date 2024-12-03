/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.whitelist.AccessManager
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.whitelist.AccessManager;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.core.AbstractWhitelist;
import com.atlassian.plugins.whitelist.core.WhitelistInput;
import com.atlassian.plugins.whitelist.core.matcher.SelfUrlMatcher;
import com.atlassian.sal.api.user.UserKey;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OutboundWhitelistImpl
extends AbstractWhitelist
implements OutboundWhitelist {
    private static final Logger logger = LoggerFactory.getLogger(OutboundWhitelistImpl.class);

    public OutboundWhitelistImpl(WhitelistOnOffSwitch whitelistOnOffSwitch, WhitelistService whitelistService, SelfUrlMatcher selfUrlMatcher, ReadOnlyApplicationLinkService applicationLinkService, AccessManager accessManager) {
        super(whitelistOnOffSwitch, whitelistService, selfUrlMatcher, applicationLinkService, accessManager);
    }

    @Override
    public boolean isAllowed(URI uri) {
        boolean allowed = super.isAllowed(uri);
        if (!allowed) {
            logger.info("No outbound rule found matching URI: " + uri);
        }
        return allowed;
    }

    public boolean isAllowed(URI uri, UserKey userKey) {
        boolean allowed = super.isAllowed(new WhitelistInput(uri, userKey, false));
        if (!allowed) {
            logger.info("No outbound rule found matching input: {" + uri + ", userKey provided: " + (userKey != null) + "}");
        }
        return allowed;
    }
}

