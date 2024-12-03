/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.storage.pagelayouts.StoragePageLayoutConstants;
import com.atlassian.confluence.xhtml.api.XhtmlVisitor;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageLayoutVisitor
implements XhtmlVisitor {
    private static final Logger log = LoggerFactory.getLogger(PageLayoutVisitor.class);
    private String pageLayoutOneType;
    private int pageLayout2CellCount;
    private int pageLayout2RowCount;

    @Override
    public boolean handle(XMLEvent xmlEvent, ConversionContext context) {
        if (xmlEvent.isStartElement()) {
            Attribute layoutAttr;
            StartElement startElement = xmlEvent.asStartElement();
            if (startElement.getName().equals(StoragePageLayoutConstants.PAGE_LAYOUT_CELL_ELEMENT_QNAME)) {
                ++this.pageLayout2CellCount;
            }
            if (startElement.getName().equals(StoragePageLayoutConstants.PAGE_LAYOUT_SECTION_ELEMENT_QNAME)) {
                ++this.pageLayout2RowCount;
            }
            if ((layoutAttr = startElement.getAttributeByName(new QName("data-atlassian-layout"))) != null) {
                JsonParser parser = new JsonParser();
                try {
                    this.pageLayoutOneType = parser.parse(layoutAttr.getValue()).getAsJsonObject().get("name").getAsString();
                }
                catch (JsonSyntaxException e) {
                    log.error("Invalid layout type provided: '{}', increase logging for details.", (Object)StringUtils.abbreviate((String)layoutAttr.getValue(), (int)250));
                    log.debug("Invalid layout type provided", (Throwable)e);
                }
            }
        }
        boolean keepHandling = true;
        return keepHandling;
    }

    public String getPageLayoutOneType() {
        return this.pageLayoutOneType;
    }

    public int getPageLayout2CellCount() {
        return this.pageLayout2CellCount;
    }

    public int getPageLayout2RowCount() {
        return this.pageLayout2RowCount;
    }
}

