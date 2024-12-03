/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.util;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.rest.util.BadHttpRequestException;
import com.atlassian.applinks.core.rest.util.BlockedHostException;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.ui.BadRequestException;
import com.atlassian.applinks.ui.NotFoundException;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);
    private static final Set<String> BLOCKED_HOSTS_REGEX = ImmutableSet.of((Object)"^0.0.0.0$", (Object)"^169.254.\\d{1,3}\\.\\d{1,3}$", (Object)"^127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", (Object)"^0:0:0:0:0:0:[a-z0-9]{1,4}:[a-z0-9]{1,4}");
    private static final int HTTP_DEFAULT_PORT = 80;
    private static final int HTTPS_DEFAULT_PORT = 443;
    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";

    public static URI getBaseURLFromRequest(HttpServletRequest request, URI baseUrl) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            String scheme = request.getScheme();
            urlBuilder.append(scheme);
            urlBuilder.append("://");
            urlBuilder.append(request.getServerName());
            int port = request.getServerPort();
            if (!RequestUtil.isStandardPort(scheme, port)) {
                urlBuilder.append(":");
                urlBuilder.append(port);
            }
            urlBuilder.append(request.getContextPath());
            return new URI(urlBuilder.toString());
        }
        catch (Exception ex) {
            return baseUrl;
        }
    }

    public static int getDefaultPort(String scheme) {
        if (scheme.equalsIgnoreCase(HTTP_SCHEME)) {
            return 80;
        }
        if (scheme.equalsIgnoreCase(HTTPS_SCHEME)) {
            return 443;
        }
        return -1;
    }

    private static boolean isStandardPort(String scheme, int port) {
        if (scheme.equalsIgnoreCase(HTTP_SCHEME) && port == 80) {
            return true;
        }
        return scheme.equalsIgnoreCase(HTTPS_SCHEME) && port == 443;
    }

    public static ApplicationLink getApplicationLink(ApplicationLinkService applicationLinkService, MessageFactory messageFactory, HttpServletRequest request) throws NotFoundException, BadRequestException {
        String pathInfo = URI.create(request.getPathInfo()).normalize().toString();
        String[] elements = StringUtils.split((String)pathInfo, (char)'/');
        if (elements.length > 0) {
            ApplicationId id = new ApplicationId(elements[0]);
            try {
                ApplicationLink link = applicationLinkService.getApplicationLink(id);
                if (link != null) {
                    return link;
                }
                NotFoundException exception = new NotFoundException();
                exception.setTemplate("com/atlassian/applinks/ui/auth/applink-missing.vm");
                throw exception;
            }
            catch (TypeNotInstalledException e) {
                logger.warn(String.format("Unable to load ApplicationLink %s due to uninstalled type definition (%s).", id.toString(), e.getType()), (Throwable)e);
                throw new NotFoundException(messageFactory.newI18nMessage("auth.config.applink.notfound", new Serializable[]{id.toString()}));
            }
        }
        throw new BadRequestException(messageFactory.newI18nMessage("auth.config.applinkpath.missing", new Serializable[0]));
    }

    public static void validateUriAgainstBlocklist(String url, I18nResolver i18nResolver) {
        try {
            URI uri = URI.create(url);
            String localIP = InetAddress.getLocalHost().getHostAddress();
            String host = uri.getHost();
            InetAddress inetAddress = InetAddress.getByName(host);
            String hostAddress = inetAddress.getHostAddress();
            if (null != localIP && localIP.equals(hostAddress)) {
                throw new BlockedHostException(i18nResolver.getText("applinks.service.error.restapi.url.host.restricted"));
            }
            for (String blockedHostRegex : BLOCKED_HOSTS_REGEX) {
                if (!hostAddress.matches(blockedHostRegex)) continue;
                throw new BlockedHostException(i18nResolver.getText("applinks.service.error.restapi.url.host.restricted"));
            }
        }
        catch (IllegalArgumentException | UnknownHostException badRequestException) {
            throw new BadHttpRequestException(i18nResolver.getText("applinks.service.error.restapi.url.format"));
        }
    }
}

