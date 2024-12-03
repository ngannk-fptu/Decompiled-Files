/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 *  com.atlassian.confluence.util.UserAgentUtil
 *  com.atlassian.confluence.util.UserAgentUtil$BrowserFamily
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 */
package com.atlassian.confluence.plugins.macros.multimedia.renderer;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.macros.multimedia.renderer.AbstractEmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.util.UserAgentUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.Map;

public class EmbeddedQuickTimeRenderer
extends AbstractEmbeddedResourceRenderer {
    public static final String HTML5_VIDEO_TEMPLATE_PATH = "templates/embeddedhtml5video.vm";
    private static final String RESOURCE_TYPE = "video/quicktime";
    private static final String DEFAULT_WIDTH = "480";
    private static final String DEFAULT_HEIGHT = "380";

    @Override
    protected String renderEmbeddedObject(EmbeddedObject resource, Map<String, Object> wrapperContextMap) {
        return VelocityUtils.getRenderedTemplate((String)HTML5_VIDEO_TEMPLATE_PATH, wrapperContextMap);
    }

    @Override
    protected Map<String, Object> refineParams(Attachment attachment, Map<String, Object> properties) {
        if (UserAgentUtil.isBrowserFamily((UserAgentUtil.BrowserFamily)UserAgentUtil.BrowserFamily.SAFARI)) {
            if (properties.getOrDefault("autostart", "").equals("true")) {
                properties.put("html5Autoplay", "autoplay");
            }
            return properties;
        }
        properties.put("scale", "aspect");
        if (!properties.containsKey("classid")) {
            properties.put("classid", "clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B");
        }
        if (!properties.containsKey("codebase")) {
            properties.put("codebase", "https://www.apple.com/qtactivex/qtplugin.cab");
        }
        if (!properties.containsKey("pluginspage")) {
            properties.put("pluginspage", "https://www.apple.com/quicktime/download/");
        }
        if (properties.containsKey("autostart")) {
            properties.put("autostart", properties.get("autostart"));
            properties.put("autoplay", properties.get("autostart"));
        }
        return properties;
    }

    public boolean matchesType(EmbeddedObject resource) {
        return resource.getContentType().startsWith(RESOURCE_TYPE);
    }

    @Override
    protected String getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    @Override
    protected String getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }
}

