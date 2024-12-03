/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.plugins.mobile.dto.LoginInfoDto;
import com.atlassian.confluence.plugins.mobile.dto.ServerInfoDto;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationStatus;
import com.atlassian.confluence.plugins.mobile.service.MobileFeatureManager;
import com.atlassian.confluence.plugins.mobile.service.MobileInfoService;
import com.atlassian.confluence.plugins.mobile.service.PushNotificationService;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileInfoServiceImpl
implements MobileInfoService {
    private final SettingsManager settingsManager;
    private final PushNotificationService pushNotificationService;
    private final MobileFeatureManager mobileFeatureManager;

    @Autowired
    public MobileInfoServiceImpl(@ComponentImport SettingsManager settingsManager, PushNotificationService pushNotificationService, MobileFeatureManager mobileFeatureManager) {
        this.settingsManager = settingsManager;
        this.pushNotificationService = pushNotificationService;
        this.mobileFeatureManager = mobileFeatureManager;
    }

    @Override
    public LoginInfoDto getLoginInfo() {
        Settings settings = this.settingsManager.getGlobalSettings();
        return new LoginInfoDto(settings.getSiteTitle(), settings.getBaseUrl());
    }

    @Override
    public ServerInfoDto getServerInfo() {
        return ServerInfoDto.builder().pushNotificationEnabled(this.pushNotificationService.getStatus() == PushNotificationStatus.ENABLED).sessionTimeoutFixEnabled(this.mobileFeatureManager.isStatusCodeRewritingEnabled()).build();
    }
}

