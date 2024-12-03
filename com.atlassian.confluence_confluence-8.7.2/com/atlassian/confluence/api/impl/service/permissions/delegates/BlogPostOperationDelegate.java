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
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.delegate.BlogPostPermissionsDelegate;
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
public class BlogPostOperationDelegate
extends AbstractOperationDelegate {
    private final BlogPostPermissionsDelegate permissionDelegate;
    private final SpacePermissionManager spacePermissionManager;
    private final Logger log = LoggerFactory.getLogger(BlogPostOperationDelegate.class);

    public BlogPostOperationDelegate(BlogPostPermissionsDelegate permissionDelegate, ConfluenceUserResolver confluenceUserResolver, TargetResolver targetResolver, SpacePermissionManager spacePermissionManager) {
        super(confluenceUserResolver, targetResolver);
        this.permissionDelegate = (BlogPostPermissionsDelegate)Preconditions.checkNotNull((Object)permissionDelegate);
        this.spacePermissionManager = (SpacePermissionManager)Preconditions.checkNotNull((Object)spacePermissionManager);
    }

    @Override
    protected List<OperationCheck> makeOperations() {
        return ImmutableList.builder().add((Object)new ReadBlogPostOperationCheck()).add((Object)new UpdateBlogPostOperationCheck()).add((Object)new CreateBlogPostOperationCheck()).add((Object)new DeleteBlogPostOperationCheck()).build();
    }

    private boolean canViewBlogPostUnderSpace(ConfluenceUser user, Space hibernateContainer) {
        return this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", hibernateContainer, user);
    }

    private boolean canDeleteBlogPostUnderSpace(ConfluenceUser user, Space space) {
        return this.spacePermissionManager.hasPermissionNoExemptions("REMOVEBLOG", space, user);
    }

    private boolean canUpdateBlogPostUnderSpace(ConfluenceUser user, Space space) {
        return this.spacePermissionManager.hasPermissionNoExemptions("EDITBLOG", space, user);
    }

    private class DeleteBlogPostOperationCheck
    extends BlogPostOperationCheck {
        DeleteBlogPostOperationCheck() {
            super(OperationKey.DELETE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (BlogPostOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Space> hibernateContainer = BlogPostOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
                if (!hibernateContainer.isDefined()) {
                    BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, BlogPostOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
                }
                if (!BlogPostOperationDelegate.this.canViewBlogPostUnderSpace(user, (Space)hibernateContainer.get())) {
                    BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view under space permission.", target, user, BlogPostOperationDelegate.this.log));
                    return SimpleValidationResult.FORBIDDEN;
                }
                if (BlogPostOperationDelegate.this.canDeleteBlogPostUnderSpace(user, (Space)hibernateContainer.get())) {
                    return SimpleValidationResult.VALID;
                }
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete under space permission.", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<BlogPost> hibernateBlogPost = BlogPostOperationDelegate.this.targetResolver.resolveHibernateObject(target, BlogPost.class);
            if (!hibernateBlogPost.isDefined()) {
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Blog post does not exist", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Blog post does not exist", (Object[])new Object[0]);
            }
            BlogPost blogPost = (BlogPost)hibernateBlogPost.get();
            if (BlogPostOperationDelegate.this.permissionDelegate.canRemove((User)user, blogPost)) {
                return SimpleValidationResult.VALID;
            }
            BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete permission.", target, user, BlogPostOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class CreateBlogPostOperationCheck
    extends BlogPostOperationCheck {
        CreateBlogPostOperationCheck() {
            super(OperationKey.CREATE);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            Option<Space> spaceOption = BlogPostOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
            if (!spaceOption.isDefined()) {
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
            }
            Space containerHibernateObject = (Space)spaceOption.get();
            if (BlogPostOperationDelegate.this.permissionDelegate.canCreate(user, containerHibernateObject)) {
                Option<BlogPost> blogPostOption;
                if (!BlogPostOperationDelegate.this.targetResolver.isContainerTarget(target) && (blogPostOption = BlogPostOperationDelegate.this.targetResolver.resolveHibernateObject(target, BlogPost.class)).isDefined()) {
                    BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Conflict. Blog post already exists.", target, user, BlogPostOperationDelegate.this.log));
                    return SimpleValidationResults.conflictResult((String)"Blog post already exists.", (Object[])new Object[0]);
                }
                return SimpleValidationResult.VALID;
            }
            BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing create in space permission.", target, user, BlogPostOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class UpdateBlogPostOperationCheck
    extends BlogPostOperationCheck {
        UpdateBlogPostOperationCheck() {
            super(OperationKey.UPDATE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (BlogPostOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Space> hibernateContainer = BlogPostOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
                if (!hibernateContainer.isDefined()) {
                    BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, BlogPostOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
                }
                if (!BlogPostOperationDelegate.this.canViewBlogPostUnderSpace(user, (Space)hibernateContainer.get())) {
                    BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view under space permission.", target, user, BlogPostOperationDelegate.this.log));
                    return SimpleValidationResult.FORBIDDEN;
                }
                if (BlogPostOperationDelegate.this.canUpdateBlogPostUnderSpace(user, (Space)hibernateContainer.get())) {
                    return SimpleValidationResult.VALID;
                }
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing update under space permission.", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<BlogPost> hibernateBlogPost = BlogPostOperationDelegate.this.targetResolver.resolveHibernateObject(target, BlogPost.class);
            if (!hibernateBlogPost.isDefined()) {
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Blog post does not exist", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Blog post does not exist", (Object[])new Object[0]);
            }
            BlogPost blogPost = (BlogPost)hibernateBlogPost.get();
            if (BlogPostOperationDelegate.this.permissionDelegate.canEdit((User)user, blogPost)) {
                return SimpleValidationResult.VALID;
            }
            BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing update permission.", target, user, BlogPostOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private class ReadBlogPostOperationCheck
    extends BlogPostOperationCheck {
        ReadBlogPostOperationCheck() {
            super(OperationKey.READ);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (BlogPostOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Space> hibernateContainer = BlogPostOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
                if (!hibernateContainer.isDefined()) {
                    BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, BlogPostOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
                }
                if (BlogPostOperationDelegate.this.canViewBlogPostUnderSpace(user, (Space)hibernateContainer.get())) {
                    return SimpleValidationResult.VALID;
                }
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view under space permission.", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<BlogPost> hibernateBlogPost = BlogPostOperationDelegate.this.targetResolver.resolveHibernateObject(target, BlogPost.class);
            if (!hibernateBlogPost.isDefined()) {
                BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Blog post does not exist.", target, user, BlogPostOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"BlogPost does not exist", (Object[])new Object[0]);
            }
            if (BlogPostOperationDelegate.this.permissionDelegate.canView((User)user, (BlogPost)hibernateBlogPost.get())) {
                return SimpleValidationResult.VALID;
            }
            BlogPostOperationDelegate.this.log.debug(BlogPostOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view permission.", target, user, BlogPostOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }
    }

    private abstract class BlogPostOperationCheck
    extends AbstractOperationDelegate.ConfluenceUserBaseOperationCheck {
        protected BlogPostOperationCheck(OperationKey operationKey) {
            super(BlogPostOperationDelegate.this, operationKey, TargetType.BLOG_POST);
        }
    }
}

