/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.PreDestroy
 *  javax.inject.Named
 *  org.apache.commons.codec.binary.StringUtils
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.assessment.BrowserMetricsDto;
import com.atlassian.migration.agent.entity.BrowserMetrics;
import com.atlassian.migration.agent.service.guardrails.InvalidDataException;
import com.atlassian.migration.agent.store.guardrails.GuardrailsBrowserMetricsStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class BrowserMetricsService {
    private static final int FILE_BUFFER_SIZE = 0x100000;
    private static final Integer PAGE_SIZE = 20000;
    private final GuardrailsBrowserMetricsStore guardrailsBrowserMetricsStore;
    private final MigrationDarkFeaturesManager features;
    private final BootstrapManager bootstrapManager;
    private final Logger log = LoggerFactory.getLogger(BrowserMetricsService.class);
    private final byte[] seed;
    private final Cache<String, Long> browserMetricsCache;
    private final PluginTransactionTemplate ptx;
    private static final String BROWSER_METRICS_CACHE_NAME = "com.atlassian.migration.agent.service.guardrails.browserMetricsCache";

    public BrowserMetricsService(GuardrailsBrowserMetricsStore guardrailsBrowserMetricsStore, MigrationDarkFeaturesManager features, CacheManager cacheManager, BootstrapManager bootstrapManager, PluginTransactionTemplate ptx) {
        this(guardrailsBrowserMetricsStore, features, cacheManager, bootstrapManager, ptx, new SecureRandom().generateSeed(32));
    }

    @VisibleForTesting
    protected BrowserMetricsService(GuardrailsBrowserMetricsStore guardrailsBrowserMetricsStore, MigrationDarkFeaturesManager features, CacheManager cacheManager, BootstrapManager bootstrapManager, PluginTransactionTemplate ptx, byte[] seed) {
        this.guardrailsBrowserMetricsStore = guardrailsBrowserMetricsStore;
        this.features = features;
        this.bootstrapManager = bootstrapManager;
        this.ptx = ptx;
        this.seed = seed;
        int cacheMaxEntries = 100000;
        long cacheExpirationTime = 6L;
        this.browserMetricsCache = cacheManager.getCache(BROWSER_METRICS_CACHE_NAME, null, new CacheSettingsBuilder().expireAfterWrite(cacheExpirationTime, TimeUnit.HOURS).maxEntries(cacheMaxEntries).local().build());
    }

    @PreDestroy
    void preDestroy() {
        this.browserMetricsCache.removeAll();
        this.log.info("Browser-metrics cache has been cleared.");
    }

    public void recordBrowserMetrics(ConfluenceUser loggedInUser, BrowserMetricsDto browserMetricsDto) {
        if (this.shouldCollectBrowserMetrics(loggedInUser)) {
            this.validateMetrics(browserMetricsDto);
            BrowserMetrics result = this.ptx.write(() -> this.guardrailsBrowserMetricsStore.createBrowserMetrics(loggedInUser, browserMetricsDto));
            this.insertUserToCache(result.getUserKey(), result.getCreatedAt());
        }
    }

    public boolean shouldCollectBrowserMetrics(ConfluenceUser loggedInUser) {
        if (this.features.isBrowserMetricsEnabled()) {
            if (this.browserMetricsCache.containsKey((Object)loggedInUser.getKey().getStringValue())) {
                this.log.info("Browser metrics was already collected for user ${loggedInUser.key} in the last 24h.");
                return false;
            }
            return this.checkMetricInDB(loggedInUser);
        }
        return false;
    }

    private Boolean checkMetricInDB(ConfluenceUser loggedInUser) {
        Optional<BrowserMetrics> browserMetrics = this.guardrailsBrowserMetricsStore.findMostRecent(loggedInUser);
        if (browserMetrics.isPresent()) {
            this.insertUserToCache(browserMetrics.get().getUserKey(), browserMetrics.get().getCreatedAt());
            return false;
        }
        return true;
    }

    private void insertUserToCache(String userKey, Long createdAt) {
        this.browserMetricsCache.put((Object)userKey, (Object)createdAt);
    }

    public void validateMetrics(BrowserMetricsDto browserMetricsDto) {
        this.log.info("Validating metrics data received from the browser.");
        if (browserMetricsDto.getDevice() != null) {
            this.validateProcessors(browserMetricsDto.getDevice().getProcessors());
            this.validateMemory(browserMetricsDto.getDevice().getMemory());
        }
        if (browserMetricsDto.getNetwork() != null) {
            this.validateDownlink(browserMetricsDto.getNetwork().getDownlink());
            this.validateRtt(browserMetricsDto.getNetwork().getRtt());
            this.validateEffectiveType(browserMetricsDto.getNetwork().getEffectiveType());
        }
        this.validatePlatform(browserMetricsDto.getPlatform());
        this.validateBrowserName(browserMetricsDto.getBrowserName());
    }

    public void validateProcessors(Integer processors) {
        int maxProcessors = 64;
        if (processors == null || processors < 0) {
            throw new InvalidDataException("Number of processors invalid. It should be between 1 and " + maxProcessors + "Value provided " + processors);
        }
        if (processors > maxProcessors) {
            throw new InvalidDataException("Number of processors invalid. It should be between 1 and " + maxProcessors + "Value provided " + processors);
        }
    }

    private void validateMemory(Integer memory) {
        if (memory == null) {
            throw new InvalidDataException("Memory is invalid. It shouldn't be a null number. Value provided " + memory);
        }
        if (memory <= 0) {
            throw new InvalidDataException("Memory is invalid. It shouldn't be a negative number. Value provided " + memory);
        }
    }

    private void validateDownlink(Integer downlink) {
        if (downlink == null) {
            throw new InvalidDataException("Downlink is invalid. It shouldn't be a null number. Value provided " + downlink);
        }
        if (downlink <= 0) {
            throw new InvalidDataException("Downlink is invalid. It shouldn't be a negative number. Value provided " + downlink);
        }
    }

    private void validateRtt(Integer rtt) {
        if (rtt == null) {
            throw new InvalidDataException("RTT is invalid. It shouldn't be a null number. Value provided " + rtt);
        }
        if (rtt <= 0) {
            throw new InvalidDataException("RTT is invalid. It shouldn't be a negative number. Value provided " + rtt);
        }
    }

    private void validateEffectiveType(String effectiveType) {
        if (effectiveType == null) {
            throw new InvalidDataException("Effective type is null");
        }
        int maxEffectiveTypeLength = 255;
        if (effectiveType.length() > maxEffectiveTypeLength) {
            throw new InvalidDataException("Effective type is invalid. Length is greater than 255 characters.");
        }
    }

    private void validatePlatform(String platform) {
        if (platform == null) {
            throw new InvalidDataException("Platform is null");
        }
        int maxPlatformLength = 512;
        if (platform.length() > maxPlatformLength) {
            throw new InvalidDataException("Platform is invalid. Length is greater than 512 characters.");
        }
    }

    private void validateBrowserName(String browserName) {
        if (browserName == null) {
            throw new InvalidDataException("User agent is null");
        }
        int maxUseragentLength = 512;
        if (browserName.length() > maxUseragentLength) {
            throw new InvalidDataException("User agent is invalid. Length is greater than 512 characters.");
        }
    }

    Path exportBrowserMetrics(String filename) throws IOException {
        this.log.info("Exporting browser-metrics.");
        if (!this.features.isBrowserMetricsEnabled()) {
            this.log.info("No browser-metrics to export.");
            return null;
        }
        Path jsonPathFile = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), filename + ".json");
        try {
            Files.deleteIfExists(jsonPathFile);
            Files.createFile(jsonPathFile, new FileAttribute[0]);
            long start = System.currentTimeMillis();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(jsonPathFile.toFile().toPath(), new OpenOption[0]), StandardCharsets.UTF_8), 0x100000);){
                try {
                    this.writeBrowserMetricsTo(writer);
                }
                catch (RuntimeException e) {
                    throw new IOException("Unable to export browser-metrics: " + e.getMessage(), e);
                }
            }
            this.log.info("Metrics written in {}", (Object)Duration.ofMillis(System.currentTimeMillis() - start));
        }
        catch (IOException e) {
            throw new IOException("Unable to create or write to file", e);
        }
        this.log.info("Browser-metrics exported.");
        return jsonPathFile;
    }

    private void writeBrowserMetricsTo(BufferedWriter writer) throws IOException {
        int offset = 0;
        while (true) {
            List<BrowserMetrics> metricsList;
            if ((metricsList = this.guardrailsBrowserMetricsStore.getPage(PAGE_SIZE, offset)).isEmpty()) {
                if (offset != 0) break;
                writer.newLine();
                break;
            }
            for (BrowserMetrics metrics : metricsList) {
                writer.write(this.metricsToString(metrics));
            }
            offset += PAGE_SIZE.intValue();
        }
    }

    private String metricsToString(BrowserMetrics metrics) {
        return "{\"user\":\"" + this.hashUserKey(metrics.getUserKey()) + "\",\"browserData\":" + metrics.getMetricsJson() + "}\n";
    }

    private String hashUserKey(String userKey) {
        return DigestUtils.sha256Hex((byte[])ArrayUtils.addAll((byte[])StringUtils.getBytesUtf8((String)userKey), (byte[])this.seed));
    }
}

