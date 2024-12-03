/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.EmbeddedObjectRenderer;
import com.atlassian.renderer.embedded.EmbeddedResource;
import java.util.Map;

public class EmbeddedRealMediaRenderer
extends EmbeddedObjectRenderer {
    public static String DEFAULT_WIDTH = "320";
    public static String DEFAULT_HEIGHT = "240";
    public static String DEFAULT_CONTROLS_HEIGHT = "30";

    public EmbeddedRealMediaRenderer(RendererAttachmentManager attachmentManager) {
        super(attachmentManager);
    }

    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        Map contextMap = this.setupObjectProperties(resource, context);
        String origHeight = (String)contextMap.get("height");
        String origWidth = (String)contextMap.get("width");
        contextMap.put("controls", "imagewindow");
        contextMap.put("console", "video");
        if (origWidth == null) {
            contextMap.put("width", DEFAULT_WIDTH);
        }
        if (origHeight == null) {
            contextMap.put("height", DEFAULT_HEIGHT);
        }
        String videoContent = this.renderEmbeddedObject(contextMap);
        contextMap.put("controls", "ControlPanel");
        contextMap.put("console", "video");
        if (origWidth == null) {
            contextMap.put("width", DEFAULT_WIDTH);
        }
        if (origHeight == null) {
            contextMap.put("height", DEFAULT_CONTROLS_HEIGHT);
        }
        String playerContent = this.renderEmbeddedObject(contextMap);
        return this.renderRealMediaWrapper(videoContent, playerContent, contextMap);
    }

    private String renderRealMediaWrapper(String videoContent, String playerContent, Map contextMap) {
        StringBuffer sb = new StringBuffer();
        String classString = "embeddedObject";
        if (contextMap.containsKey("id")) {
            classString = classString + "-" + contextMap.get("id");
        }
        sb.append("<div class='");
        sb.append(classString);
        sb.append("'>");
        sb.append("<table border='0' cellpadding='0'>\n<tr>\n<td>\n");
        sb.append(videoContent);
        sb.append("</td>\n</tr>\n<tr>\n<td>");
        sb.append(playerContent);
        sb.append("</td>\n</tr>\n</table>\n</div>");
        return sb.toString();
    }
}

