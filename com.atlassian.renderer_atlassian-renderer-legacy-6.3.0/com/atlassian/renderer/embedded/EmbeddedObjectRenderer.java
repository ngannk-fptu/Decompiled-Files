/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachment;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.EmbeddedObject;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddedObjectRenderer
implements EmbeddedResourceRenderer {
    protected RendererAttachmentManager attachmentManager;
    protected static List validObjectTags = new ArrayList();
    protected static List validEmbedTags;
    protected static List validParamTags;

    public EmbeddedObjectRenderer(RendererAttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    protected Map setupObjectProperties(EmbeddedResource resource, RenderContext context) {
        EmbeddedObject emObject = (EmbeddedObject)resource;
        RendererAttachment attachment = null;
        attachment = this.attachmentManager.getAttachment(context, emObject);
        if (attachment == null) {
            throw new IllegalArgumentException("Unable to render embedded object: File (" + emObject.getFilename() + ") not found.");
        }
        HashMap<Object, Object> objectParams = new HashMap<Object, Object>();
        objectParams.putAll(emObject.getProperties());
        objectParams.put("type", resource.getType());
        String objectUrl = attachment.getSrc();
        objectParams.put("object", objectUrl);
        objectParams.put("src", objectUrl);
        objectParams.put("data", objectUrl);
        return objectParams;
    }

    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        Map contextMap = null;
        try {
            contextMap = this.setupObjectProperties(resource, context);
        }
        catch (RuntimeException re) {
            return context.addRenderedContent(RenderUtils.error(re.getMessage()));
        }
        String renderedObjectHtml = this.renderEmbeddedObject(contextMap);
        return this.renderEmbeddedObjectWrapper(renderedObjectHtml, contextMap);
    }

    protected String renderEmbeddedObjectWrapper(String renderedObjectHtml, Map contextMap) {
        StringBuffer sb = new StringBuffer();
        String classString = "embeddedObject";
        if (contextMap.containsKey("id")) {
            classString = classString + "-" + HtmlEscaper.escapeAll((String)contextMap.get("id"), true);
        }
        sb.append("<div class=\"");
        sb.append(classString);
        sb.append("\">");
        sb.append(renderedObjectHtml);
        sb.append("</div>");
        return sb.toString();
    }

    protected String renderEmbeddedObject(Map contextMap) {
        StringBuffer sb = new StringBuffer("<object ");
        for (String tag : validObjectTags) {
            if (!contextMap.containsKey(tag)) continue;
            sb.append(tag);
            sb.append("=\"");
            sb.append(HtmlEscaper.escapeAll((String)contextMap.get(tag), true));
            sb.append("\" ");
        }
        sb.append(">");
        for (String paramTag : validParamTags) {
            if (!contextMap.containsKey(paramTag)) continue;
            sb.append("<param name=\"");
            sb.append(paramTag);
            sb.append("\" value=\"");
            sb.append(HtmlEscaper.escapeAll((String)contextMap.get(paramTag), true));
            sb.append("\"/>");
        }
        sb.append("<embed ");
        for (String embedTag : validEmbedTags) {
            if (!contextMap.containsKey(embedTag)) continue;
            sb.append(embedTag);
            sb.append("=\"");
            sb.append(HtmlEscaper.escapeAll((String)contextMap.get(embedTag), true));
            sb.append("\" ");
        }
        sb.append("/>");
        sb.append("</object>");
        return sb.toString();
    }

    static {
        validObjectTags.add("align");
        validObjectTags.add("archive");
        validObjectTags.add("border");
        validObjectTags.add("class");
        validObjectTags.add("classid");
        validObjectTags.add("codebase");
        validObjectTags.add("codetype");
        validObjectTags.add("data");
        validObjectTags.add("declare");
        validObjectTags.add("dir");
        validObjectTags.add("height");
        validObjectTags.add("hspace");
        validObjectTags.add("id");
        validObjectTags.add("lang");
        validObjectTags.add("name");
        validObjectTags.add("standby");
        validObjectTags.add("style");
        validObjectTags.add("tabindex");
        validObjectTags.add("title");
        validObjectTags.add("type");
        validObjectTags.add("usemap");
        validObjectTags.add("vspace");
        validObjectTags.add("width");
        validEmbedTags = new ArrayList();
        validEmbedTags.add("align");
        validEmbedTags.add("autostart");
        validEmbedTags.add("bgcolor");
        validEmbedTags.add("controller");
        validEmbedTags.add("controls");
        validEmbedTags.add("console");
        validEmbedTags.add("class");
        validEmbedTags.add("height");
        validEmbedTags.add("href");
        validEmbedTags.add("id");
        validEmbedTags.add("name");
        validEmbedTags.add("pluginspage");
        validEmbedTags.add("pluginurl");
        validEmbedTags.add("quality");
        validEmbedTags.add("showcontrols");
        validEmbedTags.add("showtracker");
        validEmbedTags.add("showdisplay");
        validEmbedTags.add("src");
        validEmbedTags.add("target");
        validEmbedTags.add("type");
        validEmbedTags.add("width");
        validEmbedTags.add("flashvars");
        validParamTags = new ArrayList();
        validParamTags.add("animationatStart");
        validParamTags.add("autoStart");
        validParamTags.add("controller");
        validParamTags.add("controls");
        validParamTags.add("console");
        validParamTags.add("data");
        validParamTags.add("fileName");
        validParamTags.add("href");
        validParamTags.add("loop");
        validParamTags.add("menu");
        validParamTags.add("movie");
        validParamTags.add("quality");
        validParamTags.add("scale");
        validParamTags.add("showControls");
        validParamTags.add("src");
        validParamTags.add("target");
        validParamTags.add("transparentatStart");
        validParamTags.add("type");
        validParamTags.add("flashvars");
    }
}

