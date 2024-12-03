/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$ContentPropertyFinder
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$Validator
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderFactory;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyFactory;
import com.atlassian.confluence.plugins.contentproperty.StorageJsonPropertyManager;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="contentPropertyService")
@ExportAsService(value={ContentPropertyService.class})
public class ContentPropertyServiceImpl
implements ContentPropertyService {
    private static final Expansions DEFAULT_EXPANSIONS = new Expansions(ExpansionsParser.parse((String)"version"));
    private final CustomContentManager customContentManager;
    private final JsonPropertyFactory jsonPropertyFactory;
    private final StorageJsonPropertyManager storageContentPropertyManager;
    private final ContentPropertyFinderFactory contentPropertyFinderFactory;
    private final ContentPropertyService.Validator validator;
    private final ConfluenceIndexer confluenceIndexer;

    @Autowired
    public ContentPropertyServiceImpl(@ComponentImport CustomContentManager customContentManager, JsonPropertyFactory jsonPropertyFactory, StorageJsonPropertyManager storageContentPropertyManager, ContentPropertyFinderFactory contentPropertyFinderFactory, ContentPropertyService.Validator validator, @ComponentImport ConfluenceIndexer confluenceIndexer) {
        this.customContentManager = Objects.requireNonNull(customContentManager);
        this.jsonPropertyFactory = Objects.requireNonNull(jsonPropertyFactory);
        this.storageContentPropertyManager = Objects.requireNonNull(storageContentPropertyManager);
        this.contentPropertyFinderFactory = Objects.requireNonNull(contentPropertyFinderFactory);
        this.validator = Objects.requireNonNull(validator);
        this.confluenceIndexer = Objects.requireNonNull(confluenceIndexer);
    }

    public ContentPropertyService.ContentPropertyFinder find(Expansion ... expansions) {
        if (expansions.length == 0) {
            expansions = DEFAULT_EXPANSIONS.toArray();
        }
        return this.contentPropertyFinderFactory.createContentPropertyFinder(expansions);
    }

    public JsonContentProperty create(JsonContentProperty newProperty) throws ServiceException {
        this.validator().validateCreate(newProperty).throwIfNotSuccessful("Cannot create new content property: " + newProperty);
        CustomContentEntityObject storageContentProperty = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.plugins.confluence-content-property-storage:content-property");
        this.storageContentPropertyManager.updateStorageFromApi(newProperty, storageContentProperty);
        this.customContentManager.saveContentEntity((ContentEntityObject)storageContentProperty, DefaultSaveContext.DEFAULT);
        this.confluenceIndexer.index((Searchable)storageContentProperty.getContainer());
        return this.jsonPropertyFactory.buildContentPropertyFrom(storageContentProperty, DEFAULT_EXPANSIONS);
    }

    public JsonContentProperty update(JsonContentProperty property) throws ServiceException {
        this.validator().validateUpdate(property).throwIfNotSuccessful("Cannot update content property: " + property);
        CustomContentEntityObject storageContentProperty = this.storageContentPropertyManager.getStorageContentProperty(null, property);
        CustomContentEntityObject originalContentProperty = (CustomContentEntityObject)storageContentProperty.clone();
        this.storageContentPropertyManager.updateStorageFromApi(property, storageContentProperty);
        this.customContentManager.saveContentEntity((ContentEntityObject)storageContentProperty, (ContentEntityObject)originalContentProperty, DefaultSaveContext.DEFAULT);
        this.confluenceIndexer.reIndex((Searchable)storageContentProperty.getContainer());
        return this.jsonPropertyFactory.buildContentPropertyFrom(storageContentProperty, DEFAULT_EXPANSIONS);
    }

    public void delete(JsonContentProperty property) throws ServiceException {
        this.validator().validateDelete(property).throwIfNotSuccessful("Cannot delete content property: " + property);
        CustomContentEntityObject storageContentProperty = this.storageContentPropertyManager.getStorageContentProperty(null, property);
        ContentEntityObject container = storageContentProperty.getContainer();
        this.customContentManager.removeContentEntity((ContentEntityObject)storageContentProperty);
        this.confluenceIndexer.reIndex((Searchable)container);
    }

    public void copyAllJsonContentProperties(ContentSelector source, ContentSelector target) throws ServiceException {
        PageResponse results = this.find(DEFAULT_EXPANSIONS.toArray()).withContentId(source.getId()).fetchMany((PageRequest)new SimplePageRequest(0, Integer.MAX_VALUE));
        for (JsonContentProperty jsonContentProperty : results.getResults()) {
            JsonContentProperty property = JsonContentProperty.builder().content(Content.buildReference((ContentSelector)target)).key(jsonContentProperty.getKey()).value(jsonContentProperty.getValue()).build();
            this.create(property);
        }
    }

    public ContentPropertyService.Validator validator() {
        return this.validator;
    }
}

