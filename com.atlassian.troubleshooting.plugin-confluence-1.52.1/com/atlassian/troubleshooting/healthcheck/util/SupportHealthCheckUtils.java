/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.sun.jersey.api.NotFoundException
 */
package com.atlassian.troubleshooting.healthcheck.util;

import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheck;
import com.atlassian.troubleshooting.healthcheck.util.CurrentTime;
import com.sun.jersey.api.NotFoundException;
import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SupportHealthCheckUtils {
    private SupportHealthCheckUtils() {
    }

    public static UserKey getUserKey(UserManager userManager, String username) {
        return Optional.ofNullable(userManager.getUserProfile(username)).map(UserProfile::getUserKey).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public static String formatRelativeDate(long timeStamp, TimeZoneManager timeZoneManager) {
        long nowTimeStamp = CurrentTime.currentTimeMillis();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), timeZoneManager.getUserTimeZone().toZoneId());
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowTimeStamp), timeZoneManager.getUserTimeZone().toZoneId());
        long secondsAgo = (nowTimeStamp - timeStamp) / 1000L;
        long daysAgo = Period.between(dateTime.toLocalDate(), now.toLocalDate()).getDays();
        long minutesAgo = secondsAgo / 60L;
        long hoursAgo = minutesAgo / 60L;
        if (secondsAgo >= 0L) {
            if (secondsAgo < 60L) {
                return "Just now";
            }
            if (hoursAgo < 1L) {
                if (minutesAgo == 1L) {
                    return "1 minute ago";
                }
                return minutesAgo + " minutes ago";
            }
            if (hoursAgo == 1L) {
                return "1 hour ago";
            }
            if (daysAgo == 1L && hoursAgo > 5L) {
                return "Yesterday";
            }
            if (daysAgo < 1L) {
                return hoursAgo + " hours ago";
            }
            if (daysAgo < 7L) {
                return daysAgo + " days ago";
            }
            if (daysAgo == 7L) {
                return "1 week ago";
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy h:mma z");
        return dateTimeFormatter.format(dateTime);
    }

    public static HealthCheck asHealthCheckJson(ExtendedSupportHealthCheck check) {
        return HealthCheck.builder().name(check.getName()).isSoftLaunch(check.isSoftLaunch()).isEnabled(check.isEnabled()).description(check.getDescription()).completeKey(check.getKey()).timeout(check.getTimeOut()).tag(check.getTag()).build();
    }

    public static String getCompactKey(String completeKey) {
        String[] parts = completeKey.split(":");
        return parts.length > 1 ? parts[1] : completeKey;
    }

    public static HealthCheckStatus asHealthCheckStatus(ExtendedSupportHealthCheck healthCheck, SupportHealthStatus status) {
        return HealthCheckStatus.builder().name(healthCheck.getName()).isSoftLaunch(healthCheck.isSoftLaunch()).isEnabled(healthCheck.isEnabled()).completeKey(healthCheck.getKey()).description(healthCheck.getDescription()).isHealthy(status.isHealthy()).failureReason(status.failureReason()).application(status.getApplication().name()).nodeId(status.getNodeId()).time(status.getTime()).severity(status.getSeverity()).documentation(status.getDocumentation()).tag(healthCheck.getTag()).additionalLinks(SupportHealthCheckUtils.asLinks(status.getAdditionalLinks())).build();
    }

    public static Set<HealthCheckStatus.Link> asLinks(Set<SupportHealthStatus.Link> links) {
        return links.stream().map(l -> new HealthCheckStatus.Link(l.getDisplayName(), l.getUrl())).collect(Collectors.toSet());
    }
}

