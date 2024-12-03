/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import java.io.Serializable;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AuthServletUtils {
    private static final Logger logger = LoggerFactory.getLogger(AuthServletUtils.class);

    private AuthServletUtils() {
    }

    protected static ApplicationLink getApplicationLink(ApplicationLinkService applicationLinkService, MessageFactory messageFactory, HttpServletRequest request) throws AbstractApplinksServlet.NotFoundException, AbstractApplinksServlet.BadRequestException {
        String pathInfo = URI.create(request.getPathInfo()).normalize().toString();
        String[] elements = StringUtils.split((String)pathInfo, (char)'/');
        if (elements.length > 0) {
            ApplicationId id = new ApplicationId(elements[0]);
            try {
                ApplicationLink link = applicationLinkService.getApplicationLink(id);
                if (link != null) {
                    return link;
                }
                AbstractApplinksServlet.NotFoundException exception = new AbstractApplinksServlet.NotFoundException();
                exception.setTemplate("com/atlassian/applinks/ui/auth/applink-missing.vm");
                throw exception;
            }
            catch (TypeNotInstalledException e) {
                logger.warn(String.format("Unable to load ApplicationLink %s due to uninstalled type definition (%s).", id.toString(), e.getType()), (Throwable)e);
                throw new AbstractApplinksServlet.NotFoundException(messageFactory.newI18nMessage("auth.config.applink.notfound", new Serializable[]{id.toString()}));
            }
        }
        throw new AbstractApplinksServlet.BadRequestException(messageFactory.newI18nMessage("auth.config.applinkpath.missing", new Serializable[0]));
    }
}

