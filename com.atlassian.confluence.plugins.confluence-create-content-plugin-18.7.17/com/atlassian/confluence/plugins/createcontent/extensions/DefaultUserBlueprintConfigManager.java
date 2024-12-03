/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Sets
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.actions.BPUserPreferences;
import com.atlassian.confluence.plugins.createcontent.extensions.UserBlueprintConfigManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Sets;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={UserBlueprintConfigManager.class})
public class DefaultUserBlueprintConfigManager
implements UserBlueprintConfigManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserBlueprintConfigManager.class);
    private static final String CREATED_BLUEPRINT_KEYS = "createdBlueprints";
    private static final String SKIP_HOW_TO_USE_BLUEPRINT_KEYS = "skip-how-to-use-blueprint-keys";
    private final UserAccessor userAccessor;
    private final ContentBlueprintManager contentBlueprintManager;

    @Autowired
    public DefaultUserBlueprintConfigManager(@ComponentImport UserAccessor userAccessor, ContentBlueprintManager contentBlueprintManager) {
        this.userAccessor = userAccessor;
        this.contentBlueprintManager = contentBlueprintManager;
    }

    @Override
    public Set<UUID> getSkipHowToUseKeys(ConfluenceUser user) {
        BPUserPreferences userPreferences = this.getUserPreferences(user);
        return this.getSkipKeys(userPreferences);
    }

    @Override
    public void setSkipHowToUse(ConfluenceUser user, UUID contentBlueprintId, boolean skip) {
        BPUserPreferences userPreferences = this.getUserPreferences(user);
        Set<UUID> skipKeys = this.getSkipKeys(userPreferences);
        if (skip) {
            skipKeys.add(contentBlueprintId);
        } else {
            skipKeys.remove(contentBlueprintId);
        }
        String keysStr = StringUtils.join(skipKeys, (String)",");
        userPreferences.setText(SKIP_HOW_TO_USE_BLUEPRINT_KEYS, keysStr);
    }

    @Override
    public boolean isFirstBlueprintOfTypeForUser(UUID id, ConfluenceUser user) {
        BPUserPreferences userPreferences = this.getUserPreferences(user);
        String blueprintKeys = userPreferences.getText(CREATED_BLUEPRINT_KEYS);
        return blueprintKeys == null || !blueprintKeys.contains(id.toString());
    }

    @Override
    public void setBlueprintCreatedByUser(UUID id, ConfluenceUser user) {
        BPUserPreferences userPreferences = this.getUserPreferences(user);
        Object blueprintKeys = userPreferences.getText(CREATED_BLUEPRINT_KEYS);
        String idStr = id.toString();
        boolean modified = false;
        if (blueprintKeys == null) {
            blueprintKeys = idStr;
            modified = true;
        }
        if (!((String)blueprintKeys).contains(idStr)) {
            blueprintKeys = (String)blueprintKeys + "," + idStr;
            modified = true;
        }
        if (modified) {
            userPreferences.setText(CREATED_BLUEPRINT_KEYS, (String)blueprintKeys);
        }
    }

    public BPUserPreferences getUserPreferences(ConfluenceUser user) {
        PropertySet propertySet = this.userAccessor.getPropertySet(user);
        this.migratePrefs(propertySet, CREATED_BLUEPRINT_KEYS);
        this.migratePrefs(propertySet, SKIP_HOW_TO_USE_BLUEPRINT_KEYS);
        return new BPUserPreferences(propertySet);
    }

    private void migratePrefs(PropertySet propertySet, String key) {
        try {
            int type = propertySet.getType(key);
            if (type != 5) {
                return;
            }
            String string = propertySet.getString(key);
            if (!string.contains(":")) {
                return;
            }
            String[] bpKeys = string.split(",");
            List uids = Arrays.stream(bpKeys).map(bpKey -> {
                if (!bpKey.contains(":")) {
                    return bpKey;
                }
                ContentBlueprint blueprint = this.contentBlueprintManager.getPluginBlueprint(new ModuleCompleteKey(bpKey));
                return blueprint != null ? blueprint.getId().toString() : null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            propertySet.remove(key);
            propertySet.setText(key, StringUtils.join(uids, (char)','));
        }
        catch (NullPointerException type) {
        }
        catch (Exception ex) {
            log.warn("Unable to migrate Confluence Blueprint user preferences", (Throwable)ex);
        }
    }

    private Set<UUID> getSkipKeys(BPUserPreferences userPreferences) {
        String keysStr = userPreferences.getText(SKIP_HOW_TO_USE_BLUEPRINT_KEYS);
        if (StringUtils.isBlank((CharSequence)keysStr)) {
            return Sets.newHashSet();
        }
        String[] split = keysStr.split(",");
        HashSet ids = Sets.newHashSet();
        for (String id : split) {
            ids.add(UUID.fromString(id));
        }
        return ids;
    }
}

