/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.action.impl;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.stp.action.SupportActionFactory;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.action.impl.DetectIssuesAction;
import com.atlassian.troubleshooting.stp.action.impl.SystemInfoAction;
import com.atlassian.troubleshooting.stp.action.impl.TabsAction;
import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.hercules.SupportToolsHerculesScanAction;
import com.atlassian.troubleshooting.stp.request.CreateSupportRequestAction;
import com.atlassian.troubleshooting.stp.request.SupportRequestService;
import com.atlassian.troubleshooting.stp.request.SupportZipAction;
import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.security.UserService;
import com.atlassian.troubleshooting.stp.zip.SupportZipService;
import com.atlassian.troubleshooting.thready.DiagnosticSettingsAction;
import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSupportActionFactory
implements SupportActionFactory {
    private final Map<String, SupportToolsAction> supportToolsActionTreeMap = new TreeMap<String, SupportToolsAction>();
    private final Map<String, List<SupportToolsAction>> actionsByCategory = new LinkedHashMap<String, List<SupportToolsAction>>();

    @Autowired
    public DefaultSupportActionFactory(SupportApplicationInfo info, MailUtility mailUtility, LogScanService scanService, SupportRequestService supportRequestService, SupportZipService supportZipService, EventPublisher eventPublisher, LicenseService licenseService, UserService userService) {
        this.addAction(new TabsAction(info));
        if (DetectIssuesAction.isAvailable(info.getApplicationType())) {
            this.addAction(new DetectIssuesAction(info));
        }
        this.addAction(new SupportToolsHerculesScanAction(info, scanService, eventPublisher));
        this.addAction(new SupportZipAction(info, supportZipService));
        this.addAction(new DiagnosticSettingsAction(info));
        this.addAction(new CreateSupportRequestAction(licenseService, info, mailUtility, supportRequestService, eventPublisher, userService, Clock.systemUTC()));
        ApplicationType appType = info.getApplicationType();
        if (appType == ApplicationType.STASH || appType == ApplicationType.BITBUCKET) {
            this.addAction(new SystemInfoAction(info));
        }
    }

    private void addAction(SupportToolsAction action) {
        this.supportToolsActionTreeMap.put(action.getName(), action);
        if (action.getCategory() != null && action.getTitle() != null) {
            List actions = this.actionsByCategory.computeIfAbsent(action.getCategory(), k -> new ArrayList());
            actions.add(action);
        }
    }

    @Override
    public SupportToolsAction getAction(String name) {
        SupportToolsAction action = this.supportToolsActionTreeMap.get(Optional.ofNullable(name).orElse("tabs"));
        if (action != null) {
            return action.newInstance();
        }
        return this.supportToolsActionTreeMap.get("tabs").newInstance();
    }

    @Override
    public List<String> getActionCategories() {
        return new ArrayList<String>(this.actionsByCategory.keySet());
    }

    @Override
    public List<SupportToolsAction> getActionsByCategory(String category) {
        return this.actionsByCategory.get(category);
    }

    @Override
    public List<SupportToolsAction> getActions() {
        return (List)this.supportToolsActionTreeMap.values();
    }
}

