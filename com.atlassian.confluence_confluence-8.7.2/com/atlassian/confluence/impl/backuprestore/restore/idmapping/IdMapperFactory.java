/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.FakeObjectProvider;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.FilteredPersistedObjectsRegister;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.InMemoryPersistedObjectsRegister;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.SiteRestoreIdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.SpaceRestoreIdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFindersProvider;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collection;
import java.util.List;

public class IdMapperFactory {
    private final RestoreDao restoreDao;
    private final List<ExistingEntityFinder> defaultExistingEntityFinders;
    private final PluginAccessor pluginAccessor;

    public IdMapperFactory(List<ExistingEntityFinder> defaultExistingEntityFinders, PluginAccessor pluginAccessor, RestoreDao restoreDao) {
        this.restoreDao = restoreDao;
        this.pluginAccessor = pluginAccessor;
        this.defaultExistingEntityFinders = defaultExistingEntityFinders;
    }

    public IdMapper createIdMapper(BackupRestoreJob job, JobSource jobSource, Collection<ExportableEntityInfo> importableEntitiesInfo, OnObjectsProcessingHandler onObjectsProcessingHandler) throws BackupRestoreException {
        if (JobScope.SPACE.equals((Object)job.getJobScope())) {
            return this.createSpaceRestoreIdMapper(importableEntitiesInfo, jobSource, onObjectsProcessingHandler);
        }
        if (JobScope.SITE.equals((Object)job.getJobScope())) {
            return this.createSiteRestoreIdMapper(importableEntitiesInfo);
        }
        throw new IllegalStateException(String.format("Unknown job scope %s", job));
    }

    private SpaceRestoreIdMapper createSpaceRestoreIdMapper(Collection<ExportableEntityInfo> importableEntitiesInfo, JobSource jobSource, OnObjectsProcessingHandler onObjectsProcessingHandler) throws BackupRestoreException {
        FakeObjectProvider fakeObjectProvider = new FakeObjectProvider(importableEntitiesInfo);
        FilteredPersistedObjectsRegister persistedObjectRegister = new FilteredPersistedObjectsRegister(new InMemoryPersistedObjectsRegister(), importableEntitiesInfo);
        return new SpaceRestoreIdMapper(persistedObjectRegister, this.restoreDao, new ExistingEntityFindersProvider(this.defaultExistingEntityFinders, this.pluginAccessor), fakeObjectProvider, jobSource, onObjectsProcessingHandler);
    }

    private SiteRestoreIdMapper createSiteRestoreIdMapper(Collection<ExportableEntityInfo> importableEntitiesInfo) {
        FilteredPersistedObjectsRegister persistedObjectRegister = new FilteredPersistedObjectsRegister(new InMemoryPersistedObjectsRegister(), importableEntitiesInfo);
        return new SiteRestoreIdMapper(persistedObjectRegister);
    }
}

