/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.rest.utils;

import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.history.RateLimitingReportOrder;
import com.atlassian.ratelimiting.rest.api.RestJobControlSettings;
import com.atlassian.ratelimiting.rest.api.RestTokenBucketSettings;
import com.atlassian.ratelimiting.rest.exception.InvalidDateStringFormatException;
import com.atlassian.ratelimiting.rest.exception.InvalidSortException;
import com.atlassian.ratelimiting.rest.exception.UserNotFoundException;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUtils {
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);
    private static final List<String> ALLOWED_TIME_UNITS = Arrays.asList(ChronoUnit.SECONDS.name(), ChronoUnit.MINUTES.name(), ChronoUnit.HOURS.name());
    private static final String I18N_PREFIX = "ratelimit.rest.error.settings.";
    private static final String INVALID_TOKEN_BUCKET_SETTINGS_PROPERTY_VALUE = "ratelimit.rest.error.settings.token.bucket.property.invalid";
    private static final String EMPTY_JOB_CONTROLS_SETTINGS_PROPERTY_VALUE = "ratelimit.rest.error.settings.job.control.empty.request";
    private static final String INVALID_JOB_CONTROLS_SETTINGS_PROPERTY_VALUE = "ratelimit.rest.error.settings.job.control.property.invalid";
    private static final String INVALID_TIME_UNIT_PROPERTY_VALUE = "ratelimit.rest.error.settings.token.bucket.time.unit.invalid";
    private static final String INVALID_DATE_FORMAT_PROPERTY_VALUE = "ratelimit.rest.error.history.date.invalid";
    private static final String INVALID_SORT_PROPERTY_VALUE = "ratelimit.rest.error.history.sort.invalid";
    private static final String UNKNOWN_USER_KEY = "UNKNOWN_USER";

    private RestUtils() {
    }

    public static TokenBucketSettings validateRestTokenBucketSettings(RestTokenBucketSettings settings, I18nResolver i18nService) {
        Preconditions.checkArgument((settings.getCapacity() >= -1 ? 1 : 0) != 0, (Object)i18nService.getText(INVALID_TOKEN_BUCKET_SETTINGS_PROPERTY_VALUE, new Serializable[]{"capacity", Integer.valueOf(settings.getCapacity())}));
        Preconditions.checkArgument((settings.getFillRate() >= -1 ? 1 : 0) != 0, (Object)i18nService.getText(INVALID_TOKEN_BUCKET_SETTINGS_PROPERTY_VALUE, new Serializable[]{"fillRate", Integer.valueOf(settings.getFillRate())}));
        Preconditions.checkArgument((settings.getIntervalFrequency() >= 0 ? 1 : 0) != 0, (Object)i18nService.getText(INVALID_TOKEN_BUCKET_SETTINGS_PROPERTY_VALUE, new Serializable[]{"intervalFrequency", Integer.valueOf(settings.getIntervalFrequency())}));
        Preconditions.checkState((boolean)ALLOWED_TIME_UNITS.contains(settings.getIntervalTimeUnit()), (Object)i18nService.getText(INVALID_TIME_UNIT_PROPERTY_VALUE, new Serializable[]{"intervalTimeUnit", ALLOWED_TIME_UNITS.toString(), settings.getIntervalTimeUnit()}));
        return new TokenBucketSettings(settings.getCapacity(), settings.getFillRate(), settings.getIntervalFrequency(), ChronoUnit.valueOf(settings.getIntervalTimeUnit()));
    }

    public static UserProfile validateUserProfile(String userId, UserService userService) {
        Optional<UserProfile> userProfile = userService.getUser(Objects.requireNonNull(userId, "userId"));
        return userProfile.orElseThrow(() -> new UserNotFoundException(String.format("User Profile for user NOT found: [%s]", userId)));
    }

    public static Set<UserProfile> validateUserProfiles(Collection<String> userIds, UserService userService) {
        return userIds.stream().map(userId -> RestUtils.validateUserProfile(userId, userService)).collect(Collectors.toSet());
    }

    public static SystemJobControlSettings validateRestJobControlSettings(RestJobControlSettings jobControlSettings, I18nResolver i18nService) {
        Duration reportingDbArchivingJobFrequencyDuration = RestUtils.validateDurationField("reportingDbArchivingJobFrequencyDuration", jobControlSettings.getReportingDbArchivingJobFrequencyDuration(), i18nService);
        Duration reportingDbRetentionPeriodDuration = RestUtils.validateDurationField("reportingDbRetentionPeriodDuration", jobControlSettings.getReportingDbRetentionPeriodDuration(), i18nService);
        Duration bucketCleanupJobFrequencyDuration = RestUtils.validateDurationField("bucketCleanupJobFrequencyDuration", jobControlSettings.getBucketCleanupJobFrequencyDuration(), i18nService);
        Duration bucketCollectionJobFrequencyDuration = RestUtils.validateDurationField("bucketCollectionJobFrequencyDuration", jobControlSettings.getBucketCollectionJobFrequencyDuration(), i18nService);
        Preconditions.checkArgument((!Objects.isNull(bucketCollectionJobFrequencyDuration) || !Objects.isNull(reportingDbArchivingJobFrequencyDuration) || !Objects.isNull(reportingDbRetentionPeriodDuration) || !Objects.isNull(bucketCleanupJobFrequencyDuration) ? 1 : 0) != 0, (Object)i18nService.getText(EMPTY_JOB_CONTROLS_SETTINGS_PROPERTY_VALUE));
        return SystemJobControlSettings.builder().reportingDbArchivingJobFrequencyDuration(reportingDbArchivingJobFrequencyDuration).reportingDbRetentionPeriodDuration(reportingDbRetentionPeriodDuration).bucketCleanupJobFrequencyDuration(bucketCleanupJobFrequencyDuration).bucketCollectionJobFrequencyDuration(bucketCollectionJobFrequencyDuration).build();
    }

    private static Duration validateDurationField(String fieldName, String fieldValue, I18nResolver i18nService) {
        try {
            logger.trace("Validation duration field: [{}] with value: [{}]", (Object)fieldName, (Object)fieldValue);
            return Objects.nonNull(fieldValue) ? Duration.parse(fieldValue) : null;
        }
        catch (DateTimeParseException e) {
            String errorMessage = i18nService.getText(INVALID_JOB_CONTROLS_SETTINGS_PROPERTY_VALUE, new Serializable[]{fieldName, fieldValue});
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static ZonedDateTime validateDateString(String fieldName, String fieldValue, I18nResolver i18nResolver) {
        try {
            return Objects.nonNull(fieldValue) ? ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(fieldValue)) : null;
        }
        catch (DateTimeException e) {
            throw new InvalidDateStringFormatException(i18nResolver.getText(INVALID_DATE_FORMAT_PROPERTY_VALUE, new Serializable[]{fieldName, fieldValue}));
        }
    }

    public static RateLimitingReportOrder validateHistoryRequest(String orderString, I18nResolver i18nResolver) {
        try {
            return RateLimitingReportOrder.fromString(orderString, RateLimitingReportOrder.FREQUENCY);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidSortException(i18nResolver.getText(INVALID_SORT_PROPERTY_VALUE, new Serializable[]{RestUtils.constructInvalidSortErrorMessage()}));
        }
    }

    public static List<String> lookupUserKeysForUsernames(List<String> userFilterList, UserService userService) {
        return userFilterList.stream().filter(userId -> !Strings.isNullOrEmpty((String)userId)).map(userService::getUser).map(RestUtils::getUserKeyStringFromUserProfile).collect(Collectors.toList());
    }

    private static String getUserKeyStringFromUserProfile(Optional<UserProfile> userProfile) {
        return userProfile.map(UserProfile::getUserKey).map(UserKey::getStringValue).orElse(UNKNOWN_USER_KEY);
    }

    private static String constructInvalidSortErrorMessage() {
        return Arrays.stream(RateLimitingReportOrder.values()).map(Enum::name).collect(Collectors.joining(", "));
    }
}

