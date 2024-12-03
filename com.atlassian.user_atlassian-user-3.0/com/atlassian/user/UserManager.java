/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user;

import com.atlassian.user.EntityException;
import com.atlassian.user.EntityManager;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface UserManager
extends EntityManager {
    public Pager<User> getUsers() throws EntityException;

    public Pager<String> getUserNames() throws EntityException;

    public User getUser(String var1) throws EntityException;

    public User createUser(String var1) throws EntityException;

    public User createUser(User var1, Credential var2) throws EntityException, UnsupportedOperationException, IllegalArgumentException;

    public void alterPassword(User var1, String var2) throws EntityException;

    public void saveUser(User var1) throws EntityException, IllegalArgumentException;

    public void removeUser(User var1) throws EntityException, IllegalArgumentException;

    public boolean isReadOnly(User var1) throws EntityException;
}

