/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 *  com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.util.ConfluenceRenderUtils
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlFragment
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.multimedia.renderer;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEmbeddedResourceRenderer
implements EmbeddedResourceRenderer {
    public static final String TEMPLATE_PATH = "templates/embeddedobject.vm";
    public static final String WRAPPER_TEMPLATE_PATH = "templates/embeddedobject-wrapper.vm";
    private XsrfTokenGenerator xsrfTokenGenerator;
    protected static final List<String> validObjectTags = new ArrayList<String>();
    private static final String PARAM_WIDTH = "width";
    private static final String PARAM_HEIGHT = "height";
    protected static final List<String> validEmbedTags;
    protected static final List<String> validParamTags;

    protected Map<String, Object> setupObjectProperties(EmbeddedObject emObject, RenderContext context) {
        String defaultHeight;
        String defaultWidth;
        Attachment attachment = emObject.getAttachment();
        if (attachment == null) {
            throw new IllegalArgumentException("Unable to render embedded object: File not found.");
        }
        HashMap<String, Object> objectParams = new HashMap<String, Object>();
        objectParams.putAll(emObject.getProperties());
        objectParams.put("type", emObject.getContentType());
        Object objectUrl = "";
        String attachmentsPath = context.getAttachmentsPath();
        if (attachmentsPath == null) {
            attachmentsPath = ConfluenceRenderUtils.getAttachmentRemotePath((Attachment)attachment);
        }
        if (attachmentsPath != null) {
            objectUrl = HtmlEscaper.escapeAmpersands((String)attachmentsPath, (boolean)true) + "/";
        }
        objectUrl = (String)objectUrl + HtmlEscaper.escapeAmpersands((String)attachment.getFileName(), (boolean)true);
        objectParams.put("object", objectUrl);
        objectParams.put("src", objectUrl);
        objectParams.put("data", objectUrl);
        this.refineParams(attachment, objectParams);
        if (StringUtils.isEmpty((CharSequence)((String)objectParams.get(PARAM_WIDTH))) && StringUtils.isNotEmpty((CharSequence)(defaultWidth = this.getDefaultWidth()))) {
            objectParams.put(PARAM_WIDTH, defaultWidth);
        }
        if (StringUtils.isEmpty((CharSequence)((String)objectParams.get(PARAM_HEIGHT))) && StringUtils.isNotEmpty((CharSequence)(defaultHeight = this.getDefaultHeight()))) {
            objectParams.put(PARAM_HEIGHT, defaultHeight);
        }
        return objectParams;
    }

    public String renderResource(EmbeddedObject resource, RenderContext context) {
        Map<String, Object> contextMap = this.setupObjectProperties(resource, context);
        Map wrapperContextMap = MacroUtils.defaultVelocityContext();
        wrapperContextMap.put("properties", contextMap);
        String renderedObjectHtml = this.renderEmbeddedObject(resource, wrapperContextMap);
        wrapperContextMap.put("embeddedObject", new HtmlFragment((Object)renderedObjectHtml));
        return VelocityUtils.getRenderedTemplate((String)WRAPPER_TEMPLATE_PATH, (Map)wrapperContextMap);
    }

    protected String renderEmbeddedObject(EmbeddedObject resource, Map<String, Object> wrapperContextMap) {
        wrapperContextMap.put("validObjectTags", validObjectTags);
        wrapperContextMap.put("validEmbedTags", validEmbedTags);
        wrapperContextMap.put("validParamTags", validParamTags);
        wrapperContextMap.put("attachmentDownloadPath", resource.getAttachment().getDownloadPath());
        return VelocityUtils.getRenderedTemplate((String)TEMPLATE_PATH, wrapperContextMap);
    }

    protected abstract Map<String, Object> refineParams(Attachment var1, Map<String, Object> var2);

    protected String getDefaultWidth() {
        return null;
    }

    protected String getDefaultHeight() {
        return null;
    }

    public void setXsrfTokenGenerator(XsrfTokenGenerator xsrfTokenGenerator) {
        this.xsrfTokenGenerator = xsrfTokenGenerator;
    }

    protected XsrfTokenGenerator getXsrfTokenGenerator() {
        return this.xsrfTokenGenerator;
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
        validObjectTags.add(PARAM_HEIGHT);
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
        validObjectTags.add(PARAM_WIDTH);
        validEmbedTags = new ArrayList<String>();
        validEmbedTags.add("align");
        validEmbedTags.add("play");
        validEmbedTags.add("autostart");
        validEmbedTags.add("autoplay");
        validEmbedTags.add("bgcolor");
        validEmbedTags.add("controller");
        validEmbedTags.add("controls");
        validEmbedTags.add("console");
        validEmbedTags.add("class");
        validEmbedTags.add(PARAM_HEIGHT);
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
        validEmbedTags.add("scale");
        validEmbedTags.add(PARAM_WIDTH);
        validParamTags = new ArrayList<String>();
        validParamTags.add("animationatStart");
        validParamTags.add("play");
        validParamTags.add("autostart");
        validParamTags.add("autoplay");
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
    }
}

