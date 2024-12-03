/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.CascadeType
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.Entity
 *  javax.persistence.OneToMany
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ExcludeApp;
import com.atlassian.migration.agent.entity.NeededInCloudApp;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue(value="APPS")
public class MigrateAppsTask
extends Task {
    @OneToMany(mappedBy="task", cascade={CascadeType.ALL})
    private Set<ExcludeApp> excludedApps;
    @OneToMany(mappedBy="task", cascade={CascadeType.ALL})
    private Set<NeededInCloudApp> neededInCloudApps;

    @Override
    public Task copy() {
        return new MigrateAppsTask(this);
    }

    public MigrateAppsTask() {
    }

    private MigrateAppsTask(MigrateAppsTask task) {
        super(task.getWeight(), task.getIndex());
        this.excludedApps = task.getExcludedApps().stream().map(app -> new ExcludeApp(this, app.getAppKey())).collect(Collectors.toSet());
        this.neededInCloudApps = task.getNeededInCloudApps().stream().map(app -> new NeededInCloudApp(this, app.getAppKey())).collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return "Migrate apps";
    }

    public Set<NeededInCloudApp> getNeededInCloudApps() {
        return this.neededInCloudApps;
    }

    public void setNeededInCloudApps(Set<NeededInCloudApp> neededInCloudApps) {
        this.neededInCloudApps = neededInCloudApps;
    }

    @Override
    public TaskType getType() {
        return TaskType.APPS;
    }

    public Set<ExcludeApp> getExcludedApps() {
        return this.excludedApps;
    }

    public void setExcludedApps(Set<ExcludeApp> excludedApps) {
        this.excludedApps = excludedApps;
    }
}

