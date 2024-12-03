/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.core.properties;

import com.atlassian.oauth2.common.properties.BooleanSystemProperty;
import com.atlassian.oauth2.common.properties.DurationSystemProperty;
import com.atlassian.oauth2.common.properties.IntegerSystemProperty;
import com.atlassian.oauth2.common.properties.StringSystemProperty;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class SystemProperty {
    static final String PREFIX = "atlassian.oauth2.provider.";
    public static final BooleanSystemProperty DEV_MODE = new BooleanSystemProperty("atlassian.dev.mode", false);
    public static final BooleanSystemProperty ENABLE_ACCESS_TOKENS = new BooleanSystemProperty("atlassian.oauth2.provider.enable.access.tokens", true);
    public static final BooleanSystemProperty SKIP_BASE_URL_HTTPS_REQUIREMENT = new BooleanSystemProperty("atlassian.oauth2.provider.skip.base.url.https.requirement", false);
    public static final BooleanSystemProperty SKIP_REDIRECT_URL_HTTPS_REQUIREMENT = new BooleanSystemProperty("atlassian.oauth2.provider.skip.redirect.url.https.requirement", false);
    public static final BooleanSystemProperty DISALLOW_LOCALHOST_REDIRECT = new BooleanSystemProperty("atlassian.oauth2.provider.disallow.localhost.redirect", false);
    public static final IntegerSystemProperty GLOBAL_CLUSTER_LOCK_TIMEOUT_SECONDS = new IntegerSystemProperty("atlassian.oauth2.provider.max.lock.timeout.seconds", 10);
    public static final DurationSystemProperty MAX_AUTHORIZATION_CODE_LIFETIME = new DurationSystemProperty("atlassian.oauth2.provider.max.client.delay.seconds", ChronoUnit.SECONDS, Duration.ofMinutes(1L).getSeconds(), Duration.ofMinutes(10L).getSeconds());
    public static final StringSystemProperty PRUNE_EXPIRED_AUTHORIZATIONS_SCHEDULE = new StringSystemProperty("atlassian.oauth2.provider.prune.expired.authorizations.schedule", "0 0 * * * ?");
    public static final StringSystemProperty PRUNE_EXPIRED_TOKENS_SCHEDULE = new StringSystemProperty("atlassian.oauth2.provider.prune.expired.tokens.schedule", "0 0 * * * ?");
    public static final DurationSystemProperty MAX_ACCESS_TOKEN_LIFETIME = new DurationSystemProperty("atlassian.oauth2.provider.access.token.expiration.seconds", ChronoUnit.SECONDS, Duration.ofHours(1L).getSeconds());
    public static final DurationSystemProperty MAX_REFRESH_TOKEN_LIFETIME = new DurationSystemProperty("atlassian.oauth2.provider.refresh.token.expiration.seconds", ChronoUnit.SECONDS, Duration.ofDays(90L).getSeconds());
    public static final BooleanSystemProperty INVALIDATE_SESSION_ENABLED = new BooleanSystemProperty("atlassian.oauth2.provider.invalidate.session.enabled", true);
    public static final BooleanSystemProperty VALIDATE_CLIENT_SECRET = new BooleanSystemProperty("atlassian.oauth2.provider.validate.client.secret", true);
    public static final BooleanSystemProperty USE_QUOTES_IN_SQL = new BooleanSystemProperty("atlassian.oauth2.provider.use.quotes.in.sql", false);
    public static final BooleanSystemProperty DO_NOT_USE_QUOTES_IN_SQL = new BooleanSystemProperty("atlassian.oauth2.provider.do.not.use.quotes.in.sql", false);
    public static final BooleanSystemProperty TOKEN_VIA_BASIC_AUTHENTICATION = new BooleanSystemProperty("atlassian.oauth2.provider.token.via.basic.authentication", true);
}

