/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.BaseContentTypeApiSupport;
import com.atlassian.confluence.content.apisupport.ContentCreator;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.time.OffsetDateTime;
import java.util.Date;

public class BlogPostContentTypeApiSupport
extends BaseContentTypeApiSupport<BlogPost> {
    private final ContentCreator contentCreator;

    public BlogPostContentTypeApiSupport(ApiSupportProvider provider, ContentCreator contentCreator) {
        super(provider);
        this.contentCreator = contentCreator;
    }

    @Override
    public ContentType getHandledType() {
        return ContentType.BLOG_POST;
    }

    @Override
    protected PageResponse<Content> getChildrenForThisType(BlogPost content, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    public boolean supportsChildrenOfType(ContentType otherType) {
        return !otherType.equals((Object)ContentType.PAGE);
    }

    @Override
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    public boolean supportsChildrenForParentType(ContentType parentType) {
        return false;
    }

    @Override
    public Class<BlogPost> getEntityClass() {
        return BlogPost.class;
    }

    @Override
    public ValidationResult validateCreate(Content newContent) {
        return this.contentCreator.validateCreate(AuthenticatedUserThreadLocal.get(), newContent, this.getEntityClass());
    }

    @Override
    public ValidationResult validateUpdate(Content updatedContent, BlogPost existingEntity) {
        return this.contentCreator.validateUpdate(AuthenticatedUserThreadLocal.get(), updatedContent, existingEntity);
    }

    @Override
    public BlogPost create(Content newContent) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        BlogPost blog = new BlogPost();
        this.contentCreator.setCommonPropertiesForCreate(newContent, blog, user);
        this.setCreationDate(newContent, blog);
        Draft draft = (Draft)newContent.getExtension("draft");
        return this.contentCreator.saveNewContent(blog, newContent.getVersion(), draft);
    }

    private void setCreationDate(Content newContent, BlogPost blog) {
        History history;
        OffsetDateTime publishDate;
        Reference historyRef = newContent.getHistoryRef();
        if (historyRef.isExpanded() && historyRef.exists() && (publishDate = (history = (History)newContent.getHistoryRef().get()).getCreatedAt()) != null) {
            blog.setCreationDate(Date.from(publishDate.toInstant()));
        }
    }

    @Override
    public BlogPost update(Content updatedContent, BlogPost blog) {
        boolean isNewBlog;
        BlogPost originalBlog = this.contentCreator.cloneForUpdate(blog);
        boolean blogPropertiesChanged = this.contentCreator.setCommonPropertiesForUpdate(updatedContent, blog);
        boolean blogMetadataChanged = this.contentCreator.setCommonMetadata(updatedContent, blog);
        boolean bl = isNewBlog = originalBlog.getOriginalVersionId() == null && originalBlog.getContentStatusObject() == ContentStatus.DRAFT;
        if (isNewBlog) {
            this.setCreationDate(updatedContent, blog);
        }
        boolean changed = blogPropertiesChanged;
        if (changed |= blogMetadataChanged) {
            return this.contentCreator.update(blog, originalBlog, updatedContent.getVersion());
        }
        return blog;
    }
}

