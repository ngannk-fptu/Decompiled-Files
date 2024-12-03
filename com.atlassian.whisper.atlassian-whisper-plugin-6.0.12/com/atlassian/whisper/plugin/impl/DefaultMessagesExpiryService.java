/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.whisper.plugin.api.MessagesExpiryService
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.apache.commons.lang.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.whisper.plugin.api.MessagesExpiryService;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultMessagesExpiryService
implements MessagesExpiryService {
    static final String LAST_SUCCESSFUL_FETCH_TIME_SETTINGS_KEY = "com.atlassian.whisper.plugin:last-successful-fetch-time";
    static final String EXPIRY_TIME_TO_LIVE_PROPERTY = "atlassian.whisper.fetch.expiry.time-to-live";
    private static final long EXPIRY_DEFAULT_TIME_TO_LIVE_HOURS = 72L;
    private final Duration EXPIRY_TIME_TO_LIVE = Duration.ofMillis(Long.getLong("atlassian.whisper.fetch.expiry.time-to-live", TimeUnit.HOURS.toMillis(72L)));
    private static final Logger log = LoggerFactory.getLogger(DefaultMessagesExpiryService.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private Supplier<Instant> currentTimeSupplier = Instant::now;

    @Inject
    public DefaultMessagesExpiryService(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    void setCurrentTimeSupplier(Supplier<Instant> supplier) {
        this.currentTimeSupplier = supplier;
    }

    public void notifySuccessfulFetch() {
        this.setLastSuccessfulFetchTime(this.currentTimeSupplier.get());
    }

    public boolean areMessagesExpired() {
        Optional<Instant> lastSuccessfulFetchTime = this.getLastSuccessfulFetchTime();
        Instant now = this.currentTimeSupplier.get();
        log.debug("Last successful fetch time: " + (lastSuccessfulFetchTime.isPresent() ? lastSuccessfulFetchTime.get().toString() : "none"));
        log.debug("Now: " + now.toString());
        log.debug("TTL: " + this.EXPIRY_TIME_TO_LIVE.toString());
        return lastSuccessfulFetchTime.isPresent() && now.isAfter(lastSuccessfulFetchTime.get().plus(this.EXPIRY_TIME_TO_LIVE));
    }

    public void reset() {
        this.clearLastSuccessfulFetchTime();
    }

    private Optional<Instant> getLastSuccessfulFetchTime() {
        Long timeMillis = NumberUtils.createLong((String)((String)this.pluginSettingsFactory.createGlobalSettings().get(LAST_SUCCESSFUL_FETCH_TIME_SETTINGS_KEY)));
        return timeMillis != null ? Optional.of(Instant.ofEpochMilli(timeMillis)) : Optional.empty();
    }

    private void setLastSuccessfulFetchTime(Instant time) {
        this.pluginSettingsFactory.createGlobalSettings().put(LAST_SUCCESSFUL_FETCH_TIME_SETTINGS_KEY, (Object)String.valueOf(time.toEpochMilli()));
    }

    private void clearLastSuccessfulFetchTime() {
        this.pluginSettingsFactory.createGlobalSettings().remove(LAST_SUCCESSFUL_FETCH_TIME_SETTINGS_KEY);
    }
}

