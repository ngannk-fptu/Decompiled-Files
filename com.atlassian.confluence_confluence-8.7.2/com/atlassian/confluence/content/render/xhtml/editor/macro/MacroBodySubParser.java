/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MacroBodyType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEntityExpander;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

class MacroBodySubParser {
    private static final QName BR_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "br");
    private static final QName PRE_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "pre");
    private final XMLOutputFactory xmlFragmentOutputFactory;
    private final XMLEventFactory xmlEventFactory;
    private final XmlEntityExpander xmlEntityExpander;

    MacroBodySubParser(XMLOutputFactory xmlFragmentOutputFactory, XMLEventFactory xmlEventFactory, XmlEntityExpander xmlEntityExpander) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventFactory = xmlEventFactory;
        this.xmlEntityExpander = xmlEntityExpander;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parse(XMLEventReader reader, MacroDefinitionBuilder macroDefinitionBuilder, FragmentTransformer fragmentTransformer, ConversionContext conversionContext, MacroBodyType macroBodyType) throws XhtmlException {
        block11: {
            if (!reader.hasNext()) {
                return;
            }
            try {
                block12: {
                    if (macroBodyType != MacroBodyType.PLAIN_TEXT) break block12;
                    StringWriter preformattedBody = new StringWriter();
                    XMLEventWriter xmlEventWriter = null;
                    try {
                        xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(preformattedBody);
                        boolean contentBlockWasClosed = false;
                        while (reader.hasNext()) {
                            XMLEvent nextBodyEvent = reader.nextEvent();
                            if (nextBodyEvent.isCharacters()) {
                                if (contentBlockWasClosed && StringUtils.isWhitespace((CharSequence)nextBodyEvent.asCharacters().getData())) continue;
                                xmlEventWriter.add(nextBodyEvent);
                                continue;
                            }
                            if (nextBodyEvent.isStartElement()) {
                                if (PRE_ELEMENT.equals(nextBodyEvent.asStartElement().getName()) && contentBlockWasClosed) {
                                    xmlEventWriter.add(this.xmlEventFactory.createCharacters("\n"));
                                }
                                if (BR_ELEMENT.equals(nextBodyEvent.asStartElement().getName())) {
                                    xmlEventWriter.add(this.xmlEventFactory.createCharacters("\n"));
                                }
                                contentBlockWasClosed = false;
                                continue;
                            }
                            if (nextBodyEvent.isEndElement()) {
                                if (!PRE_ELEMENT.equals(nextBodyEvent.asEndElement().getName())) continue;
                                contentBlockWasClosed = true;
                                continue;
                            }
                            if (!nextBodyEvent.isEntityReference()) continue;
                            xmlEventWriter.add(nextBodyEvent);
                        }
                    }
                    catch (Throwable throwable) {
                        StaxUtils.closeQuietly(xmlEventWriter);
                        throw throwable;
                    }
                    StaxUtils.closeQuietly(xmlEventWriter);
                    String expandedBody = this.xmlEntityExpander.expandEntities(preformattedBody.toString());
                    macroDefinitionBuilder.withMacroBody(new PlainTextMacroBody(expandedBody));
                    break block11;
                }
                macroDefinitionBuilder.withMacroBody(RichTextMacroBody.withStorage(fragmentTransformer.transform(reader, fragmentTransformer, conversionContext)));
            }
            catch (XMLStreamException ex) {
                throw new XhtmlException(ex);
            }
        }
    }
}

