/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewUnresolvedLinkMarshaller;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class EditorUnresolvedLinkMarshaller
extends ViewUnresolvedLinkMarshaller {
    public EditorUnresolvedLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, StaxStreamMarshaller<ResourceIdentifier> resourceIdentifierStaxStreamMarshaller, Marshaller<Link> unresolvedLinkBodyMarshaller, ModelToRenderedClassMapper mapper) {
        super(xmlStreamWriterTemplate, resourceIdentifierStaxStreamMarshaller, unresolvedLinkBodyMarshaller, mapper);
    }

    @Override
    protected void writeAdditionalAttributes(UnresolvedLink unresolvedLink, XMLStreamWriter xmlStreamWriter, Marshaller<Link> unresolvedLinkBodyMarshaller, ConversionContext conversionContext) throws XMLStreamException {
        Link link = unresolvedLink.getDelegate();
        if (StringUtils.isNotBlank((CharSequence)link.getTooltip())) {
            xmlStreamWriter.writeAttribute("title", link.getTooltip());
        }
        if (StringUtils.isNotBlank((CharSequence)link.getAnchor())) {
            xmlStreamWriter.writeAttribute("data-anchor", link.getAnchor());
        }
        if (link.getBody() == null || link.getBody() instanceof RichTextLinkBody || link.getBody() instanceof PlainTextLinkBody) {
            String defaultAlias;
            DefaultLink linkCopy = new DefaultLink(link.getDestinationResourceIdentifier(), null);
            try {
                defaultAlias = Streamables.writeToString(unresolvedLinkBodyMarshaller.marshal(linkCopy, conversionContext));
            }
            catch (XhtmlException e) {
                throw new XMLStreamException(e);
            }
            if (StringUtils.isNotBlank((CharSequence)defaultAlias)) {
                xmlStreamWriter.writeAttribute("data-linked-resource-default-alias", defaultAlias);
            }
        }
    }
}

