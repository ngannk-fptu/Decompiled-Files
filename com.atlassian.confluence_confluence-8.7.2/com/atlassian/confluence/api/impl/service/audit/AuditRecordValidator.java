/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.AffectedObject
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.audit.ChangedValue
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.audit.AuditService$Validator
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.confluence.api.model.audit.AffectedObject;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.audit.ChangedValue;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.audit.AuditService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Strings;
import java.time.Instant;

@Deprecated
public class AuditRecordValidator
implements AuditService.Validator {
    private final PermissionManager permissionManager;

    AuditRecordValidator(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    private ValidationResult validateChangedValue(ChangedValue changedValue) {
        if (changedValue == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Changed value cannot be null", new Object[0]).build();
        }
        if (Strings.isNullOrEmpty((String)changedValue.getName())) {
            return SimpleValidationResult.builder().authorized(true).addError("Changed value name cannot be null or empty", new Object[0]).build();
        }
        if (Strings.isNullOrEmpty((String)changedValue.getOldValue()) && Strings.isNullOrEmpty((String)changedValue.getNewValue())) {
            return SimpleValidationResult.builder().authorized(true).addError("Both the new and old values of a ChangedValue cannot be empty", new Object[0]).build();
        }
        return SimpleValidationResult.VALID;
    }

    private ValidationResult validateAffectedObject(AffectedObject affectedObject) {
        if (affectedObject == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Affected object cannot be null", new Object[0]).build();
        }
        if (affectedObject.getName() == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Affected object name cannot be null", new Object[0]).build();
        }
        if (affectedObject.getObjectType() == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Affected object type cannot be null", new Object[0]).build();
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateCreate(AuditRecord record) throws ServiceException {
        if (record.getSummary() == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Summary cannot be null", new Object[0]).build();
        }
        if (record.getCategory() == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Category cannot be null", new Object[0]).build();
        }
        if (record.getChangedValues() == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Changed values cannot be null", new Object[0]).build();
        }
        if (record.getAssociatedObjects() == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Associated objects cannot be null", new Object[0]).build();
        }
        record.getChangedValues().forEach(value -> this.validateChangedValue((ChangedValue)value).throwIfNotSuccessful("Could not create changed value"));
        record.getAssociatedObjects().forEach(object -> this.validateAffectedObject((AffectedObject)object).throwIfNotSuccessful("Could not create associated object"));
        this.validateAffectedObject(record.getAffectedObject()).throwIfNotSuccessful("Could not create affected object");
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateDelete(Instant before) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.isSystemAdministrator(currentUser)) {
            return SimpleValidationResult.FORBIDDEN;
        }
        if (before == null) {
            return SimpleValidationResult.builder().authorized(true).addError("Cannot clean records from before null", new Object[0]).build();
        }
        return SimpleValidationResult.VALID;
    }
}

