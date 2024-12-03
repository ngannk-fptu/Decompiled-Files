/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayout;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCell;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSection;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSectionLayoutType;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ViewPageLayoutMarshaller
implements Marshaller<PageLayout> {
    private final XMLOutputFactory xmlOutputFactory;

    public ViewPageLayoutMarshaller(XMLOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(PageLayout pagelayout, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                xmlStreamWriter.writeStartElement("div");
                xmlStreamWriter.writeAttribute("class", "contentLayout2");
                xmlStreamWriter.writeCharacters("\n");
                for (PageLayoutSection section : pagelayout.getSections()) {
                    this.processSection(section, out, xmlStreamWriter);
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeCharacters("\n");
                StaxUtils.closeQuietly(xmlStreamWriter);
            }
            catch (XMLStreamException ex) {
                throw new IOException("Exception while writing page layout for the editor", ex);
            }
        };
    }

    private String getLayoutTypeXHTMLAttrValueFromSection(PageLayoutSectionLayoutType pageLayoutSectionLayoutType) {
        switch (pageLayoutSectionLayoutType) {
            case SINGLE: {
                return "single";
            }
            case TWO_LEFT_SIDEBAR: {
                return "two-left-sidebar";
            }
            case TWO_RIGHT_SIDEBAR: {
                return "two-right-sidebar";
            }
            case TWO_EQUAL: {
                return "two-equal";
            }
            case THREE_EQUAL: {
                return "three-equal";
            }
            case THREE_WITH_SIDEBARS: {
                return "three-with-sidebars";
            }
        }
        return "single";
    }

    private void processSection(PageLayoutSection section, Writer out, XMLStreamWriter xmlStreamWriter) throws XMLStreamException, IOException {
        xmlStreamWriter.writeStartElement("div");
        String layoutType = this.getLayoutTypeXHTMLAttrValueFromSection(section.getSectionLayout());
        xmlStreamWriter.writeAttribute("class", "columnLayout " + layoutType);
        xmlStreamWriter.writeAttribute("data-layout", layoutType);
        xmlStreamWriter.writeCharacters("\n");
        for (PageLayoutCell cell : section.getCells()) {
            this.processCell(cell, out, xmlStreamWriter);
        }
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }

    private void processCell(PageLayoutCell cell, Writer out, XMLStreamWriter xmlStreamWriter) throws XMLStreamException, IOException {
        xmlStreamWriter.writeStartElement("div");
        String cellType = "normal";
        if (cell.isAside()) {
            cellType = "aside";
        } else if (cell.isSideBars()) {
            cellType = "sidebars";
        }
        xmlStreamWriter.writeAttribute("class", "cell " + cellType);
        xmlStreamWriter.writeAttribute("data-type", cellType);
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeStartElement("div");
        xmlStreamWriter.writeAttribute("class", "innerCell");
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.flush();
        cell.getBody().writeTo(out);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters("\n");
    }
}

