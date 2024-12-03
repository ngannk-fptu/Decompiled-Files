/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.helpers.TableAndFieldNameValidator;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import com.atlassian.confluence.labels.Label;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelFinder
implements ExistingEntityFinder {
    public static final Logger log = LoggerFactory.getLogger(LabelFinder.class);
    private static final String FIND_LABEL_QUERY_PREFIX = "SELECT LABELID FROM LABEL WHERE ";
    private final TableAndFieldNameValidator tableAndFieldNameValidator = new TableAndFieldNameValidator();
    private final RestoreDao restoreDao;

    public LabelFinder(RestoreDao restoreDao) {
        this.restoreDao = restoreDao;
    }

    @Override
    public Map<ImportedObjectV2, Object> findExistingObjectIds(Collection<ImportedObjectV2> importedLabels) {
        HashMap<ImportedObjectV2, Object> foundLabels = new HashMap<ImportedObjectV2, Object>();
        importedLabels.forEach(importedObject -> {
            Long labelId = this.findLabel((ImportedObjectV2)importedObject);
            if (labelId != null) {
                log.trace("Existing label found for imported label with id {}. Found label id is {}", importedObject.getId(), (Object)labelId);
                foundLabels.put((ImportedObjectV2)importedObject, labelId);
            }
        });
        return foundLabels;
    }

    private Long findLabel(ImportedObjectV2 importedLabel) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(FIND_LABEL_QUERY_PREFIX);
        this.addLabelCondition("NAME", importedLabel.getFieldValue("name"), queryBuilder, params);
        queryBuilder.append(" AND ");
        this.addLabelCondition("NAMESPACE", importedLabel.getFieldValue("namespace"), queryBuilder, params);
        queryBuilder.append(" AND ");
        this.addLabelCondition("OWNER", importedLabel.getFieldValue("ownerUser"), queryBuilder, params);
        queryBuilder.append(" ORDER BY LABELID");
        List<Long> foundLabelsIds = this.restoreDao.runNativeQueryInTransaction(queryBuilder.toString(), params, 2).stream().map(rawObjectData -> AbstractDatabaseDataConverter.convertToLong(rawObjectData.getObjectProperty("labelid"))).collect(Collectors.toList());
        return this.getSingleValue(foundLabelsIds);
    }

    private Long getSingleValue(List<Long> foundLabelsIds) {
        if (foundLabelsIds.isEmpty()) {
            return null;
        }
        Long labelId = foundLabelsIds.get(0);
        if (foundLabelsIds.size() > 1) {
            log.warn("We found two or more similar labels. Space restore will use label with lower id ({}). Please consider removing other labels with the same name, owner and namespace", (Object)labelId);
        }
        return labelId;
    }

    private void addLabelCondition(String columnName, Object columnValue, StringBuilder queryBuilder, HashMap<String, Object> queryParams) {
        String safeColumnName = TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections(columnName);
        if (columnValue != null) {
            queryBuilder.append(safeColumnName);
            queryBuilder.append(" = :");
            queryBuilder.append(safeColumnName);
            queryParams.put(safeColumnName, columnValue);
        } else {
            queryBuilder.append(safeColumnName);
            queryBuilder.append(" IS NULL");
        }
    }

    @Override
    public Class<?> getSupportedClass() {
        return Label.class;
    }

    @Override
    public boolean isSupportedJobSource(JobSource jobSource) {
        return true;
    }
}

