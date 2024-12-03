/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.sharelinks.metaextractor;

import com.atlassian.confluence.plugins.sharelinks.DOMMetadataExtractor;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaData;
import com.atlassian.confluence.plugins.sharelinks.metaextractor.JsoupUtil;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDOMMetadataExtractor
implements DOMMetadataExtractor {
    private static final Logger log = LoggerFactory.getLogger(SimpleDOMMetadataExtractor.class);
    private static final String TITLE_QUERY = "title";
    private static final String META_TITLE_QUERY = "meta[name=title]";
    private static final String META_DESCRIPTION_QUERY = "meta[name=description]";
    private static final String FAVICON_QUERY = "link[rel=shortcut icon]";
    private final RequestFactory<?> requestFactory;

    public SimpleDOMMetadataExtractor(RequestFactory<?> requestFactory) {
        this.requestFactory = requestFactory;
    }

    @Override
    public void updateMetadata(LinkMetaData meta, Document head) {
        if (StringUtils.isBlank((CharSequence)meta.getTitle())) {
            Elements titleElements;
            String title = JsoupUtil.getMetaContent(head, META_TITLE_QUERY);
            if (title == null && !(titleElements = head.select(TITLE_QUERY)).isEmpty()) {
                title = titleElements.text();
            }
            meta.setTitle(title);
        }
        if (StringUtils.isBlank((CharSequence)meta.getDescription())) {
            meta.setDescription(JsoupUtil.getMetaContent(head, META_DESCRIPTION_QUERY));
        }
        if (StringUtils.isBlank((CharSequence)meta.getFaviconURL())) {
            Elements faviconElements = head.select(FAVICON_QUERY);
            if (!faviconElements.isEmpty() && ((Element)faviconElements.get(0)).attr("href") != null) {
                String faviconURL = ((Element)faviconElements.get(0)).attr("href");
                faviconURL = this.getAbsolutePath(faviconURL, meta.getResponseHost());
                meta.setFaviconURL(faviconURL);
            } else {
                try {
                    this.retrieveWebRootFavicon(meta);
                }
                catch (Exception e) {
                    log.warn("Unable to retrieve web root favicon for [{}], defaulting to placeholder", (Object)meta.getResponseHost());
                }
            }
        }
    }

    private void retrieveWebRootFavicon(LinkMetaData meta) {
        if (meta.getFaviconURL() == null) {
            URI webRootFavicon = SimpleDOMMetadataExtractor.getFaviconUri(meta.getResponseHost());
            String webRootFaviconPath = webRootFavicon.toString();
            Request request = this.requestFactory.createRequest(Request.MethodType.GET, webRootFaviconPath);
            try {
                request.execute(response -> {
                    if (response.getStatusCode() == 200) {
                        meta.setFaviconURL(webRootFaviconPath);
                    }
                });
            }
            catch (ResponseException e) {
                log.error("Error with io exception: ", (Throwable)e);
            }
        }
    }

    private String getAbsolutePath(String path, URI host) {
        try {
            URI uri = host.resolve(path.trim());
            return uri.toString();
        }
        catch (IllegalArgumentException e) {
            log.info("Favicon path {} could not be resolved.", (Object)path);
            return null;
        }
    }

    protected static URI getFaviconUri(URI uri) {
        return uri.resolve("/favicon.ico");
    }
}

