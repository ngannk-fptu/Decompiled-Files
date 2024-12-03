/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MasterDetailConfigurator {
    private final Supplier<Boolean> isLegacyParsingEnabled = () -> darkFeatureManager.isEnabledForAllUsers("masterdetail.legacy.parse").orElse(false);
    private static Logger logger = LoggerFactory.getLogger(MasterDetailConfigurator.class);
    @Value(value="${pagePropertiesReportContentRetrieverMaxResult:3000}")
    private int pagePropertiesReportContentRetrieverMaxResult;
    @Value(value="${pagePropertiesReportContentRetrieverBatchSize:500}")
    private int pagePropertiesReportContentRetrieverBatchSize;
    @Value(value="${pagePropertiesReportBodyContentRetrieverBatchSize:200}")
    private int pagePropertiesReportBodyContentRetrieverBatchSize;
    @Value(value="${confluence.masterdetails.thread.pool.size:4}")
    private int pagePropertiesThreadPoolSize;
    @Value(value="${confluence.masterdetails.maximum.recursion.depth:2}")
    private int pagePropertiesReportMaximumRecursionDepth;

    MasterDetailConfigurator(@ComponentImport DarkFeatureManager darkFeatureManager) {
    }

    public int getPagePropertiesThreadPoolSize() {
        if (this.pagePropertiesThreadPoolSize <= this.pagePropertiesReportMaximumRecursionDepth) {
            logger.error("confluence.masterdetails.thread.pool.size must be greater than recursion depth of {}", (Object)this.pagePropertiesReportMaximumRecursionDepth);
            return this.pagePropertiesReportMaximumRecursionDepth + 1;
        }
        return this.pagePropertiesThreadPoolSize;
    }

    public int getPagePropertiesReportContentRetrieverMaxResult() {
        if (this.isLegacyParsingEnabled.get().booleanValue()) {
            return 1000;
        }
        return this.pagePropertiesReportContentRetrieverMaxResult;
    }

    public int getPagePropertiesReportContentRetrieverBatchSize() {
        return this.pagePropertiesReportContentRetrieverBatchSize;
    }

    public int getPagePropertiesReportBodyContentRetrieverBatchSize() {
        return this.pagePropertiesReportBodyContentRetrieverBatchSize;
    }

    public int getPagePropertiesReportMaximumRecursionDepth() {
        if (this.pagePropertiesReportMaximumRecursionDepth <= 0) {
            logger.error("confluence.masterdetails.maximum.recursion.depth must a positive integer");
            return 2;
        }
        return this.pagePropertiesReportMaximumRecursionDepth;
    }
}

