/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.Status
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.ia.rest;

import com.atlassian.confluence.plugins.ia.model.DateNodeBean;
import com.atlassian.confluence.plugins.ia.service.BlogTreeService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.rest.common.Status;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="pagetree/blog")
@AnonymousAllowed
@Produces(value={"application/json"})
public class BlogTreeResource {
    private final BlogTreeService blogTreeService;

    public BlogTreeResource(BlogTreeService blogTreeService) {
        this.blogTreeService = blogTreeService;
    }

    @GET
    public Response getBlogTree(@QueryParam(value="pageId") long id) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        List<DateNodeBean> tree = this.blogTreeService.getBlogTree((User)user, id);
        return tree == null ? Status.notFound().response() : Response.ok(tree).build();
    }

    @GET
    @Path(value="subtree")
    public Response getBlogSubtree(@QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="groupType") int groupType, @QueryParam(value="groupValue") String groupValue) {
        List<Object> subtree = null;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (groupType == 1) {
            subtree = this.blogTreeService.getMonthsWithBlogPosts((User)user, spaceKey, groupValue);
        } else if (groupType == 2) {
            subtree = this.blogTreeService.getBlogsForMonth((User)user, spaceKey, groupValue);
        }
        return subtree == null ? Status.notFound().response() : Response.ok(subtree).build();
    }
}

