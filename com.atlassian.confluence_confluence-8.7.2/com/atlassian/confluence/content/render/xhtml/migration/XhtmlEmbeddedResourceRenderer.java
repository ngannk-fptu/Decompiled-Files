/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.embedded.EmbeddedImage
 *  com.atlassian.renderer.embedded.EmbeddedResource
 *  com.atlassian.renderer.embedded.EmbeddedResourceRenderer
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.EmbeddedResourceResolver;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.renderer.BlogPostReferenceParser;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import java.text.ParseException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class XhtmlEmbeddedResourceRenderer
implements EmbeddedResourceRenderer {
    private final Marshaller marshaller;
    private final EmbeddedResourceResolver embeddedResourceResolver;

    public XhtmlEmbeddedResourceRenderer(Marshaller marshaller, EmbeddedResourceResolver embeddedResourceResolver) {
        this.marshaller = marshaller;
        this.embeddedResourceResolver = embeddedResourceResolver;
    }

    public String renderResource(EmbeddedResource v2EmbeddedResource, RenderContext renderContext) {
        String alignment;
        String border;
        NamedResourceIdentifier resourceIdentifier;
        if (v2EmbeddedResource.isExternal()) {
            String url = v2EmbeddedResource.getUrl();
            resourceIdentifier = new UrlResourceIdentifier(url);
        } else {
            if (StringUtils.isBlank((CharSequence)v2EmbeddedResource.getPage()) && !StringUtils.isBlank((CharSequence)v2EmbeddedResource.getSpace())) {
                throw new RuntimeException("The embedded resource specifies a space key but no content title.  Space: " + v2EmbeddedResource.getSpace() + ", Url: " + v2EmbeddedResource.getUrl());
            }
            if (StringUtils.isBlank((CharSequence)v2EmbeddedResource.getPage()) && StringUtils.isBlank((CharSequence)v2EmbeddedResource.getSpace())) {
                resourceIdentifier = new AttachmentResourceIdentifier(null, v2EmbeddedResource.getFilename());
            } else {
                AttachmentContainerResourceIdentifier container = null;
                try {
                    BlogPostReferenceParser parser = new BlogPostReferenceParser(v2EmbeddedResource.getPage());
                    container = new BlogPostResourceIdentifier(v2EmbeddedResource.getSpace(), parser.getEntityName(), parser.getCalendarPostingDay());
                }
                catch (ParseException ex) {
                    container = new PageResourceIdentifier(v2EmbeddedResource.getSpace(), v2EmbeddedResource.getPage());
                }
                resourceIdentifier = new AttachmentResourceIdentifier(container, v2EmbeddedResource.getFilename());
            }
        }
        DefaultEmbeddedImage xhtmlEmbeddedResource = new DefaultEmbeddedImage(resourceIdentifier);
        Properties props = v2EmbeddedResource.getProperties();
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("class"))) {
            xhtmlEmbeddedResource.setHtmlClass(props.getProperty("class"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("src"))) {
            xhtmlEmbeddedResource.setSource(props.getProperty("src"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("alt"))) {
            xhtmlEmbeddedResource.setAlternativeText(props.getProperty("alt"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("height"))) {
            xhtmlEmbeddedResource.setHeight(props.getProperty("height"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("width"))) {
            xhtmlEmbeddedResource.setWidth(props.getProperty("width"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("title"))) {
            xhtmlEmbeddedResource.setTitle(props.getProperty("title"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("hspace"))) {
            xhtmlEmbeddedResource.setHspace(props.getProperty("hspace"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("vspace"))) {
            xhtmlEmbeddedResource.setVspace(props.getProperty("vspace"));
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("border")) && !"0".equals(border = props.getProperty("border"))) {
            xhtmlEmbeddedResource.setBorder(true);
        }
        if (StringUtils.isNotBlank((CharSequence)props.getProperty("align")) && !"absmiddle".equals(alignment = props.getProperty("align"))) {
            xhtmlEmbeddedResource.setAlignment(alignment);
        }
        if (v2EmbeddedResource instanceof EmbeddedImage && ((EmbeddedImage)v2EmbeddedResource).isThumbNail()) {
            xhtmlEmbeddedResource.setThumbnail(true);
        }
        try {
            return Streamables.writeToString(this.marshaller.marshal(xhtmlEmbeddedResource, new DefaultConversionContext(renderContext)));
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
    }
}

