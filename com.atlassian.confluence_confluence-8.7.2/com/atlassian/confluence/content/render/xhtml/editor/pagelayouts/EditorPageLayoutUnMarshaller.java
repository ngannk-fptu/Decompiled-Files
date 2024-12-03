/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.editor.pagelayouts.EditorPageLayoutConstants;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayout;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCell;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCellType;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSection;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.ArrayList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class EditorPageLayoutUnMarshaller
implements Unmarshaller<PageLayout> {
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public EditorPageLayoutUnMarshaller(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return "div".equals(startElement.getName().getLocalPart()) && StaxUtils.hasClass(startElement, "contentLayout2");
    }

    @Override
    public PageLayout unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            xmlEventReader.nextEvent();
            ArrayList<PageLayoutSection> sections = new ArrayList<PageLayoutSection>();
            while (xmlEventReader.hasNext()) {
                XMLEvent nextEvent = xmlEventReader.peek();
                if (nextEvent.isStartElement()) {
                    if (StaxUtils.hasClass(nextEvent.asStartElement(), "columnLayout")) {
                        XMLEventReader sectionReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(xmlEventReader);
                        PageLayoutSection section = this.handleSection(sectionReader, mainFragmentTransformer, conversionContext);
                        sections.add(section);
                        sectionReader.close();
                        continue;
                    }
                    PageLayoutSection section = this.handleSection(xmlEventReader, mainFragmentTransformer, conversionContext);
                    sections.add(section);
                    continue;
                }
                xmlEventReader.nextEvent();
            }
            return new PageLayout(sections);
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException(ex);
        }
    }

    private PageLayoutSection handleSection(XMLEventReader sectionReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        ArrayList<PageLayoutCell> cells = new ArrayList<PageLayoutCell>();
        while (sectionReader.hasNext()) {
            XMLEvent nextEvent = sectionReader.peek();
            if (nextEvent.isStartElement()) {
                if (StaxUtils.hasClass(nextEvent.asStartElement(), "cell")) {
                    XMLEventReader cellReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(sectionReader);
                    PageLayoutCellType cellType = this.getCellTypeFromXHTMLAttribute(nextEvent.asStartElement());
                    PageLayoutCell cell = this.handleCell(cellType, cellReader, fragmentTransformer, conversionContext);
                    cells.add(cell);
                    cellReader.close();
                    continue;
                }
                PageLayoutCell cell = this.handleCell(PageLayoutCellType.NORMAL, sectionReader, fragmentTransformer, conversionContext);
                cells.add(cell);
                continue;
            }
            sectionReader.nextEvent();
        }
        return new PageLayoutSection(cells);
    }

    private PageLayoutCell handleCell(PageLayoutCellType cellType, XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        while (xmlEventReader.hasNext()) {
            XMLEvent nextEvent = xmlEventReader.peek();
            if (nextEvent.isStartElement()) {
                if (StaxUtils.hasClass(nextEvent.asStartElement(), "innerCell")) {
                    XMLEventReader bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(xmlEventReader);
                    Streamable body = mainFragmentTransformer.transform(bodyReader, mainFragmentTransformer, conversionContext);
                    return new PageLayoutCell(cellType, body);
                }
                return this.getContentAsCell(cellType, xmlEventReader, mainFragmentTransformer, conversionContext);
            }
            xmlEventReader.nextEvent();
        }
        return new PageLayoutCell(cellType, Streamables.from(""));
    }

    private PageLayoutCell getContentAsCell(PageLayoutCellType cellType, XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        ArrayList<Streamable> body = new ArrayList<Streamable>();
        while (xmlEventReader.hasNext()) {
            XMLEvent nextEvent = xmlEventReader.peek();
            if (nextEvent.isStartElement()) {
                XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                Streamable fragment = mainFragmentTransformer.transform(fragmentReader, mainFragmentTransformer, conversionContext);
                body.add(fragment);
                continue;
            }
            xmlEventReader.nextEvent();
        }
        return new PageLayoutCell(cellType, Streamables.combine(body));
    }

    private PageLayoutCellType getCellTypeFromXHTMLAttribute(StartElement element) {
        String cellDataTypeValue;
        Attribute dataTypeAttribute = element.getAttributeByName(EditorPageLayoutConstants.PAGE_LAYOUT_CELL_XHTML_DATATYPE_ATTR_NAME);
        if (dataTypeAttribute != null && (cellDataTypeValue = dataTypeAttribute.getValue()) != null) {
            if (cellDataTypeValue.toLowerCase().equals("aside")) {
                return PageLayoutCellType.ASIDE;
            }
            if (cellDataTypeValue.toLowerCase().equals("sidebars")) {
                return PageLayoutCellType.SIDEBARS;
            }
        }
        return PageLayoutCellType.NORMAL;
    }
}

