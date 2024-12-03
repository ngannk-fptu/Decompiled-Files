/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.security.AdminOnly
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiUploadDisabledAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiUploadEnabledAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.AdminConfigurationService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.security.AdminOnly;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AdminOnly
@Path(value="/admin")
public class AdminResource {
    private static final Logger log = LoggerFactory.getLogger(AdminResource.class);
    private final AdminConfigurationService adminConfigurationService;
    private final EventPublisher eventPublisher;

    public AdminResource(AdminConfigurationService adminConfigurationService, EventPublisher eventPublisher) {
        this.adminConfigurationService = adminConfigurationService;
        this.eventPublisher = eventPublisher;
    }

    @POST
    @Path(value="/user-upload-emoji/{isChecked}")
    public Response setUserUploadEmojis(@PathParam(value="isChecked") String isChecked) {
        log.debug("Will set user upload emojis with param=" + isChecked);
        this.adminConfigurationService.setAllowUserUploadCustomEmojis(Boolean.parseBoolean(isChecked));
        if (Boolean.parseBoolean(isChecked)) {
            this.eventPublisher.publish((Object)new CustomEmojiUploadEnabledAuditEvent());
        } else {
            this.eventPublisher.publish((Object)new CustomEmojiUploadDisabledAuditEvent());
        }
        return Response.ok().build();
    }
}

