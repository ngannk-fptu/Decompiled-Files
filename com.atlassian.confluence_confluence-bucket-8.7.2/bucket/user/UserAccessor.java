/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.query.EntityQueryParser
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.security.password.Credential
 *  com.opensymphony.module.propertyset.PropertySet
 */
package bucket.user;

import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.security.password.Credential;
import com.opensymphony.module.propertyset.PropertySet;

@Deprecated
public interface UserAccessor
extends EntityQueryParser {
    @Deprecated
    public User addUser(String var1, String var2, String var3, String var4, String[] var5);

    @Deprecated
    public User addUser(String var1, String var2, String var3, String var4);

    public User getUser(String var1);

    public Pager<String> getUserNames();

    public Pager<User> getUsers();

    public Group getGroup(String var1);

    public Pager<Group> getGroups();

    public Group addGroup(String var1);

    public void removeGroup(Group var1);

    public void removeUser(User var1) throws InfrastructureException;

    public Group getGroupCreateIfNecessary(String var1);

    @Deprecated
    public UserPreferences getUserPreferences(User var1);

    public void saveUser(User var1);

    public SearchResult getUsersByEmail(String var1);

    public void deactivateUser(User var1);

    public void reactivateUser(User var1);

    public boolean isLicensedToAddMoreUsers();

    public boolean isUserRemovable(User var1) throws EntityException;

    public Pager<Group> getGroups(User var1);

    public boolean hasMembership(Group var1, User var2);

    public boolean hasMembership(String var1, String var2);

    public void addMembership(Group var1, User var2);

    public void addMembership(String var1, String var2);

    public boolean removeMembership(Group var1, User var2);

    @Deprecated
    public PropertySet getPropertySet(User var1);

    public Pager<String> getMemberNames(Group var1);

    public User createUser(User var1, Credential var2);

    public Group createGroup(String var1);

    public boolean isDeactivated(User var1);

    public boolean isDeactivated(String var1);

    public boolean authenticate(String var1, String var2);

    public SearchResult<User> findUsers(Query<User> var1) throws EntityException;

    public void alterPassword(User var1, String var2) throws EntityException;

    public boolean removeMembership(String var1, String var2);
}

