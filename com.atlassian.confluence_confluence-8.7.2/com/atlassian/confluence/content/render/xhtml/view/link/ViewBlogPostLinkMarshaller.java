/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Objects;
import org.apache.commons.lang3.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewBlogPostLinkMarshaller
implements Marshaller<Link> {
    private final ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> blogPostResourceIdentifierResolver;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;
    private final Marshaller<Link> unresolvedLinkMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final HrefEvaluator hrefEvaluator;

    public ViewBlogPostLinkMarshaller(ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> blogPostResourceIdentifierResolver, XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<Link> unresolvedLinkMarshaller, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, HrefEvaluator hrefEvaluator, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.blogPostResourceIdentifierResolver = Objects.requireNonNull(blogPostResourceIdentifierResolver);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        BlogPostResourceIdentifier blogPostResourceIdentifier = (BlogPostResourceIdentifier)link.getDestinationResourceIdentifier();
        BlogPost blogPost = this.blogPostResourceIdentifierResolver.resolve(blogPostResourceIdentifier, conversionContext);
        if (blogPost == null) {
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(link), conversionContext);
        }
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(link, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", StringEscapeUtils.unescapeHtml4((String)this.hrefEvaluator.createHref(conversionContext, blogPost, link.getAnchor())));
            if (this.linkStaxStreamMarshaller != null) {
                this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
            }
            StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
            xmlStreamWriter.writeEndElement();
        });
    }
}

