/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.task.TaskRejectedException
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.mobile.activeobject.dao.PushNotificationDao;
import com.atlassian.confluence.plugins.mobile.activeobject.entity.PushNotificationAO;
import com.atlassian.confluence.plugins.mobile.analytic.MobileSimpleAnalyticEvent;
import com.atlassian.confluence.plugins.mobile.dto.notification.RegistrationDto;
import com.atlassian.confluence.plugins.mobile.exception.MobilePushNotificationException;
import com.atlassian.confluence.plugins.mobile.helper.PushNotificationHelper;
import com.atlassian.confluence.plugins.mobile.notification.NotificationCategory;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationContent;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationSetting;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationStatus;
import com.atlassian.confluence.plugins.mobile.remoteservice.PushNotificationClient;
import com.atlassian.confluence.plugins.mobile.service.PushNotificationService;
import com.atlassian.confluence.plugins.mobile.service.executor.PushNotificationTask;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.model.Notification;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public final class PushNotificationServiceImpl
implements PushNotificationService {
    private Logger LOG = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    private final PushNotificationDao pushNotificationDao;
    private final SettingsManager settingsManager;
    private final PermissionManager permissionManager;
    private final PushNotificationClient pushNotificationClient;
    private final PushNotificationHelper pushNotificationHelper;
    private final EventPublisher eventPublisher;
    private final ThreadPoolTaskExecutor pushTaskExecutor;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public PushNotificationServiceImpl(PushNotificationDao pushNotificationDao, @ComponentImport SettingsManager settingsManager, @ComponentImport PermissionManager permissionManager, PushNotificationClient pushNotificationClient, PushNotificationHelper pushNotificationHelper, @ComponentImport EventPublisher eventPublisher, ThreadPoolTaskExecutor pushTaskExecutor, @ComponentImport PluginAccessor pluginAccessor) {
        this.pushNotificationDao = pushNotificationDao;
        this.settingsManager = settingsManager;
        this.permissionManager = permissionManager;
        this.pushNotificationClient = pushNotificationClient;
        this.pushNotificationHelper = pushNotificationHelper;
        this.eventPublisher = eventPublisher;
        this.pushTaskExecutor = pushTaskExecutor;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public void push(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            this.LOG.debug("Don't send push because notification list is null or empty");
            return;
        }
        if (this.getStatus() == PushNotificationStatus.DISABLED) {
            this.LOG.debug("Don't send push because push notification is disabled");
            return;
        }
        Map userNotificationMap = notifications.stream().filter(notification -> StringUtils.isNotBlank((CharSequence)notification.getUser())).collect(Collectors.toMap(Notification::getUser, Function.identity()));
        if (userNotificationMap.isEmpty()) {
            this.LOG.debug("Don't send push because notification user map is empty");
            return;
        }
        this.LOG.debug("Start sending push notification");
        List<PushNotificationContent> contents = this.pushNotificationDao.findByUserNames(userNotificationMap.keySet(), true).stream().filter(ao -> this.pushNotificationHelper.isPushAllowed((PushNotificationAO)ao, (Notification)userNotificationMap.get(ao.getUserName()))).map(ao -> new PushNotificationContent(ao.getAppName(), String.valueOf(((Notification)userNotificationMap.get(ao.getUserName())).getId()), ao.getId(), StringUtils.defaultString((String)ao.getEndpoint()), ao.getToken())).collect(Collectors.toList());
        try {
            if (contents == null || contents.isEmpty()) {
                this.LOG.debug("Don't send push because cannot find any registration");
                return;
            }
            this.pushTaskExecutor.execute((Runnable)new PushNotificationTask(this.pushNotificationClient, this.pushNotificationDao, contents));
        }
        catch (TaskRejectedException e) {
            this.LOG.warn("Push task executor is rejected", (Throwable)e);
        }
    }

    @Override
    public RegistrationDto register(@Nonnull RegistrationDto registration) {
        this.validateRegistrationData(registration);
        PushNotificationAO notification = this.getByAppNameAndDeviceOrTokenWithCurrentUser(registration);
        if (notification == null) {
            return this.createRegistration(registration);
        }
        notification.setEndpoint(this.getPushEndpoint(registration, notification));
        notification.setDeviceId(registration.getDeviceId());
        notification.setActive(true);
        notification.setStatusUpdatedTime(Calendar.getInstance().getTimeInMillis());
        notification.save();
        this.LOG.debug("Register success with registration id: {}", (Object)notification.getId());
        return new RegistrationDto(notification.getId(), registration.getOs(), registration.getBuild(), registration.getToken(), registration.getDeviceId());
    }

    @Override
    public void unregister(@Nonnull String id) {
        PushNotificationAO notification = this.pushNotificationDao.findById(id);
        if (notification == null) {
            throw new NotFoundException("Cannot find registration with id: " + id);
        }
        this.removePushEndpoint(notification);
        if (this.isDefaultGroupSetting(notification.getGroupSetting())) {
            this.pushNotificationDao.delete(notification);
        } else {
            notification.setStatusUpdatedTime(Calendar.getInstance().getTimeInMillis());
            notification.setActive(false);
            notification.save();
        }
    }

    @Override
    @Nonnull
    public PushNotificationStatus getStatus() {
        try {
            if (!this.pluginAccessor.isPluginEnabled("com.atlassian.mywork.mywork-confluence-host-plugin")) {
                return PushNotificationStatus.DISABLED;
            }
            String value = (String)((Object)this.settingsManager.getPluginSettings("com.atlassian.confluence.plugins.confluence-mobile-plugin:push-notification-status"));
            PushNotificationStatus status = PushNotificationStatus.valueOf(value);
            return status == null ? PushNotificationStatus.ENABLED : status;
        }
        catch (Exception e) {
            this.LOG.error("Error happen when get push status", (Throwable)e);
            return PushNotificationStatus.DISABLED;
        }
    }

    @Override
    public void updateStatus(@Nullable PushNotificationStatus status) {
        if (status == null) {
            throw new BadRequestException("Only support enabled/disable status");
        }
        if (!this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get())) {
            throw new PermissionException("You don't have permission to update status");
        }
        this.eventPublisher.publish((Object)new MobileSimpleAnalyticEvent("push.status." + status.getValue()));
        this.settingsManager.updatePluginSettings("com.atlassian.confluence.plugins.confluence-mobile-plugin:push-notification-status", (Serializable)((Object)status.getValue()));
    }

    @Override
    @Nonnull
    public PushNotificationSetting getSetting(@Nonnull String deviceId, @Nonnull String appName) {
        if (this.getStatus() == PushNotificationStatus.DISABLED) {
            throw new UnsupportedOperationException("Cannot get setting because push notification is disabled");
        }
        PushNotificationAO notification = this.getByAppNameAndDeviceIdWithCurrentUser(appName, deviceId);
        if (notification == null) {
            return new PushNotificationSetting(PushNotificationSetting.Group.STANDARD, NotificationCategory.BUILT_IN.stream().collect(Collectors.toMap(category -> category, category -> true)));
        }
        PushNotificationSetting.Group group = PushNotificationSetting.Group.toValue(notification.getGroupSetting());
        return new PushNotificationSetting(group == null ? PushNotificationSetting.Group.STANDARD : group, this.pushNotificationHelper.convertCustomSetting(notification.getCustomSetting()));
    }

    @Override
    @Nonnull
    public PushNotificationSetting updateSetting(@Nonnull String deviceId, @Nonnull String appName, @Nonnull PushNotificationSetting setting) {
        if (this.getStatus() == PushNotificationStatus.DISABLED) {
            throw new UnsupportedOperationException("Cannot update setting because push notification is disabled");
        }
        PushNotificationSetting.Group group = setting.getGroup() == null ? PushNotificationSetting.Group.STANDARD : setting.getGroup();
        String customSetting = this.pushNotificationHelper.convertCustomSetting(setting.getCustomSettings());
        PushNotificationAO notification = this.getByAppNameAndDeviceIdWithCurrentUser(appName, deviceId);
        if (notification == null) {
            this.createSetting(deviceId, appName, group.getName(), customSetting);
            return new PushNotificationSetting(group, this.pushNotificationHelper.convertCustomSetting(customSetting));
        }
        notification.setGroupSetting(group.getName());
        if (group == PushNotificationSetting.Group.CUSTOM) {
            notification.setCustomSetting(customSetting);
        }
        notification.save();
        return new PushNotificationSetting(group, this.pushNotificationHelper.convertCustomSetting(notification.getCustomSetting()));
    }

    @Override
    public void removePushNotification(@Nonnull User user) {
        this.pushNotificationDao.deleteByUsername(user.getName());
        this.LOG.info("User {} removed from mobile plugin", (Object)user.getName());
    }

    private String getPushEndpoint(RegistrationDto registration, PushNotificationAO notification) {
        if (this.getStatus() == PushNotificationStatus.DISABLED) {
            return "";
        }
        if (registration.getToken().equals(notification.getToken()) && StringUtils.isNotBlank((CharSequence)notification.getEndpoint())) {
            return notification.getEndpoint();
        }
        return this.pushNotificationClient.updatePushEndpoint(registration.getAppName(), registration.getToken(), StringUtils.defaultString((String)notification.getEndpoint()));
    }

    private void removePushEndpoint(PushNotificationAO notification) {
        if (this.getStatus() == PushNotificationStatus.ENABLED && StringUtils.isNotBlank((CharSequence)notification.getEndpoint()) && StringUtils.isNotBlank((CharSequence)notification.getAppName())) {
            try {
                this.pushNotificationClient.removePushEndpoint(notification.getAppName(), notification.getEndpoint());
                notification.setEndpoint("");
            }
            catch (Exception e) {
                this.LOG.warn("Error happens when remove endpoint");
            }
        }
    }

    private boolean isDefaultGroupSetting(String groupSetting) {
        return StringUtils.isBlank((CharSequence)groupSetting) || PushNotificationSetting.Group.toValue(groupSetting) == PushNotificationSetting.Group.STANDARD;
    }

    private void createSetting(String deviceId, String appName, String group, String customSetting) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        String id = UUID.randomUUID().toString();
        data.put("ID", id);
        data.put("USER_NAME", this.getUserName());
        data.put("APP_NAME", appName);
        data.put("GROUP_SETTING", group);
        data.put("CUSTOM_SETTING", customSetting);
        data.put("DEVICE_ID", deviceId);
        data.put("ACTIVE", false);
        this.pushNotificationDao.create(data);
    }

    private PushNotificationAO getByAppNameAndDeviceOrTokenWithCurrentUser(RegistrationDto registration) {
        return this.getSinglePushNotification(this.pushNotificationDao.findByUserNameAndAppNameAndDeviceIdOrToken(this.getUserName(), registration.getAppName(), registration.getDeviceId(), registration.getToken()));
    }

    private PushNotificationAO getByAppNameAndDeviceIdWithCurrentUser(String appName, String deviceId) {
        return this.getSinglePushNotification(this.pushNotificationDao.findByUserNameAndAppNameAndDeviceId(this.getUserName(), appName, deviceId));
    }

    private PushNotificationAO getSinglePushNotification(List<PushNotificationAO> notificationAOS) {
        if (notificationAOS == null || notificationAOS.isEmpty()) {
            return null;
        }
        if (notificationAOS.size() > 1) {
            this.pushNotificationDao.delete(notificationAOS.subList(1, notificationAOS.size()));
        }
        return notificationAOS.get(0);
    }

    private RegistrationDto createRegistration(RegistrationDto registration) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        String id = UUID.randomUUID().toString();
        data.put("ID", id);
        data.put("USER_NAME", this.getUserName());
        data.put("APP_NAME", registration.getAppName());
        data.put("TOKEN", registration.getToken());
        data.put("GROUP_SETTING", PushNotificationSetting.Group.STANDARD.getName());
        data.put("DEVICE_ID", registration.getDeviceId());
        data.put("ACTIVE", true);
        data.put("STATUS_UPDATED_TIME", Calendar.getInstance().getTimeInMillis());
        data.put("CUSTOM_SETTING", NotificationCategory.BUILT_IN.stream().map(category -> category.getValue() + "*").collect(Collectors.joining(",")));
        try {
            if (this.getStatus() == PushNotificationStatus.ENABLED) {
                data.put("ENDPOINT", this.pushNotificationClient.updatePushEndpoint(registration.getAppName(), registration.getToken(), ""));
            }
            this.pushNotificationDao.create(data);
            this.LOG.debug("Register successful with registration id: {}", (Object)id);
            return new RegistrationDto(id, registration.getOs(), registration.getBuild(), registration.getToken(), registration.getDeviceId());
        }
        catch (MobilePushNotificationException e) {
            this.LOG.debug("Register unsuccessful", (Throwable)e);
            throw new ServiceException(e.getMessage());
        }
    }

    private void validateRegistrationData(RegistrationDto registration) {
        ArrayList<String> errorMessages = new ArrayList<String>();
        if (StringUtils.isBlank((CharSequence)registration.getBuild())) {
            errorMessages.add("build");
        }
        if (StringUtils.isBlank((CharSequence)registration.getOs())) {
            errorMessages.add("os");
        }
        if (StringUtils.isBlank((CharSequence)registration.getToken())) {
            errorMessages.add("device token");
        }
        if (StringUtils.isBlank((CharSequence)registration.getDeviceId())) {
            errorMessages.add("device id");
        }
        if (!errorMessages.isEmpty()) {
            throw new BadRequestException("Missing required value of fields: " + String.join((CharSequence)",", errorMessages));
        }
    }

    private String getUserName() {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new PermissionException("Anonymous is not allowed to unregister push notification");
        }
        return AuthenticatedUserThreadLocal.getUsername();
    }
}

