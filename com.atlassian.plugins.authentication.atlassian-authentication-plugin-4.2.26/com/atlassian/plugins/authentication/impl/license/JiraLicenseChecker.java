/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.jira.application.ApplicationRole
 *  com.atlassian.jira.application.ApplicationRoleManager
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.license;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.application.ApplicationRole;
import com.atlassian.jira.application.ApplicationRoleManager;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.plugins.authentication.impl.license.ProductLicenseChecker;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JiraComponent
public class JiraLicenseChecker
implements ProductLicenseChecker {
    private static final Logger log = LoggerFactory.getLogger(JiraLicenseChecker.class);
    @VisibleForTesting
    static final String JIRA_CORE_KEY = "jira-core";
    private final CrowdService crowdService;
    private final ApplicationRoleManager applicationRoleManager;

    @Inject
    public JiraLicenseChecker(@JiraImport CrowdService crowdService, @JiraImport ApplicationRoleManager applicationRoleManager) {
        this.crowdService = crowdService;
        this.applicationRoleManager = applicationRoleManager;
    }

    @Override
    public boolean areSlotsAvailable(Set<String> groupNames) {
        Set<ApplicationRole> rolesUsed = groupNames.stream().flatMap(this::toRolesForGroups).collect(Collectors.toSet());
        Set<ApplicationRole> licensesUsed = this.filterOutJiraCore(rolesUsed);
        for (ApplicationRole applicationRole : licensesUsed) {
            if (this.applicationRoleManager.hasSeatsAvailable(applicationRole.getKey(), 1)) continue;
            log.debug("No seats available for application role [{}]", (Object)applicationRole.getKey());
            return false;
        }
        return true;
    }

    private Set<ApplicationRole> filterOutJiraCore(Set<ApplicationRole> roles) {
        if (roles.size() > 1) {
            return roles.stream().filter(role -> !JIRA_CORE_KEY.equals(role.getKey().toString())).collect(Collectors.toSet());
        }
        return roles;
    }

    private Stream<ApplicationRole> toRolesForGroups(String groupName) {
        Group group = this.crowdService.getGroup(groupName);
        if (group == null) {
            return Stream.empty();
        }
        return this.applicationRoleManager.getRolesForGroup(group).stream();
    }
}

