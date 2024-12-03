/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.pats.utils;

import com.atlassian.pats.utils.AbstractProductHelper;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;

public class JiraHelper
extends AbstractProductHelper {
    public JiraHelper(ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        super(applicationProperties, i18nResolver);
    }

    @Override
    public String getTokensManageUrl() {
        return this.getBaseUrl() + "/secure/ViewProfile.jspa?selectedTab=com.atlassian.pats.pats-plugin:jira-user-personal-access-tokens";
    }

    @Override
    public String getProductName() {
        return this.i18nResolver.getText("personal.access.tokens.product.name.jira");
    }

    @Override
    public String getLogoResource() {
        return "/templates/email/token/logo/jira.png";
    }
}

