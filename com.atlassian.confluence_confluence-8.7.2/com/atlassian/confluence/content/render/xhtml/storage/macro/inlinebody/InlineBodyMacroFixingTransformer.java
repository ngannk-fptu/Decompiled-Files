/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.exc.WstxLazyException
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroUtil;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.InlineMacroFragment;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.ParagraphFragment;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.ParagraphFragmentsBuffer;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.macro.Macro;
import com.ctc.wstx.exc.WstxLazyException;
import java.io.Reader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class InlineBodyMacroFixingTransformer
implements Transformer {
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLEventFactory xmlEventFactory;

    public InlineBodyMacroFixingTransformer(XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, XMLEventFactoryProvider xmlEventFactoryProvider) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlEventFactory = xmlEventFactoryProvider.getXmlEventFactory();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        StringWriter result = new StringWriter();
        ResettableXmlEventReader resettableReader = null;
        XMLEventWriter xmlEventWriter = null;
        ParagraphFragmentsBuffer paragraphFragmentsBuffer = new ParagraphFragmentsBuffer(this.xmlEventFactory);
        try {
            resettableReader = new ResettableXmlEventReader(this.xmlEventReaderFactory.createStorageXmlEventReader(input));
            xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(result);
            while (resettableReader.hasNext()) {
                XMLEvent xmlEvent = resettableReader.peek();
                if (xmlEvent.isStartElement() && "p".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
                    XMLEventReader paragraphFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(resettableReader);
                    try {
                        ParagraphFragment p = new ParagraphFragment(paragraphFragmentReader);
                        if (p.isAutoCursorTarget()) continue;
                        paragraphFragmentsBuffer.add(p);
                        continue;
                    }
                    finally {
                        StaxUtils.closeQuietly(paragraphFragmentReader);
                        continue;
                    }
                }
                if (this.isInlineBodyMacroFragment(resettableReader)) {
                    XMLEventReader inlineMacroFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(resettableReader);
                    try {
                        paragraphFragmentsBuffer.add(new InlineMacroFragment(inlineMacroFragmentReader, this.xmlEventReaderFactory));
                        continue;
                    }
                    finally {
                        StaxUtils.closeQuietly(inlineMacroFragmentReader);
                        continue;
                    }
                }
                this.flushParagraphFragments(xmlEventWriter, paragraphFragmentsBuffer);
                xmlEventWriter.add(resettableReader.nextEvent());
            }
            this.flushParagraphFragments(xmlEventWriter, paragraphFragmentsBuffer);
        }
        catch (WstxLazyException e) {
            try {
                throw StaxUtils.convertToXhtmlException(e);
                catch (XMLStreamException e2) {
                    throw new XhtmlException("Error occurred while reading stream", e2);
                }
                catch (RuntimeException re) {
                    throw StaxUtils.processWrappedWstxExceptionOrTrowMapped(re, XhtmlException::new);
                }
                catch (Exception e3) {
                    throw new XhtmlException(e3);
                }
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(resettableReader);
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(resettableReader);
        StaxUtils.closeQuietly(xmlEventWriter);
        return result.toString();
    }

    private void flushParagraphFragments(XMLEventWriter xmlEventWriter, ParagraphFragmentsBuffer paragraphFragments) throws XMLStreamException {
        for (XMLEvent event : paragraphFragments.flush()) {
            xmlEventWriter.add(event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean isInlineBodyMacroFragment(ResettableXmlEventReader reader) throws XMLStreamException {
        int eventPosition = reader.getCurrentEventPosition();
        try {
            XMLEvent firstEvent = reader.peek();
            if (!firstEvent.isStartElement() || !StorageMacroUtil.isMacroElement(firstEvent.asStartElement())) {
                boolean bl = false;
                return bl;
            }
            XMLEventReader fragmentEventReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(reader);
            while (fragmentEventReader.hasNext()) {
                XMLEvent xmlEvent = fragmentEventReader.peek();
                if (xmlEvent.isStartElement() && StorageMacroConstants.MACRO_PARAMETER_ELEMENT.equals(xmlEvent.asStartElement().getName()) && "atlassian-macro-output-type".equals(StaxUtils.getAttributeValue(xmlEvent.asStartElement(), StorageMacroConstants.NAME_ATTRIBUTE))) {
                    fragmentEventReader.nextEvent();
                    XMLEvent nextEvent = fragmentEventReader.nextEvent();
                    if (!nextEvent.isCharacters() || !Macro.OutputType.INLINE.name().equals(nextEvent.asCharacters().getData())) continue;
                    boolean bl = true;
                    return bl;
                }
                if (xmlEvent.isStartElement() && StorageMacroConstants.RICH_TEXT_BODY_PARAMETER_ELEMENT.equals(xmlEvent.asStartElement().getName())) {
                    XMLEventReader richTextBodyFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(reader);
                    while (richTextBodyFragmentReader.hasNext()) {
                        richTextBodyFragmentReader.nextEvent();
                    }
                    continue;
                }
                fragmentEventReader.nextEvent();
            }
        }
        finally {
            reader.restoreEventPosition(eventPosition);
        }
        return false;
    }
}

