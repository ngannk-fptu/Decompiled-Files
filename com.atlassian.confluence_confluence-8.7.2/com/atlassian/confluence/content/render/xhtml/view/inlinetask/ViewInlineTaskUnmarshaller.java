/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.inlinetask;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskConstants;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class ViewInlineTaskUnmarshaller
implements Unmarshaller<InlineTaskList> {
    private static final Pattern taskIdPattern = Pattern.compile("<" + StorageInlineTaskConstants.TASK_ID_ELEMENT.getPrefix() + ":" + StorageInlineTaskConstants.TASK_ID_ELEMENT_NAME + ">([0-9]+)</" + StorageInlineTaskConstants.TASK_ID_ELEMENT.getPrefix() + ":" + StorageInlineTaskConstants.TASK_ID_ELEMENT_NAME + ">");
    private static final String CONTEXT_PROPERTY_ALL_SEQUENCE_IDS = "confluence.inline.tasks.sequence.all";
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final ContentPropertyManager contentPropertyManager;

    public ViewInlineTaskUnmarshaller(XmlEventReaderFactory xmlEventReaderFactory, ContentPropertyManager contentPropertyManager) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.contentPropertyManager = contentPropertyManager;
    }

    @Override
    public InlineTaskList unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        InlineTaskList inlineTaskList = new InlineTaskList();
        try {
            XMLEventReader listReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(xmlEventReader);
            while (listReader.hasNext()) {
                XMLEvent event = listReader.peek();
                if (event.isStartElement() && ViewInlineTaskConstants.TASK_TAG.equals(event.asStartElement().getName())) {
                    inlineTaskList.addItem(this.unmarshalListItem(this.xmlEventReaderFactory.createXmlFragmentEventReader(listReader), mainFragmentTransformer, conversionContext));
                    continue;
                }
                listReader.nextEvent();
            }
            StaxUtils.closeQuietly(listReader);
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
        return ViewInlineTaskConstants.TASK_LIST_TAG.equals(startElementEvent.getName()) && StaxUtils.hasClass(startElementEvent, ViewInlineTaskConstants.TASK_LIST_IDENTIFYING_CSS_CLASS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InlineTaskListItem unmarshalListItem(XMLEventReader listItemReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        try {
            StartElement liStart = listItemReader.peek().asStartElement();
            String style = StaxUtils.getAttributeValue(liStart, "style");
            boolean emptyListItem = style != null && style.contains("background-image: none");
            String id = null;
            if (!emptyListItem) {
                id = TaskIdHelper.ensureIdIsUniqueOrGetReplacement(this.contentPropertyManager, conversionContext, StaxUtils.getAttributeValue(liStart, ViewInlineTaskConstants.TASK_ID_DATA_ATTRIBUTE));
            }
            String classes = StaxUtils.getAttributeValue(liStart, "class");
            String body = Streamables.writeToString(fragmentTransformer.transform(this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(listItemReader), fragmentTransformer, conversionContext));
            boolean completed = classes != null && classes.contains(ViewInlineTaskConstants.COMPLETED_TASK_CSS_CLASS);
            InlineTaskListItem inlineTaskListItem = new InlineTaskListItem(emptyListItem ? ViewInlineTaskConstants.EMPTY_LIST_ITEM_ID : id, completed, body);
            return inlineTaskListItem;
        }
        finally {
            StaxUtils.closeQuietly(listItemReader);
        }
    }

    static class TaskIdHelper {
        TaskIdHelper() {
        }

        static String ensureIdIsUniqueOrGetReplacement(ContentPropertyManager contentPropertyManager, ConversionContext conversionContext, String candidateId) throws XhtmlException {
            Set parsedInlineTaskIds = (Set)conversionContext.getProperty(ViewInlineTaskUnmarshaller.CONTEXT_PROPERTY_ALL_SEQUENCE_IDS);
            if (parsedInlineTaskIds == null) {
                parsedInlineTaskIds = Sets.newHashSet();
            }
            if (StringUtils.isBlank((CharSequence)candidateId) || parsedInlineTaskIds.contains(candidateId)) {
                candidateId = TaskIdHelper.getUniqueIdForNewInlineTask(contentPropertyManager, conversionContext, parsedInlineTaskIds);
            }
            parsedInlineTaskIds.add(candidateId);
            conversionContext.setProperty(ViewInlineTaskUnmarshaller.CONTEXT_PROPERTY_ALL_SEQUENCE_IDS, parsedInlineTaskIds);
            return candidateId;
        }

        static String getUniqueIdForNewInlineTask(ContentPropertyManager contentPropertyManager, ConversionContext conversionContext, Set<String> parsedInlineTasks) throws XhtmlException {
            ContentEntityObject entity = conversionContext.getEntity();
            if (entity == null) {
                return TaskIdHelper.nextId(parsedInlineTasks);
            }
            long lastSequenceId = 0L;
            String lastSequenceIdString = contentPropertyManager.getStringProperty(entity, "confluence.inline.tasks.sequence.last");
            lastSequenceId = lastSequenceIdString == null ? TaskIdHelper.getHighestSequenceNumber(lastSequenceId, entity.getBodyAsString()) : Long.parseLong(lastSequenceIdString);
            long newSequenceId = lastSequenceId + 1L;
            if (newSequenceId >= Long.MAX_VALUE || parsedInlineTasks.contains(String.valueOf(newSequenceId))) {
                newSequenceId = TaskIdHelper.getFirstUnusedSequenceNumber(entity.getBodyAsString());
            }
            contentPropertyManager.setStringProperty(entity, "confluence.inline.tasks.sequence.last", String.valueOf(newSequenceId));
            return String.valueOf(newSequenceId);
        }

        private static String nextId(Set<String> parsedInlineTasks) {
            TreeSet<Integer> sorted = new TreeSet<Integer>();
            for (String idString : parsedInlineTasks) {
                try {
                    sorted.add(Integer.parseInt(idString));
                }
                catch (NumberFormatException numberFormatException) {}
            }
            if (sorted.size() > 0 && sorted.last() != null) {
                return Integer.toString((Integer)sorted.last() + 1);
            }
            return "1";
        }

        static long getHighestSequenceNumber(long lastSequenceId, String contentBody) {
            Matcher taskIdMatcher = taskIdPattern.matcher(contentBody);
            boolean result = taskIdMatcher.find();
            while (result) {
                long currentSequenceId = Long.parseLong(taskIdMatcher.group(1));
                lastSequenceId = currentSequenceId > lastSequenceId ? currentSequenceId : lastSequenceId;
                result = taskIdMatcher.find();
            }
            return lastSequenceId;
        }

        static long getFirstUnusedSequenceNumber(String contentBody) throws XhtmlException {
            Matcher taskIdMatcher = taskIdPattern.matcher(contentBody);
            HashSet usedNumbers = Sets.newHashSet();
            boolean result = taskIdMatcher.find();
            while (result) {
                usedNumbers.add(Long.parseLong(taskIdMatcher.group(1)));
                result = taskIdMatcher.find();
            }
            for (long potentialId = 0L; potentialId < Long.MAX_VALUE; ++potentialId) {
                if (usedNumbers.contains(potentialId)) continue;
                return potentialId;
            }
            throw new XhtmlException("Unable to assign an id to newly added tasks while unmarshalling editor content.");
        }
    }
}

