/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.services;

import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={VelocityRenderService.class})
public class DefaultVelocityRenderService
implements VelocityRenderService {
    private static final String DEFAULT_WIDTH = "400";
    private static final String DEFAULT_HEIGHT = "300";
    private static final String DEFAULT_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/embed.vm";

    @Override
    @Nullable
    public String render(String url, Map<String, String> params) {
        String width = params.get("width");
        String height = params.get("height");
        String template = params.get("_template");
        if (StringUtils.isEmpty((CharSequence)template)) {
            template = DEFAULT_TEMPLATE;
        }
        if (StringUtils.isEmpty((CharSequence)url)) {
            return null;
        }
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().contentEquals("tweetHtml")) {
                contextMap.put(entry.getKey(), entry.getValue());
                continue;
            }
            contextMap.put(entry.getKey(), HtmlUtil.htmlEncode((String)entry.getValue()));
        }
        contextMap.put("urlHtml", HtmlUtil.htmlEncode((String)url));
        if (StringUtils.isNotEmpty((CharSequence)width)) {
            contextMap.put("width", HtmlUtil.htmlEncode((String)width));
        } else {
            contextMap.put("width", DEFAULT_WIDTH);
        }
        if (StringUtils.isNotEmpty((CharSequence)height)) {
            contextMap.put("height", HtmlUtil.htmlEncode((String)height));
        } else {
            contextMap.put("height", DEFAULT_HEIGHT);
        }
        return this.getRenderedTemplate(template, contextMap);
    }

    protected String getRenderedTemplate(String template, Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)template, contextMap);
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }
}

