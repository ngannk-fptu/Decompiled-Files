/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.rest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path(value="/currentUser")
@Produces(value={"application/json"})
public class CurrentUserResource {
    @GET
    public Response getCurrentUser() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Response.status((int)401).build();
        }
        return Response.ok((Object)new UserBean(user.getName(), user.getFullName(), user.getEmail())).build();
    }

    @XmlRootElement
    private static class UserBean {
        @XmlElement
        private String username;
        @XmlElement
        private String fullName;
        @XmlElement
        private String email;

        private UserBean() {
        }

        UserBean(String username, String fullName, String email) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
        }
    }
}

