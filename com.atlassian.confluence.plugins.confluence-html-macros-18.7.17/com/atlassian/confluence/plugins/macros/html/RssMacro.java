/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.opensymphony.util.TextUtils
 *  com.rometools.rome.feed.synd.SyndEntry
 *  com.rometools.rome.feed.synd.SyndFeed
 *  com.rometools.rome.io.FeedException
 *  com.rometools.rome.io.SyndFeedInput
 *  com.rometools.rome.io.XmlReader
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.macros.html;

import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.macros.html.WhitelistedHttpRetrievalMacro;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.annotations.VisibleForTesting;
import com.opensymphony.util.TextUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RssMacro
extends WhitelistedHttpRetrievalMacro {
    private static final Logger log = LoggerFactory.getLogger(RssMacro.class);
    private static final List<String> SCHEMAS = Arrays.asList("http", "https");
    private final VelocityHelperService velocityHelperService;
    private final RenderedContentCleaner contentCleaner;

    @Autowired
    public RssMacro(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport NonMarshallingRequestFactory<Request<?, Response>> requestFactory, @ComponentImport ReadOnlyApplicationLinkService applicationLinkService, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport OutboundWhitelist whitelist, @ComponentImport RenderedContentCleaner contentCleaner, @ComponentImport UserManager userManager) {
        super(localeManager, i18NBeanFactory, requestFactory, applicationLinkService, whitelist, userManager, velocityHelperService);
        this.velocityHelperService = velocityHelperService;
        this.contentCleaner = contentCleaner;
    }

    @Override
    protected String successfulResponse(Map<String, String> parameters, ConversionContext conversionContext, String url, Response response) throws MacroExecutionException {
        SyndFeed feed;
        int maxItems = TextUtils.parseInt((String)RenderUtils.getParameter(parameters, (String)"max", (int)1));
        String titleBar = TextUtils.noNull((String)RenderUtils.getParameter(parameters, (String)"titleBar", (int)2)).trim();
        boolean showTitlesOnly = TextUtils.parseBoolean((String)RenderUtils.getParameter(parameters, (String)"showTitlesOnly", (int)2));
        try {
            feed = this.parseRSSFeed(url, IOUtils.toByteArray((InputStream)response.getResponseBodyAsStream()));
        }
        catch (ResponseException | IOException e) {
            throw new MacroExecutionException(this.getText("rss.error.parse", Collections.singletonList(e.toString())), e);
        }
        Map<String, Object> contextMap = this.getMacroVelocityContext();
        contextMap.put("url", url);
        contextMap.put("feed", feed);
        contextMap.put("max", maxItems);
        contextMap.put("showTitlesOnly", showTitlesOnly);
        contextMap.put("contentCleaner", this.contentCleaner);
        contextMap.put("titleBar", Boolean.toString(!titleBar.equalsIgnoreCase("false") && !titleBar.equalsIgnoreCase("no")));
        try {
            this.verifyLinksInFeed(feed);
            return this.renderRssFeeds(contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to assemble the RSS result!", (Throwable)e);
            throw new MacroExecutionException(e.getMessage());
        }
    }

    @VisibleForTesting
    Map<String, Object> getMacroVelocityContext() {
        return this.velocityHelperService.createDefaultVelocityContext();
    }

    @VisibleForTesting
    String renderRssFeeds(Map<String, Object> contextMap) {
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/plugins/macros/html/rss.vm", contextMap);
    }

    private void verifyLinksInFeed(SyndFeed feed) throws FeedException, URISyntaxException {
        if (feed == null) {
            return;
        }
        this.verifyLink(feed.getLink());
        for (Object entry : feed.getEntries()) {
            this.verifyLink(((SyndEntry)entry).getLink());
        }
    }

    private void verifyLink(String link) throws FeedException, URISyntaxException {
        if (link == null) {
            return;
        }
        URI uri = new URI(link);
        if (SCHEMAS.stream().noneMatch(schema -> schema.equalsIgnoreCase(uri.getScheme()))) {
            throw new FeedException("Unsupported schema found in RSS feed");
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private SyndFeed parseRSSFeed(String url, byte[] webContent) throws IOException {
        try {
            ByteArrayInputStream bufferedIn = new ByteArrayInputStream(webContent);
            try {
                SyndFeedInput input = new SyndFeedInput();
                input.setAllowDoctypes(true);
                SyndFeed syndFeed = input.build((Reader)new XmlReader((InputStream)bufferedIn));
                return syndFeed;
            }
            catch (IOException ioe) {
                throw new FeedException("Unable to read XML from " + url, (Throwable)ioe);
            }
            finally {
                try {
                    bufferedIn.close();
                }
                catch (Throwable throwable) {
                    Throwable throwable2;
                    throwable2.addSuppressed(throwable);
                }
            }
        }
        catch (FeedException e) {
            log.error("Error while trying to assemble the RSS result! Url: " + url, (Throwable)e);
            throw new IOException("Unable to parse rss feed from [" + url + "] due to " + e.getMessage());
        }
    }
}

