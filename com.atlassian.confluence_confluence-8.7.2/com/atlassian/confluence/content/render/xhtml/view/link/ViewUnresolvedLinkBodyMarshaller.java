/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class ViewUnresolvedLinkBodyMarshaller
implements Marshaller<Link> {
    private final Marshaller<EmbeddedImage> embeddedImageMarshaller;

    public ViewUnresolvedLinkBodyMarshaller(Marshaller<EmbeddedImage> embeddedImageMarshaller) {
        this.embeddedImageMarshaller = embeddedImageMarshaller;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        if (link.getBody() instanceof EmbeddedImageLinkBody) {
            return this.embeddedImageMarshaller.marshal(((EmbeddedImageLinkBody)link.getBody()).getBody(), conversionContext);
        }
        if (link.getBody() instanceof RichTextLinkBody) {
            return Streamables.from(((RichTextLinkBody)link.getBody()).getBody());
        }
        if (link.getBody() == null || link.getBody() instanceof PlainTextLinkBody) {
            PlainTextLinkBody linkBody = (PlainTextLinkBody)link.getBody();
            if (linkBody == null || StringUtils.isBlank((CharSequence)linkBody.getBody())) {
                Object linkText = "";
                if (link.getDestinationResourceIdentifier() instanceof PageResourceIdentifier) {
                    linkText = ((PageResourceIdentifier)link.getDestinationResourceIdentifier()).getTitle();
                } else if (link.getDestinationResourceIdentifier() instanceof BlogPostResourceIdentifier) {
                    linkText = ((BlogPostResourceIdentifier)link.getDestinationResourceIdentifier()).getTitle();
                } else if (link.getDestinationResourceIdentifier() instanceof SpaceResourceIdentifier) {
                    linkText = ((SpaceResourceIdentifier)link.getDestinationResourceIdentifier()).getSpaceKey();
                } else if (link.getDestinationResourceIdentifier() instanceof AttachmentResourceIdentifier) {
                    linkText = ((AttachmentResourceIdentifier)link.getDestinationResourceIdentifier()).getFilename();
                } else if (link.getDestinationResourceIdentifier() instanceof ContentEntityResourceIdentifier) {
                    linkText = String.valueOf(((ContentEntityResourceIdentifier)link.getDestinationResourceIdentifier()).getContentId());
                } else if (link.getDestinationResourceIdentifier() instanceof ShortcutResourceIdentifier) {
                    ShortcutResourceIdentifier shortcut = (ShortcutResourceIdentifier)link.getDestinationResourceIdentifier();
                    linkText = shortcut.getShortcutParameter() + "@" + shortcut.getShortcutKey();
                } else if (link.getDestinationResourceIdentifier() instanceof UserResourceIdentifier) {
                    linkText = "~" + ((UserResourceIdentifier)link.getDestinationResourceIdentifier()).getUnresolvedUsernameResourceIdentifier().getUsername();
                }
                linkBody = new PlainTextLinkBody((String)linkText);
            }
            return Streamables.from(StringEscapeUtils.escapeHtml4((String)linkBody.getBody()));
        }
        throw new UnsupportedOperationException("Unsupported link body: " + link.getBody());
    }
}

