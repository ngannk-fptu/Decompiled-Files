/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.service.content.SpacePropertyService
 *  com.atlassian.confluence.api.service.content.SpacePropertyService$SpacePropertyFinder
 *  com.atlassian.confluence.api.service.content.SpacePropertyService$Validator
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.spaceproperty;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyFactory;
import com.atlassian.confluence.plugins.contentproperty.StorageJsonPropertyManager;
import com.atlassian.confluence.plugins.contentproperty.spaceproperty.SpacePropertyFinderFactory;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="spacePropertyService")
@ExportAsService(value={SpacePropertyService.class})
public class SpacePropertyServiceImpl
implements SpacePropertyService {
    private static final Expansions DEFAULT_EXPANSIONS = new Expansions(ExpansionsParser.parse((String)"version"));
    private final CustomContentManager customContentManager;
    private final StorageJsonPropertyManager storageContentPropertyManager;
    private final JsonPropertyFactory jsonPropertyFactory;
    private final SpacePropertyService.Validator validator;
    private final SpacePropertyFinderFactory finderFactory;

    @Autowired
    public SpacePropertyServiceImpl(@ComponentImport CustomContentManager customContentManager, StorageJsonPropertyManager storageContentPropertyManager, JsonPropertyFactory jsonPropertyFactory, SpacePropertyService.Validator validator, SpacePropertyFinderFactory finderFactory) {
        this.customContentManager = customContentManager;
        this.storageContentPropertyManager = storageContentPropertyManager;
        this.jsonPropertyFactory = jsonPropertyFactory;
        this.validator = validator;
        this.finderFactory = finderFactory;
    }

    public JsonSpaceProperty create(JsonSpaceProperty newProperty) throws ServiceException {
        this.validator().validateCreate(newProperty).throwIfNotSuccessful("Cannot create new space property: " + newProperty);
        CustomContentEntityObject storageSpaceProperty = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.plugins.confluence-content-property-storage:content-property");
        this.storageContentPropertyManager.updateStorageFromApi(newProperty, storageSpaceProperty);
        this.customContentManager.saveContentEntity((ContentEntityObject)storageSpaceProperty, DefaultSaveContext.DEFAULT);
        return this.jsonPropertyFactory.buildSpacePropertyFrom(storageSpaceProperty, DEFAULT_EXPANSIONS);
    }

    public SpacePropertyService.SpacePropertyFinder find(Expansion ... expansions) {
        return this.finderFactory.createSpacePropertyFinder(expansions);
    }

    public JsonSpaceProperty update(JsonSpaceProperty property) {
        this.validator().validateUpdate(property).throwIfNotSuccessful("Cannot update space property: " + property);
        CustomContentEntityObject storageSpaceProperty = this.storageContentPropertyManager.getStorageSpaceProperty(property);
        CustomContentEntityObject originalSpaceProperty = (CustomContentEntityObject)storageSpaceProperty.clone();
        this.storageContentPropertyManager.updateStorageFromApi(property, storageSpaceProperty);
        this.customContentManager.saveContentEntity((ContentEntityObject)storageSpaceProperty, (ContentEntityObject)originalSpaceProperty, DefaultSaveContext.DEFAULT);
        return this.jsonPropertyFactory.buildSpacePropertyFrom(storageSpaceProperty, DEFAULT_EXPANSIONS);
    }

    public void delete(JsonSpaceProperty property) {
        this.validator().validateDelete(property).throwIfNotSuccessful("Cannot delete space property: " + property);
        CustomContentEntityObject storageContentProperty = this.storageContentPropertyManager.getStorageSpaceProperty(property);
        this.customContentManager.removeContentEntity((ContentEntityObject)storageContentProperty);
    }

    public SpacePropertyService.Validator validator() {
        return this.validator;
    }
}

