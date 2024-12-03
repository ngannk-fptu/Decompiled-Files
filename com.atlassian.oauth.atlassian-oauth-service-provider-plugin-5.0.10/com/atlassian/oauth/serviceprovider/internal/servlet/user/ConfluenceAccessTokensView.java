/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.actions.AbstractUserProfileAction
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletContext;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletValidation;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Map;
import java.util.Objects;

public class ConfluenceAccessTokensView
extends AbstractUserProfileAction {
    private final AccessTokensServletValidation accessTokensServletValidation;
    private final ApplicationProperties applicationProperties;
    private final Map<String, Object> context;

    public ConfluenceAccessTokensView(AccessTokensServletValidation accessTokensServletValidation, AccessTokensServletContext accessTokensServletContext, ApplicationProperties applicationProperties) {
        this.accessTokensServletValidation = Objects.requireNonNull(accessTokensServletValidation, "accessTokensServletValidation");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.context = accessTokensServletContext.getContext(this.getUsername());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return this.accessTokensServletValidation.validate(this.getCurrentRequest()).map(__ -> "success").orElse("login");
    }

    public Object getTokenItems() {
        return this.context.get("tokenItems");
    }

    public Object getDateFormat() {
        return this.context.get("dateFormat");
    }

    public Object getTimeFormat() {
        return this.context.get("timeFormat");
    }

    public Object getProductName() {
        return this.context.get("productName");
    }

    public Object getBaseUrl() {
        return this.context.get("baseUrl");
    }

    public Object getScopeDescriptions() {
        return this.context.get("scopeDescriptions");
    }

    public ApplicationProperties getApplicationProperties() {
        return this.applicationProperties;
    }
}

