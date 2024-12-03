/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.application.ImmutableApplicationDirectoryMapping
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.crowd.model.tombstone.AbstractTombstone
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterCreatorHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterFactory;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.GenericSiteExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.WholeTableExporter;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.application.ImmutableApplicationDirectoryMapping;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SiteExportersCreator {
    private final ExporterFactory exporterFactory;
    final Collection<Exporter> siteExporters = new HashSet<Exporter>();
    final Collection<WholeTableExporter> wholeTableExporters = new HashSet<WholeTableExporter>();

    public SiteExportersCreator(ExporterFactory exporterFactory, HibernateMetadataHelper hibernateMetadataHelper, boolean includeAttachments) {
        this.exporterFactory = exporterFactory;
        HashSet<ExportableEntityInfo> exportableEntitiesToProcess = new HashSet<ExportableEntityInfo>(hibernateMetadataHelper.getAllExportableEntities().values());
        this.createAllExporters(exportableEntitiesToProcess, includeAttachments);
    }

    private void createAllExporters(Set<ExportableEntityInfo> pendingExportableEntitiesToProcess, boolean includeAttachments) {
        this.createContentSiteExporter(pendingExportableEntitiesToProcess, includeAttachments);
        this.createSingleTableExtractors(pendingExportableEntitiesToProcess);
    }

    private void createSingleTableExtractors(Set<ExportableEntityInfo> pendingExportableEntitiesToProcess) {
        Iterator<ExportableEntityInfo> entitiesIterator = pendingExportableEntitiesToProcess.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            entitiesIterator.remove();
            Exporter exporter = null;
            if (entityInfo.getEntityClass().equals(AliasedKey.class)) {
                exporter = this.exporterFactory.createAliasedKeySiteExporter(entityInfo);
            } else if (AbstractTombstone.class.isAssignableFrom(entityInfo.getEntityClass())) {
                exporter = this.exporterFactory.createTombstoneExporter(entityInfo);
            } else if (entityInfo.getEntityClass().equals(BodyContent.class)) {
                exporter = this.exporterFactory.createSingleDependencyGenericSiteExporter(entityInfo);
            } else if (entityInfo.getEntityClass().equals(BucketPropertySetItem.class)) {
                exporter = this.exporterFactory.createBucketPropertySetItemsExporter(entityInfo);
            } else if (entityInfo.getEntityClass().equals(ApplicationImpl.class)) {
                exporter = this.exporterFactory.createApplicationExporter(entityInfo);
            } else if (entityInfo.getEntityClass().equals(DirectoryImpl.class)) {
                exporter = this.exporterFactory.createDirectoryExporter(entityInfo);
            } else if (entityInfo.getEntityClass().equals(DirectoryMapping.class)) {
                exporter = this.exporterFactory.createDirectoryMappingExporter(entityInfo);
            } else {
                if (entityInfo.getEntityClass().equals(ImmutableDirectory.class) || entityInfo.getEntityClass().equals(ImmutableApplicationDirectoryMapping.class) || entityInfo.getEntityClass().equals(ImmutableApplication.class)) {
                    throw new UnsupportedOperationException(String.format("Entity %s is not supported. Please add support of this entity to site backup/restore procedure", entityInfo.getEntityClass().getName()));
                }
                exporter = this.exporterFactory.createGenericSiteExporter(entityInfo);
            }
            if (exporter == null) continue;
            this.siteExporters.add(exporter);
            if (!(exporter instanceof WholeTableExporter)) continue;
            this.wholeTableExporters.add((WholeTableExporter)((Object)exporter));
        }
    }

    private void createContentSiteExporter(Set<ExportableEntityInfo> pendingExportableEntitiesToProcess, boolean includeAttachments) {
        List<ExportableEntityInfo> allContentEntityInfos = ExporterCreatorHelper.cutAllContentEntityInfos(pendingExportableEntitiesToProcess);
        if (allContentEntityInfos.isEmpty()) {
            return;
        }
        GenericSiteExporter contentExporter = this.exporterFactory.createContentSiteExporter(allContentEntityInfos, includeAttachments);
        this.siteExporters.add(contentExporter);
        this.wholeTableExporters.add(contentExporter);
    }

    public Collection<Exporter> getSiteExporters() {
        return this.siteExporters;
    }

    public Collection<WholeTableExporter> getWholeTableExporters() {
        return this.wholeTableExporters;
    }
}

