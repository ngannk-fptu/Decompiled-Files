/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentProperty
 *  com.atlassian.confluence.plugin.descriptor.restore.ImportedObjectModel
 *  com.atlassian.confluence.plugin.descriptor.restore.PluginExistingEntityFinder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.restore;

import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.plugin.descriptor.restore.ImportedObjectModel;
import com.atlassian.confluence.plugin.descriptor.restore.PluginExistingEntityFinder;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class CustomEmoticonExistingEntityFinder
implements PluginExistingEntityFinder {
    private static final Logger logger = LoggerFactory.getLogger(CustomEmoticonExistingEntityFinder.class);
    public static final String NAME_PROPERTY = "name";
    public static final String STRING_VALUE_PROPERTY = "stringValue";
    private final CustomEmoticonService customEmoticonService;

    public CustomEmoticonExistingEntityFinder(@Qualifier(value="customEmoticonService") CustomEmoticonService customEmoticonService) {
        this.customEmoticonService = customEmoticonService;
    }

    public Map<ImportedObjectModel, Object> findExistingObjectIds(Collection<ImportedObjectModel> collection) {
        HashMap<String, ImportedObjectModel> shortcutToImportedObjMap = new HashMap<String, ImportedObjectModel>(Objects.requireNonNull(collection).size());
        HashMap<ImportedObjectModel, Object> existingObjectMap = new HashMap<ImportedObjectModel, Object>();
        if (collection.isEmpty()) {
            return existingObjectMap;
        }
        for (ImportedObjectModel importedObjectModel : collection) {
            Map propertyValueMap = importedObjectModel.getOriginalPropertyValueMap();
            if (propertyValueMap.get(NAME_PROPERTY) != null && !propertyValueMap.get(NAME_PROPERTY).equals("emoticon-shortcut")) continue;
            shortcutToImportedObjMap.putIfAbsent((String)propertyValueMap.get(STRING_VALUE_PROPERTY), importedObjectModel);
        }
        for (CustomEmoticon customEmoticon : this.customEmoticonService.list()) {
            if (!shortcutToImportedObjMap.containsKey(customEmoticon.getShortcut())) continue;
            existingObjectMap.putIfAbsent((ImportedObjectModel)shortcutToImportedObjMap.get(customEmoticon.getShortcut()), customEmoticon.getId());
        }
        return existingObjectMap;
    }

    public Class<?> getSupportedClass() {
        return ContentProperty.class;
    }
}

