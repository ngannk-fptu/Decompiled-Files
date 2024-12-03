/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import org.apache.commons.lang3.StringUtils;

public class UserUtils {
    @Deprecated
    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank((CharSequence)email) && email.contains("@");
    }

    static String[] getFirstNameLastName(String fullname) {
        String lastName;
        String firstName;
        String[] strings = StringUtils.split((String)fullname, (String)" ", (int)2);
        if (strings == null || strings.length == 0) {
            firstName = "";
            lastName = "";
        } else if (strings.length > 1) {
            firstName = strings[0].trim();
            lastName = strings[1].trim();
        } else {
            firstName = "";
            lastName = strings[0].trim();
        }
        return new String[]{firstName, lastName};
    }

    static String getDisplayName(String displayName, String firstName, String lastName, String username) {
        UserUtils.notBlank(username);
        if (StringUtils.isNotBlank((CharSequence)displayName)) {
            return displayName;
        }
        if (StringUtils.isNotBlank((CharSequence)firstName) && StringUtils.isNotBlank((CharSequence)lastName)) {
            return firstName + " " + lastName;
        }
        if (StringUtils.isNotBlank((CharSequence)firstName)) {
            return firstName;
        }
        if (StringUtils.isNotBlank((CharSequence)lastName)) {
            return lastName;
        }
        return username;
    }

    static String getFirstName(String firstName, String displayName) {
        if (StringUtils.isNotBlank((CharSequence)firstName)) {
            return firstName;
        }
        String[] firstLast = UserUtils.getFirstNameLastName(displayName);
        return firstLast[0];
    }

    static String getLastName(String lastName, String displayName) {
        UserUtils.notBlank(displayName);
        if (StringUtils.isNotBlank((CharSequence)lastName)) {
            return lastName;
        }
        String[] firstLast = UserUtils.getFirstNameLastName(displayName);
        if (StringUtils.isNotBlank((CharSequence)firstLast[1])) {
            return firstLast[1];
        }
        return displayName;
    }

    public static User populateNames(User user) {
        UserTemplate populatedUser = new UserTemplate(user);
        String calculatedDisplayName = UserUtils.getDisplayName(user.getDisplayName(), user.getFirstName(), user.getLastName(), user.getName());
        populatedUser.setDisplayName(calculatedDisplayName);
        if (StringUtils.isNotBlank((CharSequence)user.getDisplayName()) || StringUtils.isBlank((CharSequence)user.getLastName())) {
            populatedUser.setFirstName(UserUtils.getFirstName(user.getFirstName(), calculatedDisplayName));
            populatedUser.setLastName(UserUtils.getLastName(user.getLastName(), calculatedDisplayName));
        } else {
            populatedUser.setFirstName(StringUtils.defaultString((String)populatedUser.getFirstName(), (String)""));
        }
        return populatedUser;
    }

    private static void notBlank(String string) throws IllegalArgumentException {
        if (StringUtils.isBlank((CharSequence)string)) {
            throw new IllegalArgumentException("[Assertion Failed] - argument passed to method must be non-empty");
        }
    }
}

