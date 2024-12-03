/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.ExportObjectsEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributesEnrichment
implements ExportObjectsEnrichment {
    private static final String ATTRIBUTES_QUERY = "SELECT %s, attribute_name, attribute_value FROM %s WHERE %s in (:ids)";
    private final DatabaseExporterHelper helper;
    private final String query;
    private final String idColumnName;

    public AttributesEnrichment(DatabaseExporterHelper helper, String attributesTableName, String idColumnName) {
        this.helper = helper;
        this.idColumnName = idColumnName;
        this.query = String.format(ATTRIBUTES_QUERY, this.idColumnName, attributesTableName, this.idColumnName);
    }

    @Override
    public void enrichElements(List<EntityObjectReadyForExport> entityObjects) {
        Set<Long> ids = entityObjects.stream().map(e -> e.getId().getLongValue()).collect(Collectors.toSet());
        Map<Object, Map<Object, Object>> attributeMapsGroupedById = this.getAttributeMapsGroupedById(ids);
        for (EntityObjectReadyForExport entityObject : entityObjects) {
            Map<Object, Object> attributesMap = attributeMapsGroupedById.get(entityObject.getId().getValue());
            if (attributesMap == null) continue;
            entityObject.addCollectionOfElements(new EntityObjectReadyForExport.CollectionOfElements("attributes", attributesMap));
        }
    }

    private Map<Object, Map<Object, Object>> getAttributeMapsGroupedById(Set<Long> ids) {
        ArrayList attributeRecords = new ArrayList();
        List partitions = Lists.partition(new ArrayList<Long>(ids), (int)this.helper.getRegularBatchSize());
        for (List partition : partitions) {
            List extractedRawObjects = (List)this.helper.doInReadOnlyTransaction(tx -> this.helper.runQueryWithInCondition(this.query, "ids", partition));
            attributeRecords.addAll(extractedRawObjects);
        }
        return attributeRecords.stream().collect(Collectors.groupingBy(v -> AbstractDatabaseDataConverter.convertToLong(v.getObjectProperty(this.idColumnName)), Collectors.toMap(k -> k.getObjectProperty("attribute_name"), v -> v.getObjectProperty("attribute_value"))));
    }
}

