/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.eventmacro.rest;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.eventmacro.EventMacroManager;
import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import com.atlassian.confluence.extra.calendar3.eventmacro.ReplyDetailsPermissionException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="eventmacro/page/{pageId}/macroid/{occurrence}/responseid/{id}")
public class ReplyResource {
    private static final Logger LOG = LoggerFactory.getLogger(ReplyResource.class);
    private final EventMacroManager eventMacroManager;
    private final PageManager pageManager;

    ReplyResource(EventMacroManager eventMacroManager, PageManager pageManager) {
        this.eventMacroManager = eventMacroManager;
        this.pageManager = pageManager;
    }

    @GET
    @Produces(value={"application/json"})
    public Response get(@PathParam(value="pageId") long pageId, @PathParam(value="occurrence") String occurrence, @PathParam(value="id") String id) {
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("No page with id " + pageId)).build();
        }
        try {
            Reply reply = this.eventMacroManager.checkedGetReply((ContentEntityObject)page, occurrence, Long.parseLong(id));
            if (reply != null) {
                return Response.ok((Object)new ReplyRepresentation(reply)).build();
            }
        }
        catch (ReplyDetailsPermissionException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"You are not permitted to view reply details.").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No reply with id=" + id + " in macro #" + occurrence + " on page with id=" + pageId)).build();
    }

    @PUT
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response put(@PathParam(value="pageId") long pageId, @PathParam(value="occurrence") String occurrence, ReplyRepresentation replyRepresentation) {
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("No page with id " + pageId)).build();
        }
        Reply reply = this.eventMacroManager.updateReply((ContentEntityObject)page, replyRepresentation.getId(), replyRepresentation.getName(), replyRepresentation.getEmail(), replyRepresentation.isConfirm(), occurrence);
        if (reply != null) {
            return Response.ok((Object)new ReplyRepresentation(reply)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No reply with id=" + replyRepresentation.getId() + " in macro #" + occurrence + " on page with id=" + pageId)).build();
    }

    @DELETE
    @Produces(value={"application/json"})
    public Response delete(@PathParam(value="pageId") long pageId, @PathParam(value="occurrence") String occurrence, @PathParam(value="id") long id) {
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("No page with id " + pageId)).build();
        }
        try {
            Reply promotedReply = this.eventMacroManager.removeFromList((ContentEntityObject)page, id, occurrence);
            return promotedReply == null ? Response.noContent().build() : Response.ok((Object)new ReplyRepresentation(promotedReply)).build();
        }
        catch (Exception e) {
            if (e.getMessage() != null) {
                LOG.info(e.getMessage());
            }
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)("Cannot delete reply: " + e + (e.getMessage() != null ? e.getMessage() : ""))).build();
        }
    }

    public static final class ReplyRepresentation {
        @JsonProperty
        private final long id;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String email;
        @JsonProperty
        private final boolean confirm;
        @JsonProperty
        private final int guests;
        @JsonProperty
        private final String comment;
        @JsonProperty
        private final Map<String, String> customColumnsMap;
        @JsonProperty
        private final Map<String, Boolean> customCheckboxsMap;
        @JsonProperty
        private final boolean inWaitingList;

        @JsonCreator
        public ReplyRepresentation(@JsonProperty(value="id") long id, @JsonProperty(value="name") String name, @JsonProperty(value="email") String email, @JsonProperty(value="confirm") boolean confirm, @JsonProperty(value="url") String url, @JsonProperty(value="guests") int guests, @JsonProperty(value="comment") String comment, @JsonProperty(value="customColumnsMap") Map<String, String> customColumnsMap, @JsonProperty(value="customCheckboxsMap") Map<String, Boolean> customCheckboxsMap, @JsonProperty(value="inWaitingList") boolean inWaitingList) {
            this.name = name;
            this.id = id;
            this.email = email;
            this.confirm = confirm;
            this.guests = guests;
            this.comment = comment;
            this.customColumnsMap = customColumnsMap;
            this.customCheckboxsMap = customCheckboxsMap;
            this.inWaitingList = inWaitingList;
        }

        public ReplyRepresentation(Reply reply) {
            this.name = reply.getName();
            this.id = reply.getId();
            this.email = reply.getEmail();
            this.confirm = reply.isConfirm();
            this.guests = reply.getGuests();
            this.comment = reply.getComment();
            this.customColumnsMap = reply.getCustomValues();
            this.customCheckboxsMap = reply.getCustomCheckboxes();
            this.inWaitingList = reply.isInWaitingList();
        }

        public long getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getEmail() {
            return this.email;
        }

        public boolean isConfirm() {
            return this.confirm;
        }

        public Map<String, String> getCustomColumnsMap() {
            return this.customColumnsMap;
        }

        public Map<String, Boolean> getCustomCheckboxsMap() {
            return this.customCheckboxsMap;
        }

        public boolean isInWaitingList() {
            return this.inWaitingList;
        }
    }
}

