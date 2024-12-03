/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayout;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCell;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCellType;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSection;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.ArrayList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StorageLegacyPageLayoutUnMarshaller
implements Unmarshaller<PageLayout> {
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public StorageLegacyPageLayoutUnMarshaller(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return "div".equals(startElement.getName().getLocalPart()) && this.matchesContentLayout(startElement);
    }

    @Override
    public PageLayout unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            ArrayList<PageLayoutSection> sections = new ArrayList<PageLayoutSection>();
            while (xmlEventReader.hasNext()) {
                XMLEvent nextEvent = xmlEventReader.peek();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (this.matchesContentLayout(startElement)) {
                        xmlEventReader.nextEvent();
                        continue;
                    }
                    if (this.matchesColumnLayout(startElement) || this.matchesHeaderOrFooter(startElement)) {
                        XMLEventReader sectionReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(xmlEventReader);
                        sections.add(this.handleSection(sectionReader, mainFragmentTransformer, conversionContext));
                        continue;
                    }
                    XMLEventReader boundedCopyOfReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                    sections.add(new PageLayoutSection(this.handleCell(PageLayoutCellType.NORMAL, boundedCopyOfReader, mainFragmentTransformer, conversionContext)));
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

    private boolean matchesContentLayout(StartElement startElement) {
        return StaxUtils.hasClass(startElement, "contentLayout");
    }

    private boolean matchesColumnLayout(StartElement element) {
        return StaxUtils.hasClass(element, "columnLayout");
    }

    private boolean matchesHeaderOrFooter(StartElement element) {
        return StaxUtils.hasClass(element, "header") || StaxUtils.hasClass(element, "footer");
    }

    private PageLayoutSection handleSection(XMLEventReader sectionReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        ArrayList<PageLayoutCell> cells = new ArrayList<PageLayoutCell>();
        while (sectionReader.hasNext()) {
            XMLEvent nextEvent = sectionReader.peek();
            if (nextEvent.isStartElement()) {
                if (this.matchesAnyCell(nextEvent.asStartElement())) {
                    XMLEventReader cellReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(sectionReader);
                    PageLayoutCellType cellType = this.getCellTypeFromXHTMLAttribute(nextEvent.asStartElement());
                    PageLayoutCell cell = this.handleCell(cellType, cellReader, fragmentTransformer, conversionContext);
                    cells.add(cell);
                    continue;
                }
                cells.add(this.handleCell(PageLayoutCellType.NORMAL, sectionReader, fragmentTransformer, conversionContext));
                continue;
            }
            sectionReader.nextEvent();
        }
        return new PageLayoutSection(cells);
    }

    private boolean matchesAnyCell(StartElement startElement) {
        return this.matchesCell(startElement) || this.matchesInnerCell(startElement);
    }

    private boolean matchesCell(StartElement startElement) {
        return StaxUtils.hasClass(startElement, "cell");
    }

    private PageLayoutCell handleCell(PageLayoutCellType cellType, XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        ArrayList<Streamable> bodyParts = new ArrayList<Streamable>();
        boolean outerCellOpen = false;
        boolean innerCellOpen = false;
        while (xmlEventReader.hasNext()) {
            XMLEvent nextEvent = xmlEventReader.peek();
            if (nextEvent.isStartElement() && this.matchesCell(nextEvent.asStartElement())) {
                if (outerCellOpen) {
                    return new PageLayoutCell(cellType, Streamables.combine(bodyParts));
                }
                outerCellOpen = true;
                xmlEventReader.nextEvent();
                continue;
            }
            if (nextEvent.isStartElement() && this.matchesInnerCell(nextEvent.asStartElement())) {
                if (innerCellOpen) {
                    return new PageLayoutCell(cellType, Streamables.combine(bodyParts));
                }
                innerCellOpen = true;
                xmlEventReader.nextEvent();
                continue;
            }
            if (nextEvent.isCharacters() && nextEvent.asCharacters().isWhiteSpace()) {
                xmlEventReader.nextEvent();
                continue;
            }
            if (nextEvent.isEndElement()) {
                xmlEventReader.nextEvent();
                continue;
            }
            XMLEventReader boundedCopyOfReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
            bodyParts.add(mainFragmentTransformer.transform(boundedCopyOfReader, mainFragmentTransformer, conversionContext));
        }
        return new PageLayoutCell(cellType, Streamables.combine(bodyParts));
    }

    private boolean matchesInnerCell(StartElement startElement) {
        return StaxUtils.hasClass(startElement, "innerCell");
    }

    private PageLayoutCellType getCellTypeFromXHTMLAttribute(StartElement element) {
        if (StaxUtils.hasClass(element, "aside")) {
            return PageLayoutCellType.ASIDE;
        }
        if (StaxUtils.hasClass(element, "sidebars")) {
            return PageLayoutCellType.SIDEBARS;
        }
        return PageLayoutCellType.NORMAL;
    }
}

