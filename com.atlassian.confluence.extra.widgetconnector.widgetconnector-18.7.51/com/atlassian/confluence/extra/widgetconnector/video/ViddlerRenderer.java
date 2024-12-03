/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.video;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetConnectorUtil;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.exceptions.EmbedRetrievalException;
import com.atlassian.confluence.extra.widgetconnector.services.HttpRetrievalEmbedService;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class ViddlerRenderer
extends AbstractWidgetRenderer {
    public static final Pattern PATTERN = Pattern.compile("www.viddler.com/player/([a-z0-9]+)");
    private static final Pattern VIDEO_PATTERN = Pattern.compile("www.viddler.com/v/((?:[a-z0-9]*))");
    private static final Pattern PRIVATE_VIDEO_PATTERN = Pattern.compile("(https?://)?www.viddler.com/v/.*?(secret=(.+?))(&.+)*$");
    private final VelocityRenderService velocityRenderService;
    private final HttpRetrievalEmbedService httpRetrievalEmbedService;
    private static final String SERVICE_NAME = "Viddler";

    @Autowired
    public ViddlerRenderer(HttpRetrievalEmbedService httpRetrievalEmbedService, VelocityRenderService velocityRenderService) {
        this.httpRetrievalEmbedService = httpRetrievalEmbedService;
        this.velocityRenderService = velocityRenderService;
    }

    public String getEmbedUrl(String url) throws EmbedRetrievalException {
        String embedParameter = this.httpRetrievalEmbedService.getEmbedData(url, PATTERN, this.getClass().getName());
        return "//www.viddler.com/player/" + embedParameter;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        String location = this.httpRetrievalEmbedService.getNewLocation(url);
        Matcher m = VIDEO_PATTERN.matcher(location);
        String videoId = "";
        if (m.find()) {
            videoId = m.group(1);
        }
        params.put("flashVars", "key=" + videoId);
        m = PRIVATE_VIDEO_PATTERN.matcher(location);
        if (m.matches()) {
            params.put("flashVars", "key=" + videoId + "&openURL=" + m.group(3));
        }
        try {
            String embedUrl = this.getEmbedUrl(location);
            return this.velocityRenderService.render(embedUrl, params);
        }
        catch (EmbedRetrievalException e) {
            params.put("_template", "com/atlassian/confluence/extra/widgetconnector/templates/error.vm");
            params.put("baseUrlHtml", HtmlUtil.htmlEncode((String)WidgetConnectorUtil.getBaseUrl(location)));
            params.put("errorMessage", HtmlUtil.htmlEncode((String)e.getMessage()));
            return this.velocityRenderService.render(location, params);
        }
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}

