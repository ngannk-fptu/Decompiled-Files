/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.rpc.auth;

import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.confluence.event.events.security.RpcAuthenticatedEvent;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.rpc.InvalidSessionException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.security.login.LoginResult;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.Map;

public class TokenAuthenticationManager {
    public static final String CACHE_KEY = TokenAuthenticationManager.class.getName() + ".tokens";
    private final LoginManager loginManager;
    private final ConfluenceUserResolver userResolver;
    private final PermissionManager permissionManager;
    private final SettingsManager settingsManager;
    private final EventPublisher eventPublisher;
    private final SharedDataManager clusterSharedDataManager;

    public TokenAuthenticationManager(LoginManager loginManager, ConfluenceUserResolver userResolver, PermissionManager permissionManager, SettingsManager settingsManager, EventPublisher eventPublisher, SharedDataManager clusterSharedDataManager) {
        this.loginManager = (LoginManager)Preconditions.checkNotNull((Object)loginManager);
        this.userResolver = (ConfluenceUserResolver)Preconditions.checkNotNull((Object)userResolver);
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager);
        this.settingsManager = (SettingsManager)Preconditions.checkNotNull((Object)settingsManager);
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.clusterSharedDataManager = (SharedDataManager)Preconditions.checkNotNull((Object)clusterSharedDataManager);
    }

    public String login(String username, String password) throws RemoteException {
        if (this.loginManager.requiresElevatedSecurityCheck(username)) {
            this.loginManager.onFailedLoginAttempt(username, null);
            throw new AuthenticationFailedException("Attempt to log in user '" + username + "' failed. The maximum number of failed login attempts has been reached. Please log into the web application through the web interface to reset the number of failed login attempts.");
        }
        LoginResult result = this.loginManager.authenticate(username, password);
        if (!LoginResult.OK.equals((Object)result)) {
            throw new AuthenticationFailedException("Attempt to log in user '" + username + "' failed - incorrect username/password combination.");
        }
        ConfluenceUser user = this.userResolver.getUserByName(username);
        if (null == user) {
            throw new AuthenticationFailedException("Attempt to log in user '" + username + "' failed - incorrect username/password combination.");
        }
        String token = this.createToken(user);
        this.eventPublisher.publish((Object)new RpcAuthenticatedEvent(this, user, token));
        return token;
    }

    public boolean logout(String token) throws RemoteException {
        String value = this.getTokenMap().get(token);
        if (value == null) {
            return false;
        }
        this.getTokenMap().remove(token);
        return true;
    }

    private String createToken(User user) throws RemoteException {
        String token = DefaultSecureTokenGenerator.getInstance().generateToken();
        int count = 0;
        while (this.getTokenMap().get(token) != null && count++ < 10) {
            token = DefaultSecureTokenGenerator.getInstance().generateToken();
        }
        if (count >= 10) {
            throw new RemoteException("Error generating auth token - what the?");
        }
        this.getTokenMap().put(token, user.getName());
        return token;
    }

    public ConfluenceUser makeNonAnonymousConfluenceUserFromToken(String token) throws InvalidSessionException {
        ConfluenceUser user = this.retrieveUser(token);
        if (user == null) {
            throw new InvalidSessionException("User not authenticated or session expired. Call login() to open a new session");
        }
        return user;
    }

    public ConfluenceUser makeAnonymousConfluenceUser() throws NotPermittedException {
        if (!this.settingsManager.getGlobalSettings().isAllowRemoteApiAnonymous()) {
            throw new NotPermittedException("Anonymous RPC access is disabled on this server");
        }
        return null;
    }

    public boolean hasUseConfluencePermission(User user) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }

    private ConfluenceUser retrieveUser(String token) {
        String name = this.getTokenMap().get(token);
        return this.userResolver.getUserByName(name);
    }

    private Map<String, String> getTokenMap() {
        return this.clusterSharedDataManager.getSharedData(this.getClass().getSimpleName() + ".tokens").getMap();
    }
}

