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
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerMetricsKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollectors;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MetricsCollectingMarshaller;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.xhtml.api.Link;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewPageTemplateLinkMarshaller
implements Marshaller<Link> {
    private static final ViewLinkMarshallerMetricsKey METRICS_ACCUMULATION_KEY = new ViewLinkMarshallerMetricsKey("pageLink");
    private final ResourceIdentifierResolver<PageTemplateResourceIdentifier, PageTemplate> resourceIdentifierResolver;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final Marshaller<Link> linkBodyMarshaller;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final HrefEvaluator hrefEvaluator;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;

    public ViewPageTemplateLinkMarshaller(ResourceIdentifierResolver<PageTemplateResourceIdentifier, PageTemplate> resourceIdentifierResolver, XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<Link> linkBodyMarshaller, CommonLinkAttributesWriter commonLinkAttributesWriter, HrefEvaluator hrefEvaluator, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.resourceIdentifierResolver = resourceIdentifierResolver;
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
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
        PageTemplate pageTemplate;
        PageTemplateResourceIdentifier ri = (PageTemplateResourceIdentifier)link.getDestinationResourceIdentifier();
        try {
            pageTemplate = this.resourceIdentifierResolver.resolve(ri, conversionContext);
        }
        catch (Exception e) {
            throw new XhtmlException("Unable to resolve PageTemplateResourceIdentifer " + ri.toString());
        }
        return Streamables.from(this.xmlStreamWriterTemplate, new XmlStreamWriterCallback(){
            final Streamable marshalledLinkBody;
            {
                this.marshalledLinkBody = ViewPageTemplateLinkMarshaller.this.linkBodyMarshaller.marshal(link, conversionContext);
            }

            @Override
            public void withStreamWriter(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter) throws XMLStreamException, IOException {
                xmlStreamWriter.writeStartElement("a");
                ViewPageTemplateLinkMarshaller.this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
                xmlStreamWriter.writeAttribute("href", StringEscapeUtils.unescapeHtml4((String)ViewPageTemplateLinkMarshaller.this.hrefEvaluator.createHref(conversionContext, pageTemplate, link.getAnchor())));
                if (ViewPageTemplateLinkMarshaller.this.linkStaxStreamMarshaller != null) {
                    ViewPageTemplateLinkMarshaller.this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
                }
                StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, this.marshalledLinkBody);
                xmlStreamWriter.writeEndElement();
            }
        });
    }
}

