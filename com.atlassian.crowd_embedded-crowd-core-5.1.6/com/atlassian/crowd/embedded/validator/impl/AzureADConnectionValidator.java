/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.DirectoryValidator;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.embedded.validator.rule.DirectoryRuleBuilder;
import com.atlassian.crowd.embedded.validator.rule.RuleBuilder;
import com.atlassian.crowd.util.I18nHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AzureADConnectionValidator
extends DirectoryValidator {
    public static final String GRAPH_ENDPOINT_FIELD = "graphEndpoint";
    public static final String WEB_APP_ID_FIELD = "webAppId";
    public static final String WEB_APP_SECRET_FIELD = "webAppSecret";
    public static final String TENANT_ID_FIELD = "tenantId";

    public AzureADConnectionValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(TENANT_ID_FIELD).check(DirectoryRuleBuilder.valueOf("AZURE_AD_TENANT_ID"), RuleBuilder.matchesAny(StringUtils::isBlank, StringUtils::containsWhitespace)).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.tenant.id.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(GRAPH_ENDPOINT_FIELD).check(DirectoryRuleBuilder.valueOf("AZURE_AD_REGION"), RuleBuilder.eq("CUSTOM")).check(DirectoryRuleBuilder.valueOf("AZURE_AD_GRAPH_API_ENDPOINT"), RuleBuilder.not(RuleBuilder.isValidURI())).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.graph.api.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(WEB_APP_ID_FIELD).check(DirectoryRuleBuilder.valueOf("AZURE_AD_WEBAPP_CLIENT_ID"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.web.app.id.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(WEB_APP_SECRET_FIELD).check(DirectoryRuleBuilder.valueOf("AZURE_AD_WEBAPP_CLIENT_SECRET"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.web.app.secret.invalid")).build());
        return ruleListBuilder.build();
    }
}

