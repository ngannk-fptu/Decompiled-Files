/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  net.java.ao.ActiveObjectsException
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.apache.commons.lang3.time.DateUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckUserSettingsService;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ClockFactory;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.NotificationService;
import com.atlassian.troubleshooting.healthcheck.rest.HealthCheckPropertiesRepresentation;
import com.atlassian.troubleshooting.stp.persistence.SupportHealthcheckSchema;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationServiceImpl
implements NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final ActiveObjects ao;
    private final HealthCheckUserSettingsService userSettingsService;
    private final HealthStatusPersistenceService statusPersistenceService;
    private final HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService;
    private final ClockFactory clock;

    @Autowired
    public NotificationServiceImpl(ActiveObjects ao, HealthCheckUserSettingsService userSettingsService, HealthStatusPersistenceService statusPersistenceService, HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService, ClockFactory clockFactory) {
        this.ao = ao;
        this.userSettingsService = userSettingsService;
        this.statusPersistenceService = statusPersistenceService;
        this.healthStatusPropertiesPersistenceService = healthStatusPropertiesPersistenceService;
        this.clock = clockFactory;
    }

    @Override
    public List<HealthCheckStatus> getStatusesForUserNotifications(UserKey userKey) {
        List<HealthCheckStatus> failedStatuses = this.getFailedStatusForSeverity(userKey);
        if (failedStatuses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> dismissedNotificationByUser = this.getDismissedNotificationByUser(userKey);
        return failedStatuses.stream().filter(status -> dismissedNotificationByUser.stream().noneMatch(id -> status.getId() == id.intValue())).collect(Collectors.toList());
    }

    private Date getCurrentDate() {
        return new Date(this.clock.makeClock().millis());
    }

    @Override
    public void storeDismissedNotification(UserKey userkey, Integer notificationId, boolean isSnoozed) {
        Date currentDate = this.getCurrentDate();
        if (isSnoozed) {
            SupportHealthcheckSchema.HealthCheckNotificationDismiss[] existingEntities = this.getSnoozedNotificationByUserAndId(userkey, notificationId);
            if (existingEntities.length > 0) {
                for (SupportHealthcheckSchema.HealthCheckNotificationDismiss entity : existingEntities) {
                    int snoozeCount = entity.getSnoozeCount() + 1;
                    if (snoozeCount < 3) {
                        entity.setSnoozeDate(currentDate);
                        entity.setSnoozeCount(snoozeCount);
                        entity.save();
                        continue;
                    }
                    entity.setIsSnoozed(false);
                    entity.save();
                }
            } else {
                this.addNewNotificationEntity(userkey, notificationId, true, currentDate, 1);
            }
        } else {
            this.addNewNotificationEntity(userkey, notificationId, false, null, null);
        }
    }

    private List<HealthCheckStatus> getFailedStatusForSeverity(UserKey userKey) {
        SupportHealthStatus.Severity severityThreshold = this.userSettingsService.getUserSettings(userKey).getSeverityThresholdForNotifications();
        return this.statusPersistenceService.getFailedStatuses(severityThreshold);
    }

    @VisibleForTesting
    List<Integer> getDismissedNotificationByUser(UserKey userKey) {
        ArrayList notificationsId = Lists.newArrayList();
        try {
            for (SupportHealthcheckSchema.HealthCheckNotificationDismiss notification : (SupportHealthcheckSchema.HealthCheckNotificationDismiss[])this.ao.find(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, Query.select().where("USER_KEY = ? AND IS_SNOOZED = ?", new Object[]{userKey.getStringValue(), Boolean.FALSE}))) {
                notificationsId.add(notification.getNotificationId());
            }
            for (SupportHealthcheckSchema.HealthCheckNotificationDismiss notification : (SupportHealthcheckSchema.HealthCheckNotificationDismiss[])this.ao.find(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, Query.select().where("USER_KEY = ? AND IS_SNOOZED = ? AND SNOOZE_DATE > ?", new Object[]{userKey.getStringValue(), Boolean.TRUE, this.getSnoozeExpiryDate()}))) {
                notificationsId.add(notification.getNotificationId());
            }
        }
        catch (ActiveObjectsException e) {
            LOG.error("There's an error retrieving dismissed flag for user {}", (Object)userKey, (Object)e);
        }
        return notificationsId;
    }

    @Override
    public Boolean checkIsAutoDismissed(UserKey userkey, Integer notificationId) {
        SupportHealthcheckSchema.HealthCheckNotificationDismiss[] entity = (SupportHealthcheckSchema.HealthCheckNotificationDismiss[])this.ao.find(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, Query.select().where("USER_KEY = ? AND NOTIFICATION_ID = ? AND IS_SNOOZED = ?", new Object[]{userkey.getStringValue(), notificationId, Boolean.FALSE}));
        return entity.length > 0;
    }

    @Override
    public void deleteDismissByUser(UserKey userkey) {
        this.ao.deleteWithSQL(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, "USER_KEY = ?", new Object[]{userkey.getStringValue()});
    }

    @Override
    public void deleteDismissById(List<Integer> removedStatusIds) {
        for (Integer id : removedStatusIds) {
            this.ao.deleteWithSQL(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, "NOTIFICATION_ID = ?", new Object[]{id});
        }
    }

    private void addNewNotificationEntity(UserKey userkey, Integer notificationId, Boolean isSnoozed, Date currentDate, Integer snoozeCount) {
        try {
            SupportHealthcheckSchema.HealthCheckNotificationDismiss notificationEntity = (SupportHealthcheckSchema.HealthCheckNotificationDismiss)this.ao.create(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, new DBParam[]{new DBParam("USER_KEY", (Object)userkey.getStringValue()), new DBParam("NOTIFICATION_ID", (Object)notificationId), new DBParam("IS_SNOOZED", (Object)isSnoozed), new DBParam("SNOOZE_DATE", (Object)currentDate), new DBParam("SNOOZE_COUNT", (Object)snoozeCount)});
            notificationEntity.save();
        }
        catch (ActiveObjectsException e) {
            LOG.error("There's a problem persisting notification dismiss flag into the database for the user {}", (Object)userkey, (Object)e);
        }
    }

    private SupportHealthcheckSchema.HealthCheckNotificationDismiss[] getSnoozedNotificationByUserAndId(UserKey userkey, Integer notificationId) {
        return (SupportHealthcheckSchema.HealthCheckNotificationDismiss[])this.ao.find(SupportHealthcheckSchema.HealthCheckNotificationDismiss.class, Query.select().where("USER_KEY = ? AND NOTIFICATION_ID = ? AND IS_SNOOZED = ?", new Object[]{userkey.getStringValue(), notificationId, Boolean.TRUE}));
    }

    private Date getSnoozeExpiryDate() {
        return DateUtils.addDays((Date)this.getLastRunDate().orElseGet(this::getCurrentDate), (int)-1);
    }

    private Optional<Date> getLastRunDate() {
        HealthCheckPropertiesRepresentation propRepresentation = this.healthStatusPropertiesPersistenceService.getLastRun();
        if (propRepresentation != null) {
            long timeStamp = Long.parseLong(propRepresentation.getPropertyValue());
            return Optional.of(new Date(timeStamp));
        }
        return Optional.empty();
    }
}

