/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.watch.ContentWatch
 *  com.atlassian.confluence.api.model.watch.SpaceWatch
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.people.PersonService$PersonFinder
 *  com.atlassian.confluence.api.service.watch.WatchService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.watch.ContentWatch;
import com.atlassian.confluence.api.model.watch.SpaceWatch;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.api.service.watch.WatchService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/user/watch")
public class UserWatchResource {
    private static final String WATCHING = "watching";
    private final WatchService watchService;
    private final PersonService personService;

    public UserWatchResource(@ComponentImport WatchService watchService, @ComponentImport PersonService personService) {
        this.watchService = watchService;
        this.personService = personService;
    }

    @POST
    @Path(value="/space/{spaceKey}")
    @PublicApi
    public SpaceWatch addSpaceWatch(@QueryParam(value="key") UserKey key, @QueryParam(value="username") String username, @QueryParam(value="contentType") List<ContentType> contentTypes, @PathParam(value="spaceKey") String spaceKey) throws ServiceException {
        UserKey watcher = this.figureOutWatcher(key, username);
        return this.watchService.watchSpace(watcher, spaceKey, (List)(contentTypes != null ? contentTypes : new ArrayList()));
    }

    @DELETE
    @Path(value="/space/{spaceKey}")
    @PublicApi
    public Response removeSpaceWatch(@QueryParam(value="key") UserKey key, @QueryParam(value="username") String username, @QueryParam(value="contentType") List<ContentType> contentTypes, @PathParam(value="spaceKey") String spaceKey) throws ServiceException {
        UserKey watcher = this.figureOutWatcher(key, username);
        this.watchService.unwatchSpace(watcher, spaceKey, (List)(contentTypes != null ? contentTypes : new ArrayList()));
        return Response.noContent().build();
    }

    @GET
    @Path(value="/space/{spaceKey}")
    @PublicApi
    public Response isWatchingSpace(@QueryParam(value="key") UserKey key, @QueryParam(value="username") String username, @QueryParam(value="contentType") ContentType contentType, @PathParam(value="spaceKey") String spaceKey) throws ServiceException {
        UserKey watcher = this.figureOutWatcher(key, username);
        boolean watching = contentType != null ? this.watchService.isWatchingSpace(watcher, spaceKey, contentType) : this.watchService.isWatchingSpace(watcher, spaceKey);
        return Response.ok(Collections.singletonMap(WATCHING, watching)).build();
    }

    @POST
    @Path(value="/content/{contentId}")
    @PublicApi
    public ContentWatch addContentWatcher(@QueryParam(value="key") UserKey key, @QueryParam(value="username") String username, @PathParam(value="contentId") ContentId contentId) throws ServiceException {
        UserKey watcher = this.figureOutWatcher(key, username);
        return this.watchService.watchContent(watcher, contentId);
    }

    @DELETE
    @Path(value="/content/{contentId}")
    @PublicApi
    public Response removeContentWatcher(@QueryParam(value="key") UserKey key, @QueryParam(value="username") String username, @PathParam(value="contentId") ContentId contentId) throws ServiceException {
        UserKey watcher = this.figureOutWatcher(key, username);
        this.watchService.unwatchContent(watcher, contentId);
        return Response.noContent().build();
    }

    @GET
    @Path(value="/content/{contentId}")
    @PublicApi
    public Response isWatchingContent(@QueryParam(value="key") UserKey key, @QueryParam(value="username") String username, @PathParam(value="contentId") ContentId contentId) throws ServiceException {
        UserKey watcher = this.figureOutWatcher(key, username);
        boolean value = this.watchService.isWatchingContent(watcher, contentId);
        return Response.ok(Collections.singletonMap(WATCHING, value)).build();
    }

    private UserKey figureOutWatcher(@Nullable UserKey key, @Nullable String username) {
        Person watcher;
        if (key != null && StringUtils.isNotEmpty((CharSequence)username)) {
            throw new BadRequestException("Only one query param of key or username (or none) is allowed");
        }
        PersonService.PersonFinder personFinder = this.personService.find(new Expansion[0]);
        if (key != null) {
            personFinder.withUserKey(key);
            watcher = (Person)personFinder.fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No user found with key :" + key)));
        } else if (username != null) {
            personFinder.withUsername(username);
            watcher = (Person)personFinder.fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No user found with username :" + username)));
        } else {
            watcher = this.personService.getCurrentUser(new Expansion[0]);
        }
        return (UserKey)watcher.optionalUserKey().orElseThrow(ServiceExceptionSupplier.notFound((String)"User doesn't have a userKey"));
    }
}

