/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.inlinetask;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StorageInlineTaskUnmarshaller
implements Unmarshaller<InlineTaskList> {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private static final int NUM_EXPECTED_TAGS_IN_TASK_ELEMENT = 3;

    public StorageInlineTaskUnmarshaller(XmlEventReaderFactory xmlEventReaderFactory, MarshallingRegistry registry) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        registry.register(this, InlineTaskList.class, MarshallingType.STORAGE);
    }

    @Override
    public InlineTaskList unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        InlineTaskList inlineTaskList = new InlineTaskList();
        try {
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.peek();
                if (event.isStartElement() && StorageInlineTaskConstants.TASK_ELEMENT.equals(event.asStartElement().getName())) {
                    inlineTaskList.addItem(this.unmarshalListItem(this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader), mainFragmentTransformer, conversionContext));
                    continue;
                }
                xmlEventReader.nextEvent();
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        finally {
            StaxUtils.closeQuietly(xmlEventReader);
        }
        return inlineTaskList;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StorageInlineTaskConstants.TASK_LIST_ELEMENT.equals(startElementEvent.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InlineTaskListItem unmarshalListItem(XMLEventReader listItemReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        try {
            int tagsConsumed = 0;
            String id = null;
            boolean completed = false;
            String body = "";
            while (listItemReader.hasNext() && tagsConsumed < 3) {
                if (listItemReader.peek().isStartElement()) {
                    StartElement upcomingStartElement = listItemReader.peek().asStartElement();
                    if (StorageInlineTaskConstants.TASK_ID_ELEMENT.equals(upcomingStartElement.getName())) {
                        listItemReader.nextEvent();
                        id = listItemReader.nextEvent().asCharacters().getData();
                        listItemReader.nextEvent();
                        ++tagsConsumed;
                        continue;
                    }
                    if (StorageInlineTaskConstants.TASK_STATUS_ELEMENT.equals(upcomingStartElement.getName())) {
                        listItemReader.nextEvent();
                        if (StorageInlineTaskConstants.TASK_STATUS_COMPLETE.equals(listItemReader.nextEvent().asCharacters().getData())) {
                            completed = true;
                        }
                        listItemReader.nextEvent();
                        ++tagsConsumed;
                        continue;
                    }
                    if (StorageInlineTaskConstants.TASK_BODY_ELEMENT.equals(upcomingStartElement.getName())) {
                        body = Streamables.writeToString(this.processTaskTitle(listItemReader, fragmentTransformer, conversionContext));
                        ++tagsConsumed;
                        continue;
                    }
                    listItemReader.nextEvent();
                    continue;
                }
                listItemReader.nextEvent();
            }
            InlineTaskListItem inlineTaskListItem = new InlineTaskListItem(id, completed, body);
            return inlineTaskListItem;
        }
        finally {
            StaxUtils.closeQuietly(listItemReader);
        }
    }

    protected Streamable processTaskTitle(XMLEventReader reader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        return fragmentTransformer.transform(this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader), fragmentTransformer, conversionContext);
    }
}

