/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import org.apache.commons.lang.StringUtils;

public class PlaceholderImageRenderer
implements EmbeddedResourceRenderer {
    public static final String ICON_PARAMETER = "com.atlassian.renderer.embedded.placeholder.image.name";
    public static final String DEFAULT_ICON_NAME = "/icons/attachments/image.gif";

    @Override
    public String renderResource(EmbeddedResource resource, RenderContext context) {
        String iconName = (String)context.getParam(ICON_PARAMETER);
        if (iconName == null) {
            iconName = DEFAULT_ICON_NAME;
        }
        String iconPath = context.getImagePath() + iconName;
        String widthValue = resource.getProperties().getProperty("width");
        String widthStr = StringUtils.isBlank((String)widthValue) ? "" : "width=\"" + widthValue + "\" ";
        String heightValue = resource.getProperties().getProperty("height");
        String heightStr = StringUtils.isBlank((String)heightValue) ? "" : "height=\"" + heightValue + "\" ";
        return context.addRenderedContent("<img src=\"" + iconPath + "\" " + widthStr + heightStr + "imagetext=\"" + HtmlEscaper.escapeAll(resource.getOriginalLinkText(), true) + "\"/>");
    }
}

