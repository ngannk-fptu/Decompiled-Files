/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.datacenter.database.clustered.aws.aurora;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.DefaultSupportHealthCheckSupplier;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AmazonAuroraDCOnlyCheck
implements SupportHealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSupportHealthCheckSupplier.class);
    private static final String CONFLUENCE_MIN_SUPPORTED_VERSION = "Confluence Data Center 6.13";
    private static final String JIRA_MIN_SUPPORTED_VERSION = "Jira Data Center 8.4";
    private static final String HEALTH_CHECK_PREFIX = "healthcheck.datacenter.database.clustered";
    private static final String AURORA_VERSION_QUERY = "select AURORA_VERSION() as aurora_version";
    private static final String SUPPORTED_DIALECT = "postgres";
    private static final String ENGINE_IDENTIFIER = "aws.aurora";
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;
    private final DatabaseService databaseService;
    private final ApplicationProperties properties;

    @Autowired
    public AmazonAuroraDCOnlyCheck(DatabaseService databaseService, ApplicationProperties properties, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.properties = Objects.requireNonNull(properties);
        this.supportHealthStatusBuilder = Objects.requireNonNull(supportHealthStatusBuilder);
        this.databaseService = Objects.requireNonNull(databaseService);
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        if (this.databaseService.runInConnection(this::isRunningOn).booleanValue()) {
            if (this.databaseService.getDialect().toLowerCase().contains(SUPPORTED_DIALECT.toLowerCase())) {
                return this.supportHealthStatusBuilder.warning(this, this.buildKey(true), new Serializable[]{this.getResponseSubstitutionForProduct()});
            }
            return this.supportHealthStatusBuilder.ok(this, this.buildKey(false), new Serializable[0]);
        }
        return this.supportHealthStatusBuilder.ok(this, this.buildKey(false), new Serializable[0]);
    }

    private String getResponseSubstitutionForProduct() {
        Application application = Application.byAppDisplayName(this.properties.getDisplayName());
        switch (application) {
            case Confluence: {
                return CONFLUENCE_MIN_SUPPORTED_VERSION;
            }
            case JIRA: {
                return JIRA_MIN_SUPPORTED_VERSION;
            }
        }
        throw new UnsupportedOperationException(String.format("%s Is not supported with product: %s", new Object[]{this.getClass().getCanonicalName(), application}));
    }

    private String buildKey(boolean inUse) {
        return inUse ? String.format("%s.%s.supported.false", HEALTH_CHECK_PREFIX, ENGINE_IDENTIFIER) : String.format("%s.unused", HEALTH_CHECK_PREFIX);
    }

    /*
     * Exception decompiling
     */
    private boolean isRunningOn(Connection connection) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

