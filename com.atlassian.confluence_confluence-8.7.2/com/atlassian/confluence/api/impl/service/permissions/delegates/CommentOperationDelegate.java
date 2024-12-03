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
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.security.delegate.CommentPermissionsDelegate;
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
public class CommentOperationDelegate
extends AbstractOperationDelegate {
    private final CommentPermissionsDelegate permissionDelegate;
    private final Logger log = LoggerFactory.getLogger(CommentOperationDelegate.class);

    public CommentOperationDelegate(CommentPermissionsDelegate permissionDelegate, ConfluenceUserResolver confluenceUserResolver, TargetResolver targetResolver) {
        super(confluenceUserResolver, targetResolver);
        this.permissionDelegate = (CommentPermissionsDelegate)Preconditions.checkNotNull((Object)permissionDelegate);
    }

    @Override
    protected List<OperationCheck> makeOperations() {
        return ImmutableList.builder().add((Object)new ReadCommentOperationCheck()).add((Object)new UpdateCommentOperationCheck()).add((Object)new CreateCommentOperationCheck()).add((Object)new DeleteCommentOperationCheck()).build();
    }

    private boolean canViewCommentUnderContainer(ConfluenceUser user, ContentEntityObject container) {
        Comment comment = new Comment();
        comment.setContainer(container);
        return this.permissionDelegate.canView((User)user, comment);
    }

    private boolean canUpdateCommentUnderContainer(ConfluenceUser user, ContentEntityObject container) {
        Comment comment = new Comment();
        comment.setContainer(container);
        return this.permissionDelegate.canEdit((User)user, comment);
    }

    private boolean canDeleteCommentUnderContainer(ConfluenceUser user, ContentEntityObject container) {
        Comment comment = new Comment();
        comment.setContainer(container);
        return this.permissionDelegate.canRemove((User)user, comment);
    }

    private class DeleteCommentOperationCheck
    extends CommentOperationCheck {
        DeleteCommentOperationCheck() {
            super(OperationKey.DELETE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (CommentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<ContentEntityObject> hibernateContainerOption = CommentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, ContentEntityObject.class);
                if (!hibernateContainerOption.isDefined()) {
                    CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, CommentOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
                }
                if (CommentOperationDelegate.this.canDeleteCommentUnderContainer(user, (ContentEntityObject)hibernateContainerOption.get())) {
                    return SimpleValidationResult.VALID;
                }
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete permission.", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<Comment> hibernateCommentOption = CommentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Comment.class);
            if (!hibernateCommentOption.isDefined()) {
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Comment does not exist.", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Comment does not exist", (Object[])new Object[0]);
            }
            Comment hibernateComment = (Comment)hibernateCommentOption.get();
            if (CommentOperationDelegate.this.permissionDelegate.canRemove((User)user, hibernateComment)) {
                return SimpleValidationResult.VALID;
            }
            CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden.", target, user, CommentOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class CreateCommentOperationCheck
    extends CommentOperationCheck {
        CreateCommentOperationCheck() {
            super(OperationKey.CREATE);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            Option<ContentEntityObject> hibernateContainerOption = CommentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, ContentEntityObject.class);
            if (!hibernateContainerOption.isDefined()) {
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
            }
            ContentEntityObject hibernateContainer = (ContentEntityObject)hibernateContainerOption.get();
            if (CommentOperationDelegate.this.permissionDelegate.canCreate(user, hibernateContainer)) {
                Option<Comment> commentOption;
                if (!CommentOperationDelegate.this.targetResolver.isContainerTarget(target) && (commentOption = CommentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Comment.class)).isDefined()) {
                    CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Conflict. Comment already exists.", target, user, CommentOperationDelegate.this.log));
                    return SimpleValidationResults.conflictResult((String)"Comment already exists.", (Object[])new Object[0]);
                }
                return SimpleValidationResult.VALID;
            }
            CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. No create permission.", target, user, CommentOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class UpdateCommentOperationCheck
    extends CommentOperationCheck {
        UpdateCommentOperationCheck() {
            super(OperationKey.UPDATE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (CommentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<ContentEntityObject> hibernateContainerOption = CommentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, ContentEntityObject.class);
                if (!hibernateContainerOption.isDefined()) {
                    CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, CommentOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
                }
                if (CommentOperationDelegate.this.canUpdateCommentUnderContainer(user, (ContentEntityObject)hibernateContainerOption.get())) {
                    return SimpleValidationResult.VALID;
                }
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing update permission.", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<Comment> hibernateCommentOption = CommentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Comment.class);
            if (!hibernateCommentOption.isDefined()) {
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Comment does not exist.", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Comment does not exist", (Object[])new Object[0]);
            }
            Comment hibernateComment = (Comment)hibernateCommentOption.get();
            if (CommentOperationDelegate.this.permissionDelegate.canEdit((User)user, hibernateComment)) {
                return SimpleValidationResult.VALID;
            }
            CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden.", target, user, CommentOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class ReadCommentOperationCheck
    extends CommentOperationCheck {
        ReadCommentOperationCheck() {
            super(OperationKey.READ);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (CommentOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<ContentEntityObject> hibernateContainerOption = CommentOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, ContentEntityObject.class);
                if (!hibernateContainerOption.isDefined()) {
                    CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Container does not exist.", target, user, CommentOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Container does not exist", (Object[])new Object[0]);
                }
                if (CommentOperationDelegate.this.canViewCommentUnderContainer(user, (ContentEntityObject)hibernateContainerOption.get())) {
                    return SimpleValidationResult.VALID;
                }
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Unable to view comment under container.", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<Comment> hibernateCommentOption = CommentOperationDelegate.this.targetResolver.resolveHibernateObject(target, Comment.class);
            if (!hibernateCommentOption.isDefined()) {
                CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Comment does not exist.", target, user, CommentOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Comment does not exist", (Object[])new Object[0]);
            }
            if (CommentOperationDelegate.this.permissionDelegate.canView((User)user, (Comment)hibernateCommentOption.get())) {
                return SimpleValidationResult.VALID;
            }
            CommentOperationDelegate.this.log.debug(CommentOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. No view permission.", target, user, CommentOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private abstract class CommentOperationCheck
    extends AbstractOperationDelegate.ConfluenceUserBaseOperationCheck {
        protected CommentOperationCheck(OperationKey operationKey) {
            super(operationKey, TargetType.COMMENT);
        }
    }
}

