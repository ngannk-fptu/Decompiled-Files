/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableMap
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
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSectionLayoutType;
import com.atlassian.confluence.content.render.xhtml.storage.pagelayouts.StoragePageLayoutConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StoragePageLayoutUnMarshaller
implements Unmarshaller<PageLayout> {
    public static final ImmutableMap<String, PageLayoutSectionLayoutType> STRING_TO_SECTION_TYPE_MAP = ImmutableMap.builder().put((Object)"single", (Object)PageLayoutSectionLayoutType.SINGLE).put((Object)"two_equal", (Object)PageLayoutSectionLayoutType.TWO_EQUAL).put((Object)"two_left_sidebar", (Object)PageLayoutSectionLayoutType.TWO_LEFT_SIDEBAR).put((Object)"two_right_sidebar", (Object)PageLayoutSectionLayoutType.TWO_RIGHT_SIDEBAR).put((Object)"three_equal", (Object)PageLayoutSectionLayoutType.THREE_EQUAL).put((Object)"three_with_sidebars", (Object)PageLayoutSectionLayoutType.THREE_WITH_SIDEBARS).build();
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public StoragePageLayoutUnMarshaller(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return startElementEvent.getName().equals(StoragePageLayoutConstants.PAGE_LAYOUT_ELEMENT_QNAME);
    }

    @Override
    public PageLayout unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            ArrayList<PageLayoutSection> sections = new ArrayList<PageLayoutSection>();
            PageLayoutSectionBuilder sectionBuilder = null;
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.peek();
                if (nextEvent.isStartElement() && StoragePageLayoutConstants.PAGE_LAYOUT_SECTION_ELEMENT_QNAME.equals(nextEvent.asStartElement().getName())) {
                    sectionBuilder = new PageLayoutSectionBuilder(this.getSectionLayoutTypeFromElement(nextEvent.asStartElement()));
                }
                if (nextEvent.isEndElement() && StoragePageLayoutConstants.PAGE_LAYOUT_SECTION_ELEMENT_QNAME.equals(nextEvent.asEndElement().getName())) {
                    sections.add(sectionBuilder.build());
                    sectionBuilder = null;
                }
                if (nextEvent.isStartElement() && StoragePageLayoutConstants.PAGE_LAYOUT_CELL_ELEMENT_QNAME.equals(nextEvent.asStartElement().getName())) {
                    Streamable body = mainFragmentTransformer.transform(this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader), mainFragmentTransformer, conversionContext);
                    sectionBuilder.addCellBody(body);
                    continue;
                }
                reader.nextEvent();
            }
            PageLayout pageLayout = new PageLayout(sections);
            return pageLayout;
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException(ex);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
    }

    private Option<PageLayoutSectionLayoutType> getSectionLayoutTypeFromElement(StartElement startElementSection) {
        Attribute attribute = startElementSection.getAttributeByName(StoragePageLayoutConstants.PAGE_LAYOUT_SECTION_TYPE_ATTRIBUTE_QNAME);
        if (attribute != null) {
            return Option.option((Object)((Object)((PageLayoutSectionLayoutType)((Object)STRING_TO_SECTION_TYPE_MAP.get((Object)attribute.getValue())))));
        }
        return Option.none();
    }

    private static class PageLayoutSectionBuilder {
        private final Option<PageLayoutSectionLayoutType> sectionLayoutTypeFromElement;
        private List<Streamable> bodies = new ArrayList<Streamable>();

        public PageLayoutSectionBuilder(Option<PageLayoutSectionLayoutType> sectionLayoutTypeFromElementOrNULL) {
            this.sectionLayoutTypeFromElement = sectionLayoutTypeFromElementOrNULL;
        }

        public PageLayoutSection build() {
            return new PageLayoutSection(this.fillCells(this.sectionLayoutTypeFromElement, this.bodies));
        }

        public void addCellBody(Streamable body) {
            this.bodies.add(body);
        }

        private List<PageLayoutCell> fillCells(Option<PageLayoutSectionLayoutType> sectionLayoutType, List<Streamable> bodies) {
            if (sectionLayoutType.isDefined()) {
                return this.fillCellsUnexpectedFormatAware(bodies, ((PageLayoutSectionLayoutType)((Object)sectionLayoutType.get())).getExpectedCellTypes());
            }
            return this.getCellsForInvalidSectionType(bodies);
        }

        private List<PageLayoutCell> fillCellsUnexpectedFormatAware(List<Streamable> bodies, PageLayoutCellType ... expectedCellLayouts) {
            ArrayList<PageLayoutCell> cells = new ArrayList<PageLayoutCell>();
            int numberContentCells = bodies.size();
            if (numberContentCells == expectedCellLayouts.length) {
                for (int i = 0; i < numberContentCells; ++i) {
                    cells.add(new PageLayoutCell(expectedCellLayouts[i], bodies.get(i)));
                }
            } else if (numberContentCells > expectedCellLayouts.length) {
                for (int i = 0; i < expectedCellLayouts.length - 1; ++i) {
                    cells.add(new PageLayoutCell(expectedCellLayouts[i], bodies.get(i)));
                }
                Streamable lastBody = Streamables.combine(bodies.subList(expectedCellLayouts.length - 1, numberContentCells));
                cells.add(new PageLayoutCell(expectedCellLayouts[expectedCellLayouts.length - 1], lastBody));
            } else {
                int i;
                for (i = 0; i < numberContentCells; ++i) {
                    cells.add(new PageLayoutCell(expectedCellLayouts[i], bodies.get(i)));
                }
                for (i = numberContentCells; i < expectedCellLayouts.length; ++i) {
                    cells.add(new PageLayoutCell(expectedCellLayouts[i], Streamables.from("<p></p>")));
                }
            }
            return cells;
        }

        private List<PageLayoutCell> getCellsForInvalidSectionType(List<Streamable> bodies) {
            if (bodies.size() <= 1) {
                return this.fillCellsUnexpectedFormatAware(bodies, PageLayoutCellType.NORMAL);
            }
            if (bodies.size() == 2) {
                return this.fillCellsUnexpectedFormatAware(bodies, PageLayoutCellType.NORMAL, PageLayoutCellType.NORMAL);
            }
            return this.fillCellsUnexpectedFormatAware(bodies, PageLayoutCellType.NORMAL, PageLayoutCellType.NORMAL, PageLayoutCellType.NORMAL);
        }
    }
}

