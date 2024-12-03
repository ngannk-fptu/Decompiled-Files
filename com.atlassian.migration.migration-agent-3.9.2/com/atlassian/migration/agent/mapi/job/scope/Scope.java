/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job.scope;

import com.atlassian.migration.agent.mapi.job.scope.AppScope;
import com.atlassian.migration.agent.mapi.job.scope.GlobalEntitiesScope;
import com.atlassian.migration.agent.mapi.job.scope.SpaceScope;
import com.atlassian.migration.agent.mapi.job.scope.UsersGroupsScope;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Scope {
    @JsonProperty(value="spaces")
    @Nullable
    private SpaceScope spaces;
    @JsonProperty(value="usersAndGroups")
    @Nullable
    private UsersGroupsScope usersAndGroups;
    @JsonProperty(value="apps")
    @Nullable
    private AppScope apps;
    @JsonProperty(value="globalEntities")
    @Nullable
    private GlobalEntitiesScope globalEntities;

    @Generated
    public Scope(@Nullable SpaceScope spaces, @Nullable UsersGroupsScope usersAndGroups, @Nullable AppScope apps, @Nullable GlobalEntitiesScope globalEntities) {
        this.spaces = spaces;
        this.usersAndGroups = usersAndGroups;
        this.apps = apps;
        this.globalEntities = globalEntities;
    }

    @Generated
    public Scope() {
    }

    @Nullable
    @Generated
    public SpaceScope getSpaces() {
        return this.spaces;
    }

    @Nullable
    @Generated
    public UsersGroupsScope getUsersAndGroups() {
        return this.usersAndGroups;
    }

    @Nullable
    @Generated
    public AppScope getApps() {
        return this.apps;
    }

    @Nullable
    @Generated
    public GlobalEntitiesScope getGlobalEntities() {
        return this.globalEntities;
    }
}

