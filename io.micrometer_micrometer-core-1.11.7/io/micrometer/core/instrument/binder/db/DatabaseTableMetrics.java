/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.db;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.function.ToDoubleFunction;
import javax.sql.DataSource;

@NonNullApi
@NonNullFields
public class DatabaseTableMetrics
implements MeterBinder {
    private final DataSource dataSource;
    private final String query;
    private final String dataSourceName;
    private final String tableName;
    private final Iterable<Tag> tags;

    public DatabaseTableMetrics(DataSource dataSource, String dataSourceName, String tableName, Iterable<Tag> tags) {
        this(dataSource, "SELECT COUNT(1) FROM " + tableName, dataSourceName, tableName, tags);
    }

    public DatabaseTableMetrics(DataSource dataSource, String query, String dataSourceName, String tableName, Iterable<Tag> tags) {
        this.dataSource = dataSource;
        this.query = query;
        this.dataSourceName = dataSourceName;
        this.tableName = tableName;
        this.tags = tags;
    }

    public static void monitor(MeterRegistry registry, String tableName, String dataSourceName, DataSource dataSource, String ... tags) {
        DatabaseTableMetrics.monitor(registry, dataSource, dataSourceName, tableName, Tags.of(tags));
    }

    public static void monitor(MeterRegistry registry, DataSource dataSource, String dataSourceName, String tableName, Iterable<Tag> tags) {
        new DatabaseTableMetrics(dataSource, dataSourceName, tableName, tags).bindTo(registry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        ToDoubleFunction<DataSource> totalRows = ds -> {
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
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        };
        Gauge.builder("db.table.size", this.dataSource, totalRows).tags(this.tags).tag("db", this.dataSourceName).tag("table", this.tableName).description("Number of rows in a database table").baseUnit("rows").register(registry);
    }
}

