/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.User;
import java.util.Collection;

public interface InternalUserDao<T extends User>
extends UserDao {
    public InternalUser internalFindByName(long var1, String var3) throws UserNotFoundException;

    public InternalUser internalFindByUser(User var1) throws UserNotFoundException;

    public void removeAllUsers(long var1);

    public Collection<InternalUser> findByNames(long var1, Collection<String> var3);

    public T add(User var1, PasswordCredential var2) throws UserAlreadyExistsException, IllegalArgumentException, DirectoryNotFoundException;
}

