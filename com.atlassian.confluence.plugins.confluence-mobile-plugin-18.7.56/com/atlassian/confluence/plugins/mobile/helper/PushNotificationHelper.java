/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.httpclient.api.Request$Builder
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.helper;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.mobile.activeobject.entity.PushNotificationAO;
import com.atlassian.confluence.plugins.mobile.helper.NotificationHelper;
import com.atlassian.confluence.plugins.mobile.notification.NotificationCategory;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationSetting;
import com.atlassian.confluence.plugins.mobile.remoteservice.MobileHttpClient;
import com.atlassian.httpclient.api.Request;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicensePair;
import com.atlassian.mywork.model.Notification;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationHelper {
    private Logger LOG = LoggerFactory.getLogger(PushNotificationHelper.class);
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final MobileHttpClient mobileHttpClient;
    private final ApplicationConfiguration configuration;

    @Autowired
    public PushNotificationHelper(MobileHttpClient mobileHttpClient, @ComponentImport ApplicationConfiguration configuration) {
        this.mobileHttpClient = mobileHttpClient;
        this.configuration = configuration;
    }

    public boolean isPushAllowed(PushNotificationAO ao, Notification notification) {
        PushNotificationSetting.Group group = PushNotificationSetting.Group.toValue(ao.getGroupSetting());
        if (group == null || group == PushNotificationSetting.Group.NONE) {
            return false;
        }
        if (group == PushNotificationSetting.Group.STANDARD) {
            return true;
        }
        NotificationCategory category = NotificationHelper.getCategory(notification);
        if (category == null) {
            return false;
        }
        if (group == PushNotificationSetting.Group.QUIET) {
            return category != NotificationCategory.COMMENT;
        }
        String customSetting = ao.getCustomSetting();
        return customSetting.contains(category.getValue() + "*");
    }

    public Request.Builder getRequestBuilder(String restUrl) {
        try {
            return (Request.Builder)this.mobileHttpClient.getInstance().newRequest(restUrl).setHeader(AUTHORIZATION_HEADER_NAME, this.getLicense()).setContentType(JSON_CONTENT_TYPE);
        }
        catch (LicenseException e) {
            this.LOG.debug("Cannot parse license", (Throwable)e);
            throw new ServiceException("Cannot get authorization to make request builder");
        }
    }

    public String convertCustomSetting(Map<NotificationCategory, Boolean> customSettingMap) {
        if (customSettingMap == null) {
            return "";
        }
        List settings = customSettingMap.keySet().stream().filter(customSettingMap::get).map(category -> category.getValue() + "*").collect(Collectors.toList());
        return String.join((CharSequence)",", settings);
    }

    public Map<NotificationCategory, Boolean> convertCustomSetting(String customSetting) {
        HashSet<String> customSettingList = new HashSet<String>(Arrays.asList(customSetting.split(",")));
        return NotificationCategory.BUILT_IN.stream().collect(Collectors.toMap(category -> category, category -> StringUtils.isNotBlank((CharSequence)customSetting) && customSettingList.contains(category.getValue() + "*")));
    }

    private String getLicense() throws LicenseException {
        String licenseKey = (String)this.configuration.getProperty((Object)"atlassian.license.message");
        if (StringUtils.isNotBlank((CharSequence)licenseKey)) {
            return licenseKey;
        }
        String licenseMessage = (String)this.configuration.getProperty((Object)"confluence.license.message");
        String licenseHash = (String)this.configuration.getProperty((Object)"confluence.license.hash");
        return new LicensePair(licenseMessage, licenseHash).getOriginalLicenseString();
    }
}

