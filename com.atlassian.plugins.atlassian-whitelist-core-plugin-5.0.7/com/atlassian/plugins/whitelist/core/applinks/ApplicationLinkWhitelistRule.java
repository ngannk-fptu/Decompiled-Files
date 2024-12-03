/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core.applinks;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.google.common.base.Preconditions;

public class ApplicationLinkWhitelistRule
implements WhitelistRule {
    private final ReadOnlyApplicationLink applicationLink;
    private final boolean authenticationRequired;

    public ApplicationLinkWhitelistRule(ReadOnlyApplicationLink applicationLink, boolean authenticationRequired) {
        this.applicationLink = (ReadOnlyApplicationLink)Preconditions.checkNotNull((Object)applicationLink, (Object)"applicationLink");
        this.authenticationRequired = authenticationRequired;
    }

    public Integer getId() {
        return null;
    }

    public String getExpression() {
        return ApplicationLinkWhitelistRule.getExpressionFrom(this.applicationLink);
    }

    public WhitelistType getType() {
        return WhitelistType.APPLICATION_LINK;
    }

    public boolean isAllowInbound() {
        return false;
    }

    public boolean isAuthenticationRequired() {
        return this.authenticationRequired;
    }

    public static String getExpressionFrom(ApplicationId applicationId) {
        Preconditions.checkNotNull((Object)applicationId, (Object)"applicationId");
        return applicationId.get();
    }

    public static String getExpressionFrom(ReadOnlyApplicationLink applicationLink) {
        Preconditions.checkNotNull((Object)applicationLink, (Object)"applicationLink");
        return ApplicationLinkWhitelistRule.getExpressionFrom(applicationLink.getId());
    }
}

