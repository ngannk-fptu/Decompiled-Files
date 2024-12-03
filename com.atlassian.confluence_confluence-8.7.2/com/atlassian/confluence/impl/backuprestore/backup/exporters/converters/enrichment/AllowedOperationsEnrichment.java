/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.ExportObjectsEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.crowd.embedded.api.OperationType;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedOperationsEnrichment
implements ExportObjectsEnrichment {
    private static final String OPERATION_QUERY = "SELECT %s, operation_type FROM %s WHERE %s in (:ids)";
    private final DatabaseExporterHelper helper;
    private final String query;
    private final String idColumnName;

    public AllowedOperationsEnrichment(DatabaseExporterHelper helper, String operationsTableName, String operationsTableIdColumn) {
        this.helper = helper;
        this.idColumnName = operationsTableIdColumn;
        this.query = String.format(OPERATION_QUERY, this.idColumnName, operationsTableName, this.idColumnName);
    }

    @Override
    public void enrichElements(List<EntityObjectReadyForExport> entityObjects) {
        Set<Long> ids = entityObjects.stream().map(e -> e.getId().getLongValue()).collect(Collectors.toSet());
        Map<Object, Set<Object>> operationTypesGroupedById = this.getOperationTypesGroupedById(ids);
        for (EntityObjectReadyForExport entityObject : entityObjects) {
            Set<Object> operationTypes = operationTypesGroupedById.get(entityObject.getId().getValue());
            if (operationTypes == null) continue;
            entityObject.addCollectionOfElements(new EntityObjectReadyForExport.CollectionOfElements("allowedOperations", Set.class, OperationType.class, operationTypes));
        }
    }

    private Map<Object, Set<Object>> getOperationTypesGroupedById(Set<Long> ids) {
        ArrayList operationRecords = new ArrayList();
        List partitions = Lists.partition(new ArrayList<Long>(ids), (int)this.helper.getRegularBatchSize());
        for (List partition : partitions) {
            List extractedRawObjects = (List)this.helper.doInReadOnlyTransaction(tx -> this.helper.runQueryWithInCondition(this.query, "ids", partition));
            operationRecords.addAll(extractedRawObjects);
        }
        return operationRecords.stream().collect(Collectors.groupingBy(o -> AbstractDatabaseDataConverter.convertToLong(o.getObjectProperty(this.idColumnName)), Collectors.mapping(o -> o.getObjectProperty("operation_type"), Collectors.toSet())));
    }
}

