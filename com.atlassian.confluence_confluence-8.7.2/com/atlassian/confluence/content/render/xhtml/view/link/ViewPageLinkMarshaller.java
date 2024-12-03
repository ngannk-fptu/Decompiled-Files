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
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterCallback;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerMetricsKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollectors;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MetricsCollectingMarshaller;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewPageLinkMarshaller
implements Marshaller<Link> {
    private static final ViewLinkMarshallerMetricsKey METRICS_ACCUMULATION_KEY = new ViewLinkMarshallerMetricsKey("pageLink");
    private final ResourceIdentifierResolver<PageResourceIdentifier, Page> resourceIdentifierResolver;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;
    private final Marshaller<CreatePageLink> createPageLinkMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final HrefEvaluator hrefEvaluator;

    public ViewPageLinkMarshaller(ResourceIdentifierResolver<PageResourceIdentifier, Page> resourceIdentifierResolver, XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<CreatePageLink> createPageLinkMarshaller, CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, HrefEvaluator hrefEvaluator, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.resourceIdentifierResolver = Objects.requireNonNull(resourceIdentifierResolver);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.createPageLinkMarshaller = Objects.requireNonNull(createPageLinkMarshaller);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
    }

    @Override
    public Streamable marshal(Link attachmentLink, ConversionContext conversionContext) throws XhtmlException {
        MarshallerMetricsCollector metricsCollector = MarshallerMetricsCollectors.metricsCollector(conversionContext, METRICS_ACCUMULATION_KEY);
        Marshaller<Link> timedMarshaller = MetricsCollectingMarshaller.forMarshaller(metricsCollector, this::marshalLink);
        return timedMarshaller.marshal(attachmentLink, conversionContext);
    }

    private Streamable marshalLink(final Link link, final ConversionContext conversionContext) throws XhtmlException {
        PageResourceIdentifier pageResourceIdentifier = (PageResourceIdentifier)link.getDestinationResourceIdentifier();
        final Page page = this.resourceIdentifierResolver.resolve(pageResourceIdentifier, conversionContext);
        if (page == null) {
            CreatePageLink createPageLink = link.getBody() == null || link.getBody() instanceof PlainTextLinkBody || link.getBody() instanceof RichTextLinkBody ? new CreatePageLink(link) : new CreatePageLink(new DefaultLink(link.getDestinationResourceIdentifier(), null));
            return this.createPageLinkMarshaller.marshal(createPageLink, conversionContext);
        }
        return Streamables.from(this.xmlStreamWriterTemplate, new XmlStreamWriterCallback(){
            final Streamable marshalledLinkBody;
            {
                this.marshalledLinkBody = ViewPageLinkMarshaller.this.linkBodyMarshaller.marshal(link, conversionContext);
            }

            @Override
            public void withStreamWriter(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter) throws XMLStreamException, IOException {
                xmlStreamWriter.writeStartElement("a");
                ViewPageLinkMarshaller.this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
                xmlStreamWriter.writeAttribute("href", StringEscapeUtils.unescapeHtml4((String)ViewPageLinkMarshaller.this.hrefEvaluator.createHref(conversionContext, page, link.getAnchor())));
                if (ViewPageLinkMarshaller.this.linkStaxStreamMarshaller != null) {
                    ViewPageLinkMarshaller.this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
                }
                StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, this.marshalledLinkBody);
                xmlStreamWriter.writeEndElement();
            }
        });
    }
}

