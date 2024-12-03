/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.ratelimiting.internal.audit;

import com.atlassian.ratelimiting.audit.AuditChangedValue;
import com.atlassian.ratelimiting.audit.AuditEntry;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.events.RateLimitingDisabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingDryRunEnabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingEnabledEvent;
import com.atlassian.ratelimiting.events.SystemRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsCreatedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsDeletedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AuditEntryFactory {
    private static final String DISABLED = "ratelimit.audit.summary.disabled";
    private static final String DRY_RUN = "ratelimit.audit.summary.dry_run";
    private static final String ENABLED = "ratelimit.audit.summary.enabled";
    private static final String SETTING_MODIFIED = "ratelimit.audit.summary.setting_modified";
    private static final String EXEMPTION_ADDED = "ratelimit.audit.summary.exemption_added";
    private static final String EXEMPTION_DELETED = "ratelimit.audit.summary.exemption_deleted";
    private static final String EXEMPTION_MODIFIED = "ratelimit.audit.summary.exemption_modified";
    private static final String OPTION_NAME = "ratelimit.audit.changed_value.option_name";
    private static final String CAPACITY_NAME = "ratelimit.audit.changed_value.capacity_name";
    private static final String FILL_RATE_NAME = "ratelimit.audit.changed_value.fill_rate_name";
    private static final String INTERVAL = "ratelimit.audit.changed_value.interval_name";
    private static final String UNLIMITED = "ratelimit.audit.changed_value.unlimited";
    private static final String BLOCKED = "ratelimit.audit.changed_value.blocked";
    private static final String CUSTOMIZED = "ratelimit.audit.changed_value.customized";
    private final I18nResolver i18nResolver;
    private final UserService userService;

    public AuditEntryFactory(I18nResolver i18nResolver, UserService userService) {
        this.i18nResolver = i18nResolver;
        this.userService = userService;
    }

    public AuditEntry create(RateLimitingDisabledEvent event) {
        return AuditEntry.builder().summary(DISABLED).build();
    }

    public AuditEntry create(RateLimitingDryRunEnabledEvent event) {
        return AuditEntry.builder().summary(DRY_RUN).build();
    }

    public AuditEntry create(RateLimitingEnabledEvent event) {
        return AuditEntry.builder().summary(ENABLED).build();
    }

    public AuditEntry create(SystemRateLimitSettingsModifiedEvent event) {
        return AuditEntry.builder().summary(SETTING_MODIFIED).changes(this.diffBucketSetting(event.getOldSettings(), event.getNewSettings())).build();
    }

    public AuditEntry create(UserRateLimitSettingsCreatedEvent event) {
        return AuditEntry.builder().summary(EXEMPTION_ADDED).changes(this.diffUserSetting(null, event.getCreatedSettings())).userProfile(this.userService.getUser(event.getUserKey())).build();
    }

    public AuditEntry create(UserRateLimitSettingsDeletedEvent event) {
        return AuditEntry.builder().summary(EXEMPTION_DELETED).changes(this.diffUserSetting(event.getSettings(), null)).userProfile(this.userService.getUser(event.getUserKey())).build();
    }

    public AuditEntry create(UserRateLimitSettingsModifiedEvent event) {
        return AuditEntry.builder().summary(EXEMPTION_MODIFIED).changes(this.diffUserSetting(event.getOldSettings(), event.getNewSettings())).userProfile(this.userService.getUser(event.getUserKey())).build();
    }

    private List<AuditChangedValue> diffUserSetting(UserRateLimitSettings originalValue, UserRateLimitSettings newValue) {
        Optional<UserRateLimitSettings> fromOption = Optional.ofNullable(originalValue);
        Optional<UserRateLimitSettings> toOption = Optional.ofNullable(newValue);
        return this.diffBucketSetting(fromOption.map(UserRateLimitSettings::getBucketSettings), toOption.map(UserRateLimitSettings::getBucketSettings));
    }

    private List<AuditChangedValue> diffBucketSetting(TokenBucketSettings originalValue, TokenBucketSettings newValue) {
        return this.diffBucketSetting(Optional.ofNullable(originalValue), Optional.ofNullable(newValue));
    }

    private List<AuditChangedValue> diffBucketSetting(Optional<TokenBucketSettings> originalValueOption, Optional<TokenBucketSettings> newValueOption) {
        AuditChangedValue.AuditChangedValuesBuilder builder = new AuditChangedValue.AuditChangedValuesBuilder(this.i18nResolver).addIfChanged(OPTION_NAME, this.getOptionKey(originalValueOption), this.getOptionKey(newValueOption));
        this.addIfChanged(builder, FILL_RATE_NAME, originalValueOption, newValueOption, TokenBucketSettings::getFillRate);
        this.addIfChanged(builder, INTERVAL, originalValueOption, newValueOption, this::formatDuration);
        this.addIfChanged(builder, CAPACITY_NAME, originalValueOption, newValueOption, TokenBucketSettings::getCapacity);
        return builder.build();
    }

    private <V> void addIfChanged(AuditChangedValue.AuditChangedValuesBuilder builder, String name, Optional<TokenBucketSettings> originalValueOption, Optional<TokenBucketSettings> newValueOption, Function<TokenBucketSettings, V> func) {
        builder.addIfChanged(name, this.getDetailValue(originalValueOption, func), this.getDetailValue(newValueOption, func));
    }

    private <V> Optional<V> getDetailValue(Optional<TokenBucketSettings> option, Function<TokenBucketSettings, V> func) {
        return option.filter(TokenBucketSettings::isCustomSettings).map(func);
    }

    private Optional<String> getOptionKey(Optional<TokenBucketSettings> option) {
        return option.map(this::getOptionKey);
    }

    private String getOptionKey(TokenBucketSettings settings) {
        if (settings.isWhitelisted()) {
            return UNLIMITED;
        }
        if (settings.isBlacklisted()) {
            return BLOCKED;
        }
        return CUSTOMIZED;
    }

    private String formatDuration(TokenBucketSettings t) {
        return String.format("%d %s", t.getIntervalFrequency(), t.getIntervalTimeUnit().toString().toLowerCase());
    }
}

