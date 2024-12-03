/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.supercsv.io.CsvListWriter
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.export.MigrationExportException;
import com.atlassian.migration.agent.newexport.CSVExportTaskContext;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.DescriptorBuilder;
import com.atlassian.migration.agent.newexport.Queries;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.processor.CsvSerializingProcessor;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import com.atlassian.migration.agent.newexport.processor.UserKeyColumnExtractor;
import com.atlassian.migration.agent.newexport.processor.UserWithEmailSerializingProcessor;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.UserMappingsManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;

public abstract class RapidExporter<T extends CSVExportTaskContext> {
    private static final Logger log = LoggerFactory.getLogger(RapidExporter.class);
    public static final int BATCH_SIZE_FOR_IN_OPERATOR = 1000;
    private static final String EXPORT_COUNTS_FILE_SUFFIX = "_export_counts.csv.gz";
    private static final Supplier<Instant> DEFAULT_INSTANT_SUPPLER = Instant::now;
    protected final UserMappingsFileManager userMappingsFileManager;
    protected final JdbcConfluenceStore confluenceStore;
    protected final DescriptorBuilder descriptorBuilder;
    protected final Supplier<Instant> instantSupplier;
    protected final AnalyticsEventService analyticsEventService;
    protected final AnalyticsEventBuilder analyticsEventBuilder;
    protected final DbType dbType;

