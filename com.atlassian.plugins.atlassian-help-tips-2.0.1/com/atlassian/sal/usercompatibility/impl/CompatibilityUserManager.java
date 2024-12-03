/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.user.UserResolutionException
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.usercompatibility.impl;

import com.atlassian.sal.api.user.UserResolutionException;
import com.atlassian.sal.usercompatibility.CompatibilityUserUtilAccessException;
import com.atlassian.sal.usercompatibility.UserKey;
import com.atlassian.sal.usercompatibility.UserKeys;
import com.atlassian.sal.usercompatibility.UserManager;
import com.atlassian.sal.usercompatibility.UserProfile;
import com.atlassian.sal.usercompatibility.impl.CompatibilityUserProfile;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public class CompatibilityUserManager
implements UserManager {
    private final com.atlassian.sal.api.user.UserManager salUserManager;

    public CompatibilityUserManager(com.atlassian.sal.api.user.UserManager salUserManager) {
        this.salUserManager = (com.atlassian.sal.api.user.UserManager)Preconditions.checkNotNull((Object)salUserManager, (Object)"salUserManager");
    }

    @Override
    public UserProfile getRemoteUser() {
        if (UserKeys.isUserKeyImplemented()) {
            try {
                Method method = this.getUserManagerMethod("getRemoteUser", new Class[0]);
                Object userProfileObject = method.invoke((Object)this.salUserManager, new Object[0]);
                if (userProfileObject != null) {
                    return new CompatibilityUserProfile((com.atlassian.sal.api.user.UserProfile)userProfileObject);
                }
            }
            catch (Exception e) {
                throw new CompatibilityUserUtilAccessException(e);
            }
        }
        return this.getUserProfileByUsername(this.salUserManager.getRemoteUsername());
    }

    @Override
    public UserKey getRemoteUserKey() {
        if (UserKeys.isUserKeyImplemented()) {
            try {
                Method method = this.getUserManagerMethod("getRemoteUserKey", new Class[0]);
                Object userKeyObject = method.invoke((Object)this.salUserManager, new Object[0]);
                if (userKeyObject != null) {
                    return new UserKey(userKeyObject.toString());
                }
            }
            catch (Exception e) {
                throw new CompatibilityUserUtilAccessException(e);
            }
        }
        return UserKeys.getUserKeyFrom(this.salUserManager.getRemoteUsername());
    }

    @Override
    public UserProfile getRemoteUser(HttpServletRequest request) {
        if (UserKeys.isUserKeyImplemented()) {
            try {
                Method method = this.getUserManagerMethod("getRemoteUser", HttpServletRequest.class);
                Object userProfileObject = method.invoke((Object)this.salUserManager, request);
                if (userProfileObject != null) {
                    return new CompatibilityUserProfile((com.atlassian.sal.api.user.UserProfile)userProfileObject);
                }
            }
            catch (Exception e) {
                throw new CompatibilityUserUtilAccessException(e);
            }
        }
        return this.getUserProfileByUsername(this.salUserManager.getRemoteUsername(request));
    }

    @Override
    public UserKey getRemoteUserKey(HttpServletRequest request) {
        if (UserKeys.isUserKeyImplemented()) {
            try {
                Method method = this.getUserManagerMethod("getRemoteUserKey", HttpServletRequest.class);
                Object userKeyObject = method.invoke((Object)this.salUserManager, request);
                if (userKeyObject != null) {
                    return new UserKey(userKeyObject.toString());
                }
            }
            catch (Exception e) {
                throw new CompatibilityUserUtilAccessException(e);
            }
        }
        return UserKeys.getUserKeyFrom(this.salUserManager.getRemoteUsername(request));
    }

    @Override
    public UserProfile getUserProfile(UserKey userKey) {
        try {
            Object userProfileObject = this.invokeMethodForUserKeyOrString("getUserProfile", this.username(userKey));
            if (userProfileObject != null) {
                return new CompatibilityUserProfile((com.atlassian.sal.api.user.UserProfile)userProfileObject);
            }
        }
        catch (Exception e) {
            throw new CompatibilityUserUtilAccessException(e);
        }
        return this.getUserProfileByUsername(this.username(userKey));
    }

    @Override
    public UserProfile getUserProfileByUsername(String username) {
        com.atlassian.sal.api.user.UserProfile salUserProfile = this.salUserManager.getUserProfile(username);
        if (salUserProfile != null) {
            return new CompatibilityUserProfile(salUserProfile);
        }
        return null;
    }

    @Override
    public boolean isUserInGroup(UserKey userKey, String group) {
        Object userKeyOrString = UserKeys.getSalUserKeyOrStringFrom(this.username(userKey));
        try {
            Method method = this.getUserManagerMethod("isUserInGroup", this.classFromUserKey(userKeyOrString), String.class);
            return Boolean.valueOf(method.invoke((Object)this.salUserManager, userKeyOrString, group).toString());
        }
        catch (Exception e) {
            throw new CompatibilityUserUtilAccessException(e);
        }
    }

    @Override
    public boolean isSystemAdmin(UserKey userKey) {
        try {
            return Boolean.valueOf(this.invokeMethodForUserKeyOrString("isSystemAdmin", this.username(userKey)).toString());
        }
        catch (Exception e) {
            throw new CompatibilityUserUtilAccessException(e);
        }
    }

    @Override
    public boolean isAdmin(UserKey userKey) {
        try {
            return Boolean.valueOf(this.invokeMethodForUserKeyOrString("isAdmin", this.username(userKey)).toString());
        }
        catch (Exception e) {
            throw new CompatibilityUserUtilAccessException(e);
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.salUserManager.authenticate(username, password);
    }

    @Override
    public Principal resolve(String username) throws UserResolutionException {
        return this.salUserManager.resolve(username);
    }

    private String username(UserKey userKey) {
        if (userKey != null) {
            return userKey.getStringValue();
        }
        return null;
    }

    private Method getUserManagerMethod(String methodName, Class<?> ... paramClasses) throws NoSuchMethodException {
        Class<com.atlassian.sal.api.user.UserManager> userManagerClass = com.atlassian.sal.api.user.UserManager.class;
        return userManagerClass.getMethod(methodName, paramClasses);
    }

    private Object invokeMethodForUserKeyOrString(String methodName, String userKey) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object userKeyOrString = UserKeys.getSalUserKeyOrStringFrom(userKey);
        Method method = this.getUserManagerMethod(methodName, this.classFromUserKey(userKeyOrString));
        return method.invoke((Object)this.salUserManager, userKeyOrString);
    }

    private Class<?> classFromUserKey(Object userKey) {
        if (userKey == null) {
            return UserKeys.isUserKeyImplemented() ? UserKeys.getSalUserKeyClass() : String.class;
        }
        return userKey.getClass();
    }
}

