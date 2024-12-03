/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.rest.model.ContentDto;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

public interface ContentResourceInterface {
    @GET
    @Path(value="/{id}")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public ContentDto getContent(@PathParam(value="id") Long var1, @QueryParam(value="knownResources") String var2, @QueryParam(value="knownContexts") String var3);

    @GET
    @Path(value="/page/{spaceKey}/{title}/")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public ContentDto getContent(@PathParam(value="spaceKey") String var1, @PathParam(value="title") String var2, @QueryParam(value="knownResources") String var3, @QueryParam(value="knownContexts") String var4);

    @GET
    @Path(value="/blogpost/{spaceKey}/{year}/{month}/{day}/{title}/")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public ContentDto getContent(@PathParam(value="spaceKey") String var1, @PathParam(value="title") String var2, @PathParam(value="year") int var3, @PathParam(value="month") int var4, @PathParam(value="day") int var5, @QueryParam(value="knownResources") String var6, @QueryParam(value="knownContexts") String var7);

    @GET
    @Path(value="/{id}/comments")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public List<CommentDto> getComments(@PathParam(value="id") Long var1, @QueryParam(value="knownResources") Set<String> var2, @QueryParam(value="knownContexts") Set<String> var3);
}

