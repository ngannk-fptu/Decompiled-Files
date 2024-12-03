/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 */
package com.atlassian.confluence.plugins.macros.multimedia.renderer;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.macros.multimedia.renderer.AbstractEmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.Map;

public class EmbeddedAudioRenderer
extends AbstractEmbeddedResourceRenderer {
    private static final String RESOURCE_TYPE = "audio/";
    private static final String DEFAULT_WIDTH = "300";
    private static final String DEFAULT_HEIGHT = "42";
    public static final String HTML5_AUDIO_TEMPLATE_PATH = "templates/embeddedhtml5audio.vm";

    @Override
    protected String renderEmbeddedObject(EmbeddedObject resource, Map<String, Object> wrapperContextMap) {
        return VelocityUtils.getRenderedTemplate((String)HTML5_AUDIO_TEMPLATE_PATH, wrapperContextMap);
    }

    @Override
    protected Map<String, Object> refineParams(Attachment attachment, Map<String, Object> properties) {
        if (properties.getOrDefault("autostart", "").equals("true")) {
            properties.put("html5Autoplay", "autoplay");
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

