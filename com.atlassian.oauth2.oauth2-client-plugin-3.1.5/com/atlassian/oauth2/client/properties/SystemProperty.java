/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.properties;

import com.atlassian.oauth2.common.properties.BooleanSystemProperty;
import com.atlassian.oauth2.common.properties.DurationSystemProperty;
import com.atlassian.oauth2.common.properties.IntegerSystemProperty;
import com.atlassian.oauth2.common.properties.StringSystemProperty;
import java.time.temporal.ChronoUnit;

public abstract class SystemProperty {
    public static final String PREFIX = "atlassian.oauth2.client.";
    public static final DurationSystemProperty DEFAULT_REFRESH_TOKEN_DURATION = new DurationSystemProperty("atlassian.oauth2.client.default.refresh.token.duration.days", ChronoUnit.DAYS, 30L);
    public static final DurationSystemProperty MINIMUM_ACCESS_TOKEN_ONLY_LIFETIME = new DurationSystemProperty("atlassian.oauth2.client.minimum.access.token.only.lifetime.days", ChronoUnit.DAYS, 30L);
    public static final DurationSystemProperty UNRECOVERABLE_TOKEN_FAILING_PERIOD = new DurationSystemProperty("atlassian.oauth2.client.unrecoverable.token.failing.period.days", ChronoUnit.DAYS, 7L);
    public static final DurationSystemProperty MAX_CLOCK_SKEW = new DurationSystemProperty("atlassian.oauth2.client.max.clock.skew.seconds", ChronoUnit.SECONDS, 60L);
    public static final DurationSystemProperty MAX_CLIENT_DELAY = new DurationSystemProperty("atlassian.oauth2.client.max.client.delay.seconds", ChronoUnit.SECONDS, 60L);
    public static final DurationSystemProperty MAX_SERVER_DELAY = new DurationSystemProperty("atlassian.oauth2.client.max.server.delay.seconds", ChronoUnit.SECONDS, 120L);
    public static final DurationSystemProperty MAX_SERVER_TIMEOUT = new DurationSystemProperty("atlassian.oauth2.client.max.server.request.timeout.seconds", ChronoUnit.SECONDS, 300L);
    public static final DurationSystemProperty LIFETIME_OF_INVALID_TOKEN = new DurationSystemProperty("atlassian.oauth2.client.lifetime.invalid.token.days", ChronoUnit.DAYS, 30L);
    public static final StringSystemProperty PRUNE_EXPIRED_TOKENS_SCHEDULE = new StringSystemProperty("atlassian.oauth2.client.prune.expired.tokens.schedule", "0 0 23 * * ?");
    public static final IntegerSystemProperty DEFAULT_MONITOR_STRIPE_COUNT = new IntegerSystemProperty("atlassian.oauth2.client.default.monitor.stripe.count", 64);
    public static final BooleanSystemProperty SKIP_BASE_URL_HTTPS_REQUIREMENT = new BooleanSystemProperty("atlassian.oauth2.client.skip.base.url.https.requirement", false);
    public static final BooleanSystemProperty SKIP_PROVIDER_HTTPS_REQUIREMENT = new BooleanSystemProperty("atlassian.oauth2.client.skip.provider.https.requirement", false);
    public static final BooleanSystemProperty ADD_EMPTY_USER_AGENT_FOR_TOKEN_REQUESTS = new BooleanSystemProperty("atlassian.oauth2.client.add.empty.user.agent.for.token.requests", false);
    public static final BooleanSystemProperty DEV_MODE = new BooleanSystemProperty("atlassian.dev.mode", false);
    public static final BooleanSystemProperty DISABLE_CLIENT_STATE_VALIDATION = new BooleanSystemProperty("atlassian.oauth2.client.validate.client.state", false);
}

