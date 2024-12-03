/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.documents;

import com.atlassian.confluence.extra.widgetconnector.GoogleWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class GoogleDocsRenderer
extends GoogleWidgetRenderer {
    public static final String PRESENTATION_TYPE = "presentation";
    public static final String DOCUMENT_TYPE = "document";
    public static final String PRESENTATION_EMBED_URL = "/embed";
    public static final String DOCUMENT_EMBED_URL = "/pub?embedded=true";
    public static final Collection<Pattern> MATCHER_PATTERNS;
    private static final Pattern DOCUMENT_PATTERN;
    private static final Pattern PRESENTATION_PATTERN;
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "768";
    private static final String DEFAULT_HEIGHT = "342";
    private static final String EMBED_URL = "//docs.google.com/";
    private static final String SERVICE_NAME = "GoogleDocs";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public GoogleDocsRenderer(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) {
        for (Pattern eachPattern : MATCHER_PATTERNS) {
            Matcher matcher = eachPattern.matcher(url);
            if (!matcher.find()) continue;
            String docType = matcher.group("docType");
            String docId = matcher.group("docId");
            String embedUrl = EMBED_URL + docType + "/d/" + docId;
            if (PRESENTATION_TYPE.equals(docType)) {
                embedUrl = embedUrl + PRESENTATION_EMBED_URL;
            } else if (DOCUMENT_TYPE.equals(docType)) {
                embedUrl = embedUrl + DOCUMENT_EMBED_URL;
            }
            return embedUrl;
        }
        return null;
    }

    @Override
    public boolean matches(String url) {
        if (super.matches(url)) {
            URI uri = URI.create(url.toLowerCase()).normalize();
            return this.matchesDocument(uri) || this.matchesPresentation(uri);
        }
        return false;
    }

    private boolean matchesDocument(URI uri) {
        String host = uri.getHost();
        String path = uri.getPath();
        if (host != null && path != null) {
            return host.startsWith("docs.") && path.startsWith("/document");
        }
        return false;
    }

    private boolean matchesPresentation(URI uri) {
        String host = uri.getHost();
        String path = uri.getPath();
        if (host != null && path != null) {
            return host.startsWith("docs.") && path.startsWith("/presentation");
        }
        return false;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.putIfAbsent("width", DEFAULT_WIDTH);
        params.putIfAbsent("height", DEFAULT_HEIGHT);
        params.put("_template", VELOCITY_TEMPLATE);
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    static {
        DOCUMENT_PATTERN = Pattern.compile("(?<docType>document)/d/(?<docId>(e/)?[^/]+)&?");
        PRESENTATION_PATTERN = Pattern.compile("(?<docType>presentation)/d/(?<docId>(e/)?[^/]+)&?");
        ArrayList<Pattern> matcherPatterns = new ArrayList<Pattern>();
        matcherPatterns.add(DOCUMENT_PATTERN);
        matcherPatterns.add(PRESENTATION_PATTERN);
        MATCHER_PATTERNS = Collections.unmodifiableList(matcherPatterns);
    }
}

