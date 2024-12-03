/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InlineTaskDueDateExtractor {
    private static final Logger log = LoggerFactory.getLogger(InlineTaskDueDateExtractor.class);
    private static final QName TIME_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "time");
    private static final QName DATE_TIME_ATTRIBUTE = new QName("datetime");

    InlineTaskDueDateExtractor() {
    }

    public String extractDueDateStringForInlineTask(XMLEventReader xmlEventReader) {
        try {
            while (xmlEventReader.hasNext()) {
                Attribute dateTimeAttribute;
                StartElement startElement;
                XMLEvent nextEvent = xmlEventReader.nextEvent();
                if (!nextEvent.isStartElement() || !TIME_ELEMENT.equals((startElement = nextEvent.asStartElement()).getName()) || (dateTimeAttribute = startElement.getAttributeByName(DATE_TIME_ATTRIBUTE)) == null) continue;
                return dateTimeAttribute.getValue();
            }
        }
        catch (XMLStreamException e) {
            log.error("unable to extract due date from a task", (Throwable)e);
        }
        return null;
    }
}

