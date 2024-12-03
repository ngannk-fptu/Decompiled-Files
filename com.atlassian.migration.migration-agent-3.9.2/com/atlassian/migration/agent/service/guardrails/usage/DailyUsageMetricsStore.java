/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.core.util.FileUtils
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails.usage;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.core.util.FileUtils;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.guardrails.logs.PageType;
import com.atlassian.migration.agent.service.guardrails.logs.UsageMetricsNodeData;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageDetails;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageSummary;
import com.atlassian.migration.agent.service.guardrails.util.CsvBuilder;
import com.atlassian.migration.agent.service.guardrails.util.SerializationUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DailyUsageMetricsStore {
    private static final Logger log = LoggerFactory.getLogger(DailyUsageMetricsStore.class);
    private static final Pattern REGEX_YYYYMMDD = Pattern.compile("\\d{8}");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String USAGE_METRICS_FOLDER = "usage-metrics";
    private static final String USAGE_FILE_PREFIX = "usage-metrics-";
    private static final String COMBINED_USAGE_FILE_NAME = "combined-usage-metrics";
    private final BootstrapManager bootstrapManager;
    private final MigrationDarkFeaturesManager features;

    public DailyUsageMetricsStore(BootstrapManager bootstrapManager, MigrationDarkFeaturesManager features) {
        this.bootstrapManager = bootstrapManager;
        this.features = features;
    }

    public boolean hasData(String nodeId, LocalDate date) {
        return this.filePath(date, nodeId).toFile().exists();
    }

    public Optional<Instant> lastModified(String nodeId, LocalDate date) {
        return Optional.of(this.filePath(date, nodeId).toFile()).filter(File::exists).map(File::lastModified).map(Instant::ofEpochMilli);
    }

    public void storePartial(DailyUsageDetails metrics) throws IOException {
        Path path = this.filePath(metrics.getDate(), ((UsageMetricsNodeData)Iterables.getOnlyElement(metrics.getNodes())).getId());
        SerializationUtil.saveJson(path.toFile(), metrics);
    }

    public void combine(LocalDate min, LocalDate max, List<String> requiredNodes) {
        File[] dirs = this.getMetricsDir().toFile().listFiles();
        if (dirs == null) {
            return;
        }
        ArrayList toDelete = new ArrayList();
        this.getDirectories(min, max, toDelete::add).forEach((date, dir) -> {
            try {
                this.combineDate((LocalDate)date, (File)dir, requiredNodes);
            }
            catch (Exception e) {
                log.info("Failed to compute daily usage metrics for {}", (Object)dir.getName(), (Object)e);
            }
        });
        toDelete.forEach(FileUtils::recursiveDelete);
    }

    public int getProgress(LocalDate min, LocalDate max) {
        return (int)(100L * this.streamSummaryFiles(min, max).count() / (long)(Period.between(min, max).getDays() + 1));
    }

    public List<DailyUsageSummary> listSummaries(LocalDate min, LocalDate max) {
        return this.streamSummaryFiles(min, max).flatMap(file -> {
            try {
                return Stream.of(SerializationUtil.deserializeJson(file, DailyUsageSummary.class));
            }
            catch (Exception e) {
                log.info("Error reading file {}", file);
                return Stream.empty();
            }
        }).sorted(Comparator.comparing(DailyUsageSummary::getDate)).collect(Collectors.toList());
    }

    private Stream<File> streamSummaryFiles(LocalDate min, LocalDate max) {
        return this.getDirectories(min, max, new ArrayList()::add).values().stream().map(this::combinedPath).filter(File::exists);
    }

    private Map<LocalDate, File> getDirectories(LocalDate min, LocalDate max, Consumer<File> toDelete) {
        File[] dirs = this.getMetricsDir().toFile().listFiles();
        if (dirs == null) {
            return Collections.emptyMap();
        }
        HashMap<LocalDate, File> result = new HashMap<LocalDate, File>();
        for (File dir : dirs) {
            String name = dir.getName();
            if (!REGEX_YYYYMMDD.matcher(name).matches()) continue;
            LocalDate date = LocalDate.from(DATE_FORMATTER.parse(name));
            if (date.isBefore(min)) {
                toDelete.accept(dir);
                continue;
            }
            if (date.isAfter(max)) continue;
            result.put(date, dir);
        }
        return result;
    }

    private Path filePath(LocalDate localDate, String nodeId) {
        return this.dateDir(localDate).resolve(USAGE_FILE_PREFIX + nodeId);
    }

    private Path dateDir(LocalDate localDate) {
        return this.getMetricsDir().resolve(DATE_FORMATTER.format(localDate));
    }

    private File combinedPath(File directory) {
        return directory.toPath().resolve(COMBINED_USAGE_FILE_NAME).toFile();
    }

    private void combineDate(LocalDate date, File dir, List<String> requiredNodes) throws IOException {
        File combined = this.combinedPath(dir);
        if (combined.exists()) {
            return;
        }
        File[] files = dir.listFiles(file -> file.getName().startsWith(USAGE_FILE_PREFIX));
        if (files == null) {
            return;
        }
        DailyUsageDetails.DailyUsageDetailsBuilder builder = new DailyUsageDetails.DailyUsageDetailsBuilder().date(date);
        for (File file2 : files) {
            if (SerializationUtil.isTemporaryFileName(file2.getName())) continue;
            builder.add(SerializationUtil.deserializeJson(file2, DailyUsageDetails.class));
        }
        builder.addNodesIfMissing(requiredNodes);
        SerializationUtil.saveJson(combined, builder.build().toDailyUsageSummary());
        for (File file2 : files) {
            Files.delete(file2.toPath());
        }
    }

    private Path getMetricsDir() {
        return this.bootstrapManager.getSharedHome().toPath().resolve(USAGE_METRICS_FOLDER);
    }

    @Nullable
    public Path createDailyUsageMetricsCsv(Path csvFilePath, LocalDate today) throws IOException {
        List<DailyUsageSummary> usageMetrics = this.fetchForReport(today);
        if (usageMetrics.isEmpty()) {
            return null;
        }
        return new CsvBuilder<DailyUsageSummary>().addColumn("Date", usage -> CSV_DATE_FORMATTER.format(usage.getDate())).addColumn("Active users", DailyUsageSummary::getUniqueUsers).addColumn("Peak hour users", DailyUsageSummary::getPeakHourUsers).addColumn("Nodes", summary -> Jsons.valueAsString(summary.getNodes())).build(csvFilePath, usageMetrics);
    }

    @Nullable
    public Path createPageTrafficDistributionCsv(Path csvFilePath, LocalDate today) throws IOException {
        List<DailyUsageSummary> usageMetrics = this.fetchForReport(today);
        if (usageMetrics.isEmpty()) {
            return null;
        }
        EnumMap counts = new EnumMap(PageType.class);
        for (DailyUsageSummary metrics : usageMetrics) {
            metrics.getRequestsTypeCount().forEach((type, count) -> counts.merge((PageType)((Object)type), count, Integer::sum));
        }
        counts.remove((Object)PageType.UNKNOWN);
        counts.remove((Object)PageType.REST);
        long sum = counts.values().stream().mapToLong(Integer::longValue).sum();
        List data = counts.entrySet().stream().sorted(Map.Entry.comparingByValue().reversed()).collect(Collectors.toList());
        return new CsvBuilder<Map.Entry>().addColumn("Action", entry -> this.formatPageTypeName((PageType)((Object)((Object)entry.getKey())))).addColumn("Traffic", entry -> String.format("%.2f%%", (double)((long)((Integer)entry.getValue() * 10000) / sum) / 100.0)).addColumn("ID", Map.Entry::getKey).build(csvFilePath, data);
    }

    private String formatPageTypeName(PageType pageType) {
        String name = pageType.name();
        name = name.toLowerCase().replace("_", " ");
        String[] words = name.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private List<DailyUsageSummary> fetchForReport(LocalDate today) {
        if (this.features.isBrowserMetricsEnabled()) {
            return this.listSummaries(today.minusDays(14L), today.minusDays(1L));
        }
        return ImmutableList.of();
    }
}

