/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.ExcludeApp;
import com.atlassian.migration.agent.entity.MigrateAppsTask;
import com.atlassian.migration.agent.entity.NeededInCloudApp;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.json.JsonType;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonType(value="apps")
public class MigrateAppsTaskDto
extends TaskDto {
    private static final String DELIMITER = ",";
    @JsonProperty
    private final Set<String> excludedApps;
    private Set<String> neededInCloudApps = Collections.emptySet();

    @JsonCreator
    public MigrateAppsTaskDto(@JsonProperty(value="id") String id, @JsonProperty(value="name") String name, @JsonProperty(value="excludedApps") Set<String> excludedApps, @JsonProperty(value="progress") ProgressDto progress) {
        super(progress, id, name, 0L);
        this.excludedApps = excludedApps != null ? excludedApps : Collections.emptySet();
    }

    @Override
    public Task toInternalType() {
        MigrateAppsTask task = new MigrateAppsTask();
        task.setExcludedApps(this.getExcludedAppObjects(task));
        task.setNeededInCloudApps(this.getNeededInCloudApps(task));
        return task;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.APPS;
    }

    public String getExcludedAppKeysJoined() {
        return MigrateAppsTaskDto.getExcludedAppKeysJoined(this.excludedApps);
    }

    public static String getExcludedAppKeysJoined(Set<String> appKeys) {
        return appKeys.stream().map(String::trim).filter(key -> !key.isEmpty()).map(key -> Base64.getEncoder().encodeToString(key.getBytes())).collect(Collectors.joining(DELIMITER));
    }

    public static Set<String> getExcludedAppKeysAsSet(String excludedAppKeys) {
        String[] appKeys = excludedAppKeys.split(DELIMITER);
        return Stream.of(appKeys).map(key -> new String(Base64.getDecoder().decode((String)key))).filter(key -> !key.isEmpty()).collect(Collectors.toSet());
    }

    public void setNeededInCloudApps(Set<String> neededInCloudApps) {
        this.neededInCloudApps = neededInCloudApps;
    }

    public Set<String> getNeededInCloudApps() {
        return this.neededInCloudApps;
    }

    private Set<ExcludeApp> getExcludedAppObjects(MigrateAppsTask task) {
        return this.excludedApps.stream().map(appKey -> new ExcludeApp(task, (String)appKey)).collect(Collectors.toSet());
    }

    private Set<NeededInCloudApp> getNeededInCloudApps(MigrateAppsTask task) {
        return this.neededInCloudApps.stream().map(appKey -> new NeededInCloudApp(task, (String)appKey)).collect(Collectors.toSet());
    }
}

