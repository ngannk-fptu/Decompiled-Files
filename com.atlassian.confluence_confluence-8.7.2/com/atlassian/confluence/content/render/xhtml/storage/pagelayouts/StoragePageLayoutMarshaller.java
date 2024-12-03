/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayout;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCell;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSection;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSectionLayoutType;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StoragePageLayoutMarshaller
implements Marshaller<PageLayout> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StoragePageLayoutMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(PageLayout pagelayout, ConversionContext conversionContext) throws XhtmlException {
        if (pagelayout.hasOneSectionAndOneCell()) {
            return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
                PageLayoutSection section = pagelayout.getSections().iterator().next();
                PageLayoutCell cell = section.getCells().iterator().next();
                xmlStreamWriter.flush();
                cell.getBody().writeTo(underlyingWriter);
            });
        }
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            this.writeStartElement(xmlStreamWriter, "layout");
            for (PageLayoutSection section : pagelayout.getSections()) {
                this.writeSection(xmlStreamWriter, underlyingWriter, section);
            }
            xmlStreamWriter.writeEndElement();
        });
    }

    private void writeSectionAttributeType(XMLStreamWriter xmlStreamWriter, PageLayoutSectionLayoutType sectionType) throws XMLStreamException {
        xmlStreamWriter.writeAttribute("ac", "http://atlassian.com/content", "type", this.getSectionLayoutAttributeValue(sectionType));
    }

    private String getSectionLayoutAttributeValue(PageLayoutSectionLayoutType sectionType) {
        switch (sectionType) {
            case SINGLE: {
                return "single";
            }
            case TWO_EQUAL: {
                return "two_equal";
            }
            case TWO_LEFT_SIDEBAR: {
                return "two_left_sidebar";
            }
            case TWO_RIGHT_SIDEBAR: {
                return "two_right_sidebar";
            }
            case THREE_EQUAL: {
                return "three_equal";
            }
            case THREE_WITH_SIDEBARS: {
                return "three_with_sidebars";
            }
        }
        return "single";
    }

    private void writeSection(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter, PageLayoutSection section) throws XMLStreamException, IOException {
        this.writeStartElement(xmlStreamWriter, "layout-section");
        this.writeSectionAttributeType(xmlStreamWriter, section.getSectionLayout());
        for (PageLayoutCell cell : section.getCells()) {
            this.writeCell(xmlStreamWriter, underlyingWriter, cell);
        }
        xmlStreamWriter.writeEndElement();
    }

    private void writeCell(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter, PageLayoutCell cell) throws XMLStreamException, IOException {
        this.writeStartElement(xmlStreamWriter, "layout-cell");
        xmlStreamWriter.writeCharacters("");
        xmlStreamWriter.flush();
        cell.getBody().writeTo(underlyingWriter);
        xmlStreamWriter.writeEndElement();
    }

    private void writeStartElement(XMLStreamWriter xmlStreamWriter, String tagName) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("ac", tagName, "http://atlassian.com/content");
    }
}

