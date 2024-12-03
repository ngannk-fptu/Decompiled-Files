/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.plugins.tasklist.transformer.helper.DirectXMLSink;
import com.atlassian.confluence.plugins.tasklist.transformer.helper.XMLSink;
import com.atlassian.confluence.plugins.tasklist.transformer.xml.ElementHandler;
import com.atlassian.confluence.plugins.tasklist.transformer.xml.ParsingContext;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskVisitor {
    private static final Logger log = LoggerFactory.getLogger(TaskVisitor.class);

    public static String transformTask(XmlEventReaderFactory readerFactory, XmlOutputFactory writerFactory, XMLEventFactory eventFactory, Reader reader, ConversionContext conversionContext, long lastSequenceId, VisitTask taskVisitor) throws XhtmlException {
        try {
            XMLEventReader xmlReader = readerFactory.createStorageXmlEventReader(reader);
            StringWriter writer = new StringWriter();
            XMLEventWriter xmlWriter = writerFactory.createXMLEventWriter((Writer)writer);
            DirectXMLSink customXmlWriter = new DirectXMLSink(xmlWriter);
            xmlWriter.add(eventFactory.createStartDocument());
            xmlWriter.add(eventFactory.createStartElement("", "", "xml"));
            ParsingContext parsingContext = new ParsingContext(conversionContext, eventFactory, lastSequenceId);
            InlineTaskUlHandler inlineTaskHandler = new InlineTaskUlHandler(taskVisitor);
            while (xmlReader.hasNext()) {
                XMLEvent peek = xmlReader.peek();
                if (peek.isStartElement()) {
                    peek.asStartElement().getName();
                }
                if (TaskVisitor.consumeTaskList(parsingContext, xmlReader, customXmlWriter, inlineTaskHandler)) continue;
                XMLEvent event = xmlReader.nextEvent();
                xmlWriter.add(event);
            }
            xmlWriter.add(eventFactory.createEndElement("", "", "xml"));
            xmlWriter.add(eventFactory.createEndDocument());
            xmlWriter.flush();
            String xml = writer.toString();
            xml = xml.replace("<?xml version='1.0'?><xml>", "");
            xml = xml.replace("</xml>", "");
            return xml;
        }
        catch (Exception e) {
            String result = TaskVisitor.unspoiled(reader);
            log.error(e.getMessage(), (Throwable)e);
            return result;
        }
    }

    private static boolean consumeTaskList(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter, ElementHandler inlineTaskHandler) throws XMLStreamException {
        return inlineTaskHandler.consumeIfHandled(context, xmlReader, xmlWriter);
    }

    private static String unspoiled(Reader reader) throws XhtmlException {
        StringWriter writer = new StringWriter();
        try {
            reader.reset();
            IOUtils.copy((Reader)reader, (Writer)writer);
        }
        catch (IOException ioException) {
            throw new XhtmlException((Throwable)ioException);
        }
        String result = writer.toString();
        return result;
    }

    public static interface VisitTask {
        public boolean consumeTaskIfHandled(ParsingContext var1, XMLEventReader var2, XMLSink var3) throws XMLStreamException;
    }

    private static class InlineTaskUlHandler
    extends ElementHandler {
        private VisitTask taskVisitor;

        public InlineTaskUlHandler(VisitTask taskVisitor) {
            super(StorageInlineTaskConstants.TASK_LIST_ELEMENT);
            this.taskVisitor = taskVisitor;
        }

        @Override
        protected boolean consumeChildIfHandled(ParsingContext context, XMLEventReader xmlReader, XMLSink xmlWriter) throws XMLStreamException {
            XMLEvent nextElement = xmlReader.peek();
            if (nextElement.isStartElement() && StorageInlineTaskConstants.TASK_ELEMENT.equals(nextElement.asStartElement().getName())) {
                return this.taskVisitor.consumeTaskIfHandled(context, xmlReader, xmlWriter);
            }
            return TaskVisitor.consumeTaskList(context, xmlReader, xmlWriter, this);
        }
    }
}

