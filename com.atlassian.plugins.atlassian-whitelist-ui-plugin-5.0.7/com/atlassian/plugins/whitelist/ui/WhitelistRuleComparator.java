/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Ordering
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nullable;

public class WhitelistRuleComparator
implements Comparator<WhitelistRule> {
    private final ReadOnlyApplicationLinkService applicationLinkService;

    public WhitelistRuleComparator(ReadOnlyApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    @Override
    public int compare(WhitelistRule o1, WhitelistRule o2) {
        Optional<Object> applink1 = Optional.empty();
        Optional<Object> applink2 = Optional.empty();
        if (o1.getType() == WhitelistType.APPLICATION_LINK) {
            applink1 = Optional.ofNullable(this.getApplicationLink(o1));
        }
        if (o2.getType() == WhitelistType.APPLICATION_LINK) {
            applink2 = Optional.ofNullable(this.getApplicationLink(o2));
        }
        return ComparisonChain.start().compare((Object)o1.getType(), (Object)o2.getType(), (Comparator)Ordering.natural().nullsLast()).compare(applink1.map(ReadOnlyApplicationLink::getType).map(TypeId::getTypeId).orElse(null), applink2.map(ReadOnlyApplicationLink::getType).map(TypeId::getTypeId).orElse(null), (Comparator)Ordering.natural().nullsLast()).compare(applink1.map(ReadOnlyApplicationLink::getName).orElse(null), applink2.map(ReadOnlyApplicationLink::getName).orElse(null), (Comparator)Ordering.natural().nullsLast()).compare((Object)o1.getExpression(), (Object)o2.getExpression(), String.CASE_INSENSITIVE_ORDER).result();
    }

    @Nullable
    private ReadOnlyApplicationLink getApplicationLink(WhitelistRule whitelistRule) {
        ApplicationId applicationId = new ApplicationId(whitelistRule.getExpression());
        return this.applicationLinkService.getApplicationLink(applicationId);
    }
}

