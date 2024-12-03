/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.permissions.spi.OperationCheck
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.permissions.delegates;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.impl.service.permissions.delegates.AbstractOperationDelegate;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.delegate.AttachmentPermissionsDelegate;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class AttachmentOperationDelegate
extends AbstractOperationDelegate {
    private final AttachmentPermissionsDelegate permissionDelegate;
    private final SpacePermissionManager spacePermissionManager;
    private final Logger log = LoggerFactory.getLogger(AttachmentOperationDelegate.class);

    public AttachmentOperationDelegate(AttachmentPermissionsDelegate permissionDelegate, ConfluenceUserResolver confluenceUserResolver, TargetResolver targetResolver, SpacePermissionManager spacePermissionManager) {
        super(confluenceUserResolver, targetResolver);
        this.permissionDelegate = (AttachmentPermissionsDelegate)Preconditions.checkNotNull((Object)permissionDelegate);
        this.spacePermissionManager = (SpacePermissionManager)Preconditions.checkNotNull((Object)spacePermissionManager);
    }

    @Override
    protected List<OperationCheck> makeOperations() {
        return ImmutableList.builder().add((Object)new ReadAttachmentOperationCheck()).add((Object)new UpdateAttachmentOperationCheck()).add((Object)new CreateAttachmentOperationCheck()).add((Object)new DeleteAttachmentOperationCheck()).build();
    }

    private class DeleteAttachmentOperationCheck
    extends AttachmentOperationCheck {
        DeleteAttachmentOperationCheck() {
            super(OperationKey.DELETE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            Option<Object> hibernateContainerObject = AttachmentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Object.class);
            if (!hibernateContainerObject.isDefined()) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
            }
            Object hibernateContainer = hibernateContainerObject.get();
            if (!(hibernateContainer instanceof SpaceContentEntityObject) && !(hibernateContainer instanceof PersonalInformation)) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Unimplemented. Unsupported container type: " + hibernateContainer.getClass().getName(), target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)("Unsupported container type: " + hibernateContainer.getClass().getName()), (Object[])new Object[0]);
            }
            if (AttachmentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                return SimpleValidationResults.notImplementedResult((String)"Not implemented", (Object[])new Object[0]);
            }
            Option<Attachment> hibernateAttachmentOption = AttachmentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Attachment.class);
            if (!hibernateAttachmentOption.isDefined()) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Attachment does not exist.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Attachment does not exist", (Object[])new Object[0]);
            }
            Attachment hibernateAttachment = (Attachment)hibernateAttachmentOption.get();
            if (AttachmentOperationDelegate.this.permissionDelegate.canRemove((User)user, hibernateAttachment)) {
                return SimpleValidationResult.VALID;
            }
            AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete permission.", target, user, AttachmentOperationDelegate.this.log));
            return SimpleValidationResults.forbiddenResult((String)"Missing delete permission", (Object[])new Object[0]);
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class CreateAttachmentOperationCheck
    extends AttachmentOperationCheck {
        CreateAttachmentOperationCheck() {
            super(OperationKey.CREATE);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            Option<Object> hibernateContainerOption = AttachmentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Object.class);
            if (!hibernateContainerOption.isDefined()) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
            }
            Object hibernateContainer = hibernateContainerOption.get();
            if (hibernateContainer instanceof Attachment) {
                throw new BadRequestException("Unsupported container object type " + Attachment.class.getName() + " for this operation");
            }
            if (!(hibernateContainer instanceof SpaceContentEntityObject)) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not implemented. Container type" + hibernateContainer.getClass().getName() + " not supported.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)("Unsupported type: " + hibernateContainer.getClass().getName()), (Object[])new Object[0]);
            }
            if (AttachmentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                if (hibernateContainer instanceof SpaceContentEntityObject) {
                    if (AttachmentOperationDelegate.this.permissionDelegate.canCreate(user, hibernateContainer)) {
                        return SimpleValidationResult.VALID;
                    }
                    AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing create permission.", target, user, AttachmentOperationDelegate.this.log));
                    return SimpleValidationResults.forbiddenResult((String)"Missing Create permission", (Object[])new Object[0]);
                }
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Implemented. Unsupported container type " + hibernateContainer.getClass().getName(), target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)("Unsupported container type " + hibernateContainer.getClass().getName()), (Object[])new Object[0]);
            }
            Option<Attachment> hibernateAttachment = AttachmentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Attachment.class);
            if (hibernateAttachment.isDefined()) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Conflict. Attachment already exists.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.conflictResult((String)"Attachment already exists.", (Object[])new Object[0]);
            }
            if (!AttachmentOperationDelegate.this.permissionDelegate.canCreate(user, hibernateContainer)) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing create permission.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.forbiddenResult((String)"Missing Create permission", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class UpdateAttachmentOperationCheck
    extends AttachmentOperationCheck {
        UpdateAttachmentOperationCheck() {
            super(OperationKey.UPDATE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (AttachmentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Object> hibernateContainer = AttachmentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Object.class);
                if (!hibernateContainer.isDefined()) {
                    AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, AttachmentOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
                }
                return SimpleValidationResults.notImplementedResult((String)"Not implemented", (Object[])new Object[0]);
            }
            Option<Attachment> hibernateAttachment = AttachmentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Attachment.class);
            if (!hibernateAttachment.isDefined()) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Attachment does not exist.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Attachment does not exist", (Object[])new Object[0]);
            }
            if (AttachmentOperationDelegate.this.permissionDelegate.canEdit((User)user, (Attachment)hibernateAttachment.get())) {
                return SimpleValidationResult.VALID;
            }
            AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing edit permission.", target, user, AttachmentOperationDelegate.this.log));
            return SimpleValidationResults.forbiddenResult((String)"Missing Edit Permission", (Object[])new Object[0]);
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class ReadAttachmentOperationCheck
    extends AttachmentOperationCheck {
        ReadAttachmentOperationCheck() {
            super(OperationKey.READ);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (AttachmentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Object> hibernateContainer = AttachmentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Object.class);
                if (!hibernateContainer.isDefined()) {
                    AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, AttachmentOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
                }
                return SimpleValidationResults.notImplementedResult((String)"Not implemented", (Object[])new Object[0]);
            }
            Option<Attachment> hibernateAttachment = AttachmentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Attachment.class);
            if (!hibernateAttachment.isDefined()) {
                AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Attachment does not exist.", target, user, AttachmentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Attachment does not exist", (Object[])new Object[0]);
            }
            if (AttachmentOperationDelegate.this.permissionDelegate.canView((User)user, (Attachment)hibernateAttachment.get())) {
                return SimpleValidationResult.VALID;
            }
            AttachmentOperationDelegate.this.log.debug(AttachmentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view permission.", target, user, AttachmentOperationDelegate.this.log));
            return SimpleValidationResults.forbiddenResult((String)"Missing View Permission", (Object[])new Object[0]);
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private abstract class AttachmentOperationCheck
    extends AbstractOperationDelegate.ConfluenceUserBaseOperationCheck {
        protected AttachmentOperationCheck(OperationKey operationKey) {
            super(AttachmentOperationDelegate.this, operationKey, TargetType.ATTACHMENT);
        }
    }
}

