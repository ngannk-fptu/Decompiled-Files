/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpacePermissionStore {
    private static final String WHERE_SPACE_KEYS_IN_PERMISSIONS_CONDITION = "where spacePermission.space.key in :keys ";
    private static final String WHERE_GT_PERMISSIONS_CONDITION = "where spacePermission.permType in ('SYSTEMADMINISTRATOR', 'ADMINISTRATECONFLUENCE')\n AND spacePermission.space.id is NULL ";
    private final EntityManagerTemplate entityManagerTemplate;
    private final MigrationAgentConfiguration migrationAgentConfiguration;

    public SpacePermissionStore(EntityManagerTemplate tmpl, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.entityManagerTemplate = tmpl;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    public List<String> getSpacesWithAnonymousPermissions(Set<String> spaceKeys) {
        ArrayList<String> spacesWithAnonymousPermissions = new ArrayList<String>();
        for (List subset : Iterables.partition(spaceKeys, (int)this.migrationAgentConfiguration.getDBQueryParameterLimit())) {
            String query = "select distinct spacePermission.space.key from SpacePermission spacePermission where spacePermission.space.key in :keys AND spacePermission.permGroupName is NULL AND spacePermission.permUsername is NULL";
            List<String> result = this.entityManagerTemplate.query(String.class, query).param("keys", (Object)subset).list();
            spacesWithAnonymousPermissions.addAll(result);
        }
        return spacesWithAnonymousPermissions;
    }

    public Set<String> getUsersWithSpacePermissions(Set<String> spaceKeys) {
        HashSet<String> usersWithPermissions = new HashSet<String>();
        for (List subset : Iterables.partition(spaceKeys, (int)this.migrationAgentConfiguration.getDBQueryParameterLimit())) {
            String query = "select distinct spacePermission.permUsername from SpacePermission spacePermission where spacePermission.space.key in :keys AND spacePermission.permGroupName is NULL AND spacePermission.permUsername is NOT NULL";
            List<String> users = this.entityManagerTemplate.query(String.class, query).param("keys", (Object)subset).list();
            usersWithPermissions.addAll(users);
        }
        return usersWithPermissions;
    }

    public Set<String> getMembersUnderGroupsWithSpacePermissions(Set<String> spaceKeys) {
        HashSet<String> usersWithPermissions = new HashSet<String>();
        for (List subset : Iterables.partition(spaceKeys, (int)this.migrationAgentConfiguration.getDBQueryParameterLimit())) {
            String query = "select distinct userMapping.userKey from SpacePermission spacePermission inner join CrowdGroup crowdGroup on spacePermission.permGroupName = crowdGroup.lowerGroupName inner join CrowdMembership crowdMembership on crowdGroup.id = crowdMembership.parent inner join CrowdUser crowdUser on crowdMembership.child = crowdUser.id inner join UserMapping userMapping on crowdUser.lowerUsername = userMapping.lowerUsername where spacePermission.space.key in :keys AND crowdGroup.crowdDirectory.active is true AND crowdGroup.crowdDirectory.id = crowdUser.crowdDirectory.id AND spacePermission.permGroupName is NOT NULL";
            List<String> users = this.entityManagerTemplate.query(String.class, query).param("keys", (Object)subset).list();
            usersWithPermissions.addAll(users);
        }
        return usersWithPermissions;
    }

    public Set<String> getMembersUnderGroupsWithGlobalEntitiesPermissions() {
        HashSet<String> usersWithPermissions = new HashSet<String>();
        String query = "select distinct userMapping.userKey from SpacePermission spacePermission inner join CrowdGroup crowdGroup on spacePermission.permGroupName = crowdGroup.lowerGroupName inner join CrowdMembership crowdMembership on crowdGroup.id = crowdMembership.parent inner join CrowdUser crowdUser on crowdMembership.child = crowdUser.id inner join UserMapping userMapping on crowdUser.lowerUsername = userMapping.lowerUsername where spacePermission.permType in ('SYSTEMADMINISTRATOR', 'ADMINISTRATECONFLUENCE')\n AND spacePermission.space.id is NULL AND crowdGroup.crowdDirectory.active is true AND crowdGroup.crowdDirectory.id = crowdUser.crowdDirectory.id AND spacePermission.permGroupName is NOT NULL";
        List<String> users = this.entityManagerTemplate.query(String.class, query).list();
        usersWithPermissions.addAll(users);
        return usersWithPermissions;
    }
}

