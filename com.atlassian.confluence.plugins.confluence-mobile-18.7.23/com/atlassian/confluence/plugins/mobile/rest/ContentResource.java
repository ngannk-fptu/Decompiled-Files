/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent
 *  com.atlassian.confluence.event.events.content.page.PageViewEvent
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.dto.CommentDtoFactory;
import com.atlassian.confluence.plugins.mobile.dto.WebResourceDependenciesDto;
import com.atlassian.confluence.plugins.mobile.dto.WebResourceDependenciesDtoFactory;
import com.atlassian.confluence.plugins.mobile.rest.ContentResourceInterface;
import com.atlassian.confluence.plugins.mobile.rest.model.ContentDto;
import com.atlassian.confluence.plugins.mobile.rest.model.ContentDtoFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/content")
public class ContentResource
implements ContentResourceInterface {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final CommentDtoFactory commentDtoFactory;
    private final ContentDtoFactory contentDtoFactory;
    private final WebResourceDependenciesDtoFactory webResourceDependenciesDtoFactory;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;

    public ContentResource(@Qualifier(value="pageManager") PageManager pageManager, PermissionManager permissionManager, CommentDtoFactory commentDtoFactory, ContentDtoFactory contentDtoFactory, WebResourceDependenciesDtoFactory webResourceDependenciesDtoFactory, TransactionTemplate transactionTemplate, EventPublisher eventPublisher) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.commentDtoFactory = commentDtoFactory;
        this.contentDtoFactory = contentDtoFactory;
        this.webResourceDependenciesDtoFactory = webResourceDependenciesDtoFactory;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @GET
    @Path(value="/{id}")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public ContentDto getContent(@PathParam(value="id") Long contentId, final @QueryParam(value="knownResources") String knownResources, final @QueryParam(value="knownContexts") String knownContexts) {
        return this.performContentOperation(contentId, new ContentOperation<ContentDto>(){

            @Override
            public ContentDto perform(ContentEntityObject ceo) {
                ContentDto dto = ContentResource.this.contentDtoFactory.getContentDto(ceo);
                WebResourceDependenciesDto dependenciesDto = ContentResource.this.webResourceDependenciesDtoFactory.getWebResourceDependenciesDto(knownResources, knownContexts);
                dto.setWebResourceDependencies(dependenciesDto);
                return dto;
            }
        });
    }

    @Override
    @GET
    @Path(value="/page/{spaceKey}/{title}/")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public ContentDto getContent(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="title") String title, final @QueryParam(value="knownResources") String knownResources, final @QueryParam(value="knownContexts") String knownContexts) {
        return this.performPageOperation(spaceKey, title, new ContentOperation<ContentDto>(){

            @Override
            public ContentDto perform(ContentEntityObject ceo) {
                ContentDto dto = ContentResource.this.contentDtoFactory.getContentDto(ceo);
                WebResourceDependenciesDto dependenciesDto = ContentResource.this.webResourceDependenciesDtoFactory.getWebResourceDependenciesDto(knownResources, knownContexts);
                dto.setWebResourceDependencies(dependenciesDto);
                return dto;
            }
        });
    }

    @Override
    @GET
    @Path(value="/blogpost/{spaceKey}/{year}/{month}/{day}/{title}/")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public ContentDto getContent(@PathParam(value="spaceKey") String spaceKey, @PathParam(value="title") String title, @PathParam(value="year") int year, @PathParam(value="month") int month, @PathParam(value="day") int day, final @QueryParam(value="knownResources") String knownResources, final @QueryParam(value="knownContexts") String knownContexts) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return this.performBlogPostOperation(spaceKey, title, cal, new ContentOperation<ContentDto>(){

            @Override
            public ContentDto perform(ContentEntityObject ceo) {
                ContentDto dto = ContentResource.this.contentDtoFactory.getContentDto(ceo);
                WebResourceDependenciesDto dependenciesDto = ContentResource.this.webResourceDependenciesDtoFactory.getWebResourceDependenciesDto(knownResources, knownContexts);
                dto.setWebResourceDependencies(dependenciesDto);
                return dto;
            }
        });
    }

    @Override
    @GET
    @Path(value="/{id}/comments")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public List<CommentDto> getComments(@PathParam(value="id") Long contentId, @QueryParam(value="knownResources") Set<String> knownResources, @QueryParam(value="knownContexts") Set<String> knownContexts) {
        return this.performContentOperation(contentId, new ContentOperation<List<CommentDto>>(){

            @Override
            public List<CommentDto> perform(ContentEntityObject ceo) {
                List comments = ceo.getComments();
                ArrayList<CommentDto> commentDtos = new ArrayList<CommentDto>(comments.size());
                for (Comment comment : comments) {
                    commentDtos.add(ContentResource.this.commentDtoFactory.getCommentDto(comment));
                }
                return commentDtos;
            }
        });
    }

    private <T> T performContentOperation(final Long contentId, final ContentOperation<T> operation) {
        return (T)this.transactionTemplate.execute(new TransactionCallback<T>(){

            public T doInTransaction() {
                return ContentResource.this.performContentOperation(ContentResource.this.pageManager.getById(contentId.longValue()), operation);
            }
        });
    }

    private <T> T performPageOperation(final String spaceKey, final String title, final ContentOperation<T> operation) {
        return (T)this.transactionTemplate.execute(new TransactionCallback<T>(){

            public T doInTransaction() {
                return ContentResource.this.performContentOperation((ContentEntityObject)ContentResource.this.pageManager.getPageWithComments(spaceKey, title), operation);
            }
        });
    }

    private <T> T performBlogPostOperation(final String spaceKey, final String title, final Calendar day, final ContentOperation<T> operation) {
        return (T)this.transactionTemplate.execute(new TransactionCallback<T>(){

            public T doInTransaction() {
                return ContentResource.this.performContentOperation((ContentEntityObject)ContentResource.this.pageManager.getBlogPost(spaceKey, title, day, true), operation);
            }
        });
    }

    private <T> T performContentOperation(ContentEntityObject contentEntity, ContentOperation<T> operation) {
        T result;
        if (contentEntity == null || contentEntity.isDeleted()) {
            this.throwNotFoundResponse();
        }
        if (contentEntity != null) {
            contentEntity = (ContentEntityObject)contentEntity.getLatestVersion();
        }
        if (!this.permissionManager.hasPermission(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, (Object)contentEntity)) {
            this.throwNotFoundResponse();
        }
        if ((result = operation.perform(contentEntity)) != null) {
            PageViewEvent event = null;
            if (contentEntity instanceof Page) {
                event = new PageViewEvent((Object)this, (Page)contentEntity);
            } else if (contentEntity instanceof BlogPost) {
                event = new BlogPostViewEvent((Object)this, (BlogPost)contentEntity);
            }
            this.eventPublisher.publish((Object)event);
        }
        return result;
    }

    private void throwNotFoundResponse() {
        if (AuthenticatedUserThreadLocal.getUser() == null) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.UNAUTHORIZED).build());
        }
        throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).build());
    }

    private static interface ContentOperation<T> {
        public T perform(ContentEntityObject var1);
    }
}

