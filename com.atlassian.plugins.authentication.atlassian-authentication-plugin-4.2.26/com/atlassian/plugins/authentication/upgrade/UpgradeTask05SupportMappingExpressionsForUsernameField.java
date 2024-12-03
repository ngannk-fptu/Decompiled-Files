/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.upgrade;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.impl.config.PluginSettingsUtil;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpression;
import com.atlassian.plugins.authentication.upgrade.LegacySettingsUtil;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.base.Strings;
import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class UpgradeTask05SupportMappingExpressionsForUsernameField
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UpgradeTask05SupportMappingExpressionsForUsernameField.class);
    private static final String DEFAULT_SAML_USERNAME_ATTRIBUTE_EXPRESSION = "${NameID}";
    private static final String DEFAULT_OIDC_USERNAME_CLAIM_EXPRESSION = "${sub}";
    private final PluginSettingsFactory pluginSettings;
    private final LegacySettingsUtil legacySettingsUtil;

    @Inject
    public UpgradeTask05SupportMappingExpressionsForUsernameField(PluginSettingsFactory pluginSettings, LegacySettingsUtil legacySettingsUtil) {
        this.pluginSettings = pluginSettings;
        this.legacySettingsUtil = legacySettingsUtil;
    }

    public int getBuildNumber() {
        return 5;
    }

    public String getShortDescription() {
        return "Migrate values of Username attribute/claim (SAML/OIDC) field to meet new non-empty requirement and mapping expression compatibility.";
    }

    public Collection<Message> doUpgrade() {
        PluginSettings settings = this.pluginSettings.createGlobalSettings();
        SsoType ssoType = this.legacySettingsUtil.getLegacySsoType(settings);
        if (ssoType == SsoType.SAML) {
            PluginSettingsUtil.setStringValue(settings, "username-attribute", DEFAULT_SAML_USERNAME_ATTRIBUTE_EXPRESSION);
            log.info("Upgraded SSO SAML configuraton with new username attribute field [{}]", (Object)DEFAULT_SAML_USERNAME_ATTRIBUTE_EXPRESSION);
        } else if (ssoType == SsoType.OIDC) {
            String previousUsernameClaim = PluginSettingsUtil.getStringValue(settings, "username-claim");
            String updatedUsernameClaim = this.evaluateNewOidcUsernameClaimValue(previousUsernameClaim);
            PluginSettingsUtil.setStringValue(settings, "username-claim", updatedUsernameClaim);
            log.info("Upgraded SSO OIDC username claim from [{}] to [{}}]", (Object)previousUsernameClaim, (Object)updatedUsernameClaim);
        }
        return Collections.emptyList();
    }

    public String getPluginKey() {
        return "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    }

    private String evaluateNewOidcUsernameClaimValue(String usernameClaim) {
        if (Strings.isNullOrEmpty((String)usernameClaim)) {
            return DEFAULT_OIDC_USERNAME_CLAIM_EXPRESSION;
        }
        return MappingExpression.toMappingExpressionVariable(usernameClaim);
    }
}

