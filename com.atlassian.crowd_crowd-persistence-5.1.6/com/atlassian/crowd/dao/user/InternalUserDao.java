/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 */
package com.atlassian.crowd.dao.user;

import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.InternalUserWithPasswordLastChanged;
import com.atlassian.crowd.model.user.MinimalUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchResultWithIdReferences;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InternalUserDao
extends UserDao {
    public BatchResultWithIdReferences<User> addAll(Collection<UserTemplateWithCredentialAndAttributes> var1);

    public void removeAll(long var1) throws DirectoryNotFoundException;

    public List<String> getAllUsernames(long var1);

    public InternalUser findByName(long var1, String var3) throws UserNotFoundException;

    public Collection<InternalUser> findByNames(long var1, Collection<String> var3);

    public Collection<MinimalUser> findMinimalUsersByNames(long var1, Collection<String> var3);

    public Collection<InternalUser> findByIds(Collection<Long> var1);

    public Map<String, String> findByExternalIds(long var1, Set<String> var3);

    public Collection<InternalUserWithPasswordLastChanged> findUsersForPasswordExpiryNotification(Instant var1, Duration var2, Duration var3, long var4, int var6);

    public void setAttribute(Collection<InternalUser> var1, String var2, String var3);

    public List<InternalUser> findByNumericAttributeRange(String var1, long var2, long var4);
}

