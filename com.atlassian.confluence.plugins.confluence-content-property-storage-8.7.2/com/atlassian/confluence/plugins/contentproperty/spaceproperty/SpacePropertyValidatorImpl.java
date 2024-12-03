/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.SpacePropertyService$Validator
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.validation.Validation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNull
 *  org.checkerframework.checker.nullness.qual.RequiresNonNull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.spaceproperty;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyValidator;
import com.atlassian.confluence.plugins.contentproperty.StorageJsonPropertyManager;
import com.atlassian.confluence.plugins.contentproperty.spaceproperty.SpacePropertyFinderFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.validation.Validation;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="spacePropertyValidator")
public class SpacePropertyValidatorImpl
implements SpacePropertyService.Validator {
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final SpacePropertyFinderFactory finderFactory;
    private final StorageJsonPropertyManager storageContentPropertyManager;
    private final RetentionFeatureChecker retentionFeatureChecker;

    @Autowired
    public SpacePropertyValidatorImpl(@ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, SpacePropertyFinderFactory finderFactory, StorageJsonPropertyManager storageContentPropertyManager, @ComponentImport RetentionFeatureChecker retentionFeatureChecker) {
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.finderFactory = finderFactory;
        this.storageContentPropertyManager = storageContentPropertyManager;
        this.retentionFeatureChecker = retentionFeatureChecker;
    }

    public ValidationResult validateCreate(JsonSpaceProperty newProperty) {
        return Validation.success((Object)newProperty).applyValidation(prop -> this.validateKeyAndValue((JsonSpaceProperty)prop)).applyValidation(prop -> this.validateSpaceExistsInProperty((JsonSpaceProperty)prop)).applyValidation(prop -> this.validateSpaceFound((JsonSpaceProperty)prop).applyValidation(space -> this.validateCanView((JsonSpaceProperty)prop, space).applyValidation(x -> this.validateCanEdit((JsonSpaceProperty)x, space)))).applyValidation(prop -> this.validateDuplicate((JsonSpaceProperty)prop)).getValidationResult();
    }

    public ValidationResult validateUpdate(JsonSpaceProperty property) {
        return Validation.success((Object)property).applyValidation(prop -> this.validateKeyAndValue((JsonSpaceProperty)prop)).applyValidation(prop -> this.validateSpaceExistsInProperty((JsonSpaceProperty)prop).applyValidation(space -> this.validatePropertyFound((JsonSpaceProperty)prop)).applyValidation(propertyCEO -> this.validateCanView((JsonSpaceProperty)prop, propertyCEO).applyValidation(x -> this.validateCanEdit((JsonSpaceProperty)x, propertyCEO)).applyValidation(x -> this.validateVersionFound((JsonSpaceProperty)x)).applyValidation(x -> this.validateVersion((JsonSpaceProperty)x, (CustomContentEntityObject)propertyCEO)))).getValidationResult();
    }

    public ValidationResult validateDelete(JsonSpaceProperty property) {
        return Validation.success((Object)property).applyValidation(prop -> this.validateSpaceExistsInProperty((JsonSpaceProperty)prop)).applyValidation(prop -> this.validateKeyExistsInProperty((JsonSpaceProperty)prop)).applyValidation(prop -> this.validatePropertyFound((JsonSpaceProperty)prop).applyValidation(propertyCEO -> this.validateCanView((JsonSpaceProperty)prop, propertyCEO).applyValidation(x -> this.validateCanRemove((JsonSpaceProperty)prop, propertyCEO)))).getValidationResult();
    }

    private Validation<JsonSpaceProperty> validateKeyExistsInProperty(JsonSpaceProperty prop) {
        return prop.getKey() != null ? Validation.success((Object)prop) : Validation.fail((ValidationResult)SimpleValidationResult.builder().authorized(true).addError("jsonproperty.key.required", new Object[0]).build());
    }

    private Validation<JsonSpaceProperty> validateKeyAndValue(JsonSpaceProperty property) {
        SimpleValidationResult.Builder builder = SimpleValidationResult.builder();
        JsonPropertyValidator.validateKey(builder, property.getKey());
        JsonPropertyValidator.validateValue(builder, property.getKey(), property.getValue());
        return !builder.hasErrors() ? Validation.success((Object)property) : Validation.fail((ValidationResult)builder.authorized(true).build());
    }

    private Validation<JsonSpaceProperty> validateSpaceExistsInProperty(JsonSpaceProperty property) {
        return property.getSpaceRef().exists() ? Validation.success((Object)property) : Validation.fail((ValidationResult)SimpleValidationResult.builder().authorized(true).addError("spaceproperty.invalid.space", new Object[0]).build());
    }

    private Validation<Space> validateSpaceFound(JsonSpaceProperty property) {
        String spaceKey = property.getSpace().getKey();
        Space internalSpace = this.spaceManager.getSpace(spaceKey);
        return internalSpace != null ? Validation.success((Object)internalSpace) : Validation.fail((ValidationResult)SimpleValidationResults.notFoundResult((String)"spaceproperty.invalid.space", (Object[])new Object[]{property.getSpace().getKey()}));
    }

    private Validation<JsonSpaceProperty> validateCanView(JsonSpaceProperty property, Object target) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, target) ? Validation.success((Object)property) : Validation.fail((ValidationResult)SimpleValidationResults.notFoundResult((String)"spaceproperty.invalid.space", (Object[])new Object[]{property.getSpace().getKey()}));
    }

    private boolean isSystemAdminLevelUser() {
        return this.permissionManager.isSystemAdministrator((User)AuthenticatedUserThreadLocal.get());
    }

    private Validation<JsonSpaceProperty> validateCanEdit(JsonSpaceProperty property, Object target) {
        if (this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable() && this.isSystemAdminLevelUser()) {
            return Validation.success((Object)property);
        }
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, target) ? Validation.success((Object)property) : Validation.fail((ValidationResult)SimpleValidationResult.FORBIDDEN);
    }

    private Validation<JsonSpaceProperty> validateCanRemove(JsonSpaceProperty property, Object target) {
        if (this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable() && this.isSystemAdminLevelUser()) {
            return Validation.success((Object)property);
        }
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, target) ? Validation.success((Object)property) : Validation.fail((ValidationResult)SimpleValidationResult.FORBIDDEN);
    }

    private Validation<JsonSpaceProperty> validateDuplicate(JsonSpaceProperty property) {
        return !this.findProperty(property.getSpace().getKey(), property.getKey()).isPresent() ? Validation.success((Object)property) : Validation.fail((ValidationResult)SimpleValidationResults.conflictResult((String)"spaceproperty.duplicate.key", (Object[])new Object[0]));
    }

    private Validation<CustomContentEntityObject> validatePropertyFound(JsonSpaceProperty property) {
        CustomContentEntityObject storageContentProperty = this.storageContentPropertyManager.getStorageSpaceProperty(property);
        return storageContentProperty != null ? Validation.success((Object)storageContentProperty) : Validation.fail((ValidationResult)SimpleValidationResults.notFoundResult((String)"jsonproperty.invalid.property", (Object[])new Object[0]));
    }

    @EnsuresNonNull(value={"property.getVersion()"})
    private Validation<JsonSpaceProperty> validateVersionFound(JsonSpaceProperty property) {
        return property.getVersion() != null ? Validation.success((Object)property) : Validation.fail((ValidationResult)SimpleValidationResult.builder().authorized(true).addError("jsonproperty.version.required", new Object[0]).build());
    }

    @RequiresNonNull(value={"newProperty.getVersion()"})
    private Validation<JsonSpaceProperty> validateVersion(JsonSpaceProperty newProperty, CustomContentEntityObject oldPropertyCEO) {
        return Objects.requireNonNull(newProperty.getVersion()).getNumber() == oldPropertyCEO.getVersion() + 1 ? Validation.success((Object)newProperty) : Validation.fail((ValidationResult)SimpleValidationResults.conflictResult((String)"jsonproperty.version.conflict", (Object[])new Object[]{oldPropertyCEO.getVersion(), newProperty.getVersion().getNumber()}));
    }

    private Optional<JsonSpaceProperty> findProperty(String spaceKey, String key) {
        return this.finderFactory.createSpacePropertyFinder(new Expansion("version")).withSpaceKey(spaceKey).withPropertyKey(key).fetch();
    }
}

