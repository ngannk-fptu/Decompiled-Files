/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyQueryFactory;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class StorageJsonPropertyManager {
    private final CustomContentManager customContentManager;
    private final ContentEntityManager contentEntityManager;
    private final SpaceManager spaceManager;

    StorageJsonPropertyManager(@ComponentImport CustomContentManager customContentManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport SpaceManager spaceManager) {
        this.customContentManager = customContentManager;
        this.contentEntityManager = contentEntityManager;
        this.spaceManager = spaceManager;
    }

    public CustomContentEntityObject updateStorageFromApi(JsonContentProperty apiContentProperty, CustomContentEntityObject storageJsonProperty) {
        Reference contentRef = apiContentProperty.getContentRef();
        if (!contentRef.exists()) {
            throw new IllegalArgumentException("Can not update CustomContentEntityObject if JsonContentProperty does not contain contentRef.");
        }
        long storageContentId = Content.getSelector((Reference)contentRef).getId().asLong();
        ContentEntityObject storageContent = this.contentEntityManager.getById(storageContentId);
        storageJsonProperty.setContainer(storageContent);
        if (storageContent instanceof SpaceContentEntityObject) {
            SpaceContentEntityObject sceo = (SpaceContentEntityObject)storageContent;
            storageJsonProperty.setSpace(sceo.getSpace());
        }
        if (apiContentProperty.getKey() != null) {
            storageJsonProperty.setTitle(apiContentProperty.getKey());
        }
        String value = apiContentProperty.getValue().getValue();
        storageJsonProperty.setBodyAsString(value);
        return storageJsonProperty;
    }

    public CustomContentEntityObject updateStorageFromApi(JsonSpaceProperty property, CustomContentEntityObject storageJsonProperty) {
        if (!property.getSpaceRef().exists()) {
            throw new IllegalArgumentException("Can not update CustomContentEntityObject if JsonSpaceProperty does not contain contentRef.");
        }
        Space space = this.spaceManager.getSpace(com.atlassian.confluence.api.model.content.Space.getSpaceKey((Reference)property.getSpaceRef()));
        storageJsonProperty.setSpace(space);
        storageJsonProperty.setContainer(null);
        storageJsonProperty.setTitle(property.getKey());
        String value = property.getValue().getValue();
        storageJsonProperty.setBodyAsString(value);
        return storageJsonProperty;
    }

    public CustomContentEntityObject getStorageContentProperty(SimpleValidationResult.Builder result, JsonContentProperty property) {
        long contentId;
        ContentEntityObject storageContent;
        if (property.getId() != null) {
            CustomContentEntityObject storageContentProperty = this.customContentManager.getById(property.getId().asLong());
            if (storageContentProperty == null) {
                result.addError("contentproperty.invalid.id", new Object[]{property.getId()});
            }
            return storageContentProperty;
        }
        Reference contentRef = property.getContentRef();
        if (!contentRef.exists()) {
            result.addError("contentproperty.contentid.required", new Object[0]);
        }
        if ((storageContent = this.contentEntityManager.getById(contentId = Content.getSelector((Reference)contentRef).getId().asLong())) == null) {
            result.addError("contentproperty.invalid.content", new Object[0]);
        } else if (property.getKey() == null) {
            result.addError("contentproperty.id.key.required", new Object[0]);
        } else {
            CustomContentEntityObject storageContentProperty = (CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(JsonPropertyQueryFactory.findByContentIdAndKey(contentId, property.getKey()));
            if (storageContentProperty == null) {
                result.addError("jsonproperty.invalid.key", new Object[]{property.getKey()});
            }
            return storageContentProperty;
        }
        return null;
    }

    public CustomContentEntityObject getStorageSpaceProperty(JsonSpaceProperty property) {
        return (CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(JsonPropertyQueryFactory.findBySpaceKeyAndKey(property.getSpace().getKey(), property.getKey()));
    }
}

