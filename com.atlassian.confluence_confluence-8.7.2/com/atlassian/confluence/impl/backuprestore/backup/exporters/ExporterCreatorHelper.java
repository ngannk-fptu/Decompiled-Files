/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ExporterCreatorHelper {
    public static final String CONTENT_TABLE_NAME = "CONTENT";
    public static final String PAGE_TEMPLATE_TABLE_NAME = "PAGETEMPLATES";

    public static List<ExportableEntityInfo> cutAllContentEntityInfos(Collection<ExportableEntityInfo> exportableEntities) {
        return ExporterCreatorHelper.cutEntityInfos(exportableEntities, CONTENT_TABLE_NAME);
    }

    @Nullable
    public static ExportableEntityInfo cutPageTemplateEntityInfo(Collection<ExportableEntityInfo> exportableEntities) {
        return ExporterCreatorHelper.findEntityInfo(exportableEntities, PAGE_TEMPLATE_TABLE_NAME);
    }

    private static List<ExportableEntityInfo> cutEntityInfos(Collection<ExportableEntityInfo> exportableEntities, String tableName) {
        ArrayList<ExportableEntityInfo> entityInfos = new ArrayList<ExportableEntityInfo>();
        Iterator<ExportableEntityInfo> entitiesIterator = exportableEntities.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            if (!entityInfo.getTableName().equalsIgnoreCase(tableName)) continue;
            entityInfos.add(entityInfo);
            entitiesIterator.remove();
        }
        return entityInfos;
    }

    @Nullable
    private static ExportableEntityInfo findEntityInfo(Collection<ExportableEntityInfo> exportableEntities, String tableName) {
        Iterator<ExportableEntityInfo> entitiesIterator = exportableEntities.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            if (!entityInfo.getTableName().equalsIgnoreCase(tableName)) continue;
            entitiesIterator.remove();
            return entityInfo;
        }
        return null;
    }
}

