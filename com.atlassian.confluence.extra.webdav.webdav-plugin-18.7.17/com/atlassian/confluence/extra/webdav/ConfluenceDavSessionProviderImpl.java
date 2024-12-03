/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.SeraphUtils
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionStore;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.user.User;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={DavSessionProvider.class})
public class ConfluenceDavSessionProviderImpl
implements DavSessionProvider {
    private static Logger log = LoggerFactory.getLogger(ConfluenceDavSessionProviderImpl.class);
    private final UserAccessor userAccessor;
    private final ConfluenceDavSessionStore confluenceDavSessionStore;

    @Autowired
    public ConfluenceDavSessionProviderImpl(@ComponentImport UserAccessor userAccessor, ConfluenceDavSessionStore confluenceDavSessionStore) {
        this.userAccessor = userAccessor;
        this.confluenceDavSessionStore = confluenceDavSessionStore;
    }

    protected String[] getCredentialTokens(HttpServletRequest httpServletRequest) throws IOException, DavException {
        String[] authorizationHeaderTokens = StringUtils.split((String)StringUtils.trim((String)httpServletRequest.getHeader("Authorization")), (char)' ');
        if (null == authorizationHeaderTokens) {
            throw new DavException(401, "Need authentication");
        }
        if (authorizationHeaderTokens.length < 2) {
            throw new IOException("Malformed Authorization header: " + httpServletRequest.getHeader("Authorization"));
        }
        String authorizationHeader = authorizationHeaderTokens[1];
        if (StringUtils.isBlank((String)authorizationHeader)) {
            throw new IOException("Unable to read Authorization header.");
        }
        String userNameAndPassword = new String(Base64.decodeBase64((byte[])authorizationHeader.getBytes("UTF-8")), "UTF-8");
        int indexOfColon = userNameAndPassword.indexOf(58);
        if (indexOfColon > 0) {
            String userName = userNameAndPassword.substring(0, indexOfColon);
            String password = indexOfColon < userNameAndPassword.length() - 1 ? userNameAndPassword.substring(indexOfColon + 1) : "";
            return new String[]{userName, password};
        }
        return new String[0];
    }

    protected String getUserName(HttpServletRequest httpServletRequest) throws IOException, DavException {
        String[] credentialTokens = this.getCredentialTokens(httpServletRequest);
        return credentialTokens.length == 2 ? credentialTokens[0] : null;
    }

    protected String getPassword(HttpServletRequest httpServletRequest) throws IOException, DavException {
        String[] credentialTokens = this.getCredentialTokens(httpServletRequest);
        return credentialTokens.length == 2 ? credentialTokens[1] : null;
    }

    protected ConfluenceDavSession getConfluenceDavSession(HttpServletRequest httpServletRequest) throws DavException {
        try {
            ConfluenceDavSession davSession = (ConfluenceDavSession)httpServletRequest.getSession().getAttribute(ConfluenceDavSession.class.getName());
            if (null == davSession) {
                log.debug("ConfluenceDavSession not found in HttpSession. Trying AuthenticatedUserThreadLocal.");
                User user = AuthenticatedUserThreadLocal.getUser();
                if (user != null) {
                    log.debug("Found user " + user.getName() + " in AuthenticatedUserThreadLocal. Returning a new ConfluenceDavSession based on it.");
                    return new ConfluenceDavSession(user.getName());
                }
                try {
                    String userName = this.getUserName(httpServletRequest);
                    String password = this.getPassword(httpServletRequest);
                    log.debug("Trying to find an existing session for " + userName + " with md5hex password " + DigestUtils.md5Hex((String)StringUtils.defaultString((String)password)));
                    if (StringUtils.isNotEmpty((String)userName) && StringUtils.isNotEmpty((String)password) && this.authenticateWithSeraphAuthenticator(userName, password)) {
                        davSession = this.getConfluenceDavSessionFromSessionMap(userName);
                    }
                }
                catch (AuthenticatorException ae) {
                    log.error("Unable to authenticate using the configured Seraph authenticator.", (Throwable)ae);
                    throw new DavException(500, (Throwable)ae);
                }
                catch (IOException ioe) {
                    log.error("Unable to get user name and/or password from the Authenticate header.", (Throwable)ioe);
                    throw new DavException(500, (Throwable)ioe);
                }
            }
            return davSession;
        }
        catch (ClassCastException cce) {
            httpServletRequest.getSession().removeAttribute(ConfluenceDavSession.class.getName());
            return null;
        }
    }

    protected void setConfluenceDavSessionIntoHttpSession(HttpServletRequest httpServletRequest, ConfluenceDavSession confluenceDavSession) {
        httpServletRequest.getSession().setAttribute(ConfluenceDavSession.class.getName(), (Object)confluenceDavSession);
    }

    private boolean authenticateWithSeraphAuthenticator(String userName, String password) throws AuthenticatorException {
        SecurityConfig securityConfig = SeraphUtils.getConfig((HttpServletRequest)ServletActionContext.getRequest());
        if (null != securityConfig) {
            Authenticator authenticator = securityConfig.getAuthenticator();
            boolean authenticated = authenticator.login(ServletActionContext.getRequest(), ServletActionContext.getResponse(), userName, password);
            log.debug("Authenticating as " + userName + " with md5hex password " + DigestUtils.md5Hex((String)StringUtils.defaultString((String)password)) + " by " + authenticator.getClass().getName() + " results in " + authenticated);
            return authenticated;
        }
        log.error("Unable to get an Authenticator from Seraph.");
        return false;
    }

    private ConfluenceDavSession authenticate(HttpServletRequest httpServletRequest) throws DavException {
        try {
            String userName = this.getUserName(httpServletRequest);
            String password = this.getPassword(httpServletRequest);
            log.debug("User name: " + userName + ", password: " + DigestUtils.md5Hex((String)StringUtils.defaultString((String)password)));
            if (null == userName) {
                throw new DavException(401, "User name not specified.");
            }
            if (null == password) {
                throw new DavException(401, "Password not specified.");
            }
            if (this.authenticateWithSeraphAuthenticator(userName, password)) {
                return new ConfluenceDavSession(userName);
            }
            throw new DavException(401, "Bad user name or password.");
        }
        catch (AuthenticatorException ae) {
            throw new DavException(500, (Throwable)ae);
        }
        catch (IOException ioe) {
            throw new DavException(500, (Throwable)ioe);
        }
    }

    @Override
    public boolean attachSession(WebdavRequest request) throws DavException {
        ConfluenceDavSession confluenceDavSession = this.getConfluenceDavSession(request);
        if (null == confluenceDavSession) {
            log.debug("Looks like this request is not authenticated. We'll try to authenticate our user now.");
            confluenceDavSession = this.authenticate(request);
        }
        confluenceDavSession.setUserAgent(request.getHeader("User-Agent"));
        confluenceDavSession.updateActivityTimestamp();
        confluenceDavSession.setCurrentlyBeingUsed(true);
        this.setConfluenceDavSessionIntoSessionMap(confluenceDavSession);
        this.setConfluenceDavSessionIntoHttpSession(request, confluenceDavSession);
        request.setDavSession(confluenceDavSession);
        AuthenticatedUserThreadLocal.setUser((User)this.userAccessor.getUser(confluenceDavSession.getUserName()));
        return true;
    }

    @Override
    public void releaseSession(WebdavRequest request) {
        ConfluenceDavSession confluenceDavSession = (ConfluenceDavSession)request.getDavSession();
        if (null != confluenceDavSession) {
            confluenceDavSession.setCurrentlyBeingUsed(false);
            this.setConfluenceDavSessionIntoSessionMap(confluenceDavSession);
        }
        AuthenticatedUserThreadLocal.setUser(null);
        request.setDavSession(null);
    }

    private void setConfluenceDavSessionIntoSessionMap(ConfluenceDavSession confluenceDavSession) {
        this.confluenceDavSessionStore.mapSession(confluenceDavSession, confluenceDavSession.getUserName());
    }

    protected ConfluenceDavSession getConfluenceDavSessionFromSessionMap(String userName) {
        return this.confluenceDavSessionStore.getSession(userName);
    }
}

