/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.whitelist.AccessManager
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.whitelist.AccessManager;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.core.WhitelistInput;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkMatcher;
import com.atlassian.plugins.whitelist.core.matcher.DomainNameMatcher;
import com.atlassian.plugins.whitelist.core.matcher.ExactUrlMatcher;
import com.atlassian.plugins.whitelist.core.matcher.RegularExpressionMatcher;
import com.atlassian.plugins.whitelist.core.matcher.WildcardExpressionMatcher;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultWhitelistRuleMatcher
implements Predicate<WhitelistInput> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultWhitelistRuleMatcher.class);
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final WhitelistRule whitelistRule;
    private final ImmutableMap<WhitelistType, Function<WhitelistRule, Predicate<URI>>> mappings = ImmutableMap.of((Object)WhitelistType.APPLICATION_LINK, this.createApplicationLinkRule(), (Object)WhitelistType.EXACT_URL, this.createExactUrlRule(), (Object)WhitelistType.WILDCARD_EXPRESSION, this.createWildcardExpressionRule(), (Object)WhitelistType.REGULAR_EXPRESSION, this.createRegularExpressionRule(), (Object)WhitelistType.DOMAIN_NAME, this.createDomainNameRule());
    private final AccessManager accessManager;

    public DefaultWhitelistRuleMatcher(ReadOnlyApplicationLinkService applicationLinkService, WhitelistRule whitelistRule, AccessManager accessManager) {
        this.applicationLinkService = Objects.requireNonNull(applicationLinkService);
        this.whitelistRule = Objects.requireNonNull(whitelistRule);
        this.accessManager = Objects.requireNonNull(accessManager);
    }

    @Override
    public boolean test(WhitelistInput whitelistInput) {
        Objects.requireNonNull(whitelistInput, "whitelistInput is null.");
        if (!whitelistInput.shouldSkipAuthCheck() && this.whitelistRule.isAuthenticationRequired() && !this.userCanAccessProduct(whitelistInput)) {
            return false;
        }
        WhitelistType type = this.whitelistRule.getType();
        Function mapping = (Function)this.mappings.get((Object)type);
        if (mapping == null) {
            logger.debug("No mapping found for whitelist type '{}', ignoring data '{}'.", (Object)type, (Object)this.whitelistRule);
            return false;
        }
        try {
            return ((Predicate)mapping.apply(this.whitelistRule)).test(whitelistInput.getUri());
        }
        catch (RuntimeException e) {
            logger.debug("Failed to match '{}' with whitelist rule '{}'", new Object[]{whitelistInput.getUri(), this.whitelistRule, e});
            return false;
        }
    }

    private boolean userCanAccessProduct(WhitelistInput whitelistInput) {
        return whitelistInput.getUserKey().map(arg_0 -> ((AccessManager)this.accessManager).canUserAccessProduct(arg_0)).orElse(false);
    }

    private Function<WhitelistRule, Predicate<URI>> createApplicationLinkRule() {
        return new Function<WhitelistRule, Predicate<URI>>(){

            @Override
            public Predicate<URI> apply(WhitelistRule input) {
                String applicationId = input.getExpression();
                ReadOnlyApplicationLink applicationLink = DefaultWhitelistRuleMatcher.this.applicationLinkService.getApplicationLink(new ApplicationId(applicationId));
                if (applicationLink == null) {
                    throw new IllegalArgumentException("Failed to resolved application link with application id '" + applicationId + "'; may be it has been removed and the whitelist was not updated?");
                }
                return this.createWhitelistRule(applicationLink);
            }

            private Predicate<URI> createWhitelistRule(ReadOnlyApplicationLink applicationLink) {
                return new ApplicationLinkMatcher(applicationLink);
            }
        };
    }

    private Function<WhitelistRule, Predicate<URI>> createExactUrlRule() {
        return input -> new ExactUrlMatcher(input.getExpression());
    }

    private Function<WhitelistRule, Predicate<URI>> createWildcardExpressionRule() {
        return input -> new WildcardExpressionMatcher(input.getExpression());
    }

    private Function<WhitelistRule, Predicate<URI>> createRegularExpressionRule() {
        return input -> new RegularExpressionMatcher(input.getExpression());
    }

    private Function<WhitelistRule, Predicate<URI>> createDomainNameRule() {
        return input -> new DomainNameMatcher(input.getExpression());
    }
}

