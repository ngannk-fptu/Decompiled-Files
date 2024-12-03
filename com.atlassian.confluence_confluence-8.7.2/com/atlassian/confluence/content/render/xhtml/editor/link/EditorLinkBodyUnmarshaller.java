/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEntityExpander;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.editor.link.EmptyLinkBodyException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import com.google.common.collect.ImmutableSet;
import java.io.StringWriter;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class EditorLinkBodyUnmarshaller
implements Unmarshaller<LinkBody<?>> {
    private final Unmarshaller<EmbeddedImage> embeddedImageUnmarshaller;
    private final XMLOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlEntityExpander xmlEntityExpander;
    private static final Set<String> PERMITTED_INLINE_ELEMENTS = ImmutableSet.of((Object)"b", (Object)"strong", (Object)"em", (Object)"i", (Object)"code", (Object)"tt", (Object[])new String[]{"sub", "sup", "br", "span"});

    public EditorLinkBodyUnmarshaller(Unmarshaller<EmbeddedImage> embeddedImageUnmarshaller, XMLOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, XmlEntityExpander xmlEntityExpander) {
        this.embeddedImageUnmarshaller = embeddedImageUnmarshaller;
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlEntityExpander = xmlEntityExpander;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public LinkBody unmarshal(XMLEventReader linkFragmentReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            Object elementName;
            boolean hasVisibleContent = false;
            StartElement linkStartElement = linkFragmentReader.peek().asStartElement();
            String defaultAlias = StaxUtils.getAttributeValue(linkStartElement, "data-linked-resource-default-alias");
            XMLEventReader linkBodyEventReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(linkFragmentReader);
            if (!linkBodyEventReader.hasNext()) {
                throw new EmptyLinkBodyException();
            }
            if (linkBodyEventReader.peek().isStartElement() && this.embeddedImageUnmarshaller.handles(linkBodyEventReader.peek().asStartElement(), conversionContext)) {
                EmbeddedImageLinkBody embeddedImageLinkBody = new EmbeddedImageLinkBody(this.embeddedImageUnmarshaller.unmarshal(linkFragmentReader, mainFragmentTransformer, conversionContext));
                return embeddedImageLinkBody;
            }
            StringWriter linkBody = new StringWriter();
            boolean isRichText = false;
            XMLEventWriter linkBodyFragmentWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(linkBody);
            try {
                while (linkBodyEventReader.hasNext()) {
                    XMLEvent linkBodyFragmentEvent = linkBodyEventReader.nextEvent();
                    if (linkBodyFragmentEvent.isCharacters()) {
                        linkBodyFragmentWriter.add(linkBodyFragmentEvent);
                        String text = linkBodyFragmentEvent.asCharacters().getData();
                        if (StringUtils.replaceChars((String)text, (String)"\n\r\t", (String)"").length() <= 0) continue;
                        hasVisibleContent = true;
                        continue;
                    }
                    if (linkBodyFragmentEvent.isEntityReference()) {
                        linkBodyFragmentWriter.add(linkBodyFragmentEvent);
                        hasVisibleContent = true;
                        continue;
                    }
                    if (!linkBodyFragmentEvent.isStartElement() && !linkBodyFragmentEvent.isEndElement() || !PERMITTED_INLINE_ELEMENTS.contains(elementName = linkBodyFragmentEvent.isStartElement() ? linkBodyFragmentEvent.asStartElement().getName().getLocalPart() : linkBodyFragmentEvent.asEndElement().getName().getLocalPart())) continue;
                    isRichText = true;
                    linkBodyFragmentWriter.add(linkBodyFragmentEvent);
                }
            }
            finally {
                StaxUtils.closeQuietly(linkBodyFragmentWriter);
            }
            String linkBodyString = linkBody.toString();
            if (!hasVisibleContent) {
                throw new EmptyLinkBodyException();
            }
            if (isRichText) {
                elementName = new RichTextLinkBody(linkBodyString);
                return elementName;
            }
            String unescapedLinkBody = this.xmlEntityExpander.expandEntities(linkBodyString);
            if (StringUtils.equals((CharSequence)defaultAlias, (CharSequence)unescapedLinkBody)) {
                LinkBody linkBody2 = null;
                return linkBody2;
            }
            PlainTextLinkBody plainTextLinkBody = new PlainTextLinkBody(unescapedLinkBody);
            return plainTextLinkBody;
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        finally {
            StaxUtils.closeQuietly(linkFragmentReader);
        }
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        throw new UnsupportedOperationException();
    }
}

