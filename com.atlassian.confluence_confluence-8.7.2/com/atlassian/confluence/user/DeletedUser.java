/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.HasBackingUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.digest.DigestUtils;

public class DeletedUser
extends UnknownUser {
    private final String fullName;
    private final UserKey userKey;

    public DeletedUser(UserKey userKey, I18NBean i18NBean) {
        this.fullName = this.generateFullName(userKey, i18NBean);
        this.userKey = userKey;
    }

    private String generateFullName(UserKey userKey, I18NBean i18NBean) {
        String userKeyHash = DigestUtils.sha256Hex((byte[])userKey.getStringValue().getBytes(StandardCharsets.UTF_8));
        return i18NBean.getText("deleted.user.prefix", new Object[]{userKeyHash.substring(0, 5)});
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getName() {
        return this.userKey.getStringValue();
    }

    public static boolean isDeletedUser(User user) {
        return user instanceof DeletedUser || user instanceof HasBackingUser && ((HasBackingUser)user).getBackingUser() instanceof DeletedUser;
    }
}

