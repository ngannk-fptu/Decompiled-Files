/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.database.mysql;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.healthcheck.common.Version;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.AbstractMySQLCheck;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;

public class CollationCheck
extends AbstractMySQLCheck {
    @VisibleForTesting
    static final String DATABASE_COLLATION_QUERY = "SELECT DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA S\nWHERE schema_name = ?\nAND DEFAULT_COLLATION_NAME NOT IN (%s);";
    @VisibleForTesting
    static final String TABLE_COLLATION_QUERY = "SELECT T.TABLE_NAME, C.COLLATION_NAME\nFROM information_schema.TABLES AS T, information_schema.`COLLATION_CHARACTER_SET_APPLICABILITY` AS C\nWHERE C.collation_name = T.table_collation\nAND T.table_schema = ?\nAND C.COLLATION_NAME NOT IN (%s);";
    @VisibleForTesting
    static final String COLUMN_COLLATION_QUERY = "SELECT TABLE_NAME, COLUMN_NAME, COLLATION_NAME\nFROM information_schema.COLUMNS\nWHERE TABLE_SCHEMA = ?\nAND COLLATION_NAME NOT IN (%s);";
    private static final String[] COLLATION_PRIOR_7_2 = new String[]{"utf8_bin"};
    private static final String[] COLLATION_LATER_7_3 = new String[]{"utf8_bin", "utf8mb3_bin", "utf8mb4_bin"};
    private static final Version CONFLUENCE_7_3 = new Version("7.3");
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;
    private final SystemInformationService systemInformationService;

    @Autowired
    public CollationCheck(DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder, SystemInformationService systemInformationService) {
        super(databaseService);
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
        this.systemInformationService = systemInformationService;
    }

    @Override
    public SupportHealthStatus check() {
        String[] supportedCollations = this.getSupportedCollations();
        return this.databaseService.runInConnection(connection -> {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Started 6 blocks at once
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        });
    }

    private String[] getSupportedCollations() {
        Version currentConfluenceVersion = new Version(this.systemInformationService.getConfluenceInfo().getVersion());
        if (currentConfluenceVersion.isLowerThan(CONFLUENCE_7_3)) {
            return COLLATION_PRIOR_7_2;
        }
        return COLLATION_LATER_7_3;
    }
}

