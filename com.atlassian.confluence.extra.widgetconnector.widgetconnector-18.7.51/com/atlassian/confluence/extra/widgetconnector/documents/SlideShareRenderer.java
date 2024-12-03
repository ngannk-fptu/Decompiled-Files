/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.documents;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetConnectorUtil;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.exceptions.EmbedRetrievalException;
import com.atlassian.confluence.extra.widgetconnector.services.HttpRetrievalEmbedService;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class SlideShareRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("slideshare\\.net/slideshow/embed_code/key/([a-zA-Z0-9]+)\"");
    private static final String DEFAULT_WIDTH = "425";
    private static final String DEFAULT_HEIGHT = "355";
    private static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/iframe.vm";
    private static final String SERVICE_NAME = "SlideShare";
    private final HttpRetrievalEmbedService httpRetrievalEmbedService;
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public SlideShareRenderer(HttpRetrievalEmbedService httpRetrievalEmbedService, VelocityRenderService velocityRenderService) {
        this.httpRetrievalEmbedService = httpRetrievalEmbedService;
        this.velocityRenderService = velocityRenderService;
    }

    @Nullable
    public String getEmbedUrl(String url) throws EmbedRetrievalException {
        String embedParameter = this.httpRetrievalEmbedService.getEmbedData(url, PATTERN, this.getClass().getName());
        if (StringUtils.isNotEmpty((CharSequence)embedParameter)) {
            return "//www.slideshare.net/slideshow/embed_code/key/" + embedParameter;
        }
        return null;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        try {
            String embedUrl = this.getEmbedUrl(url);
            if (!params.containsKey("width")) {
                params.put("width", DEFAULT_WIDTH);
            }
            if (!params.containsKey("height")) {
                params.put("height", DEFAULT_HEIGHT);
            }
            params.put("_template", VELOCITY_TEMPLATE);
            return this.velocityRenderService.render(embedUrl, params);
        }
        catch (EmbedRetrievalException e) {
            params.put("_template", "com/atlassian/confluence/extra/widgetconnector/templates/error.vm");
            params.put("errorMessage", e.getMessage());
            params.put("baseUrlHtml", HtmlUtil.htmlEncode((String)WidgetConnectorUtil.getBaseUrl(url)));
            return this.velocityRenderService.render(url, params);
        }
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

