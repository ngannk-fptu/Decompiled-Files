/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$Validator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderFactory;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyValidator;
import com.atlassian.confluence.plugins.contentproperty.StorageJsonPropertyManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="contentPropertyValidator")
class ContentPropertyValidatorImpl
implements ContentPropertyService.Validator {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertyValidatorImpl.class);
    private final CustomContentManager customContentManager;
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final StorageJsonPropertyManager storageContentPropertyManager;
    private final ContentPropertyFinderFactory finderFactory;

    @Autowired
    ContentPropertyValidatorImpl(@ComponentImport CustomContentManager customContentManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport PermissionManager permissionManager, StorageJsonPropertyManager storageContentPropertyManager, ContentPropertyFinderFactory finderFactory) {
        this.customContentManager = customContentManager;
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.storageContentPropertyManager = storageContentPropertyManager;
        this.finderFactory = finderFactory;
    }

    public ValidationResult validateCreate(JsonContentProperty newProperty) {
        SimpleValidationResult.Builder result = SimpleValidationResult.builder();
        String key = newProperty.getKey();
        JsonPropertyValidator.validateKey(result, key);
        JsonPropertyValidator.validateValue(result, key, newProperty.getValue());
        if (!newProperty.getContentRef().exists()) {
            result.addError("contentproperty.invalid.content", new Object[0]);
            result.authorized(true);
            return result.build();
        }
        ContentSelector selector = Content.getSelector((Reference)newProperty.getContentRef());
        try {
            Optional<JsonContentProperty> existingProperty = this.findContentProperty(selector, key);
            if (existingProperty.isPresent()) {
                return new SimpleValidationResult.Builder().addMessage((Message)SimpleMessage.withKeyAndArgs((String)"jsonproperty.duplicate.key", (Object[])new Object[0])).withExceptionSupplier(ServiceExceptionSupplier.conflictExceptionSupplier()).build();
            }
        }
        catch (NotFoundException e) {
            log.debug("Can't find/see content - will be detected by permissionManager check below");
        }
        catch (BadRequestException e) {
            log.debug("Can't resolve find request. Will be resolved to an invalid content/permission error below");
        }
        long storageContentId = selector.getId().asLong();
        ContentEntityObject storageContent = this.contentEntityManager.getById(storageContentId);
        if (storageContent == null) {
            result.addError("contentproperty.invalid.content", new Object[0]);
        }
        CustomContentEntityObject storageContentProperty = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.plugins.confluence-content-property-storage:content-property");
        try {
            this.storageContentPropertyManager.updateStorageFromApi(newProperty, storageContentProperty);
            result.authorized(this.permissionManager.hasCreatePermission((User)this.getCurrentUser(), (Object)storageContent, (Object)storageContentProperty));
        }
        catch (ServiceException e) {
            result.authorized(true);
        }
        return result.build();
    }

    public ValidationResult validateUpdate(JsonContentProperty property) throws ConflictException {
        SimpleValidationResult.Builder result = SimpleValidationResult.builder();
        String key = property.getKey();
        JsonPropertyValidator.validateKey(result, key);
        JsonPropertyValidator.validateValue(result, key, property.getValue());
        ContentSelector selector = Content.getSelector((Reference)property.getContentRef());
        try {
            Optional<JsonContentProperty> existingProperty = this.findContentProperty(selector, key);
            if (property.getId() != null && existingProperty.isPresent() && !existingProperty.get().getId().equals((Object)property.getId())) {
                result.addError("jsonproperty.invalid.id", new Object[]{key});
            }
        }
        catch (NotFoundException e) {
            log.debug("Can't find/see content - will be detected by permissionManager check below");
        }
        catch (BadRequestException e) {
            log.debug("Can't resolve find request. Will be resolved to an invalid content/permission error below");
        }
        CustomContentEntityObject storageContentProperty = this.storageContentPropertyManager.getStorageContentProperty(result, property);
        if (storageContentProperty == null) {
            return SimpleValidationResults.notFoundResult((String)"Could not find Property to update.", (Object[])new Object[0]);
        }
        this.validateUpdateVersion(result, property, storageContentProperty);
        result.authorized(this.permissionManager.hasPermission((User)this.getCurrentUser(), Permission.EDIT, (Object)storageContentProperty));
        return result.build();
    }

    public ValidationResult validateDelete(JsonContentProperty property) {
        SimpleValidationResult.Builder result = SimpleValidationResult.builder();
        if (!property.getContentRef().exists()) {
            result.addError("contentproperty.invalid.content", new Object[0]);
        }
        CustomContentEntityObject storageContentProperty = this.storageContentPropertyManager.getStorageContentProperty(result, property);
        result.authorized(this.permissionManager.hasPermission((User)this.getCurrentUser(), Permission.REMOVE, (Object)storageContentProperty));
        return result.build();
    }

    private void validateUpdateVersion(SimpleValidationResult.Builder result, JsonContentProperty updatedProperty, CustomContentEntityObject oldProperty) throws ConflictException {
        Version newVersion = updatedProperty.getVersion();
        if (newVersion == null) {
            result.addError("jsonproperty.version.required", new Object[0]);
            return;
        }
        int expectedCurrentVersion = newVersion.getNumber() - 1;
        int currentVersion = oldProperty.getVersion();
        if (currentVersion != expectedCurrentVersion) {
            throw new ConflictException("You're trying to edit an outdated version of that ContentProperty. Latest version is " + currentVersion);
        }
    }

    private Optional<JsonContentProperty> findContentProperty(ContentSelector selector, String key) throws BadRequestException, NotFoundException {
        return this.finderFactory.createContentPropertyFinder(new Expansion[0]).withContentId(selector.getId()).withPropertyKey(key).fetch();
    }

    private ConfluenceUser getCurrentUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

