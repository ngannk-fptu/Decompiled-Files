/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.LineIterator
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.checks.jvm;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.JvmMemoryInfo;
import com.atlassian.troubleshooting.api.healthcheck.LogFileHelper;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeCacheHealthCheck
implements SupportHealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(CodeCacheHealthCheck.class);
    private static final String CHECK_KEY = "healthcheck.codecache";
    private static final long KB = 1024L;
    private static final long MB = 0x100000L;
    private static final long GB = 0x40000000L;
    private static final Set<String> UNIT_MB = ImmutableSet.of((Object)"mb", (Object)"m");
    private static final Set<String> UNIT_KB = ImmutableSet.of((Object)"kb", (Object)"k");
    private static final Set<String> UNIT_GB = ImmutableSet.of((Object)"GB", (Object)"G");
    private static final String CODECACHEEXPANSIONSIZE_NAME = "CodeCacheExpansionSize";
    private static final long DEFAULT_CODECACHEEXPANSIONSIZE_X86 = 32768L;
    private static final int EXPANSION_SIZE_FUDGE_FACTOR = 20;
    private static final long JIRA_RECOMMENDED_CODECACHE_SIZE = 0x20000000L;
    private static final long CONFLUENCE_RECOMMENDED_CODECACHE_SIZE = 0x10000000L;
    private static final String RECOMMENDED_CODECACHE_SIZE_UNIT = "MB";
    private static final String RECOMMENDED_CODECACHE_SIZE_KEY = "codeCacheSizeBelowRecommendation";
    private static final Map<String, Long> APPLICATION_CODECACHE_RECOMMENDATIONS = ImmutableMap.of((Object)Application.JIRA.name(), (Object)0x20000000L, (Object)Application.Confluence.name(), (Object)0x10000000L);
    private static final double WARNING_THRESHOLD = 0.001;
    private static final String WARNING_THRESHOLD_MESSAGE_KEY = "approachingLimits";
    private static final String[] COMPILATIONLOG_FAILURE_MESSAGES = new String[]{"<failure reason='CodeCache is full'", "<failure reason='compilation is disabled'", "code_cache_full", "compilation: disabled"};
    private static final String[] CATALINAOUT_FAILURE_MESSAGES = new String[]{"is full. Compiler has been disabled"};
    private static final String[] APPLICATION_START_MESSAGES = new String[]{"jira starting...", "starting confluence..."};
    private static final String CRITICAL_MESSAGE_KEY = "codeCacheFailure";
    private static final Pattern MEMORY_VALUE_PATTERN = Pattern.compile(".*=([0-9]*)([a-zA-Z].*)?");
    private final JvmMemoryInfo jvmMemoryInfo;
    private final LogFileHelper logFileHelper;
    private final SupportHealthStatusBuilder healthStatusBuilder;
    private final ApplicationProperties applicationProperties;

    CodeCacheHealthCheck(JvmMemoryInfo jvmMemoryInfo, LogFileHelper logFileHelper, SupportHealthStatusBuilder healthStatusBuilder, ApplicationProperties properties) {
        this.jvmMemoryInfo = Objects.requireNonNull(jvmMemoryInfo);
        this.logFileHelper = logFileHelper;
        this.healthStatusBuilder = healthStatusBuilder;
        this.applicationProperties = properties;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.getArgsReport());
            LOG.debug(this.getMemoryPoolReport());
        }
        return this.runChecks();
    }

    private SupportHealthStatus runChecks() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = this.jvmMemoryInfo.getCodeCacheMemoryPoolMXBeans();
        SupportHealthStatus logsStatus = this.checkLogs();
        if (!logsStatus.isHealthy()) {
            return logsStatus;
        }
        if (memoryPoolMXBeans.size() == 1) {
            MemoryPoolMXBean unSegmentedBean = memoryPoolMXBeans.get(0);
            SupportHealthStatus continuousMemoryStatus = this.checkContinuousMemory(unSegmentedBean);
            if (!continuousMemoryStatus.isHealthy()) {
                return continuousMemoryStatus;
            }
        } else {
            SupportHealthStatus segmentedMemoryStatus = this.checkSegmentedMemory(memoryPoolMXBeans);
            if (!segmentedMemoryStatus.isHealthy()) {
                return segmentedMemoryStatus;
            }
        }
        return this.getOk();
    }

    private SupportHealthStatus checkSegmentedMemory(List<MemoryPoolMXBean> poolBeans) {
        for (MemoryPoolMXBean pool : poolBeans) {
            SupportHealthStatus usageStatus = this.checkUsage(pool.getUsage());
            if (!usageStatus.isHealthy()) {
                return usageStatus;
            }
            SupportHealthStatus peakStatus = this.checkUsage(pool.getPeakUsage());
            if (peakStatus.isHealthy()) continue;
            return peakStatus;
        }
        SupportHealthStatus usageRecommendationsStatus = this.checkIsUsingRecommendations(poolBeans);
        if (!usageRecommendationsStatus.isHealthy()) {
            return usageRecommendationsStatus;
        }
        return this.getOk();
    }

    private SupportHealthStatus checkContinuousMemory(MemoryPoolMXBean poolBean) {
        SupportHealthStatus usageStatus = this.checkUsage(poolBean.getUsage());
        if (!usageStatus.isHealthy()) {
            return usageStatus;
        }
        SupportHealthStatus peakRecommendationsStatus = this.checkIsUsingRecommendations(poolBean.getUsage());
        if (!peakRecommendationsStatus.isHealthy()) {
            return peakRecommendationsStatus;
        }
        SupportHealthStatus peakStatus = this.checkUsage(poolBean.getPeakUsage());
        if (!peakStatus.isHealthy()) {
            return peakStatus;
        }
        SupportHealthStatus usageRecommendationsStatus = this.checkIsUsingRecommendations(poolBean.getPeakUsage());
        if (!usageRecommendationsStatus.isHealthy()) {
            return usageRecommendationsStatus;
        }
        return this.getOk();
    }

    private SupportHealthStatus checkLogs() {
        SupportHealthStatus compilationLogStatus = this.checkCompilationLog();
        if (!compilationLogStatus.isHealthy()) {
            return compilationLogStatus;
        }
        SupportHealthStatus catalinaOutStatus = this.checkCatalinaOut();
        if (!catalinaOutStatus.isHealthy()) {
            return catalinaOutStatus;
        }
        return this.getOk();
    }

    private SupportHealthStatus checkUsage(MemoryUsage usage) {
        SupportHealthStatus committedStatus = this.checkHasUncommittedMemory(usage);
        if (!committedStatus.isHealthy()) {
            return committedStatus;
        }
        SupportHealthStatus approachingLimitsStatus = this.checkApproachingExpansionLimits(usage);
        if (!approachingLimitsStatus.isHealthy()) {
            return approachingLimitsStatus;
        }
        return this.getOk();
    }

    private SupportHealthStatus checkApproachingExpansionLimits(MemoryUsage usage) {
        long max = usage.getMax();
        long used = usage.getUsed();
        long unused = max - used;
        long committed = usage.getCommitted();
        long uncommitted = max - committed;
        if ((double)uncommitted / ((double)max * 1.0) < 0.001) {
            LOG.warn("Code Cache is approaching limits: Uncommitted is less than {} of maximum", (Object)0.001);
            return this.getWarning(WARNING_THRESHOLD_MESSAGE_KEY);
        }
        if ((double)unused / ((double)max * 1.0) < 0.001) {
            LOG.warn("Code Cache is approaching limits: Unused is less than {} of maximum", (Object)0.001);
            return this.getWarning(WARNING_THRESHOLD_MESSAGE_KEY);
        }
        long expansionSize = this.getExpansionSize();
        if (unused < expansionSize * 20L) {
            LOG.warn("Code Cache is approaching limits: Unused is less than {} times the expansize size {}", (Object)20, (Object)expansionSize);
            return this.getWarning(WARNING_THRESHOLD_MESSAGE_KEY);
        }
        if (uncommitted < expansionSize * 20L) {
            LOG.warn("Code Cache is approaching limits: Uncommitted is less than {} times the expansize size {}", (Object)20, (Object)expansionSize);
            return this.getWarning(WARNING_THRESHOLD_MESSAGE_KEY);
        }
        return this.getOk();
    }

    private SupportHealthStatus checkIsUsingRecommendations(List<MemoryPoolMXBean> memoryPoolMXBeans) {
        long max = memoryPoolMXBeans.stream().mapToLong(pool -> pool.getUsage().getMax()).sum();
        SupportHealthStatus recommendationStatus = this.checkIsUsingRecommendations(max);
        if (!recommendationStatus.isHealthy()) {
            return recommendationStatus;
        }
        long peakMax = memoryPoolMXBeans.stream().mapToLong(b -> b.getPeakUsage().getMax()).sum();
        SupportHealthStatus peakRecommendationStatus = this.checkIsUsingRecommendations(peakMax);
        if (!peakRecommendationStatus.isHealthy()) {
            return peakRecommendationStatus;
        }
        return this.getOk();
    }

    private SupportHealthStatus checkIsUsingRecommendations(MemoryUsage usage) {
        long max = usage.getMax();
        return this.checkIsUsingRecommendations(max);
    }

    private SupportHealthStatus checkIsUsingRecommendations(long max) {
        Long recommendedValue;
        Optional<Long> recommendation = this.getRecommendedCodeCacheSize();
        if (recommendation.isPresent() && max < (recommendedValue = recommendation.get())) {
            LOG.warn("Code Cache max {} is below the recommended {}", (Object)max, (Object)recommendedValue);
            return this.getWarning(RECOMMENDED_CODECACHE_SIZE_KEY, new Serializable[]{Long.valueOf(recommendedValue / 0x100000L), RECOMMENDED_CODECACHE_SIZE_UNIT});
        }
        return this.getOk();
    }

    private Optional<Long> getRecommendedCodeCacheSize() {
        String name = this.applicationProperties.getDisplayName();
        return Optional.ofNullable(APPLICATION_CODECACHE_RECOMMENDATIONS.get(name));
    }

    private SupportHealthStatus checkHasUncommittedMemory(MemoryUsage usage) {
        long uncommitted = usage.getMax() - usage.getCommitted();
        if (uncommitted == 0L) {
            LOG.warn("Code Cache is approaching limits: {} memory committed out of {} maximum", (Object)usage.getMax(), (Object)usage.getCommitted());
            return this.getWarning(WARNING_THRESHOLD_MESSAGE_KEY);
        }
        return this.getOk();
    }

    private SupportHealthStatus checkCatalinaOut() {
        return this.checkLog(this.logFileHelper.getCurrentCatalinaOut(), CATALINAOUT_FAILURE_MESSAGES, line -> {
            if (line == null) {
                return false;
            }
            return this.logLineContainsMessage((String)line, APPLICATION_START_MESSAGES);
        });
    }

    private SupportHealthStatus checkCompilationLog() {
        return this.checkLog(this.logFileHelper.getCurrentCompilationLog(), COMPILATIONLOG_FAILURE_MESSAGES);
    }

    private SupportHealthStatus checkLog(File logFile, String[] failureMessages) {
        return this.checkLog(logFile, failureMessages, l -> false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SupportHealthStatus checkLog(File logFile, String[] failureMessages, Predicate<String> detectRestart) {
        if (logFile == null) {
            return this.getOk();
        }
        if (!logFile.exists()) {
            return this.getOk();
        }
        SupportHealthStatus status = this.getOk();
        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator((File)logFile, (String)"UTF-8");
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                if (detectRestart.test(line)) {
                    status = this.getOk();
                    continue;
                }
                if (!this.logLineContainsMessage(line, failureMessages)) continue;
                LOG.warn("Code Cache is full or disabled: Shown by [{}]", (Object)line);
                status = this.getCritical(CRITICAL_MESSAGE_KEY);
            }
        }
        catch (Exception e) {
            LOG.error("Error reading the compilation log", (Throwable)e);
        }
        finally {
            LineIterator.closeQuietly((LineIterator)lineIterator);
        }
        return status;
    }

    private boolean logLineContainsMessage(String line, String[] failureMessages) {
        if (line == null) {
            return false;
        }
        for (String failure : failureMessages) {
            if (failure == null || !line.toLowerCase().contains(failure.toLowerCase())) continue;
            LOG.debug("{} detected in {}", (Object)failure, (Object)line);
            return true;
        }
        return false;
    }

    private long getExpansionSize() {
        Optional<Long> arg = this.getCommandLineOverride(CODECACHEEXPANSIONSIZE_NAME);
        if (arg.isPresent()) {
            return arg.get();
        }
        return 32768L;
    }

    private Optional<Long> getCommandLineOverride(String parameterName) {
        return this.getArgs().stream().filter(a -> a.toLowerCase().startsWith(("-XX:" + parameterName).toLowerCase())).map(this::getMemoryOverrideValue).findFirst();
    }

    private Long getMemoryOverrideValue(String overrideParameterString) {
        Matcher matcher = MEMORY_VALUE_PATTERN.matcher(overrideParameterString);
        long value = 0L;
        String unit = "";
        while (matcher.find()) {
            if (matcher.groupCount() < 2) continue;
            value = Integer.parseInt(matcher.group(1));
            if (matcher.groupCount() < 2) continue;
            unit = matcher.group(2);
        }
        return this.getMemoryValue(value, unit);
    }

    private Long getMemoryValue(long value, String unit) {
        long unitMultiplier = 1L;
        if (UNIT_MB.contains(unit.toLowerCase())) {
            unitMultiplier = 0x100000L;
        } else if (UNIT_KB.contains(unit.toLowerCase())) {
            unitMultiplier = 1024L;
        } else if (UNIT_GB.contains(unit.toLowerCase())) {
            unitMultiplier = 0x40000000L;
        }
        return value * unitMultiplier;
    }

    private String getMemoryPoolReport() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        return this.getMemoryPoolReport(memoryPoolMXBeans);
    }

    private String getMemoryPoolReport(List<MemoryPoolMXBean> memoryPoolMXBeans) {
        return memoryPoolMXBeans.stream().map(this::getMemoryPoolReport).collect(Collectors.joining("", new Date(System.currentTimeMillis()).toString() + "\n", ""));
    }

    private String getMemoryPoolReport(MemoryPoolMXBean pool) {
        MemoryUsage pUsage;
        MemoryUsage cUsage;
        StringBuilder report = new StringBuilder();
        report.append("Memory Pool: ").append(pool.getName()).append("\n");
        report.append("Memory Pool Object Name: ").append(pool.getObjectName()).append("\n");
        report.append("   UsageThresholdSupported :").append(pool.isUsageThresholdSupported()).append("\n");
        if (pool.isUsageThresholdSupported()) {
            report.append("   UsageThresholdCount : ").append(pool.getUsageThresholdCount()).append("\n");
            report.append("   UsageThreshold : ").append(pool.getUsageThreshold()).append("\n");
            report.append("   UsageThresholdExceeded :").append(pool.isUsageThresholdExceeded()).append("\n");
        }
        MemoryUsage usage = pool.getUsage();
        report.append("   Usage Init : ").append(usage.getInit() / 0x100000L).append("MB (").append(usage.getInit()).append(")").append("\n");
        report.append("   Usage Used: ").append(usage.getUsed() / 0x100000L).append("MB (").append(usage.getUsed()).append(")").append("\n");
        report.append("   Usage Max : ").append(usage.getMax() / 0x100000L).append("MB (").append(usage.getMax()).append(")").append("\n");
        report.append("   Usage Committed: ").append(usage.getCommitted() / 0x100000L).append("MB (").append(usage.getCommitted()).append(")").append("\n");
        report.append("   Collection UsageThresholdSupported :").append(pool.isCollectionUsageThresholdSupported()).append("\n");
        if (pool.isCollectionUsageThresholdSupported()) {
            report.append("   Collection UsageThresholdCount : ").append(pool.getCollectionUsageThresholdCount()).append("\n");
            report.append("   Collection UsageThreshold : ").append(pool.getCollectionUsageThreshold()).append("\n");
            report.append("   Collection UsageThresholdExceeded :").append(pool.isCollectionUsageThresholdExceeded()).append("\n");
        }
        if ((cUsage = pool.getCollectionUsage()) != null) {
            report.append("   Collection Usage Init : ").append(cUsage.getInit() / 0x100000L).append("MB (").append(cUsage.getInit()).append(")").append("\n");
            report.append("   Collection Usage Used: ").append(cUsage.getUsed() / 0x100000L).append("MB (").append(cUsage.getUsed()).append(")").append("\n");
            report.append("   Collection Usage Max : ").append(cUsage.getMax() / 0x100000L).append("MB (").append(cUsage.getMax()).append(")").append("\n");
            report.append("   Collection Usage Committed: ").append(cUsage.getCommitted() / 0x100000L).append("MB (").append(cUsage.getCommitted()).append(")").append("\n");
        }
        if ((pUsage = pool.getPeakUsage()) != null) {
            report.append("   Peak Usage Init : ").append(pUsage.getInit() / 0x100000L).append("MB (").append(pUsage.getInit()).append(")").append("\n");
            report.append("   Peak Usage Used: ").append(pUsage.getUsed() / 0x100000L).append("MB (").append(pUsage.getUsed()).append(")").append("\n");
            report.append("   Peak Usage Max : ").append(pUsage.getMax() / 0x100000L).append("MB (").append(pUsage.getMax()).append(")").append("\n");
            report.append("   Peak Usage Committed: ").append(pUsage.getCommitted() / 0x100000L).append("MB (").append(pUsage.getCommitted()).append(")").append("\n");
        }
        return report.toString();
    }

    private String getArgsReport() {
        StringBuilder report = new StringBuilder();
        report.append(new Date(System.currentTimeMillis()).toString()).append("\n");
        this.getArgs().forEach(a -> report.append((String)a).append("\n"));
        return report.toString();
    }

    private List<String> getArgs() {
        return this.jvmMemoryInfo.getRuntimeMXBean().getInputArguments();
    }

    private SupportHealthStatus getWarning(String reasonSuffix) {
        return this.healthStatusBuilder.warning(this, this.getMessageKey(reasonSuffix, "warning"), new Serializable[0]);
    }

    private SupportHealthStatus getWarning(String reasonSuffix, Serializable ... objects) {
        return this.healthStatusBuilder.warning(this, this.getMessageKey(reasonSuffix, "warning"), objects);
    }

    private SupportHealthStatus getCritical(String reasonSuffix) {
        return this.healthStatusBuilder.critical(this, this.getMessageKey(reasonSuffix, "critical"), new Serializable[0]);
    }

    private SupportHealthStatus getOk() {
        return this.healthStatusBuilder.ok(this, this.getMessageKey(null, "ok"), new Serializable[0]);
    }

    private String getMessageKey(String reasonSuffix, String level) {
        return "healthcheck.codecache." + level + this.getSuffix(reasonSuffix);
    }

    private String getSuffix(String reasonSuffix) {
        if (StringUtils.isEmpty((String)reasonSuffix)) {
            return "";
        }
        if (reasonSuffix.startsWith(".")) {
            return reasonSuffix;
        }
        return "." + reasonSuffix;
    }
}

