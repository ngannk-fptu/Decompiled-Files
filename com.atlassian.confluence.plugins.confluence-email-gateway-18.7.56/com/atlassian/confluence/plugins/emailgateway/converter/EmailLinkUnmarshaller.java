/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.xhtml.api.Link
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.emailgateway.api.LinkFactory;
import com.atlassian.confluence.xhtml.api.Link;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class EmailLinkUnmarshaller
implements Unmarshaller<Link> {
    public EmailLinkUnmarshaller(MarshallingRegistry registry) {
        registry.register((Unmarshaller)this, Link.class, MarshallingType.EMAIL);
    }

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return "a".equalsIgnoreCase(startElement.getName().getLocalPart()) && startElement.getAttributeByName(new QName("href")) != null;
    }

    public Link unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        String href = this.getLinkHref(xmlEventReader);
        return LinkFactory.newURLLink(href);
    }

    private String getLinkHref(XMLEventReader xmlEventReader) {
        StartElement linkStartElement;
        try {
            linkStartElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return linkStartElement.getAttributeByName(new QName("href")).getValue();
    }
}

