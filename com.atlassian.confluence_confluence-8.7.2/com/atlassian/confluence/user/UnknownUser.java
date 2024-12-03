/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.DeletedUser;
import com.atlassian.confluence.user.HasBackingUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.user.User;

public class UnknownUser
implements User {
    private final String userName;
    private final String fullName;

    private static String lookupFullNameForUnknownUser(String userName, I18NBean i18NBean) {
        return i18NBean.getText("unknown.name", new Object[]{userName});
    }

    public static User unknownUser(String userName, I18NBean i18NBean) {
        return new UnknownUser(userName, UnknownUser.lookupFullNameForUnknownUser(userName, i18NBean));
    }

    public static User unknownUser(ConfluenceUser user, I18NBean i18NBean) {
        if (user.getName().equals(user.getKey().getStringValue())) {
            return new DeletedUser(user.getKey(), i18NBean);
        }
        return UnknownUser.unknownUser(user.getName(), i18NBean);
    }

    private UnknownUser(String userName, String fullName) {
        this.userName = userName;
        this.fullName = fullName;
    }

    protected UnknownUser() {
        this.userName = "";
        this.fullName = "";
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return null;
    }

    public String getName() {
        return this.userName;
    }

    public static boolean isUnknownUser(User user) {
        return user instanceof UnknownUser || user instanceof HasBackingUser && ((HasBackingUser)user).getBackingUser() instanceof UnknownUser;
    }
}

