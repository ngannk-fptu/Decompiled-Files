/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.confluence.extra.widgetconnector.documents;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.exceptions.EmbedRetrievalException;
import com.atlassian.confluence.extra.widgetconnector.services.HttpRetrievalEmbedService;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class ScribdRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("(doc|read)/(\\d+)/.+");
    public static final Pattern ACCESS_KEY_PATTERN = Pattern.compile("access_key\":\"([^\"]+)\"");
    public static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String DEFAULT_WIDTH = "475";
    private static final String DEFAULT_HEIGHT = "355";
    private static final String SERVICE_NAME = "Scribd";
    private final HttpRetrievalEmbedService httpRetrievalEmbedService;
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public ScribdRenderer(HttpRetrievalEmbedService httpRetrievalEmbedService, VelocityRenderService velocityRenderService) {
        this.httpRetrievalEmbedService = httpRetrievalEmbedService;
        this.velocityRenderService = velocityRenderService;
    }

    private String getDocId(String url) {
        Matcher m = PATTERN.matcher(url);
        if (m.find()) {
            return m.group(2);
        }
        return null;
    }

    private String getAccessKey(String url) {
        MultiValueMap params = UriComponentsBuilder.fromUriString((String)url).build().getQueryParams();
        String accessKey = (String)params.getFirst((Object)"access_key");
        if (accessKey == null) {
            try {
                return this.httpRetrievalEmbedService.getEmbedData(url, ACCESS_KEY_PATTERN, this.getClass().getName());
            }
            catch (EmbedRetrievalException e) {
                return null;
            }
        }
        return accessKey;
    }

    public String getEmbedUrl(String url) {
        Matcher m = PATTERN.matcher(url);
        if (m.find()) {
            String doc = m.group(2);
            return "//www.scribd.com/embeds/" + doc + "/content?start_page=1&view_mode=scroll&access_key=" + this.getAccessKey(url) + "&show_recommendations=false";
        }
        return null;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put("_template", VELOCITY_TEMPLATE);
        params.put("nameHtml", this.getDocId(url));
        if (!params.containsKey("width")) {
            params.put("width", DEFAULT_WIDTH);
        }
        if (!params.containsKey("height")) {
            params.put("height", DEFAULT_HEIGHT);
        }
        return this.velocityRenderService.render(this.getEmbedUrl(url), params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

