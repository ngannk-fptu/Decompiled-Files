/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.configuration.RepositoryAccessor
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.DefaultPager
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.query.EntityQueryParser
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.QueryContext
 *  com.atlassian.user.security.authentication.Authenticator
 *  com.opensymphony.util.TextUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package bucket.user;

import bucket.user.UserAccessor;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.security.authentication.Authenticator;
import com.opensymphony.util.TextUtils;
import java.util.ConcurrentModificationException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Transactional
public abstract class DefaultUserAccessor
implements UserAccessor {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserAccessor.class);
    private final RepositoryAccessor repositoryAccessor;

    protected DefaultUserAccessor(RepositoryAccessor repositoryAccessor) {
        this.repositoryAccessor = repositoryAccessor;
    }

    @Override
    public Pager<String> getUserNames() {
        Pager pager = null;
        try {
            pager = this.getUserManager().getUserNames();
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return pager;
    }

    @Override
    public Pager<User> getUsers() {
        Pager pager = null;
        try {
            pager = this.getUserManager().getUsers();
        }
        catch (EntityException e) {
            log.error("Error in getUsers():" + e.getMessage(), (Throwable)e);
        }
        return pager;
    }

    @Override
    @Deprecated
    public @Nullable User getUser(String name) {
        if (!TextUtils.stringSet((String)name)) {
            return null;
        }
        User user = null;
        try {
            user = this.getUserManager().getUser(name);
        }
        catch (EntityException e) {
            try {
                user = this.getUserManager().getUser(name.toLowerCase());
            }
            catch (EntityException e1) {
                log.error("Error in getUser():" + e1.getMessage(), (Throwable)e1);
            }
        }
        return user;
    }

    @Override
    public Pager<Group> getGroups() {
        Pager pager = null;
        try {
            pager = this.getGroupManager().getGroups();
        }
        catch (EntityException e) {
            log.error("Error in getGroups():" + e.getMessage(), (Throwable)e);
        }
        return pager;
    }

    public SearchResult<Group> findGroups(Query<Group> query) throws EntityException {
        return this.getEntityQueryParser().findGroups(query);
    }

    public SearchResult<User> findUsers(Query<User> query, QueryContext context) throws EntityException {
        return this.getEntityQueryParser().findUsers(query);
    }

    public SearchResult<Group> findGroups(Query<Group> query, QueryContext context) throws EntityException {
        return this.getEntityQueryParser().findGroups(query);
    }

    @Override
    public Pager<Group> getGroups(User user) {
        if (user == null) {
            return DefaultPager.emptyPager();
        }
        try {
            return this.getGroupManager().getGroups(user);
        }
        catch (EntityException e) {
            log.error("Failed to retrieve groups for user [" + user.getName() + "]", (Throwable)e);
            return DefaultPager.emptyPager();
        }
        catch (ConcurrentModificationException e) {
            log.error("Failed to retrieve groups for user [" + user.getName() + "]", (Throwable)e);
            return DefaultPager.emptyPager();
        }
    }

    @Override
    public boolean hasMembership(Group group, User user) {
        if (group == null || user == null) {
            return false;
        }
        boolean result = false;
        try {
            result = this.getGroupManager().hasMembership(group, user);
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return result;
    }

    @Override
    public boolean hasMembership(String groupName, String username) {
        try {
            Group group = this.getGroupManager().getGroup(groupName);
            if (group == null || !TextUtils.stringSet((String)username)) {
                return false;
            }
            User user = this.getUserManager().getUser(username);
            if (user == null) {
                return false;
            }
            return this.hasMembership(group, user);
        }
        catch (EntityException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        boolean result = false;
        try {
            result = this.getAuthenticator().authenticate(username, password);
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return result;
    }

    protected UserManager getUserManager() {
        return this.repositoryAccessor.getUserManager();
    }

    protected GroupManager getGroupManager() {
        return this.repositoryAccessor.getGroupManager();
    }

    protected Authenticator getAuthenticator() {
        return this.repositoryAccessor.getAuthenticator();
    }

    protected EntityQueryParser getEntityQueryParser() {
        return this.repositoryAccessor.getEntityQueryParser();
    }
}

