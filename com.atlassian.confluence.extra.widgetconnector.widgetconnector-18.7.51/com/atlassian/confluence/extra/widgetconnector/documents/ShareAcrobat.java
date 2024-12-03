/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.documents;

import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class ShareAcrobat
extends AbstractWidgetRenderer {
    public static final String VELOCITY_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/embed.vm";
    private static final Pattern PATTERN = Pattern.compile("docid=([^&]+)");
    private static final String EMBED_URL = "https://share.acrobat.com/adc/flex/mpt.swf";
    private static final String FLASHVARS_PARAM = "flashVars";
    private static final String DEFAULT_WIDTH = "365px";
    private static final String DEFAULT_HEIGHT = "500px";
    private static final String SERVICE_NAME = "ShareAcrobat";
    private final VelocityRenderService velocityRenderService;

    @Autowired
    public ShareAcrobat(VelocityRenderService velocityRenderService) {
        this.velocityRenderService = velocityRenderService;
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        params.put(FLASHVARS_PARAM, this.getFlashVars(url));
        if (!params.containsKey("width")) {
            params.put("width", DEFAULT_WIDTH);
        }
        if (!params.containsKey("height")) {
            params.put("height", DEFAULT_HEIGHT);
        }
        params.put("_template", VELOCITY_TEMPLATE);
        return this.velocityRenderService.render(EMBED_URL, params);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public String getFlashVars(String url) {
        Matcher m = PATTERN.matcher(url);
        String docId = "";
        if (m.find()) {
            docId = m.group(1);
        }
        return "docId=" + docId;
    }
}

