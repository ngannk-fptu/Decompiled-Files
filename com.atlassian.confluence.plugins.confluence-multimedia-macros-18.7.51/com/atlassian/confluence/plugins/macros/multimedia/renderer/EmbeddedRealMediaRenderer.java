/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.plugins.macros.multimedia.renderer;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.macros.multimedia.renderer.AbstractEmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import java.util.HashMap;
import java.util.Map;

public class EmbeddedRealMediaRenderer
extends AbstractEmbeddedResourceRenderer {
    private static final String DEFAULT_WIDTH = "320";
    private static final String DEFAULT_HEIGHT = "240";
    private static final String DEFAULT_CONTROLS_HEIGHT = "30";
    private static final String RESOURCE_TYPE = "application/vnd.rn-realmedia";
    private static final String FILE_EXT_1 = ".rm";
    private static final String FILE_EXT_2 = ".ram";
    private static final String RM_TEMPLATE_PATH = "templates/embeddedrealmedia-wrapper.vm";

    @Override
    public String renderResource(EmbeddedObject resource, RenderContext context) {
        Map<String, Object> contextMap = this.setupObjectProperties(resource, context);
        HashMap<String, Object> wrapperContextMap = new HashMap<String, Object>();
        wrapperContextMap.put("properties", contextMap);
        wrapperContextMap.put("validObjectTags", validObjectTags);
        wrapperContextMap.put("validEmbedTags", validEmbedTags);
        wrapperContextMap.put("validParamTags", validParamTags);
        contextMap.put("controls", "imagewindow");
        String videoContent = VelocityUtils.getRenderedTemplate((String)"templates/embeddedobject.vm", wrapperContextMap);
        contextMap.put("controls", "ControlPanel");
        contextMap.put("height", DEFAULT_CONTROLS_HEIGHT);
        String playerContent = VelocityUtils.getRenderedTemplate((String)"templates/embeddedobject.vm", wrapperContextMap);
        HashMap<String, Object> tableContextMap = new HashMap<String, Object>();
        tableContextMap.put("rm-videoContent", videoContent);
        tableContextMap.put("rm-videoPlayer", playerContent);
        tableContextMap.put("properties", contextMap);
        return VelocityUtils.getRenderedTemplate((String)RM_TEMPLATE_PATH, tableContextMap);
    }

    @Override
    protected Map<String, Object> refineParams(Attachment attachment, Map<String, Object> properties) {
        if (!properties.containsKey("classid")) {
            properties.put("classid", "clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA");
        }
        properties.put("console", "video");
        return properties;
    }

    public boolean matchesType(EmbeddedObject resource) {
        return resource.getContentType().startsWith(RESOURCE_TYPE) || resource.getFileExtension().equals(FILE_EXT_1) || resource.getFileExtension().equals(FILE_EXT_2);
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

