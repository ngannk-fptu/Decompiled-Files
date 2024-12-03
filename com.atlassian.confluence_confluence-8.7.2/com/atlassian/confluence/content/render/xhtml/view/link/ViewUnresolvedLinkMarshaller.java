/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.xhtml.api.Link;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class ViewUnresolvedLinkMarshaller
implements Marshaller<UnresolvedLink> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final StaxStreamMarshaller<ResourceIdentifier> resourceIdentifierStaxStreamMarshaller;
    private final Marshaller<Link> unresolvedLinkBodyMarshaller;
    private final ModelToRenderedClassMapper mapper;

    public ViewUnresolvedLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, StaxStreamMarshaller<ResourceIdentifier> resourceIdentifierStaxStreamMarshaller, Marshaller<Link> unresolvedLinkBodyMarshaller, ModelToRenderedClassMapper mapper) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.resourceIdentifierStaxStreamMarshaller = resourceIdentifierStaxStreamMarshaller;
        this.unresolvedLinkBodyMarshaller = unresolvedLinkBodyMarshaller;
        this.mapper = mapper;
    }

    @Override
    public Streamable marshal(UnresolvedLink unresolvedLink, ConversionContext conversionContext) throws XhtmlException {
        Streamable linkBody = this.unresolvedLinkBodyMarshaller.marshal(unresolvedLink.getDelegate(), conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            String displayClass = this.mapper.getRenderedClass(unresolvedLink);
            if (StringUtils.isNotBlank((CharSequence)displayClass)) {
                xmlStreamWriter.writeAttribute("class", displayClass);
            }
            if (this.resourceIdentifierStaxStreamMarshaller != null) {
                this.resourceIdentifierStaxStreamMarshaller.marshal(unresolvedLink.getDestinationResourceIdentifier(), xmlStreamWriter, conversionContext);
            }
            this.writeAdditionalAttributes(unresolvedLink, xmlStreamWriter, this.unresolvedLinkBodyMarshaller, conversionContext);
            xmlStreamWriter.writeAttribute("href", "#");
            if (linkBody != null) {
                xmlStreamWriter.writeCharacters("");
                xmlStreamWriter.flush();
                linkBody.writeTo(underlyingWriter);
            }
            xmlStreamWriter.writeEndElement();
        });
    }

    protected void writeAdditionalAttributes(UnresolvedLink unresolvedLink, XMLStreamWriter xmlStreamWriter, Marshaller<Link> unresolvedLinkBodyMarshaller, ConversionContext conversionContext) throws XMLStreamException {
    }
}

