/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadGateKeeper
implements GateKeeper {
    private static final Logger log = LoggerFactory.getLogger(DownloadGateKeeper.class);
    private final Map<UserAndPath, Time> permittedDownloads = Collections.synchronizedMap(new HashMap());
    private final Map<UserAndPath, PermissionCheck> permittedDownloadsPredicate = Collections.synchronizedMap(new HashMap());
    private static final long ONE_DAY_MILLIS = TimeUnit.DAYS.toMillis(1L);

    @Override
    @Deprecated
    public void addKey(String path, String username) {
        if (log.isDebugEnabled()) {
            log.debug("gateKeeper.addKey(" + path + ", " + username);
        }
        if (path == null) {
            throw new IllegalArgumentException("The path is null. It must specify a file in " + GeneralUtil.getConfluenceTempDirectoryPath());
        }
        this.addPermission(path, username);
    }

    @Override
    public void addKey(String path, String username, Predicate<User> permissionPredicate) {
        if (log.isDebugEnabled()) {
            log.debug("gateKeeper.addKey(" + path + ", " + username + ", " + permissionPredicate);
        }
        if (path == null) {
            throw new IllegalArgumentException("The path is null. It must specify a file in " + GeneralUtil.getConfluenceTempDirectoryPath());
        }
        this.addPermission(path, username, permissionPredicate);
    }

    @Override
    @Deprecated
    public void addKey(String path, User user) {
        this.addKey(path, this.getUserName(user));
    }

    @Override
    public void addKey(String path, User user, Predicate<User> permissionPredicate) {
        this.addKey(path, this.getUserName(user), (Predicate<User>)this.getPredicate(permissionPredicate));
    }

    @Override
    @Deprecated
    public void allowAnonymousAccess(String path) {
        this.addPermission(path, null);
    }

    @Override
    public void allowAnonymousAccess(String path, Predicate<User> permissionPredicate) {
        this.addPermission(path, null, permissionPredicate);
    }

    @Override
    public boolean isAccessPermitted(String path, User user) {
        return this.isAccessPermitted(path, this.getUserName(user));
    }

    @Override
    public boolean isAccessPermitted(String path, String userName) {
        boolean result;
        this.runCheck();
        boolean bl = result = this.permittedDownloads.containsKey(new UserAndPath(userName, path)) || this.permittedDownloads.containsKey(new UserAndPath(path));
        if (log.isDebugEnabled()) {
            log.debug("Permission check for user '" + userName + "' and path '" + path + "' returned: " + result);
        }
        if (!result) {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            PermissionCheck check = this.permittedDownloadsPredicate.get(new UserAndPath(userName, path));
            if (log.isDebugEnabled()) {
                log.debug("PermissionCheck for user '" + userName + "' and path '" + path + "' returned: " + check);
            }
            if (check == null) {
                check = this.permittedDownloadsPredicate.get(new UserAndPath(path));
            }
            log.debug("Permission check for path '" + path + "' and check '" + check);
            if (check == null) {
                return false;
            }
            result = check.permissionPredicate.test(user);
            if (log.isDebugEnabled()) {
                log.debug("Permission check for user '" + user + "' and returned: " + result);
            }
        }
        return result;
    }

    @Override
    public void cleanAllKeys() {
        this.permittedDownloads.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanAllKeysOlderThan(long millis) {
        long now = System.currentTimeMillis();
        Collection<Time> downloads = this.permittedDownloads.values();
        Map<UserAndPath, Time> map = this.permittedDownloads;
        synchronized (map) {
            Iterator<Time> iterator = downloads.iterator();
            while (iterator.hasNext()) {
                long downloadTimestamp = iterator.next().time;
                if (now - downloadTimestamp <= millis) continue;
                iterator.remove();
            }
        }
    }

    private void addPermission(String path, String userName) {
        try {
            int startIdx = path.indexOf("download");
            path = path.substring(startIdx);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("This download path does not contain the prefix 'download': " + path);
        }
        UserAndPath key = new UserAndPath(userName, path);
        Time value = new Time(System.currentTimeMillis());
        this.runCheck();
        if (log.isDebugEnabled()) {
            log.debug("Putting key '" + key + "' into permittedDownloads.");
        }
        this.permittedDownloads.put(key, value);
    }

    private void addPermission(String path, String userName, Predicate<User> permissionPredicate) {
        try {
            int startIdx = path.indexOf("download");
            path = path.substring(startIdx);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("This download path does not contain the prefix 'download': " + path);
        }
        UserAndPath key = new UserAndPath(userName, path);
        this.runCheck();
        if (log.isDebugEnabled()) {
            log.debug("Putting key '" + key + "' into permittedDownloads.");
        }
        this.permittedDownloadsPredicate.put(key, new PermissionCheck(System.currentTimeMillis(), permissionPredicate));
    }

    private void runCheck() {
        this.cleanAllKeysOlderThan(ONE_DAY_MILLIS);
    }

    private String getUserName(User user) {
        return user != null ? user.getName() : null;
    }

    private Predicate getPredicate(Predicate<User> permissionPredicate) {
        return permissionPredicate;
    }

    private static class UserAndPath {
        private final String username;
        private final String path;

        private UserAndPath(String path) {
            this.username = null;
            this.path = path;
        }

        private UserAndPath(String username, String path) {
            this.username = username;
            this.path = path;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UserAndPath that = (UserAndPath)o;
            if (!this.path.equals(that.path)) {
                return false;
            }
            return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
        }

        public int hashCode() {
            int result = this.username != null ? this.username.hashCode() : 0;
            result = 31 * result + this.path.hashCode();
            return result;
        }
    }

    private static class PermissionCheck {
        private final long time;
        private final Predicate<User> permissionPredicate;

        private PermissionCheck(long time, Predicate<User> permissionPredicate) {
            this.time = time;
            this.permissionPredicate = permissionPredicate;
        }
    }

    private static class Time {
        private final long time;

        private Time(long time) {
            this.time = time;
        }
    }
}

