/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.util.BatchResult
 */
package com.atlassian.crowd.embedded.spi;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.util.BatchResult;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserDao {
    public TimestampedUser findByName(long var1, String var3) throws UserNotFoundException;

    public TimestampedUser findByExternalId(long var1, String var3) throws UserNotFoundException;

    public UserWithAttributes findByNameWithAttributes(long var1, String var3) throws UserNotFoundException;

    public PasswordCredential getCredential(long var1, String var3) throws UserNotFoundException;

    public List<PasswordCredential> getCredentialHistory(long var1, String var3) throws UserNotFoundException;

    public User add(User var1, PasswordCredential var2) throws UserAlreadyExistsException, IllegalArgumentException, DirectoryNotFoundException;

    public void storeAttributes(User var1, Map<String, Set<String>> var2, boolean var3) throws UserNotFoundException;

    public User update(User var1) throws UserNotFoundException, IllegalArgumentException;

    public void updateCredential(User var1, PasswordCredential var2, int var3) throws UserNotFoundException, IllegalArgumentException;

    public User rename(User var1, String var2) throws UserNotFoundException, UserAlreadyExistsException, IllegalArgumentException;

    public void removeAttribute(User var1, String var2) throws UserNotFoundException;

    public void remove(User var1) throws UserNotFoundException;

    public <T> List<T> search(long var1, EntityQuery<T> var3);

    public BatchResult<User> addAll(Set<UserTemplateWithCredentialAndAttributes> var1);

    public BatchResult<String> removeAllUsers(long var1, Set<String> var3);

    public void setAttributeForAllInDirectory(long var1, String var3, String var4);

    public Set<String> getAllExternalIds(long var1) throws DirectoryNotFoundException;

    public long getUserCount(long var1) throws DirectoryNotFoundException;

    public Set<Long> findDirectoryIdsContainingUserName(String var1);

    public Map<String, String> findByExternalIds(long var1, Set<String> var3);
}

