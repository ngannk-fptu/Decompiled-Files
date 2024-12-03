/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentVersionService
 *  com.atlassian.confluence.api.service.content.ContentVersionService$Validator
 *  com.atlassian.confluence.api.service.content.ContentVersionService$VersionFinder
 *  com.atlassian.confluence.api.service.content.VersionRestoreParameters
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.VersionFactory;
import com.atlassian.confluence.api.impl.service.content.finder.VersionFinderFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentVersionService;
import com.atlassian.confluence.api.service.content.VersionRestoreParameters;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ContentVersionServiceImpl
implements ContentVersionService {
    private final VersionFinderFactory versionFinderFactory;
    private final ContentFactory contentFactory;
    private final ContentEntityManagerInternal contentEntityManagerInternal;
    private final PermissionManager permissionManager;
    private final VersionFactory versionFactory;

    public ContentVersionServiceImpl(VersionFinderFactory versionFinderFactory, ContentEntityManagerInternal contentEntityManagerInternal, PermissionManager permissionManager, VersionFactory versionFactory, ContentFactory contentFactory) {
        this.versionFinderFactory = versionFinderFactory;
        this.contentFactory = contentFactory;
        this.contentEntityManagerInternal = contentEntityManagerInternal;
        this.permissionManager = permissionManager;
        this.versionFactory = versionFactory;
    }

    public ContentVersionService.VersionFinder find(Expansion ... expansions) {
        return this.versionFinderFactory.createVersionFinder(this, expansions);
    }

    public ContentVersionService.Validator validator() {
        return new ValidatorImpl();
    }

    public void delete(ContentId contentId, int versionNumber) {
        this.validator().validateDelete(contentId, versionNumber).throwIfNotSuccessful();
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManagerInternal.getById(contentId));
        ContentEntityObject historyVersion = Objects.requireNonNull(this.contentEntityManagerInternal.getOtherVersion(ceo, versionNumber));
        this.contentEntityManagerInternal.removeHistoricalVersion(historyVersion);
    }

    public Version restore(ContentId contentId, VersionRestoreParameters versionRestoreParameters, Expansion ... expansions) {
        this.validator().validateRestore(contentId, versionRestoreParameters).throwIfNotSuccessful();
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManagerInternal.getById(contentId));
        this.contentEntityManagerInternal.revertContentEntityBackToVersion(ceo, versionRestoreParameters.getVersionNumber(), versionRestoreParameters.getMessage(), versionRestoreParameters.getRestoreTitle());
        return this.versionFactory.build(ceo, new Expansions(expansions), this.contentFactory);
    }

    private static class ValidationResultInternal {
        private ValidationResult validationResult;
        private final ContentEntityObject ceo;

        public ValidationResultInternal(ValidationResult validationResult, @Nullable ContentEntityObject ceo) {
            this.validationResult = validationResult;
            this.ceo = ceo;
        }

        public ValidationResult getValidationResult() {
            return this.validationResult;
        }

        public ContentEntityObject getContentEntityObject() {
            return this.ceo;
        }

        public void setValidationResult(ValidationResult validationResult) {
            this.validationResult = validationResult;
        }
    }

    private class ValidatorImpl
    implements ContentVersionService.Validator {
        private ValidatorImpl() {
        }

        public ValidationResult validateDelete(ContentId contentId, int versionNumber) {
            ValidationResultInternal getVersionValidationResult = this.validateGetVersion(contentId, versionNumber);
            ValidationResult validationResult = getVersionValidationResult.getValidationResult();
            if (!validationResult.isValid() || !validationResult.isAuthorized()) {
                return validationResult;
            }
            SpaceContentEntityObject spaceCeo = (SpaceContentEntityObject)getVersionValidationResult.getContentEntityObject();
            SimpleValidationResult.Builder builder = new SimpleValidationResult.Builder().authorized(true);
            return builder.authorized(ContentVersionServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, spaceCeo.getSpace())).build();
        }

        public ValidationResult validateRestore(ContentId contentId, VersionRestoreParameters versionRestoreParameters) {
            return this.validateEdit(contentId, versionRestoreParameters.getVersionNumber());
        }

        public ValidationResult validateGet(ContentId contentId) {
            return this.validateGetInternal(contentId).getValidationResult();
        }

        private ValidationResultInternal validateGetVersion(ContentId contentId, int versionNumber) {
            ValidationResultInternal getResultInternal = this.validateGetInternal(contentId);
            ValidationResult getResult = getResultInternal.getValidationResult();
            if (!getResult.isValid() || !getResult.isAuthorized()) {
                return getResultInternal;
            }
            ContentEntityObject ceo = getResultInternal.getContentEntityObject();
            SimpleValidationResult.Builder builder = this.validateVersion(ceo, versionNumber);
            ValidationResult validationResult = builder.build();
            if (!validationResult.isValid() || !validationResult.isAuthorized()) {
                getResultInternal.setValidationResult(validationResult);
            }
            return getResultInternal;
        }

        private ValidationResultInternal validateGetInternal(ContentId contentId) {
            SimpleValidationResult.Builder builder = new SimpleValidationResult.Builder().authorized(true);
            if (contentId == null || !contentId.isSet()) {
                ValidationResult validationResult = builder.addMessage((Message)SimpleMessage.withTranslation((String)"ContentId is invalid")).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier()).build();
                return new ValidationResultInternal(validationResult, null);
            }
            ContentEntityObject ceo = ContentVersionServiceImpl.this.contentEntityManagerInternal.getById(contentId);
            if (ceo == null) {
                ValidationResult validationResult = builder.addMessage((Message)SimpleMessage.withTranslation((String)("ContentId cannot be found: " + contentId.serialise()))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
                return new ValidationResultInternal(validationResult, null);
            }
            ContentEntityObject latestVersion = (ContentEntityObject)ceo.getLatestVersion();
            ValidationResult validationResult = builder.authorized(ContentVersionServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, latestVersion)).build();
            return new ValidationResultInternal(validationResult, ceo);
        }

        private ValidationResult validateEdit(ContentId contentId, int versionNumber) {
            ValidationResultInternal getVersionValidationResult = this.validateGetVersion(contentId, versionNumber);
            ValidationResult validationResult = getVersionValidationResult.getValidationResult();
            if (!validationResult.isValid() || !validationResult.isAuthorized()) {
                return validationResult;
            }
            ContentEntityObject latestVersion = (ContentEntityObject)getVersionValidationResult.getContentEntityObject().getLatestVersion();
            SimpleValidationResult.Builder builder = new SimpleValidationResult.Builder().authorized(true);
            return builder.authorized(ContentVersionServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, latestVersion)).build();
        }

        private SimpleValidationResult.Builder validateVersion(ContentEntityObject ceo, int versionNumber) {
            SimpleValidationResult.Builder builder = new SimpleValidationResult.Builder().authorized(true);
            if (versionNumber >= ceo.getVersion() || versionNumber <= 0) {
                return builder.addMessage((Message)SimpleMessage.withTranslation((String)("Version to edit must have number less than than current version and start from 1: " + versionNumber))).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
            }
            return builder;
        }
    }
}

