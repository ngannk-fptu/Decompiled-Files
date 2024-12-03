/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.Like
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.like.rest.resources;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugins.like.LikesSorter;
import com.atlassian.confluence.plugins.like.UserEntityExpander;
import com.atlassian.confluence.plugins.like.rest.entities.LikeEntity;
import com.atlassian.confluence.plugins.like.rest.entities.UserEntity;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/content")
public class LikableContentResource {
    private static final Logger log = LoggerFactory.getLogger(LikableContentResource.class);
    private final LikeManager likeManager;
    private final ContentEntityManager contentEntityManager;
    private final NetworkService networkService;
    private final UserEntityExpander userEntityExpander;
    private final LikesSorter likesSorter;
    private final TransactionTemplate transactionTemplate;

    public LikableContentResource(@Qualifier(value="likeManager") LikeManager likeManager, ContentEntityManager contentEntityManager, NetworkService networkService, UserEntityExpander userEntityExpander, TransactionTemplate transactionTemplate) {
        this.likeManager = likeManager;
        this.contentEntityManager = contentEntityManager;
        this.networkService = networkService;
        this.userEntityExpander = userEntityExpander;
        this.likesSorter = new LikesSorter();
        this.transactionTemplate = transactionTemplate;
    }

    @POST
    @ReadOnlyAccessAllowed
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    @Path(value="/likes")
    @AnonymousAllowed
    public Response getLikesForIds(@FormParam(value="ids") Set<Long> contentIds, @FormParam(value="max") Integer max) {
        if (contentIds == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"query param \"ids\" was not specified.").build();
        }
        try {
            return (Response)this.transactionTemplate.execute(() -> {
                HashMap contentMap = Maps.newHashMap();
                for (Long contentId : contentIds) {
                    ContentEntityObject contentEntity = this.contentEntityManager.getById(contentId.longValue());
                    if (contentEntity == null) continue;
                    contentMap.put(contentId, contentEntity);
                }
                if (contentMap.isEmpty()) {
                    return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("no content found with id set: " + contentIds)).build();
                }
                Set<String> followeesUsernames = this.getFolloweesUsernames();
                Map likes = this.likeManager.getLikes(contentMap.values());
                HashMap<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
                for (Long contentId : contentIds) {
                    Map<String, Object> resultPart = this.getLikeResult((ContentEntityObject)contentMap.get(contentId), followeesUsernames, (List)likes.get(contentId), contentId, max);
                    result.put(contentId.toString(), resultPart);
                }
                return Response.ok(result).build();
            });
        }
        catch (Exception e) {
            log.error("REST resource method error: ", (Throwable)e);
            throw new WebApplicationException((Throwable)e);
        }
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/{id}/likes")
    @AnonymousAllowed
    public Response getLikes(@PathParam(value="id") Long contentId, @QueryParam(value="expand") String expand, @QueryParam(value="max") Integer max, @QueryParam(value="commentLikes") Boolean commentLikes) {
        if (contentId == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"path param \"id\" was not specified.").build();
        }
        try {
            return (Response)this.transactionTemplate.execute(() -> {
                ContentEntityObject contentEntity = this.contentEntityManager.getById(contentId.longValue());
                if (contentEntity == null) {
                    return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("no content found with id: " + contentId)).build();
                }
                Set<String> followeesUsernames = this.getFolloweesUsernames();
                Map<String, Object> result = this.getLikeResult(contentEntity, followeesUsernames, this.likeManager.getLikes(contentEntity), contentId, max);
                if (commentLikes != null && commentLikes.booleanValue()) {
                    result.put("commentLikes", this.getCommentLikesResult(contentEntity, followeesUsernames));
                }
                return Response.ok(result).build();
            });
        }
        catch (Exception e) {
            log.error("REST resource method error: ", (Throwable)e);
            throw new WebApplicationException((Throwable)e);
        }
    }

    @POST
    @Produces(value={"application/json"})
    @Path(value="/{id}/likes")
    @Consumes(value={"application/json"})
    public Response addLike(@PathParam(value="id") Long contentId) {
        if (contentId == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"path param \"id\" was not specified.").build();
        }
        try {
            return (Response)this.transactionTemplate.execute(() -> {
                ContentEntityObject contentEntity = this.contentEntityManager.getById(contentId.longValue());
                if (contentEntity == null) {
                    return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("no content found with id: " + contentId)).build();
                }
                Like like = this.likeManager.addLike(contentEntity, (User)AuthenticatedUserThreadLocal.get());
                if (like == null) {
                    return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"The content cannot be liked").build();
                }
                return this.getLikes(contentId, null, null, false);
            });
        }
        catch (ReadOnlyException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("REST resource method error: ", (Throwable)e);
            throw new WebApplicationException((Throwable)e);
        }
    }

    @DELETE
    @Produces(value={"application/json"})
    @Path(value="/{id}/likes")
    @Consumes(value={"application/json"})
    public Response removeLike(@PathParam(value="id") Long contentId) {
        if (contentId == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"path param \"id\" was not specified.").build();
        }
        try {
            return (Response)this.transactionTemplate.execute(() -> {
                ContentEntityObject contentEntity = this.contentEntityManager.getById(contentId.longValue());
                if (contentEntity == null) {
                    return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("no content found with id: " + contentId)).build();
                }
                this.likeManager.removeLike(contentEntity, (User)AuthenticatedUserThreadLocal.get());
                return this.getLikes(contentId, null, null, false);
            });
        }
        catch (ReadOnlyException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("REST resource method error: ", (Throwable)e);
            throw new WebApplicationException((Throwable)e);
        }
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/{id}/comment-likes")
    @AnonymousAllowed
    public Response getCommentLikes(@PathParam(value="id") Long contentId) {
        if (contentId == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"path param \"id\" was not specified.").build();
        }
        try {
            return (Response)this.transactionTemplate.execute(() -> {
                ContentEntityObject contentEntity = this.contentEntityManager.getById(contentId.longValue());
                if (contentEntity == null) {
                    return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("no content found with id: " + contentId)).build();
                }
                Set<String> followeesUsernames = this.getFolloweesUsernames();
                return Response.ok(this.getCommentLikesResult(contentEntity, followeesUsernames)).build();
            });
        }
        catch (Exception e) {
            log.error("REST resource method error: ", (Throwable)e);
            throw new WebApplicationException((Throwable)e);
        }
    }

    private Set<String> getFolloweesUsernames() {
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        Set<String> followeesUsernames = Collections.emptySet();
        if (remoteUser != null) {
            SimplePageRequest pageReq = new SimplePageRequest(0, 0x7FFFFFFE);
            PageResponse followees = this.networkService.getFollowing(remoteUser.getKey(), (PageRequest)pageReq);
            followeesUsernames = followees.getResults().stream().map(user -> Objects.requireNonNull(user).getUsername()).collect(Collectors.toSet());
        }
        return followeesUsernames;
    }

    private Map<String, Object> getLikeResult(ContentEntityObject contentEntity, Set<String> followeesUsernames, List<Like> likes, Long contentId, Integer max) {
        List<Like> entryLikes = this.likesSorter.sort(likes, followeesUsernames);
        if (max != null && !entryLikes.isEmpty()) {
            entryLikes = entryLikes.subList(0, Math.min(entryLikes.size(), max));
        }
        LinkedHashMap<String, Object> resultPart = new LinkedHashMap<String, Object>();
        LinkedList<LikeEntity> likeEntities = new LinkedList<LikeEntity>();
        for (Like like : entryLikes) {
            UserEntity userEntity = new UserEntity(like.getUsername(), followeesUsernames.contains(like.getUsername()));
            likeEntities.add(new LikeEntity(this.userEntityExpander.expand(userEntity)));
        }
        resultPart.put("likes", likeEntities);
        resultPart.put("content_type", contentEntity.getType());
        if (contentId != null) {
            resultPart.put("content_id", String.valueOf(contentId));
        }
        return resultPart;
    }

    private Map<String, Map<String, Object>> getCommentLikesResult(ContentEntityObject contentEntity, Set<String> followeesUsernames) {
        Map likes = this.likeManager.getLikes((Collection)contentEntity.getComments());
        HashMap<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
        for (Map.Entry entry : likes.entrySet()) {
            String entryContentId = ((Long)entry.getKey()).toString();
            ContentEntityObject entryContentEntity = this.contentEntityManager.getById(((Long)entry.getKey()).longValue());
            if (entryContentEntity == null) continue;
            Map<String, Object> resultPart = this.getLikeResult(entryContentEntity, followeesUsernames, (List)entry.getValue(), null, null);
            result.put(entryContentId, resultPart);
        }
        return result;
    }
}

