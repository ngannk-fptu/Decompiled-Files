/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RenderContextHelper;
import com.atlassian.renderer.attachments.RendererAttachment;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.escaper.RenderEscapers;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.macro.basic.validator.ColorStyleValidator;
import com.atlassian.renderer.v2.macro.basic.validator.CssSizeValidator;
import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;
import com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator;
import com.opensymphony.util.TextUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EmbeddedImageRenderer
implements EmbeddedResourceRenderer {
    private static final String BORDERCOLOR_KEY = "bordercolor";
    private static final String BORDER_KEY = "border";
    private static final Map STYLE_ATTRIBUTES_MAPPING = EmbeddedImageRenderer.createStyleAttributeMappings();
    private RendererAttachmentManager attachmentManager;

    private static Map createStyleAttributeMappings() {
        HashMap<String, StyleInformation> result = new HashMap<String, StyleInformation>();
        StyleInformation borderWidthInfo = new StyleInformation(BORDER_KEY, CssSizeValidator.getInstance());
        StyleInformation borderColorInfo = new StyleInformation("border-color", ColorStyleValidator.getInstance());
        result.put(BORDER_KEY, borderWidthInfo);
        result.put(BORDERCOLOR_KEY, borderColorInfo);
        return result;
    }

    public EmbeddedImageRenderer(RendererAttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public RendererAttachmentManager getAttachmentManager() {
        return this.attachmentManager;
    }

    public void setAttachmentManager(RendererAttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        String token;
        EmbeddedImage image = (EmbeddedImage)resource;
        RendererAttachment attachment = null;
        if (!image.isExternal()) {
            try {
                attachment = this.getAttachment(context, resource);
            }
            catch (RuntimeException re) {
                return context.addRenderedContent(RenderUtils.error(re.getMessage()));
            }
        }
        HashMap<Object, Object> imageParams = new HashMap<Object, Object>();
        imageParams.putAll(image.getProperties());
        if (context.isRenderingForWysiwyg()) {
            imageParams.put("imagetext", resource.getOriginalLinkText());
        }
        if (image.isThumbNail()) {
            token = image.isExternal() ? context.addRenderedContent(RenderUtils.error(context, "Can only create thumbnails for attached images", this.originalLink(resource), false)) : (!this.attachmentManager.systemSupportsThumbnailing() ? context.addRenderedContent(RenderUtils.error(context, "This installation can not generate thumbnails: no image support in Java runtime", this.originalLink(resource), false)) : (attachment == null && !context.isRenderingForWysiwyg() ? context.addRenderedContent(RenderUtils.error(context, "Attachment '" + image.getFilename() + "' was not found", this.originalLink(resource), false)) : context.addRenderedContent(this.generateThumbnail(imageParams, attachment, context, image))));
        } else {
            String imageUrl = "";
            if (image.isExternal()) {
                imageUrl = image.getUrl();
            } else if (attachment != null) {
                if (context.getOutputType().equals("word")) {
                    String contextPath = context.getSiteRoot();
                    String domain = context.getBaseUrl();
                    if (contextPath != null && contextPath.length() != 0 && domain.indexOf(contextPath) != -1) {
                        domain = domain.substring(0, domain.indexOf(contextPath));
                    }
                    imageUrl = imageUrl + domain;
                }
                imageUrl = imageUrl + attachment.getSrc();
            }
            try {
                imageUrl = RenderContextHelper.storeEscaperAndCreateTokensIfNeeded(imageUrl, context, RenderEscapers.LINK_RENDERER_ESCAPER);
                token = context.addRenderedContent(this.writeImage("<img src=\"" + HtmlEscaper.escapeAll(imageUrl, true) + "\" " + this.outputParameters(imageParams, context) + "/>", imageParams, context));
            }
            catch (MacroParameterValidationException ex) {
                return context.addRenderedContent(RenderUtils.error(ex.getMessage()));
            }
        }
        return token;
    }

    protected RendererAttachment getAttachment(RenderContext context, EmbeddedResource resource) {
        return this.attachmentManager.getAttachment(context, resource);
    }

    private String originalLink(EmbeddedResource resource) {
        return "!" + resource.getOriginalLinkText() + "!";
    }

    protected RendererAttachment getThumbnail(RendererAttachment attachment, RenderContext context, EmbeddedImage embeddedImage) {
        return this.attachmentManager.getThumbnail(attachment, context, embeddedImage);
    }

    private String generateThumbnail(Map imageParams, RendererAttachment attachment, RenderContext context, EmbeddedImage embeddedImage) {
        if (attachment != null && TextUtils.stringSet((String)attachment.getComment()) && !imageParams.containsKey("title") && !imageParams.containsKey("TITLE")) {
            imageParams.put("title", attachment.getComment());
        }
        RendererAttachment thumb = null;
        if (attachment != null) {
            try {
                thumb = this.getThumbnail(attachment, context, embeddedImage);
            }
            catch (RuntimeException re) {
                return context.addRenderedContent(RenderUtils.error(re.getMessage()));
            }
        }
        try {
            if (thumb != null) {
                return this.writeImage(thumb.wrapGeneratedElement("<img src=\"" + HtmlEscaper.escapeAll(thumb.getSrc(), true) + "\" " + this.outputParameters(imageParams, context) + "/>"), imageParams, context);
            }
            return this.writeImage("<img " + this.outputParameters(imageParams, context) + "/>", imageParams, context);
        }
        catch (MacroParameterValidationException ex) {
            return context.addRenderedContent(RenderUtils.error(ex.getMessage()));
        }
    }

    protected String writeImage(String imageTag, Map<Object, Object> imageParams, RenderContext context) {
        StringBuffer result = new StringBuffer();
        if (!context.isRenderingForWysiwyg()) {
            result.append("<span class=\"image-wrap\"");
            if (imageParams.get("align") != null) {
                result.append(" style=\"");
                String alignmentParam = imageParams.get("align").toString();
                if (alignmentParam.equals("left")) {
                    result.append("float: left");
                } else if (alignmentParam.equals("right")) {
                    result.append("float: right");
                } else if (alignmentParam.equals("center") || alignmentParam.equals("centre")) {
                    result.append("display: block; text-align: center");
                }
                result.append("\"");
            }
            result.append(">");
        }
        result.append(imageTag);
        if (!context.isRenderingForWysiwyg()) {
            result.append("</span>");
        }
        return result.toString();
    }

    private String outputParameters(Map params, RenderContext context) throws MacroParameterValidationException {
        StringBuffer buff = new StringBuffer(20);
        StringBuffer cssbuff = new StringBuffer(30);
        TreeMap sortedParams = new TreeMap(params);
        for (String key : sortedParams.keySet()) {
            if (key.equals("align") || key.equals(BORDERCOLOR_KEY)) continue;
            if (STYLE_ATTRIBUTES_MAPPING.containsKey(key)) {
                StyleInformation info = (StyleInformation)STYLE_ATTRIBUTES_MAPPING.get(key);
                String paramValue = (String)sortedParams.get(key);
                info.valueValidator.assertValid(paramValue);
                if (key.equals(BORDER_KEY)) {
                    if (!(paramValue.contains("px") || paramValue.contains("pt") || paramValue.contains("em"))) {
                        paramValue = paramValue + "px";
                    }
                    paramValue = paramValue + " solid ";
                    if (sortedParams.containsKey(BORDERCOLOR_KEY)) {
                        String borderColor = (String)sortedParams.get(BORDERCOLOR_KEY);
                        StyleInformation borderColorInfo = (StyleInformation)STYLE_ATTRIBUTES_MAPPING.get(BORDERCOLOR_KEY);
                        borderColorInfo.valueValidator.assertValid(borderColor);
                        paramValue = paramValue + borderColor;
                    } else {
                        paramValue = paramValue + "black";
                    }
                }
                cssbuff.append(" ").append(info.cssStyleAttribute).append(": ").append(paramValue).append(";");
                continue;
            }
            String value = (String)sortedParams.get(key);
            value = RenderContextHelper.storeEscaperAndCreateTokensIfNeeded(value, context, RenderEscapers.ATTRIBUTE_RENDERER_ESCAPER);
            String escapedValue = HtmlEscaper.escapeAll(value, true);
            buff.append(HtmlEscaper.escapeAll(key, true)).append("=\"").append(escapedValue).append("\" ");
        }
        if (cssbuff.length() > 0) {
            cssbuff.deleteCharAt(cssbuff.length() - 1);
            if (cssbuff.charAt(0) == ' ') {
                cssbuff.deleteCharAt(0);
            }
            buff.append("style=\"").append(cssbuff).append("\" ");
        }
        return buff.toString();
    }

    private static class StyleInformation {
        String cssStyleAttribute;
        ParameterValidator valueValidator;

        StyleInformation(String attr, ParameterValidator validator) {
            this.cssStyleAttribute = attr;
            this.valueValidator = validator;
        }
    }
}

