/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesService
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang.LocaleUtils
 *  org.codehaus.jettison.json.JSONException
 *  org.codehaus.jettison.json.JSONObject
 */
package com.atlassian.whisper.plugin.rest;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesService;
import com.atlassian.whisper.plugin.impl.WhisperMessagesDataProvider;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.LocaleUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path(value="messages")
@Produces(value={"application/json"})
@Named
public class MessagesResource {
    private final MessagesService messagesService;
    private final UserManager userManager;

    @Inject
    public MessagesResource(@ComponentImport UserManager userManager, MessagesService mesagesService) {
        this.userManager = userManager;
        this.messagesService = mesagesService;
    }

    @GET
    public Response getMessages() {
        return Response.ok((Object)WhisperMessagesDataProvider.messagesAsJson(this.messagesService.getMessagesForCurrentUser()).toString()).build();
    }

    @GET
    @Path(value="override/{experienceId}")
    public Response hasOverride(@PathParam(value="experienceId") String experienceId, @QueryParam(value="locale") String locale) {
        return Response.ok((Object)this.messagesService.hasOverride(this.userManager.getRemoteUser(), experienceId, LocaleUtils.toLocale((String)locale))).build();
    }

    @GET
    @Path(value="iframe/{messageId}")
    public Response getIframeContent(@PathParam(value="messageId") String messageId) {
        Set messages = this.messagesService.getMessagesForCurrentUser();
        Optional<String> content = messages.stream().filter(message -> message.getId().equals(messageId)).map(Message::getContent).findAny();
        if (content.isPresent()) {
            try {
                JSONObject json = new JSONObject(content.get());
                String srcdoc = json.getJSONObject("component").getJSONObject("iframe").getString("srcdoc");
                return Response.ok((Object)srcdoc, (MediaType)MediaType.TEXT_HTML_TYPE).build();
            }
            catch (JSONException e) {
                return Response.serverError().build();
            }
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

