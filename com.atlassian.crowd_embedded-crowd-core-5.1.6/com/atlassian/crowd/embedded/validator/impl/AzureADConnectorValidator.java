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

public class AzureADConnectorValidator
extends DirectoryValidator {
    public static final String GRAPH_ENDPOINT_FIELD = "graphEndpoint";
    public static final String AUTHORITY_ENDPOINT_FIELD = "authorityEndpoint";
    public static final String WEB_APP_ID_FIELD = "webAppId";
    public static final String WEB_APP_SECRET_FIELD = "webAppSecret";
    public static final String NATIVE_APP_ID = "nativeAppId";
    public static final String TENANT_ID_FIELD = "tenantId";
    public static final String READ_TIMEOUT_IN_SEC = "readTimeoutInSec";
    public static final String CONNECTION_TIMEOUT_IN_SEC = "connectionTimeoutInSec";

    public AzureADConnectorValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(AUTHORITY_ENDPOINT_FIELD).check(DirectoryRuleBuilder.valueOf("AZURE_AD_REGION"), RuleBuilder.eq("CUSTOM")).check(DirectoryRuleBuilder.valueOf("AZURE_AD_AUTHORITY_API_ENDPOINT"), RuleBuilder.not(RuleBuilder.isValidURI())).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.authority.api.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(NATIVE_APP_ID).check(DirectoryRuleBuilder.valueOf("AZURE_AD_NATIVE_AP_IDD"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.native.app.id.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(READ_TIMEOUT_IN_SEC).check(DirectoryRuleBuilder.valueOf("ldap.read.timeout"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(0L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.read.timeout.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(CONNECTION_TIMEOUT_IN_SEC).check(DirectoryRuleBuilder.valueOf("ldap.connection.timeout"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(0L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.azure.ad.connection.timeout.invalid")).build());
        return ruleListBuilder.build();
    }
}

