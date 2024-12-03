/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.user.profile.rest;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.plugins.user.profile.UserAvatarService;
import com.atlassian.confluence.plugins.user.profile.rest.UserAvatar;
import com.atlassian.confluence.rpc.NotPermittedException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

@Path(value="/{userKey}/avatar")
@Produces(value={"application/json"})
public class UserAvatarResource {
    private UserAvatarService userAvatarService;

    public UserAvatarResource(UserAvatarService userAvatarService) {
        this.userAvatarService = userAvatarService;
    }

    @GET
    @Path(value="default")
    public Response getDefaultLogo() {
        return Response.ok().build();
    }

    @POST
    @LimitRequestSize(value=0x500000L)
    @Consumes(value={"application/json"})
    @Path(value="upload")
    public Response setUploadedAvatar(@PathParam(value="userKey") String userKey, AvatarDetails avatarDetails) {
        try {
            if (StringUtils.isNotEmpty((CharSequence)avatarDetails.getAvatarDataURI())) {
                Attachment avatar = this.userAvatarService.saveAvatar(userKey, avatarDetails.getAvatarDataURI());
                return Response.ok((Object)new UserAvatar(avatar.getDownloadPath())).build();
            }
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)e.getMessage()).build();
        }
    }

    @XmlRootElement
    private static class AvatarDetails {
        @XmlElement
        private String avatarDataURI;

        private AvatarDetails() {
        }

        public String getAvatarDataURI() {
            return this.avatarDataURI;
        }
    }
}

