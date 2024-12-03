/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.PostExportAction;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.QueryProvider;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.WholeTableExporter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericSiteExporter
implements Exporter,
WholeTableExporter {
    private static final Logger log = LoggerFactory.getLogger(GenericSiteExporter.class);
    private final DatabaseExporterHelper helper;
    private final Converter converter;
    private final QueryProvider queryProvider;
    private final PostExportAction postExportAction;

    public GenericSiteExporter(DatabaseExporterHelper helper, Converter converter, QueryProvider queryProvider, PostExportAction postExportAction) {
        this.helper = helper;
        this.converter = converter;
        this.queryProvider = queryProvider;
        this.postExportAction = postExportAction;
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.converter.getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.converter.getEntityInfo(exportedClass);
    }

    /*
     * Exception decompiling
     */
    @Override
    public void exportAllRecords() throws BackupRestoreException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[DOLOOP]], but top level block is 5[SIMPLE_IF_TAKEN]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
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

    @Override
    public String getExporterName() {
        return this.queryProvider.getTableName();
    }

    private void exportRecords(List<EntityObjectReadyForExport> entities) throws InterruptedException, BackupRestoreException {
        this.helper.writeObjectsAndNotifyOtherExporters(entities);
    }

    private BatchReadyForExport getFirstBatch(int queryLimit) {
        return this.getBatch(this.queryProvider.getInitialQuery(), Collections.emptyMap(), queryLimit);
    }

    private BatchReadyForExport getNextBatch(List<Object> latestMaxIdValues, int queryLimit) {
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        for (int i = 0; i < latestMaxIdValues.size(); ++i) {
            paramMap.put(this.queryProvider.getLatestIdParamName(i), latestMaxIdValues.get(i));
        }
        return this.getBatch(this.queryProvider.getRepetitiveQuery(), paramMap, queryLimit);
    }

    private BatchReadyForExport getBatch(String query, Map<String, Object> paramMap, int queryLimit) {
        List<DbRawObjectData> rawObjectData = this.helper.runNativeQueryInTransaction(query, paramMap, queryLimit);
        if (rawObjectData.isEmpty()) {
            return new BatchReadyForExport(Collections.emptyList(), 0, Collections.emptyList());
        }
        List<Object> latestMaxIdValues = this.getLatestMaxIdValues(rawObjectData);
        List<EntityObjectReadyForExport> entitiesReadyForExport = this.converter.convertToObjectsReadyForSerialisation(rawObjectData);
        return new BatchReadyForExport(latestMaxIdValues, rawObjectData.size(), entitiesReadyForExport);
    }

    private List<Object> getLatestMaxIdValues(List<DbRawObjectData> rawObjectData) {
        DbRawObjectData dbRawObjectData = rawObjectData.get(rawObjectData.size() - 1);
        ArrayList<Object> latestMaxIdValues = new ArrayList<Object>();
        for (int i = 0; i < this.queryProvider.getIdColumnNames().size(); ++i) {
            String keyColumnName = this.queryProvider.getIdColumnNames().get(i);
            Object idValue = dbRawObjectData.getObjectProperty(keyColumnName);
            latestMaxIdValues.add(idValue);
        }
        return latestMaxIdValues;
    }

    private static class BatchReadyForExport {
        private final List<Object> latestMaxIdValues;
        private final int rawObjectsBatchSize;
        private final List<EntityObjectReadyForExport> entitiesReadyForExport;

        public BatchReadyForExport(List<Object> latestMaxIdValues, int rawObjectsBatchSize, List<EntityObjectReadyForExport> entitiesReadyForExport) {
            this.latestMaxIdValues = latestMaxIdValues;
            this.rawObjectsBatchSize = rawObjectsBatchSize;
            this.entitiesReadyForExport = Collections.unmodifiableList(entitiesReadyForExport);
        }
    }
}

