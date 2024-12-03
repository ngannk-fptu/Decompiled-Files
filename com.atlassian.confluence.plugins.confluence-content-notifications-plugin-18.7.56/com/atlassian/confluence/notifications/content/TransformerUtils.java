/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.Contented
 *  com.atlassian.confluence.event.events.types.UserDriven
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Function;

public class TransformerUtils {
    private static final Function<UserKey, String> USERKEY_TO_STRING = input -> input.getStringValue();

    public static Function<RoleRecipient, UserKey> toUserKey() {
        return input -> input.getUserKey();
    }

    public static Option<String> getOriginatingUserForUserDriven(UserDriven userDrivenEvent) {
        User originatingUser = userDrivenEvent.getOriginatingUser();
        UserKey uerKey = null;
        if (originatingUser instanceof ConfluenceUser) {
            uerKey = ((ConfluenceUser)originatingUser).getKey();
        }
        return Option.option(uerKey).map(USERKEY_TO_STRING);
    }

    public static Option<String> getOriginatingUserForContented(Contented contented) {
        ConfluenceUser lastModifier = contented.getContent().getLastModifier();
        if (lastModifier != null) {
            return Option.option((Object)lastModifier.getKey()).map(USERKEY_TO_STRING);
        }
        ConfluenceUser loggedInUser = AuthenticatedUserThreadLocal.get();
        if (loggedInUser != null) {
            return Option.option((Object)loggedInUser.getKey()).map(USERKEY_TO_STRING);
        }
        return Option.none();
    }
}

