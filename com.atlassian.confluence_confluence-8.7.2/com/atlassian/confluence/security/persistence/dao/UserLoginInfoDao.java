/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.persistence.dao;

import com.atlassian.confluence.security.persistence.dao.hibernate.UserLoginInfo;
import com.atlassian.user.User;

public interface UserLoginInfoDao {
    public UserLoginInfo findOrCreateUserLoginInfoForUser(User var1);

    public void saveOrUpdate(UserLoginInfo var1);

    public void deleteUserInfoFor(User var1);
}

