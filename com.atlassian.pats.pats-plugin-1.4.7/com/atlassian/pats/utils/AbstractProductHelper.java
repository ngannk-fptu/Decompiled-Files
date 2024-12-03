/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.pats.utils;

import com.atlassian.pats.utils.ProductHelper;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;

public abstract class AbstractProductHelper
implements ProductHelper {
    protected final ApplicationProperties applicationProperties;
    protected final I18nResolver i18nResolver;

    protected AbstractProductHelper(ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public String getBaseUrl() {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
    }

    @Override
    public String getProductName() {
        return this.applicationProperties.getDisplayName();
    }

    @Override
    public String getAdminViewTemplateName() {
        return "Personal.Access.Tokens.Admin.Display.Generic";
    }
}

