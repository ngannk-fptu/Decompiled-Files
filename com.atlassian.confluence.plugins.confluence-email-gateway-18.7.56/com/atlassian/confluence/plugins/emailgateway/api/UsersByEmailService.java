/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import java.util.List;
import javax.mail.internet.InternetAddress;

@PublicApi
public interface UsersByEmailService {
    public List<User> getUsersByEmail(String var1) throws EntityException;

    public User getUniqueUserByEmail(String var1) throws EntityException;

    public User getUniqueUserByEmail(InternetAddress var1) throws EntityException;
}

