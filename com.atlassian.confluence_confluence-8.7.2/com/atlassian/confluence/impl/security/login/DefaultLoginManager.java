/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.core.util.Clock
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.errorprone.annotations.Immutable
 *  com.google.errorprone.annotations.concurrent.GuardedBy
 *  javax.servlet.http.HttpServletRequest
 *  net.jcip.annotations.ThreadSafe
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.security.login;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.security.login.LoginResult;
import com.atlassian.confluence.security.persistence.dao.UserLoginInfoDao;
import com.atlassian.confluence.security.persistence.dao.hibernate.UserLoginInfo;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.beans.LoginManagerSettings;
import com.atlassian.confluence.user.AuthenticatorOverwrite;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.atlassian.core.util.Clock;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ThreadSafe
@Transactional
public class DefaultLoginManager
implements LoginManager {
    private static final int DEFAULT_FAILED_LOGIN_ATTEMPTS_THRESHOLD = 3;
    private static final Logger log = LoggerFactory.getLogger(DefaultLoginManager.class);
    private final UserAccessor userAccessor;
    private final GlobalSettingsManager settingsManager;
    private final Clock clock;
    private final UserLoginInfoDao loginInfoDao;
    @GuardedBy(value="lock")
    private final Cache<String, Integer> loginAttemptsCache;
    private final Object lock = new Object();

    public DefaultLoginManager(GlobalSettingsManager settingsManager, UserAccessor userAccessor, CacheFactory cacheFactory, Clock clock, UserLoginInfoDao loginInfoDao) {
        this.settingsManager = (GlobalSettingsManager)Preconditions.checkNotNull((Object)settingsManager, (Object)"SettingsManager cannot be null");
        this.userAccessor = (UserAccessor)Preconditions.checkNotNull((Object)userAccessor, (Object)"UserAccessor cannot be null");
        this.loginInfoDao = (UserLoginInfoDao)Preconditions.checkNotNull((Object)loginInfoDao, (Object)"UserLoginInfoDao cannot be null");
        this.clock = clock;
        this.loginAttemptsCache = CoreCache.LOGIN_MANAGER_FAILURE_CACHE.getCache(cacheFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void onFailedLoginAttempt(String userName, HttpServletRequest servletRequest) {
        this.recordLoginFailure(userName, servletRequest);
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (null != user) {
            UserLoginInfo userLoginInfo = this.loginInfoDao.findOrCreateUserLoginInfoForUser(user);
            userLoginInfo.failedLogin(this.clock.getCurrentDate());
            this.loginInfoDao.saveOrUpdate(userLoginInfo);
        } else {
            String cacheKey = DefaultLoginManager.cacheKey(userName);
            Object object = this.lock;
            synchronized (object) {
                this.loginAttemptsCache.put((Object)cacheKey, (Object)(1 + (Integer)this.loginAttemptsCache.get((Object)cacheKey, () -> 0)));
            }
        }
    }

    @Override
    public void onSuccessfulLoginAttempt(String userName, HttpServletRequest servletRequest) {
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (null != user) {
            UserLoginInfo userLoginInfo = this.loginInfoDao.findOrCreateUserLoginInfoForUser(user);
            userLoginInfo.successfulLogin(this.clock.getCurrentDate());
            this.loginInfoDao.saveOrUpdate(userLoginInfo);
        } else {
            log.error("Can not retrieve the user to set the successful login information (last login date).");
        }
    }

    @Override
    @Transactional(readOnly=true)
    public boolean isElevatedSecurityCheckEnabled() {
        LoginManagerSettings lms = this.getLoginManagerSettings();
        return null != lms && lms.isEnableElevatedSecurityCheck() && !AuthenticatorOverwrite.isPasswordConfirmationDisabled();
    }

    @Override
    public LoginResult authenticate(String userName, String password) {
        if (StringUtils.isBlank((CharSequence)userName) || StringUtils.isBlank((CharSequence)password)) {
            return LoginResult.AUTHENTICATION_FAILED;
        }
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (null == user) {
            this.onFailedLoginAttempt(userName, null);
            return LoginResult.AUTHENTICATION_FAILED;
        }
        boolean isElevatedSecurityCheckRequired = this.requiresElevatedSecurityCheck(userName);
        if (this.userAccessor.authenticate(userName, password)) {
            if (isElevatedSecurityCheckRequired) {
                this.onFailedLoginAttempt(userName, null);
                return LoginResult.OK_WITH_ELEVATED_SECURITY_CHECK_REQUIRED;
            }
            this.onSuccessfulLoginAttempt(userName, null);
            return LoginResult.OK;
        }
        this.onFailedLoginAttempt(userName, null);
        return LoginResult.AUTHENTICATION_FAILED;
    }

    @Override
    public boolean requiresElevatedSecurityCheck(String userName) {
        return this.isElevatedSecurityCheckEnabled() && this.getCurrentFailedLoginCount(userName) >= this.getLoginAttemptsThreshold();
    }

    @Override
    public void resetFailedLoginCount(User user) {
        UserLoginInfo userLoginInfo = this.loginInfoDao.findOrCreateUserLoginInfoForUser(user);
        userLoginInfo.resetFailedLoginCount();
        this.loginInfoDao.saveOrUpdate(userLoginInfo);
    }

    @Override
    @Transactional(readOnly=true)
    public @Nullable LoginInfo getLoginInfo(String userName) {
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (null == user) {
            return null;
        }
        UserLoginInfo userLoginInfo = this.loginInfoDao.findOrCreateUserLoginInfoForUser(user);
        return new DefaultLoginInfo(userLoginInfo, this.requiresElevatedSecurityCheck(userName));
    }

    @Override
    @Transactional(readOnly=true)
    public @Nullable LoginInfo getLoginInfo(User user) {
        if (null == user) {
            return null;
        }
        UserLoginInfo userLoginInfo = this.loginInfoDao.findOrCreateUserLoginInfoForUser(user);
        return new DefaultLoginInfo(userLoginInfo, this.requiresElevatedSecurityCheck(user.getName()));
    }

    private int getLoginAttemptsThreshold() {
        LoginManagerSettings loginManagerSettings = this.getLoginManagerSettings();
        if (null != loginManagerSettings) {
            return loginManagerSettings.getLoginAttemptsThreshold();
        }
        return 3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getCurrentFailedLoginCount(String userName) {
        if (null == userName) {
            return 0;
        }
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (null != user) {
            UserLoginInfo userLoginInfo = this.loginInfoDao.findOrCreateUserLoginInfoForUser(user);
            return userLoginInfo.getCurrentFailedLoginCount();
        }
        Object object = this.lock;
        synchronized (object) {
            return Optional.ofNullable((Integer)this.loginAttemptsCache.get((Object)DefaultLoginManager.cacheKey(userName))).orElse(0);
        }
    }

    private @Nullable LoginManagerSettings getLoginManagerSettings() {
        if (null == this.settingsManager) {
            return null;
        }
        Settings s = this.settingsManager.getGlobalSettings();
        if (null != s) {
            return s.getLoginManagerSettings();
        }
        return null;
    }

    private void recordLoginFailure(String userName, HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            servletRequest = new StaticHttpContext().getRequest();
        }
        if (log.isDebugEnabled()) {
            log.debug(DefaultLoginManager.getFailureLogContent(userName, servletRequest));
        } else if (log.isInfoEnabled() && this.requiresElevatedSecurityCheck(userName)) {
            log.info(DefaultLoginManager.getFailureLogContent(userName, servletRequest));
        }
    }

    private static String getFailureLogContent(String userName, HttpServletRequest servletRequest) {
        StringBuilder logContent = new StringBuilder("\nFailed login attempt for user '").append(StringUtils.deleteWhitespace((String)userName)).append("':\n");
        if (servletRequest != null) {
            logContent.append("  Request URL: ").append(StringUtils.deleteWhitespace((String)servletRequest.getRequestURL().toString())).append("\n");
            logContent.append("  User-Agent: ").append(StringUtils.deleteWhitespace((String)servletRequest.getHeader("User-Agent"))).append("\n");
            logContent.append("  Remote Address: ").append(StringUtils.deleteWhitespace((String)servletRequest.getRemoteAddr())).append("\n");
            if (servletRequest.getHeader("X-Forwarded-For") != null) {
                logContent.append("  X-Forwarded-For: ").append(StringUtils.deleteWhitespace((String)servletRequest.getHeader("X-Forwarded-For"))).append("\n");
            }
        }
        return logContent.toString();
    }

    private static String cacheKey(String userName) {
        if (null == userName) {
            return "";
        }
        return StringUtils.left((String)userName, (int)200);
    }

    @Immutable
    private static final class DefaultLoginInfo
    implements LoginInfo {
        private final boolean requiresElevatedSecurityCheck;
        private final UserLoginInfo userLoginInfo;

        DefaultLoginInfo(UserLoginInfo userLoginInfo, boolean requiresElevatedSecurityCheck) {
            this.userLoginInfo = userLoginInfo;
            this.requiresElevatedSecurityCheck = requiresElevatedSecurityCheck;
        }

        @Override
        public boolean requiresElevatedSecurityCheck() {
            return this.requiresElevatedSecurityCheck;
        }

        @Override
        public int getCurrentFailedLoginCount() {
            return this.userLoginInfo.getCurrentFailedLoginCount();
        }

        @Override
        public int getTotalFailedLoginCount() {
            return this.userLoginInfo.getTotalFailedLoginCount();
        }

        @Override
        public Date getLastSuccessfulLoginDate() {
            return this.userLoginInfo.getLastSuccessfulLoginDate();
        }

        @Override
        public Date getLastFailedLoginDate() {
            return this.userLoginInfo.getLastFailedLoginDate();
        }

        @Override
        public Date getPreviousSuccessfulLoginDate() {
            return this.userLoginInfo.getPreviousSuccessfulLoginDate();
        }
    }
}

