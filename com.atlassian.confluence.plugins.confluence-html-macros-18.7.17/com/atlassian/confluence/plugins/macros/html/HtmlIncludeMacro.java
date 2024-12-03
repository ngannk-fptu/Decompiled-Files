/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserManager
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.parser.HTMLPageParser
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.macros.html;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.macros.html.WhitelistedHttpRetrievalMacro;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserManager;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HtmlIncludeMacro
extends WhitelistedHttpRetrievalMacro {
    private static final Logger logger = LoggerFactory.getLogger(HtmlIncludeMacro.class);
    private static final Pattern HTML_TYPE_PATTERN = Pattern.compile("(?i)(content-type:\\s*?)?\\Qtext/html\\E.*$");

    @Autowired
    public HtmlIncludeMacro(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport NonMarshallingRequestFactory<Request<?, Response>> requestFactory, @ComponentImport ReadOnlyApplicationLinkService applicationLinkService, @ComponentImport OutboundWhitelist whitelist, @ComponentImport UserManager userManager, @ComponentImport VelocityHelperService velocityHelperService) {
        super(localeManager, i18NBeanFactory, requestFactory, applicationLinkService, whitelist, userManager, velocityHelperService);
    }

    protected String successfulResponse(Map parameters, ConversionContext renderContext, String url, Response response) {
        String string;
        String contentType = StringUtils.defaultString((String)StringUtils.trim((String)response.getHeader("Content-Type")));
        if (!HTML_TYPE_PATTERN.matcher(contentType).matches()) {
            logger.debug("Content type is: {}", (Object)contentType);
            return HtmlIncludeMacro.errorContent(this.getText("htmlinclude.error.content.type.not.supported", Collections.singletonList(url)));
        }
        Page siteMeshPage = new HTMLPageParser().parse(response.getResponseBodyAsString().toCharArray());
        StringWriter writer = new StringWriter();
        try {
            siteMeshPage.writeBody((Writer)writer);
            string = ((Object)writer).toString();
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Writer)writer).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (ResponseException | IOException ioe) {
                logger.error("IOException occured while parsing: " + url, ioe);
                return HtmlIncludeMacro.errorContent(ioe.getMessage());
            }
        }
        ((Writer)writer).close();
        return string;
    }

    private static String errorContent(String message) {
        return "<span class=\"error\">" + StringEscapeUtils.escapeHtml((String)message) + "</span>";
    }
}

