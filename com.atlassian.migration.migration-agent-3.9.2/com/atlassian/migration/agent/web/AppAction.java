/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.migration.agent.web;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.FrontEndService;
import com.atlassian.migration.agent.service.InitialStateService;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
public class AppAction
extends ConfluenceActionSupport {
    private final FrontEndService frontEndService;
    private final InitialStateService initialStateService;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public AppAction(FrontEndService frontEndService, InitialStateService initialStateService, MigrationAgentConfiguration migrationAgentConfiguration, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.frontEndService = frontEndService;
        this.initialStateService = initialStateService;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public String execute() {
        return "success";
    }

    public FrontEndService getFrontEndService() {
        return this.frontEndService;
    }

    public String getDefaultPlanName() {
        ConfluenceUser user = this.getAuthenticatedUser();
        String userDomain = StringUtils.substringBeforeLast((String)StringUtils.substringAfterLast((String)user.getEmail(), (String)"@"), (String)".");
        return String.format("%s %s migration plan", user.getFullName(), StringUtils.capitalize((String)userDomain));
    }

    public String getAllEnabledFeatureFlags() {
        return String.join((CharSequence)", ", this.migrationDarkFeaturesManager.getAllEnabledFeatures());
    }

    @HtmlSafe
    public String getFrontendTargetCloudEnv() {
        return this.migrationAgentConfiguration.getFrontendTargetCloudEnv().replaceAll("[^a-z]", "");
    }

    @HtmlSafe
    public String getInitialState() {
        return Jsons.valueAsString(this.initialStateService.getInitialState());
    }
}

