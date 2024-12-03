/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.dto.InstanceMetadataDto;
import com.atlassian.migration.agent.entity.GuardrailsResponse;
import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.guardrails.BrowserMetricsService;
import com.atlassian.migration.agent.service.guardrails.GuardrailsCsvOutputStreamResult;
import com.atlassian.migration.agent.service.guardrails.InstanceMetadataCollector;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageMetricsStore;
import com.atlassian.migration.agent.service.guardrails.util.CsvBuilder;
import com.atlassian.migration.agent.service.guardrails.util.ZipBuilder;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

public class InstanceAssessmentCSVService {
    private static final Logger log = ContextLoggerFactory.getLogger(InstanceAssessmentCSVService.class);
    private final BootstrapManager bootstrapManager;
    private final GuardrailsResponseStore guardrailsResponseStore;
    private final DailyUsageMetricsStore dailyUsageMetricsStore;
    private final InstanceMetadataCollector instanceMetadataCollector;
    private final BrowserMetricsService browserMetricsService;
    private final GuardrailsResponseGroupStore guardrailsResponseGroupStore;
    private final PluginTransactionTemplate ptx;

    public InstanceAssessmentCSVService(BootstrapManager bootstrapManager, GuardrailsResponseStore guardrailsResponseStore, InstanceMetadataCollector instanceMetadataCollector, BrowserMetricsService browserMetricsService, GuardrailsResponseGroupStore guardrailsResponseGroupStore, PluginTransactionTemplate ptx, DailyUsageMetricsStore dailyUsageMetricsStore) {
        this.bootstrapManager = bootstrapManager;
        this.guardrailsResponseStore = guardrailsResponseStore;
        this.instanceMetadataCollector = instanceMetadataCollector;
        this.browserMetricsService = browserMetricsService;
        this.guardrailsResponseGroupStore = guardrailsResponseGroupStore;
        this.ptx = ptx;
        this.dailyUsageMetricsStore = dailyUsageMetricsStore;
    }

    Path createBrowserMetricsJsonFile(String date) throws IOException {
        return this.browserMetricsService.exportBrowserMetrics("cloud-browser-metrics-" + date);
    }

    Path createUsageMetrics(LocalDate today, String date) throws IOException {
        return this.dailyUsageMetricsStore.createDailyUsageMetricsCsv(this.csvFilePath("usage-metrics-" + date), today);
    }

    Path createPageTrafficDistributionMetrics(LocalDate today, String date) throws IOException {
        return this.dailyUsageMetricsStore.createPageTrafficDistributionCsv(this.csvFilePath("confluence-traffic-distribution-" + date), today);
    }

    public GuardrailsCsvOutputStreamResult generate(LocalDate today, String date, OutputStream outputStream) {
        Optional<GuardrailsResponseGroup> guardrailsResponseGroup = this.guardrailsResponseGroupStore.findLastJobId();
        if (!guardrailsResponseGroup.isPresent()) {
            return GuardrailsCsvOutputStreamResult.failed(outputStream, false, new RuntimeException("Unable to generate file for download: ${e.message}"));
        }
        try {
            String jobId = guardrailsResponseGroup.get().getJobId();
            List guardrailsResponses = this.ptx.read(() -> this.guardrailsResponseStore.getResponses(jobId));
            if (guardrailsResponses.isEmpty()) {
                log.error("No records found");
                return GuardrailsCsvOutputStreamResult.failed(outputStream, true, new RuntimeException("No records found"));
            }
            ZipBuilder zipBuilder = new ZipBuilder(outputStream);
            zipBuilder.add(this.createGuardrailsCsv(guardrailsResponses, jobId, this.csvFilePath("migration-assessments-" + date)));
            zipBuilder.maybeAdd(this.createBrowserMetricsJsonFile(date));
            zipBuilder.maybeAdd(this.createUsageMetrics(today, date));
            zipBuilder.maybeAdd(this.createPageTrafficDistributionMetrics(today, date));
            zipBuilder.create(true);
            return GuardrailsCsvOutputStreamResult.succeeded(outputStream);
        }
        catch (Exception e) {
            guardrailsResponseGroup.ifPresent(responseGroup -> log.error("Unable to generate file for download: {} - {} - {}", new Object[]{responseGroup.getJobId(), e.getMessage(), ExceptionUtils.getStackTrace((Throwable)e)}));
            return GuardrailsCsvOutputStreamResult.failed(outputStream, false, new RuntimeException("Unable to generate file for download: ${e.message}"));
        }
    }

    public Path createGuardrailsCsv(List<GuardrailsResponse> responses, String jobId, Path csv) throws IOException {
        InstanceMetadataDto instanceMetadataDto = this.instanceMetadataCollector.collectMetadata(jobId);
        return new CsvBuilder<GuardrailsResponse>().addColumn("Query_item", GuardrailsResponse::getQueryId).addColumn("Query_result", GuardrailsResponse::getQueryResponse).addColumn("Query_status", GuardrailsResponse::getQueryStatus).addColumn("Product_name", instanceMetadataDto.getProduct().getName()).addColumn("Product_version", instanceMetadataDto.getProduct().getVersion()).addColumn("SEN", instanceMetadataDto.getSen()).addColumn("Server_ID", instanceMetadataDto.getServerId()).addColumn("Instance_timezone", instanceMetadataDto.getInstanceTimezone()).addColumn("Assessment_starting_date", instanceMetadataDto.getAssessmentDate()).build(csv, responses);
    }

    private Path csvFilePath(String name) {
        return this.getPath().resolve(name + ".csv");
    }

    private Path getPath() {
        return Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), "guardrails");
    }
}

