/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.collections.map.LazyMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionDefaultsStore;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.map.LazyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSpacePermissionDefaultsStore
implements SpacePermissionDefaultsStore {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DefaultSpacePermissionDefaultsStore.class);
    private final SettingsManager settingsManager;
    private final BandanaManager bandanaManager;
    private final Keys keys = new Keys();
    private final Space templateSpace;
    private final Set<String> currentGroups = new HashSet<String>();
    private ConfluenceBandanaContext context;
    static final String ADMINISTRATORS = "administrators";

    DefaultSpacePermissionDefaultsStore(SettingsManager settingsManager, BandanaManager bandanaManager) {
        this.settingsManager = settingsManager;
        this.bandanaManager = bandanaManager;
        this.templateSpace = new Space();
        this.context = new ConfluenceBandanaContext();
        this.loadFromBandana();
    }

    private void loadFromBandana() {
        Collection<String> groupNames = this.getBandanaValue(Keys.getKeyForAllGroups());
        if (groupNames == null) {
            this.prime();
            groupNames = this.getBandanaValue(Keys.getKeyForAllGroups());
        }
        log.debug("loaded group given default space permissions [{}].", groupNames);
        this.currentGroups.addAll(groupNames);
        for (String groupName : this.currentGroups) {
            HashSet permissionList = this.getBandanaValue(Keys.getKeyForGroup(groupName));
            if (permissionList == null) {
                permissionList = Sets.newHashSet();
            }
            log.debug("loaded default space permissions for group [{}]: [{}]...", (Object)groupName, (Object)permissionList);
            for (String permission : permissionList) {
                this.templateSpace.addPermission(SpacePermission.createGroupSpacePermission(permission, this.templateSpace, groupName));
            }
        }
    }

    public static Map<String, Set<String>> getDefaultDefaults(SettingsManager settingsManager) {
        HashMap<String, Set<String>> permissionTemplates = new HashMap<String, Set<String>>();
        permissionTemplates.put(settingsManager.getGlobalSettings().getDefaultUsersGroup(), Sets.newHashSet((Object[])new String[]{"VIEWSPACE", "REMOVEOWNCONTENT", "COMMENT", "EDITSPACE", "CREATEATTACHMENT", "EDITBLOG", "EXPORTSPACE"}));
        return permissionTemplates;
    }

    private void prime() {
        this.savePermissionsMap(DefaultSpacePermissionDefaultsStore.getDefaultDefaults(this.settingsManager));
    }

    @Override
    public void save() {
        Map<String, Set<String>> map = this.getPermissionsAsMap();
        this.savePermissionsMap(map);
    }

    private void savePermissionsMap(Map<String, Set<String>> map) {
        for (Map.Entry<String, Set<String>> element : map.entrySet()) {
            log.debug("saving default space permissions for group [{}]: [{}]...", (Object)element.getKey(), element.getValue());
            this.bandanaManager.setValue((BandanaContext)this.context, Keys.getKeyForGroup(element.getKey()), element.getValue());
        }
        Set<String> newGroups = map.keySet();
        HashSet<String> deletedGroups = new HashSet<String>();
        deletedGroups.addAll(this.currentGroups);
        deletedGroups.removeAll(newGroups);
        for (String deletedGroup : deletedGroups) {
            log.debug("removing default space permissions for group [{}]...", (Object)deletedGroup);
            this.bandanaManager.removeValue((BandanaContext)this.context, Keys.getKeyForGroup(deletedGroup));
        }
        this.currentGroups.clear();
        this.currentGroups.addAll(newGroups);
        log.debug("saving group names given default space permissions [{}]...", this.currentGroups);
        this.bandanaManager.setValue((BandanaContext)this.context, Keys.getKeyForAllGroups(), (Object)Sets.newHashSet(this.currentGroups));
    }

    private Map<String, Set<String>> getPermissionsAsMap() {
        Map map = LazyMap.decorate((Map)Maps.newHashMap(), Sets::newHashSet);
        for (SpacePermission spacePermission : this.getTemplatePermissions()) {
            ((Set)map.get(spacePermission.getGroup())).add(spacePermission.getType());
        }
        return map;
    }

    private Collection<String> getBandanaValue(String propertyName) {
        return (Collection)this.bandanaManager.getValue((BandanaContext)this.context, propertyName);
    }

    private Set<SpacePermission> getTemplatePermissions() {
        return Sets.newHashSet(this.templateSpace.getPermissions());
    }

    @Override
    public Space getTemplateSpace() {
        return this.templateSpace;
    }

    @Override
    public Set<SpacePermission> createPermissionsForSpace(Space space) {
        Set<SpacePermission> defaultPermissions = this.getTemplatePermissions();
        HashSet spacePermissions = Sets.newHashSet();
        for (SpacePermission defaultPermission : defaultPermissions) {
            SpacePermission spacePermission = new SpacePermission(defaultPermission);
            space.addPermission(spacePermission);
            spacePermissions.add(spacePermission);
        }
        return spacePermissions;
    }

    @Override
    public Set<String> getGroups() {
        return this.getPermissionsAsMap().keySet();
    }

    @Override
    public void reset() {
        Collection<String> groups = this.getBandanaValue(Keys.getKeyForAllGroups());
        if (groups == null) {
            return;
        }
        for (String group : groups) {
            this.bandanaManager.removeValue((BandanaContext)this.context, Keys.getKeyForGroup(group));
        }
        this.bandanaManager.removeValue((BandanaContext)this.context, Keys.getKeyForAllGroups());
    }

    static class Keys {
        private static final String PREFIX = "__DEFAULT_SPACE_PERMISSIONS__";
        private static final String GROUP_NAMES = "__DEFAULT_SPACE_PERMISSIONS____GROUP_NAMES__";

        Keys() {
        }

        static String getKeyForAllGroups() {
            return GROUP_NAMES;
        }

        static String getKeyForGroup(String groupName) {
            return PREFIX + groupName;
        }
    }
}

