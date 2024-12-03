/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Ordering
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.ui.WhitelistBean;
import com.atlassian.plugins.whitelist.ui.WhitelistBeanService;
import com.atlassian.plugins.whitelist.ui.WhitelistRuleComparator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WhitelistBeanServiceImpl
implements WhitelistBeanService {
    private static final Logger logger = LoggerFactory.getLogger(WhitelistBeanServiceImpl.class);
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final WhitelistService whitelistService;
    private final WhitelistRuleComparator whitelistRuleComparator;
    private final ImmutableMap<WhitelistType, Function<WhitelistRule, WhitelistBean>> mappings = ImmutableMap.of((Object)WhitelistType.APPLICATION_LINK, this.createApplicationLinkRule(), (Object)WhitelistType.EXACT_URL, this.createUrlExpressionRule(), (Object)WhitelistType.WILDCARD_EXPRESSION, this.createUrlExpressionRule(), (Object)WhitelistType.REGULAR_EXPRESSION, this.createUrlExpressionRule(), (Object)WhitelistType.DOMAIN_NAME, this.createUrlExpressionRule());

    public WhitelistBeanServiceImpl(ReadOnlyApplicationLinkService applicationLinkService, WhitelistService whitelistService, WhitelistRuleComparator whitelistRuleComparator) {
        this.applicationLinkService = applicationLinkService;
        this.whitelistService = whitelistService;
        this.whitelistRuleComparator = whitelistRuleComparator;
    }

    @Override
    public WhitelistBean add(WhitelistBean whitelistBean) {
        return this.asBean(this.whitelistService.add(whitelistBean.asRule()));
    }

    @Override
    public WhitelistBean update(int id, WhitelistBean whitelistBean) {
        WhitelistRule existingWhitelistRule = this.whitelistService.get(id);
        Objects.requireNonNull(existingWhitelistRule, "Whitelist rule with id '" + id + "' not existing.");
        WhitelistRule updatedRule = whitelistBean.populateWith(existingWhitelistRule);
        WhitelistRule whitelistRule = this.whitelistService.update(updatedRule);
        return this.asBean(whitelistRule);
    }

    @Override
    public List<WhitelistBean> getAll() {
        return this.asBeans((List<WhitelistRule>)Ordering.from((Comparator)this.whitelistRuleComparator).immutableSortedCopy((Iterable)this.whitelistService.getAll()));
    }

    private List<WhitelistBean> asBeans(List<WhitelistRule> whitelistRules) {
        return ImmutableList.copyOf((Collection)whitelistRules.stream().map(this.asBean()).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private WhitelistBean asBean(WhitelistRule whitelistRule) {
        Objects.requireNonNull(whitelistRule, "whitelistRule");
        WhitelistType type = whitelistRule.getType();
        Function whitelistRuleDataWhitelistBeanFunction = (Function)this.mappings.get((Object)type);
        if (whitelistRuleDataWhitelistBeanFunction == null) {
            logger.warn("No mapping found for whitelist type '{}', ignoring data '{}'.", (Object)type, (Object)whitelistRule);
            return null;
        }
        return (WhitelistBean)whitelistRuleDataWhitelistBeanFunction.apply(whitelistRule);
    }

    private Function<WhitelistRule, WhitelistBean> asBean() {
        return input -> Optional.ofNullable(input).map(this::asBean).orElse(null);
    }

    private Function<WhitelistRule, WhitelistBean> createApplicationLinkRule() {
        return input -> {
            String applicationId = input.getExpression();
            ReadOnlyApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(new ApplicationId(applicationId));
            if (applicationLink == null) {
                logger.warn("Failed to resolved application link with application id '" + applicationId + "'; maybe it has been removed and the whitelist was not updated?");
                return null;
            }
            String applicationLinkName = applicationLink.getName();
            URI displayUrl = applicationLink.getDisplayUrl();
            URI iconUrl = applicationLink.getType().getIconUrl();
            return WhitelistBean.builder().id(input.getId()).expression(String.format("%s (%s)", applicationLinkName, displayUrl.toString())).type(WhitelistType.APPLICATION_LINK).iconUrl(iconUrl != null ? iconUrl.toString() : "").allowInbound(input.isAllowInbound()).allowAnonymousUser(!input.isAuthenticationRequired()).build();
        };
    }

    private Function<WhitelistRule, WhitelistBean> createUrlExpressionRule() {
        return input -> WhitelistBean.builder().from((WhitelistRule)input).iconUrl(this.generateIconUrl((WhitelistRule)input, "")).build();
    }

    private String generateIconUrl(WhitelistRule whitelistRule, String defaultIconUrl) {
        if (whitelistRule.getType() == WhitelistType.EXACT_URL || whitelistRule.getType() == WhitelistType.DOMAIN_NAME) {
            return this.generateFavIconUrl(whitelistRule.getExpression(), defaultIconUrl);
        }
        return defaultIconUrl;
    }

    private String generateFavIconUrl(String expression, String defaultIconUrl) {
        try {
            URI uri = new URI(expression);
            return uri.getScheme() + "://" + uri.getAuthority() + "/favicon.ico";
        }
        catch (URISyntaxException e) {
            return defaultIconUrl;
        }
    }
}

