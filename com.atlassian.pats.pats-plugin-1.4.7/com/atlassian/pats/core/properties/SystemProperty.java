/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.core.properties;

import com.atlassian.pats.core.properties.BooleanSystemProperty;
import com.atlassian.pats.core.properties.IntegerSystemProperty;
import com.atlassian.pats.core.properties.StringSystemProperty;

public abstract class SystemProperty {
    public static final String PREFIX = "atlassian.pats.";
    private static final String EVERY_HOUR_CRON = "0 0 * * * ?";
    private static final String EVERY_DAY_CRON = "0 0 0 * * ?";
    public static final BooleanSystemProperty PATS_ENABLED = new BooleanSystemProperty("atlassian.pats.enabled", true);
    public static final BooleanSystemProperty ETERNAL_TOKENS_ENABLED = new BooleanSystemProperty("atlassian.pats.eternal.tokens.enabled", true);
    public static final BooleanSystemProperty MAIL_NOTIFICATIONS_ENABLED = new BooleanSystemProperty("atlassian.pats.mail.notifications.enabled", true);
    public static final IntegerSystemProperty LAST_USED_UPDATE_INTERVAL_MINS = new IntegerSystemProperty("atlassian.pats.last.used.update.interval.mins", 1);
    public static final StringSystemProperty PRUNING_SCHEDULE_CRON = new StringSystemProperty("atlassian.pats.pruning.schedule.cron", "0 0 0 * * ?");
    public static final StringSystemProperty DELETED_USER_PRUNING_SCHEDULE_CRON = new StringSystemProperty("atlassian.pats.deleted.user.pruning.schedule.cron", "0 0 0 * * ?");
    public static final IntegerSystemProperty PRUNING_DELAY_DAYS = new IntegerSystemProperty("atlassian.pats.pruning.delay.days", 30);
    public static final IntegerSystemProperty MAX_TOKEN_EXPIRY_DAYS = new IntegerSystemProperty("atlassian.pats.max.tokens.expiry.days", 365);
    public static final IntegerSystemProperty MAX_TOKENS_PER_USER = new IntegerSystemProperty("atlassian.pats.max.tokens.per.user", 10);
    public static final IntegerSystemProperty AUTH_CACHE_EXPIRY_MINS = new IntegerSystemProperty("atlassian.pats.auth.cache.expiry.mins", 60);
    public static final IntegerSystemProperty AUTH_CACHE_MAX_ITEMS = new IntegerSystemProperty("atlassian.pats.auth.cache.max.items", 5000);
    public static final IntegerSystemProperty TOKEN_NAME_LENGTH = new IntegerSystemProperty("atlassian.pats.token.name.length", 40);
    public static final StringSystemProperty EXPIRY_CHECK_SCHEDULE_CRON = new StringSystemProperty("atlassian.pats.expiry.check.schedule.cron", "0 0 * * * ?");
    public static final IntegerSystemProperty EXPIRY_WARNING_DAYS = new IntegerSystemProperty("atlassian.pats.expiry.warning.days", 5);
    public static final BooleanSystemProperty INVALIDATE_SESSION_ENABLED = new BooleanSystemProperty("atlassian.pats.invalidate.session.enabled", true);
}

