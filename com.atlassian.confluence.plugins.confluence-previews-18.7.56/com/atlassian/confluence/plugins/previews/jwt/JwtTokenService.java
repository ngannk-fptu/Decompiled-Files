/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.Cleanup
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.jwt.core.JwtUtil
 *  com.atlassian.jwt.core.TimeUtil
 *  com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder
 *  com.atlassian.jwt.reader.JwtReaderFactory
 *  com.atlassian.jwt.writer.JwtJsonBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.previews.jwt;

import com.atlassian.confluence.plugins.previews.jwt.JwtTokenGenerator;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.jwt.core.JwtUtil;
import com.atlassian.jwt.core.TimeUtil;
import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.reader.JwtReaderFactory;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);
    public static final String USER_KEY = "userKey";
    public static final String JWTID_KEY = "jti";
    private final UserAccessor userAccessor;
    private final JwtReaderFactory jwtReaderFactory;

    public JwtTokenService(@ComponentImport UserAccessor userAccessor, @ComponentImport JwtReaderFactory jwtReaderFactory) {
        this.userAccessor = userAccessor;
        this.jwtReaderFactory = jwtReaderFactory;
    }

    JwtJsonBuilder createJsonBuilder(String userKey, String issuerId) {
        return new JsonSmartJwtJsonBuilder().issuedAt(TimeUtil.currentTimeSeconds()).expirationTime(TimeUtil.currentTimePlusNSeconds((long)JwtTokenGenerator.JWT_EXPIRY_WINDOW_SECONDS)).issuer(issuerId).claim(USER_KEY, (Object)userKey);
    }

    @Nullable
    JSONObject extractJWTPayload(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            log.debug("JwtTokenService.extractJWTPayload: ServletRequest is empty");
            return null;
        }
        if (!this.isSignedByPreviewsPlugin((HttpServletRequest)request)) {
            return null;
        }
        String jwtPayloadStr = (String)request.getAttribute("jwt.payload");
        if (StringUtils.isEmpty((CharSequence)jwtPayloadStr)) {
            return null;
        }
        return new JSONObject(jwtPayloadStr);
    }

    @Nullable
    ConfluenceUser getUserFromRequest(ServletRequest request) {
        String strUserKey;
        JSONObject jwtPayloadJson = this.extractJWTPayload(request);
        if (jwtPayloadJson == null) {
            return null;
        }
        String string = strUserKey = jwtPayloadJson.has(USER_KEY) ? jwtPayloadJson.getString(USER_KEY) : null;
        if (StringUtils.isEmpty((CharSequence)strUserKey)) {
            return null;
        }
        UserKey userKey = new UserKey(strUserKey);
        return this.userAccessor.getExistingUserByKey(userKey);
    }

    public boolean isSignedByPreviewsPlugin(HttpServletRequest request) {
        return "jwt.subject.confluence-previews.templinksresource".equals(this.getJWTSubject(request).orElse(null));
    }

    private Optional<String> getJWTSubject(HttpServletRequest request) {
        try {
            String jwtSubjectFromRequestAttr = (String)request.getAttribute("jwt.subject");
            if (jwtSubjectFromRequestAttr != null) {
                return Optional.of(jwtSubjectFromRequestAttr);
            }
            String jwtString = JwtUtil.extractJwt((HttpServletRequest)request);
            if (jwtString == null) {
                return Optional.empty();
            }
            return Optional.of(this.jwtReaderFactory.getReader(jwtString).readUnverified(jwtString).getSubject());
        }
        catch (Exception e) {
            log.error("JwtTokenService.getJWTSubject: error extracting jwt from request. {}", (Throwable)e);
            return Optional.empty();
        }
    }

    Cleanup asUserFromRequest(ServletRequest request) {
        ConfluenceUser user = this.getUserFromRequest(request);
        if (user == null) {
            return () -> {};
        }
        AutoCloseable asUserCloseable = AuthenticatedUserThreadLocal.asUser((ConfluenceUser)user);
        request.setAttribute("jwt_request_username", (Object)user.getName());
        return () -> {
            try {
                asUserCloseable.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            request.setAttribute("jwt_request_username", null);
        };
    }
}

