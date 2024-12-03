/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.activeobjects.internal.ActiveObjectUpgradeManager;
import com.atlassian.activeobjects.internal.ModelVersionManager;
import com.atlassian.activeobjects.internal.Prefix;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ActiveObjectUpgradeManagerImpl
implements ActiveObjectUpgradeManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ModelVersionManager versionManager;

    public ActiveObjectUpgradeManagerImpl(ModelVersionManager versionManager) {
        this.versionManager = (ModelVersionManager)Preconditions.checkNotNull((Object)versionManager);
    }

    @Override
    public void upgrade(Prefix tableNamePrefix, List<ActiveObjectsUpgradeTask> upgradeTasks, Supplier<ActiveObjects> ao) {
        ModelVersion currentModelVersion = this.versionManager.getCurrent(tableNamePrefix);
        this.logger.info("Starting upgrade of data model, current version is {}", (Object)currentModelVersion);
        for (ActiveObjectsUpgradeTask task : this.sortAndVerify(upgradeTasks)) {
            if (!currentModelVersion.isOlderThan(task.getModelVersion())) continue;
            currentModelVersion = this.upgrade(tableNamePrefix, task, (ActiveObjects)ao.get(), currentModelVersion);
        }
        this.logger.info("Finished upgrading, model is up to date at version {}", (Object)currentModelVersion);
    }

    private List<ActiveObjectsUpgradeTask> sortAndVerify(List<ActiveObjectsUpgradeTask> upgradeTasks) {
        return this.verify(this.sort(upgradeTasks));
    }

    List<ActiveObjectsUpgradeTask> verify(List<ActiveObjectsUpgradeTask> sorted) {
        ModelVersion mv = null;
        for (ActiveObjectsUpgradeTask task : sorted) {
            if (mv != null && mv.isSame(task.getModelVersion())) {
                throw new IllegalStateException("There are more than one upgrade tasks with model version " + mv);
            }
            mv = task.getModelVersion();
        }
        return sorted;
    }

    private List<ActiveObjectsUpgradeTask> sort(List<ActiveObjectsUpgradeTask> upgradeTasks) {
        ArrayList tasks = Lists.newArrayList(upgradeTasks);
        tasks.sort(new ActiveObjectsUpgradeTaskComparator());
        return ImmutableList.copyOf((Collection)tasks);
    }

    private ModelVersion upgrade(Prefix tableNamePrefix, ActiveObjectsUpgradeTask task, ActiveObjects activeObjects, ModelVersion currentModelVersion) {
        return (ModelVersion)activeObjects.executeInTransaction(() -> {
            this.logger.debug("Upgrading data model with task {}, current version of model is {}", (Object)task.getClass().getName(), (Object)currentModelVersion);
            task.upgrade(currentModelVersion, activeObjects);
            ModelVersion updatedVersion = task.getModelVersion();
            this.versionManager.update(tableNamePrefix, updatedVersion);
            this.logger.debug("Upgraded data model to version {}", (Object)updatedVersion);
            return updatedVersion;
        });
    }

    private static class ActiveObjectsUpgradeTaskComparator
    implements Comparator<ActiveObjectsUpgradeTask> {
        private ActiveObjectsUpgradeTaskComparator() {
        }

        @Override
        public int compare(ActiveObjectsUpgradeTask o1, ActiveObjectsUpgradeTask o2) {
            return o1.getModelVersion().compareTo(o2.getModelVersion());
        }
    }
}

