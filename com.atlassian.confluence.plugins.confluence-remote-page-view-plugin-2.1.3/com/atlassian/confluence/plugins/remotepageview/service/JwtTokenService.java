/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.servlet.ServletRequest
 *  javax.ws.rs.core.UriBuilder
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  org.springframework.util.StringUtils
 */
package com.atlassian.confluence.plugins.remotepageview.service;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.remotepageview.api.service.TokenService;
import com.atlassian.confluence.plugins.remotepageview.jwt.JwtTokenGenerator;
import com.atlassian.confluence.plugins.remotepageview.rest.response.TokenResponse;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.net.URI;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenService
implements TokenService {
    static final String JWT_SUBJECT = "jwt.subject.remote-page-view.jwtTokenResource";
    private static final String REMOTE_PAGE_VIEW_URI_PATH = "/plugins/servlet/remotepageview";
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserAccessor userAccessor;

    @Autowired
    public JwtTokenService(PageManager pageManager, PermissionManager permissionManager, JwtTokenGenerator jwtTokenGenerator, UserAccessor userAccessor) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userAccessor = userAccessor;
    }

    @Override
    public Optional<TokenResponse> generateLoginTokenForUser(ConfluenceUser user, long pageId) {
        Page page = this.pageManager.getPage(pageId);
        if (page == null || page.isDeleted()) {
            return Optional.empty();
        }
        if (this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page)) {
            return this.getJwtTokenResponse(pageId, user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ConfluenceUser> getUserFromRequest(ServletRequest httpServletRequest) {
        String strUserKey;
        String jwtPayloadStr = (String)httpServletRequest.getAttribute("jwt.payload");
        if (StringUtils.isEmpty((Object)jwtPayloadStr)) {
            return Optional.empty();
        }
        String jwtSubject = (String)httpServletRequest.getAttribute("jwt.subject");
        if (!JWT_SUBJECT.equals(jwtSubject)) {
            return Optional.empty();
        }
        JSONObject jwtPayloadJson = new JSONObject(jwtPayloadStr);
        String string = strUserKey = jwtPayloadJson.has("userKey") ? jwtPayloadJson.getString("userKey") : "";
        if (StringUtils.isEmpty((Object)strUserKey)) {
            return Optional.empty();
        }
        UserKey userKey = new UserKey(strUserKey);
        return Optional.ofNullable(this.userAccessor.getExistingUserByKey(userKey));
    }

    private Optional<TokenResponse> getJwtTokenResponse(long pageId, ConfluenceUser user) {
        URI remotePageViewUrl = UriBuilder.fromPath((String)REMOTE_PAGE_VIEW_URI_PATH).queryParam("pageId", new Object[]{pageId}).build(new Object[0]);
        String jwtToken = this.jwtTokenGenerator.generate(JWT_SUBJECT, "POST", remotePageViewUrl, user.getKey().getStringValue());
        TokenResponse tokenResponse = new TokenResponse(jwtToken);
        return Optional.of(tokenResponse);
    }
}

