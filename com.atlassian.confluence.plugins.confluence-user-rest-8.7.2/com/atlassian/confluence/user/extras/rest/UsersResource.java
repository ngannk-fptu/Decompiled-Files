/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.security.password.Credential
 *  com.google.common.collect.Maps
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.extras.rest;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.extras.builders.UsersEntityBuilder;
import com.atlassian.confluence.user.extras.entities.UserCreateRequestEntity;
import com.atlassian.confluence.user.extras.entities.UserEntity;
import com.atlassian.confluence.user.extras.entities.UsersEntity;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.security.password.Credential;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/users")
@Produces(value={"application/json", "application/xml"})
public class UsersResource {
    private final UsersEntityBuilder usersEntityBuilder;
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private static final String DEFAULT_MAX_RESULTS_QUERY = "50";

    public UsersResource(UsersEntityBuilder usersEntityBuilder, PermissionManager permissionManager, UserAccessor userAccessor, SpaceManager spaceManager) {
        this.usersEntityBuilder = usersEntityBuilder;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
    }

    @GET
    public Response getUsers(@DefaultValue(value="") @QueryParam(value="query") String query, @DefaultValue(value="") @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="start") int start, @DefaultValue(value="50") @QueryParam(value="limit") int limit) {
        Space space;
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        if (authenticatedUser == null) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        if (StringUtils.isNotBlank((CharSequence)spaceKey) ? (space = this.spaceManager.getSpace(spaceKey)) == null || !this.permissionManager.hasPermission((com.atlassian.user.User)authenticatedUser, Permission.SET_PERMISSIONS, (Object)space) : !this.permissionManager.hasPermission((com.atlassian.user.User)authenticatedUser, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setUsers(this.searchUsers(query, start, limit));
        return Response.ok((Object)usersEntity).build();
    }

    @POST
    @Path(value="/create")
    @Consumes(value={"application/json"})
    public Response createUser(UserCreateRequestEntity request) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        String username = request.getUsername();
        String password = request.getPassword();
        DefaultUser user = new DefaultUser(username, request.getFullName(), request.getEmail());
        Credential unencrypted = Credential.unencrypted((String)(null != password ? password : username));
        this.userAccessor.createUser((com.atlassian.user.User)user, unencrypted);
        this.userAccessor.addMembership(this.userAccessor.getNewUserDefaultGroupName(), username);
        return Response.status((Response.Status)Response.Status.OK).build();
    }

    @POST
    @Path(value="/create/domain")
    public Response createUsers(@QueryParam(value="usernames") String usernamesStr, @QueryParam(value="domain") String emailDomain) {
        String[] usernames;
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        HashMap result = Maps.newHashMap();
        ArrayList<String> created = new ArrayList<String>();
        ArrayList<String> failed = new ArrayList<String>();
        result.put("created", created);
        result.put("failed", failed);
        for (String username : usernames = usernamesStr.split(",")) {
            String email = username + "@" + emailDomain;
            DefaultUser user = new DefaultUser(username, username, email);
            try {
                this.userAccessor.createUser((com.atlassian.user.User)user, Credential.unencrypted((String)username));
                this.userAccessor.addMembership(this.userAccessor.getNewUserDefaultGroupName(), username);
                created.add(username);
            }
            catch (Exception e) {
                failed.add(username);
            }
        }
        return Response.ok((Object)result).build();
    }

    private List<UserEntity> searchUsers(String query, int start, int limit) {
        List<User> users = this.usersEntityBuilder.getUsers(query, start, limit);
        ArrayList<UserEntity> userEntities = new ArrayList<UserEntity>();
        users.forEach(user -> userEntities.add(UserEntity.builder().username(user.getName()).displayName(user.getDisplayName()).active(user.isActive()).build()));
        return userEntities;
    }
}

