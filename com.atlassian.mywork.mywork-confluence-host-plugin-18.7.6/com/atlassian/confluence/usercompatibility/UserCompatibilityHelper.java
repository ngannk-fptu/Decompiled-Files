/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.usercompatibility;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.usercompatibility.UserKey;
import com.atlassian.sal.usercompatibility.UserKeys;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UserCompatibilityHelper {
    private static final Class<?> confluenceUserClass = UserCompatibilityHelper.getClassOrNull("com.atlassian.confluence.user.ConfluenceUser");
    private static final Method userKeyGetter = UserCompatibilityHelper.getMethodOrNull(confluenceUserClass, "getKey", new Class[0]);
    private static final Class<?> salUserKeyClass = UserCompatibilityHelper.getClassOrNull("com.atlassian.sal.api.user.UserKey");
    private static final Method userByKeyGetter = UserCompatibilityHelper.getMethodOrNull(UserAccessor.class, "getUserByKey", salUserKeyClass);
    private static final Constructor<?> salUserKeyConstructor = UserCompatibilityHelper.getConstructorOrNull(salUserKeyClass, String.class);

    public static UserKey getKeyForUser(User user) {
        if (user == null) {
            return null;
        }
        if (UserCompatibilityHelper.isRenameUserImplemented()) {
            User confluenceUser = UserCompatibilityHelper.getConfluenceUser(user);
            if (confluenceUser == null) {
                return null;
            }
            try {
                Object userKey = userKeyGetter.invoke((Object)confluenceUser, new Object[0]);
                return userKey != null ? new UserKey(userKey.toString()) : null;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return UserKeys.getUserKeyFrom(user.getName());
    }

    public static UserKey getKeyForUsername(String username) {
        User user = UserCompatibilityHelper.getUserAccessor().getUser(username);
        return UserCompatibilityHelper.getKeyForUser(user);
    }

    public static String getStringKeyForUsername(String username) {
        UserKey userKey = UserCompatibilityHelper.getKeyForUsername(username);
        return userKey != null ? userKey.getStringValue() : null;
    }

    private static User getConfluenceUser(User user) {
        User confluenceUser = confluenceUserClass.isAssignableFrom(user.getClass()) ? user : UserCompatibilityHelper.getUserAccessor().getUser(user.getName());
        return confluenceUser;
    }

    public static User getUserForKey(String key) {
        if (key == null) {
            return null;
        }
        if (UserCompatibilityHelper.isRenameUserImplemented()) {
            try {
                Object salUserKey = salUserKeyConstructor.newInstance(key);
                return (User)userByKeyGetter.invoke((Object)UserCompatibilityHelper.getUserAccessor(), salUserKey);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return UserCompatibilityHelper.getUserAccessor().getUser(key);
    }

    private static UserAccessor getUserAccessor() {
        return (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
    }

    public static boolean isRenameUserImplemented() {
        return confluenceUserClass != null;
    }

    private static Class<?> getClassOrNull(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Method getMethodOrNull(Class<?> clazz, String methodName, Class<?> ... parameterTypes) {
        try {
            return clazz != null ? clazz.getMethod(methodName, parameterTypes) : null;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Constructor<?> getConstructorOrNull(Class<?> clazz, Class<?> ... parameterTypes) {
        try {
            return clazz != null ? clazz.getConstructor(parameterTypes) : null;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
}

