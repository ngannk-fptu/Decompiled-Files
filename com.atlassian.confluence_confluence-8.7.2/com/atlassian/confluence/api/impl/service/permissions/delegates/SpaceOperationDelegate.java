/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.permissions.spi.OperationCheck
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
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
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.delegate.SpacePermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class SpaceOperationDelegate
extends AbstractOperationDelegate {
    private final SpacePermissionsDelegate permissionDelegate;
    private final SpacePermissionManager spacePermissionManager;
    private final Logger log = LoggerFactory.getLogger(SpaceOperationDelegate.class);

    public SpaceOperationDelegate(SpacePermissionsDelegate permissionDelegate, ConfluenceUserResolver confluenceUserResolver, TargetResolver targetResolver, SpacePermissionManager spacePermissionManager) {
        super(confluenceUserResolver, targetResolver);
        this.permissionDelegate = (SpacePermissionsDelegate)Preconditions.checkNotNull((Object)permissionDelegate);
        this.spacePermissionManager = (SpacePermissionManager)Preconditions.checkNotNull((Object)spacePermissionManager);
    }

    @Override
    protected List<OperationCheck> makeOperations() {
        return ImmutableList.builder().add((Object)new ReadSpaceOperationCheck()).add((Object)new UpdateSpaceOperationCheck()).add((Object)new DeleteSpaceOperationCheck()).add((Object)new CreateSpaceOperationCheck()).add((Object)new ExportSpaceOperationCheck()).build();
    }

    private class ExportSpaceOperationCheck
    extends SpaceOperationCheck {
        ExportSpaceOperationCheck() {
            super(OperationKey.EXPORT);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (SpaceOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Asking questions of things that contain spaces is not currently supported.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)"Asking questions of things that contain spaces is not currently supported.", (Object[])new Object[0]);
            }
            Option<Space> hibernateSpace = SpaceOperationDelegate.this.targetResolver.resolveHibernateObject(target, Space.class);
            if (!hibernateSpace.isDefined()) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
            }
            if (SpaceOperationDelegate.this.permissionDelegate.canExport((User)user, (Space)hibernateSpace.get())) {
                return SimpleValidationResult.VALID;
            }
            SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing export permission.", target, user, SpaceOperationDelegate.this.log));
            return SimpleValidationResults.forbiddenResult((String)"Forbidden. Missing export permission.", (Object[])new Object[0]);
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class DeleteSpaceOperationCheck
    extends SpaceOperationCheck {
        DeleteSpaceOperationCheck() {
            super(OperationKey.DELETE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (SpaceOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Asking questions of things that contain spaces is not currently supported.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)"Asking questions of things that contain spaces is not currently supported.", (Object[])new Object[0]);
            }
            Option<Space> hibernateSpace = SpaceOperationDelegate.this.targetResolver.resolveHibernateObject(target, Space.class);
            if (!hibernateSpace.isDefined()) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
            }
            Space space = (Space)hibernateSpace.get();
            if (SpaceOperationDelegate.this.permissionDelegate.canRemove((User)user, space)) {
                return SimpleValidationResult.VALID;
            }
            SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete permission.", target, user, SpaceOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class UpdateSpaceOperationCheck
    extends SpaceOperationCheck {
        UpdateSpaceOperationCheck() {
            super(OperationKey.UPDATE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (SpaceOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Asking questions of things that contain spaces is not currently supported.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)"Asking questions of things that contain spaces is not currently supported.", (Object[])new Object[0]);
            }
            Option<Space> hibernateSpace = SpaceOperationDelegate.this.targetResolver.resolveHibernateObject(target, Space.class);
            if (!hibernateSpace.isDefined()) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
            }
            Space space = (Space)hibernateSpace.get();
            if (SpaceOperationDelegate.this.permissionDelegate.canEdit((User)user, space)) {
                return SimpleValidationResult.VALID;
            }
            SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing edit permission.", target, user, SpaceOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class CreateSpaceOperationCheck
    extends SpaceOperationCheck {
        CreateSpaceOperationCheck() {
            super(OperationKey.CREATE);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (SpaceOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Asking questions of things that contain spaces is not currently supported.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)"Asking questions of things that contain spaces is not currently supported.", (Object[])new Object[0]);
            }
            Option<Space> hibernateSpaceOption = SpaceOperationDelegate.this.targetResolver.resolveHibernateObject(target, Space.class);
            if (hibernateSpaceOption.isDefined()) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Conflict. Space already exists.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.conflictResult((String)"Space already exists.", (Object[])new Object[0]);
            }
            com.atlassian.confluence.api.model.content.Space apiSpace = SpaceOperationDelegate.this.targetResolver.resolveModelObject(target, com.atlassian.confluence.api.model.content.Space.class);
            if (apiSpace.getType() != SpaceType.GLOBAL) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Non-global spaces are not currently supported. Expected:" + SpaceType.GLOBAL + " Actual:" + apiSpace.getType(), target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)("Non-global spaces are not currently supported. Expected" + SpaceType.GLOBAL + " Actual:" + apiSpace.getType()), (Object[])new Object[0]);
            }
            if (SpaceOperationDelegate.this.permissionDelegate.canCreate(user, PermissionManager.TARGET_APPLICATION)) {
                return SimpleValidationResult.VALID;
            }
            SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing global space creation permission.", target, user, SpaceOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class ReadSpaceOperationCheck
    extends SpaceOperationCheck {
        ReadSpaceOperationCheck() {
            super(OperationKey.READ);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (SpaceOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Asking questions of things that contain spaces is not currently supported.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notImplementedResult((String)"Asking questions of things that contain spaces is not currently supported.", (Object[])new Object[0]);
            }
            Option<Space> hibernateSpace = SpaceOperationDelegate.this.targetResolver.resolveHibernateObject(target, Space.class);
            if (!hibernateSpace.isDefined()) {
                SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, SpaceOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
            }
            if (SpaceOperationDelegate.this.permissionDelegate.canView((User)user, (Space)hibernateSpace.get())) {
                return SimpleValidationResult.VALID;
            }
            SpaceOperationDelegate.this.log.debug(SpaceOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view permission.", target, user, SpaceOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private abstract class SpaceOperationCheck
    extends AbstractOperationDelegate.ConfluenceUserBaseOperationCheck {
        protected SpaceOperationCheck(OperationKey operationKey) {
            super(SpaceOperationDelegate.this, operationKey, TargetType.SPACE);
        }
    }
}