    protected RapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        this(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder, DEFAULT_INSTANT_SUPPLER);
    }

    @VisibleForTesting
    protected RapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, Supplier<Instant> instantSupplier) {
        this.confluenceStore = confluenceStore;
        this.descriptorBuilder = descriptorBuilder;
        this.userMappingsFileManager = userMappingsFileManager;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.instantSupplier = instantSupplier;
        this.dbType = migrationAgentConfiguration.getDBType();
    }

    public DbType getDbType() {
        return this.dbType;
    }

    protected void exportFileCount(String exportDir, T taskConfig, String prefix) {
        String fileName = prefix + EXPORT_COUNTS_FILE_SUFFIX;
        String filePath = exportDir + fileName;
        try (OutputStreamWriter outputStreamWriter = this.createOutputStreamWriter(filePath);
             CsvListWriter csvWriter = new CsvListWriter((Writer)outputStreamWriter, CsvSerializingProcessor.DEFAULT_PREFERENCE);){
            String[] header = new String[]{"entityName", "exportCount"};
            csvWriter.writeHeader(header);
            for (Map.Entry<String, Long> entry : ((CSVExportTaskContext)taskConfig).getFileRowCount().entrySet()) {
                String[] row = new String[]{entry.getKey(), entry.getValue().toString()};
                csvWriter.write(row);
            }
            csvWriter.flush();
            log.info("Export count written to CSV file: {} successfully!", (Object)filePath);
        }
        catch (Exception ex) {
            log.error("Error while serializing export count. Reason: {}", (Object)ex.getMessage(), (Object)ex);
        }
    }

    protected void runQueries(Query[] queries, BiConsumer<Query, RowProcessor> queryRunner, String exportDir, T taskConfig) {
        for (Query query : queries) {
            long start = this.instantSupplier.get().toEpochMilli();
            String fileName = query.exportName + ".csv.gz";
            String filePath = exportDir + fileName;
            try (OutputStreamWriter outputStreamWriter = this.createOutputStreamWriter(filePath);){
                CsvSerializingProcessor csvProcessor = new CsvSerializingProcessor(outputStreamWriter, this.instantSupplier);
                queryRunner.accept(query, csvProcessor);
                long totalTime = this.instantSupplier.get().toEpochMilli() - start;
                long timeToFirstRecord = csvProcessor.getTimeOfFirstRecord() > 0L ? csvProcessor.getTimeOfFirstRecord() - start : totalTime;
                long rowCount = csvProcessor.getRowCount();
                ((CSVExportTaskContext)taskConfig).increaseTotalRowCount(rowCount);
                ((CSVExportTaskContext)taskConfig).increaseTotalCharactersExported(csvProcessor.getTotalContentCharacters());
                ((CSVExportTaskContext)taskConfig).addFileRowCount(query.exportName, rowCount);
                this.reportExportTablePerformance(taskConfig, true, query.exportName, query.sql, this.dbType.name(), totalTime, timeToFirstRecord, rowCount, csvProcessor.getTotalContentCharacters());
                this.logResults(query.tableName, taskConfig, totalTime);
            }
            catch (UncheckedInterruptedException e) {
                throw new UncheckedInterruptedException(e);
            }
            catch (Exception e) {
                log.error("Failed to execute export for query " + query.sql, (Throwable)e);
                this.reportExportTablePerformance(taskConfig, false, query.exportName, query.sql, this.dbType.name(), this.instantSupplier.get().toEpochMilli() - start, -1L, -1L, -1L);
                throw new MigrationExportException("Failed to execute export for query", e);
            }
        }
    }

    protected OutputStreamWriter createOutputStreamWriter(String filePath) throws IOException {
        boolean existed = Files.exists(Paths.get(filePath, new String[0]), new LinkOption[0]);
        if (!existed) {
            Files.createFile(Paths.get(filePath, new String[0]), new FileAttribute[0]);
        }
        FileOutputStream outputStream = new FileOutputStream(filePath, true);
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        return new OutputStreamWriter((OutputStream)gzipOutputStream, StandardCharsets.UTF_8);
    }

    protected BiConsumer<Query, RowProcessor> createQueryRunner(Map<String, ?> queryParams, Set<String> extractedUserKeys) {
        return (query, processor) -> this.confluenceStore.queryAndProcess((Query)query, queryParams, new UserKeyColumnExtractor((RowProcessor)processor, extractedUserKeys, query.userkeyColums));
    }

    protected BiConsumer<Query, RowProcessor> createQueryRunner(Map<String, ?> queryParams, BiFunction<Query, RowProcessor, RowProcessor> rowProcessorFactory) {
        return (query, processor) -> this.confluenceStore.queryAndProcess((Query)query, queryParams, (RowProcessor)rowProcessorFactory.apply((Query)query, (RowProcessor)processor));
    }

    protected BiConsumer<Query, RowProcessor> createQueryRunner(Map<String, ?> queryParams) {
        return (query, processor) -> this.confluenceStore.queryAndProcess((Query)query, queryParams, (RowProcessor)processor);
    }

    protected void exportUserMappings(String exportDir, Set<String> discoveredUserKeys, T taskConfig, UserMappingsManager userMappingsManager) {
        Query userMappingQuery = new Query(this.getUserMappingQueryString(discoveredUserKeys), "user_mapping", "user_mapping");
        this.runQueries(new Query[]{userMappingQuery}, this.createQueryRunner(Collections.emptyMap(), (Query query, RowProcessor processor) -> new UserWithEmailSerializingProcessor((RowProcessor)processor, userMappingsManager)), exportDir, taskConfig);
    }

    @VisibleForTesting
    String getUserMappingQueryString(Set<String> discoveredUserKeys) {
        discoveredUserKeys.removeIf(String::isEmpty);
        if (!discoveredUserKeys.stream().allMatch(e -> e.matches("[a-zA-Z\\d:-]*"))) {
            throw new RuntimeException("Invalid characters in userKeys . Only alphanumeric characters are allowed.");
        }
        ArrayList userKeyBatches = Lists.newArrayList((Iterable)Iterables.partition(discoveredUserKeys, (int)1000));
        ArrayList clauses = Lists.newArrayList((Object[])new String[]{"1=0"});
        for (List userKeyBatch : userKeyBatches) {
            StringBuilder sb = new StringBuilder();
            for (String key : userKeyBatch) {
                sb.append(String.format("'%s',", key));
            }
            sb.deleteCharAt(sb.length() - 1);
            clauses.add(String.format("user_key in (%s)", sb));
        }
        return String.format(Queries.userMappingsQueryString(this.dbType), String.join((CharSequence)" or ", clauses));
    }

    protected abstract void logResults(String var1, T var2, long var3);

    public abstract String export(T var1) throws AccessDeniedException;

    public abstract void reportExportTablePerformance(T var1, boolean var2, String var3, String var4, String var5, long var6, long var8, long var10, long var12);
}

