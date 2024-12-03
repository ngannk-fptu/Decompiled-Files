/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.StreamingOutput
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.eventmacro.rest;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.eventmacro.DuplicateReplyException;
import com.atlassian.confluence.extra.calendar3.eventmacro.EventMacroManager;
import com.atlassian.confluence.extra.calendar3.eventmacro.EventRepresentation;
import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import com.atlassian.confluence.extra.calendar3.eventmacro.ReplyDetailsPermissionException;
import com.atlassian.confluence.extra.calendar3.eventmacro.rest.ReplyResource;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="eventmacro/page/{pageId}/macroid/{occurrence}")
public class EventResource {
    private static final Logger LOG = LoggerFactory.getLogger(EventResource.class);
    private static final String CSV_TYPE = "csv";
    private static final String EMAIL_TYPE = "emails";
    private final EventMacroManager eventMacroManager;
    private final PermissionManager permissionManager;
    private final PageManager pageManager;

    EventResource(EventMacroManager eventMacroManager, PageManager pageManager, PermissionManager permissionManager) {
        this.eventMacroManager = eventMacroManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
    }

    @GET
    @AnonymousSiteAccess
    @Produces(value={"application/json"})
    public Response get(@PathParam(value="pageId") String pageId, @PathParam(value="occurrence") String occurrence, @QueryParam(value="type") String type) {
        AbstractPage page = this.pageManager.getAbstractPage(Long.parseLong(pageId));
        if (page == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("No page with id " + pageId)).build();
        }
        boolean hasViewPermission = this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)page);
        if (!hasViewPermission) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        if (CSV_TYPE.equals(type)) {
            try {
                return Response.ok((Object)new CsvStreamingOutput(page, occurrence, this.eventMacroManager)).type("text/csv").header("Content-disposition", (Object)"attachment;filename=RsvpReplies.csv").build();
            }
            catch (Exception e) {
                LOG.error("Unable to generate event macro csv file.", (Object)e.getMessage());
                return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)("Cannot generate CSV file: " + e + (e.getMessage() != null ? e.getMessage() : ""))).build();
            }
        }
        if (EMAIL_TYPE.equals(type)) {
            try {
                return Response.ok((Object)this.eventMacroManager.extractEmails((ContentEntityObject)page, occurrence)).type("text/plain").build();
            }
            catch (Exception e) {
                LOG.error("Error generating email list.", (Throwable)e);
                return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)("Cannot generate email list: " + e + (e.getMessage() != null ? e.getMessage() : ""))).build();
            }
        }
        try {
            return Response.ok((Object)new EventRepresentation(pageId, occurrence, this.eventMacroManager.checkedGetReplyList((ContentEntityObject)page, occurrence))).type("application/json").build();
        }
        catch (ReplyDetailsPermissionException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"You are not permitted to view reply details.").build();
        }
    }

    @POST
    @Produces(value={"application/json"})
    public Response createResponse(@PathParam(value="pageId") String pageId, @PathParam(value="occurrence") String occurrence, NewReplyRepresentation newReplyRepresentation) {
        AbstractPage page = this.pageManager.getAbstractPage(Long.parseLong(pageId));
        if (page == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("No page with id " + pageId)).build();
        }
        try {
            Reply reply = this.eventMacroManager.addReply((ContentEntityObject)page, newReplyRepresentation.getName(), newReplyRepresentation.getEmail(), newReplyRepresentation.getGuests(), newReplyRepresentation.getComment(), newReplyRepresentation.getCustomColumnsMap(), newReplyRepresentation.getCustomCheckboxsMap(), occurrence);
            return Response.ok((Object)new ReplyResponse(reply, this.eventMacroManager.getNumResponders((ContentEntityObject)page, occurrence))).build();
        }
        catch (DuplicateReplyException dpe) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)"duplicate").build();
        }
        catch (Exception e) {
            LOG.error("Error adding event response.", (Throwable)e);
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)("Cannot add reply: " + e + (e.getMessage() != null ? e.getMessage() : ""))).build();
        }
    }

    public static final class NewReplyRepresentation {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String url;
        @JsonProperty
        private final String email;
        @JsonProperty
        private final int guests;
        @JsonProperty
        private final String comment;
        @JsonProperty
        private final Map<String, String> customColumnsMap;
        @JsonProperty
        private final Map<String, Boolean> customCheckboxsMap;

        @JsonCreator
        public NewReplyRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="url") String url, @JsonProperty(value="email") String email, @JsonProperty(value="guests") int guests, @JsonProperty(value="comment") String comment, @JsonProperty(value="customColumnsMap") Map<String, String> customColumnsMap, @JsonProperty(value="customCheckboxsMap") Map<String, Boolean> customCheckboxsMap) {
            this.name = name;
            this.url = url;
            this.email = email;
            this.guests = guests;
            this.comment = comment;
            this.customColumnsMap = customColumnsMap;
            this.customCheckboxsMap = customCheckboxsMap;
        }

        public int getGuests() {
            return this.guests;
        }

        public String getName() {
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }

        public String getEmail() {
            return this.email;
        }

        public Map<String, String> getCustomColumnsMap() {
            return this.customColumnsMap;
        }

        public Map<String, Boolean> getCustomCheckboxsMap() {
            return this.customCheckboxsMap;
        }

        public String getComment() {
            return this.comment;
        }
    }

    public static final class ReplyResponse {
        @JsonProperty
        private final ReplyResource.ReplyRepresentation reply;
        @JsonProperty
        private final int totalResponders;

        public ReplyResponse(Reply reply, int totalResponders) {
            this.reply = new ReplyResource.ReplyRepresentation(reply);
            this.totalResponders = totalResponders;
        }
    }

    public static class CsvStreamingOutput
    implements StreamingOutput {
        AbstractPage page;
        String occurrence;
        EventMacroManager eventMacroManager;

        CsvStreamingOutput(AbstractPage page, String occurrence, EventMacroManager eventMacroManager) {
            this.page = page;
            this.occurrence = occurrence;
            this.eventMacroManager = eventMacroManager;
        }

        public void write(OutputStream output) throws IOException, WebApplicationException {
            OutputStreamWriter outputWriter = new OutputStreamWriter(output);
            try {
                this.eventMacroManager.getCSVText((ContentEntityObject)this.page, this.occurrence, outputWriter);
                ((Writer)outputWriter).flush();
            }
            catch (Exception e) {
                throw new WebApplicationException((Throwable)e, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }
}

