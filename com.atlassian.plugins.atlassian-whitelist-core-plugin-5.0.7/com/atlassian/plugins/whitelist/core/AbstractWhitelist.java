/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.whitelist.AccessManager
 *  com.atlassian.plugins.whitelist.Whitelist
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistService
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.whitelist.AccessManager;
import com.atlassian.plugins.whitelist.Whitelist;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.core.DefaultWhitelistRuleMatcher;
import com.atlassian.plugins.whitelist.core.WhitelistInput;
import com.atlassian.plugins.whitelist.core.matcher.SelfUrlMatcher;
import java.net.URI;
import java.util.Objects;
import java.util.function.Predicate;

abstract class AbstractWhitelist
implements Whitelist {
    private final WhitelistOnOffSwitch whitelistOnOffSwitch;
    private final WhitelistService whitelistService;
    private final SelfUrlMatcher selfUrlMatcher;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final AccessManager accessManager;

    public AbstractWhitelist(WhitelistOnOffSwitch whitelistOnOffSwitch, WhitelistService whitelistService, SelfUrlMatcher selfUrlMatcher, ReadOnlyApplicationLinkService applicationLinkService, AccessManager accessManager) {
        this.whitelistOnOffSwitch = whitelistOnOffSwitch;
        this.whitelistService = whitelistService;
        this.selfUrlMatcher = selfUrlMatcher;
        this.applicationLinkService = applicationLinkService;
        this.accessManager = accessManager;
    }

    public boolean isAllowed(URI uri) {
        boolean checkAnonymousAuth = Boolean.getBoolean("allowlist.anonymous.auth.enable");
        return this.isAllowed(new WhitelistInput(uri, null, !checkAnonymousAuth));
    }

    protected boolean isAllowed(WhitelistInput whitelistInput) {
        Objects.requireNonNull(whitelistInput.getUri(), "whitelistInput.uri cannot be null.");
        return this.isWhitelistDisabled() || this.selfUrlMatcher.test(whitelistInput.getUri()) || this.ruleMatchingUrlExists(whitelistInput);
    }

    private boolean isWhitelistDisabled() {
        return !this.whitelistOnOffSwitch.isEnabled();
    }

    private boolean ruleMatchingUrlExists(WhitelistInput whitelistInput) {
        return this.whitelistService.getAll().stream().anyMatch(this.matchesUrl(whitelistInput));
    }

    private Predicate<WhitelistRule> matchesUrl(WhitelistInput whitelistInput) {
        return input -> input != null && this.matches((WhitelistRule)input, whitelistInput);
    }

    protected boolean matches(WhitelistRule whitelistRule, WhitelistInput whitelistInput) {
        return new DefaultWhitelistRuleMatcher(this.applicationLinkService, whitelistRule, this.accessManager).test(whitelistInput);
    }
}

