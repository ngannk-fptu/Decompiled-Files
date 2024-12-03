/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.renderer.PageContext;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceContentMacroParameterParser {
    public ResourceIdentifier parse(String linkText, PageContext pageContext) {
        String destinationTitle;
        String spaceKey;
        if (linkText == null) {
            return null;
        }
        int index = linkText.indexOf(58);
        if (index >= 0) {
            spaceKey = linkText.substring(0, index);
            destinationTitle = linkText.substring(index + 1);
        } else {
            spaceKey = pageContext.getSpaceKey();
            destinationTitle = linkText;
        }
        if (BlogPostResourceIdentifier.isBlogPostLink(destinationTitle)) {
            try {
                return BlogPostResourceIdentifier.newInstanceFromLink(destinationTitle, spaceKey);
            }
            catch (ParseException e) {
                return null;
            }
        }
        if (StringUtils.isNotBlank((CharSequence)destinationTitle)) {
            return new PageResourceIdentifier(spaceKey, destinationTitle);
        }
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            return new SpaceResourceIdentifier(spaceKey);
        }
        return null;
    }
}

