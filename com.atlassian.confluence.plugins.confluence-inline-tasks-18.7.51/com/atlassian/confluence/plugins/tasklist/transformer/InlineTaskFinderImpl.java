/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlFragmentEventReader
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem
 *  com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants
 *  com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskUnmarshaller
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.transformers.LegacyFragmentTransformer
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlFragmentEventReader;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.LegacyFragmentTransformer;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.transformer.DefaultInlineTaskRenderedFieldsExtractor;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskDueDateExtractor;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskFinder;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskRenderedFieldsExtractor;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskUserExtractor;
import com.atlassian.confluence.plugins.tasklist.transformer.NullInlineTaskRenderedFieldsExtractor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class InlineTaskFinderImpl
implements InlineTaskFinder {
    private static final Logger log = LoggerFactory.getLogger(InlineTaskFinderImpl.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern((String)"yyyy-MM-dd");
    private static final ImmutableSet<QName> IGNORED_ELEMENTS_IN_TASK_BODY = ImmutableSet.of((Object)StorageInlineTaskConstants.TASK_LIST_ELEMENT);
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XhtmlContent xhtmlContent;
    private final InlineTaskUserExtractor mentionsExtractor;
    private final InlineTaskDueDateExtractor dueDateExtractor;
    private final I18NBeanFactory i18nBeanFactory;
    private final MarshallingRegistry marshallingRegistry;

    @Autowired
    public InlineTaskFinderImpl(XmlEventReaderFactory xmlEventReaderFactory, @Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory, XhtmlContent xhtmlContent, UserAccessor userAccessor, I18NBeanFactory i18nBeanFactory, MarshallingRegistry marshallingRegistry) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xhtmlContent = xhtmlContent;
        this.dueDateExtractor = new InlineTaskDueDateExtractor();
        this.mentionsExtractor = new InlineTaskUserExtractor(userAccessor);
        this.i18nBeanFactory = i18nBeanFactory;
        this.marshallingRegistry = marshallingRegistry;
    }

    @Override
    public Map<Long, InlineTaskListItem> findTasksInContent(long contentId, String body, ConversionContext conversionContext) {
        LinkedHashMap result = Maps.newLinkedHashMap();
        try {
            XMLEventReader xmlReader = this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(body));
            StorageInlineTaskUnmarshaller unmarshaller = new StorageInlineTaskUnmarshaller(this.xmlEventReaderFactory, this.marshallingRegistry){

                protected Streamable processTaskTitle(XMLEventReader reader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
                    return fragmentTransformer.transform(InlineTaskFinderImpl.this.xmlEventReaderFactory.createXmlFragmentEventReader(reader), fragmentTransformer, conversionContext);
                }
            };
            AccumulatingTasksTransformer accumulatingTransformer = new AccumulatingTasksTransformer(unmarshaller);
            accumulatingTransformer.transform(xmlReader, accumulatingTransformer, conversionContext);
            for (InlineTaskListItem inlineTaskListItem : accumulatingTransformer.getAccumulatedTasks().getItems()) {
                long taskId;
                if (inlineTaskListItem.getId() == null || (taskId = Long.parseLong(inlineTaskListItem.getId())) == -1L) continue;
                result.put(taskId, inlineTaskListItem);
            }
            return result;
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task parseTask(InlineTaskListItem taskItem, long contentId, ConversionContext conversionContext) {
        DefaultInlineTaskRenderedFieldsExtractor renderedFieldsExtractor = new DefaultInlineTaskRenderedFieldsExtractor(this.i18nBeanFactory, this.xmlEventReaderFactory, this.xhtmlContent);
        return this.parseTask(taskItem, conversionContext, contentId, renderedFieldsExtractor);
    }

    @Override
    public Map<Long, Task> extractTasks(long contentId, String body, ConversionContext conversionContext) {
        return this.findTasks(contentId, body, conversionContext, false);
    }

    private Map<Long, Task> findTasks(long contentId, String body, ConversionContext conversionContext, boolean extractDescription) {
        InlineTaskRenderedFieldsExtractor renderedFieldsExtractor = extractDescription ? new DefaultInlineTaskRenderedFieldsExtractor(this.i18nBeanFactory, this.xmlEventReaderFactory, this.xhtmlContent) : new NullInlineTaskRenderedFieldsExtractor();
        try {
            LinkedHashMap result = Maps.newLinkedHashMap();
            XMLEventReader xmlReader = this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(body));
            StorageInlineTaskUnmarshaller unmarshaller = new StorageInlineTaskUnmarshaller(this.xmlEventReaderFactory, this.marshallingRegistry){

                protected Streamable processTaskTitle(XMLEventReader reader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
                    return fragmentTransformer.transform(InlineTaskFinderImpl.this.xmlEventReaderFactory.createXmlFragmentEventReader(reader), fragmentTransformer, conversionContext);
                }
            };
            AccumulatingTasksTransformer accumulatingTransformer = new AccumulatingTasksTransformer(unmarshaller);
            accumulatingTransformer.transform(xmlReader, accumulatingTransformer, conversionContext);
            for (InlineTaskListItem task : accumulatingTransformer.getAccumulatedTasks().getItems()) {
                long taskId;
                try {
                    taskId = Long.parseLong(task.getId());
                }
                catch (NumberFormatException e) {
                    log.warn("Task {} is missing an ID", (Object)task);
                    continue;
                }
                if (taskId == -1L) continue;
                result.put(taskId, this.parseTask(task, conversionContext, contentId, renderedFieldsExtractor));
            }
            return result;
        }
        catch (XMLStreamException e) {
            log.error("Unable to parse tasks.", (Throwable)e);
            return Collections.emptyMap();
        }
        catch (XhtmlException e) {
            log.error("Unable to parse tasks.", (Throwable)e);
            return Collections.emptyMap();
        }
    }

    private Task parseTask(InlineTaskListItem task, ConversionContext conversionContext, long contentId, InlineTaskRenderedFieldsExtractor renderedFieldsExtractor) {
        conversionContext.getPageContext().setOutputType(ConversionContextOutputType.EMAIL.value());
        String titleHtml = renderedFieldsExtractor.renderTaskBody(task.getBody(), conversionContext);
        Object title = renderedFieldsExtractor.stripTagsFromRenderedBody(titleHtml);
        String description = renderedFieldsExtractor.buildDescription(titleHtml);
        TaskStatus status = task.isCompleted() ? TaskStatus.CHECKED : TaskStatus.UNCHECKED;
        String dueDateString = null;
        String assigneeName = null;
        try {
            String entityTitle;
            ConfluenceUser assignee;
            List<ConfluenceUser> mentions = this.mentionsExtractor.extractUsersForInlineTask(this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(task.getBody())));
            ConfluenceUser confluenceUser = assignee = mentions.isEmpty() ? null : mentions.get(0);
            if (assignee != null) {
                assigneeName = assignee.getName();
                if (title != null && ((String)title).trim().equals(assignee.getFullName().trim()) && (entityTitle = conversionContext.getEntity().getTitle()) != null) {
                    title = (String)title + " (" + entityTitle + ")";
                }
            }
            if (title != null && ((String)title).length() < 1 && (entityTitle = conversionContext.getEntity().getTitle()) != null) {
                title = "(" + conversionContext.getEntity().getTitle() + ")";
            }
            dueDateString = this.dueDateExtractor.extractDueDateStringForInlineTask(this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(task.getBody())));
        }
        catch (XMLStreamException e) {
            log.error("unable to extract assignee from a task", (Throwable)e);
        }
        return new Task.Builder().withId(Long.parseLong(task.getId())).withContentId(contentId).withStatus(status).withTitle((String)title).withDescription(description).withBody(task.getBody()).withAssignee(assigneeName).withDueDate(this.parseDate(dueDateString)).build();
    }

    private Date parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return DATE_TIME_FORMATTER.parseDateTime(dateString).toDate();
        }
        catch (IllegalArgumentException e) {
            log.error("An error occurred due to invalid date time format during unmarshalling a time from storage format.", (Throwable)e);
            return null;
        }
    }

    private final class AccumulatingTasksTransformer
    extends LegacyFragmentTransformer
    implements FragmentTransformer {
        private final StorageInlineTaskUnmarshaller unmarshaller;
        private final InlineTaskList innerTasks = new InlineTaskList();

        private AccumulatingTasksTransformer(StorageInlineTaskUnmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public String transformToString(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
            String result = null;
            try {
                ResettableXmlEventReader resettableXmlEventReader = new ResettableXmlEventReader(reader);
                while (resettableXmlEventReader.hasNext()) {
                    XMLEvent peek = resettableXmlEventReader.peek();
                    if (peek.isStartElement() && this.unmarshaller.handles(peek.asStartElement(), conversionContext)) {
                        InlineTaskList list = this.unmarshaller.unmarshal((XMLEventReader)new XmlFragmentEventReader((XMLEventReader)resettableXmlEventReader), mainFragmentTransformer, conversionContext);
                        for (InlineTaskListItem task : list.getItems()) {
                            this.innerTasks.addItem(task);
                        }
                        continue;
                    }
                    if (peek.isStartElement() && StorageInlineTaskConstants.TASK_BODY_ELEMENT.equals(peek.asStartElement().getName())) {
                        int position = resettableXmlEventReader.getCurrentEventPosition();
                        result = StaxUtils.toXmlStringWithoutTag((XMLEventReader)new XmlFragmentEventReader((XMLEventReader)resettableXmlEventReader), (XmlOutputFactory)InlineTaskFinderImpl.this.xmlFragmentOutputFactory, IGNORED_ELEMENTS_IN_TASK_BODY);
                        result = result.length() <= 16 ? "" : result.substring(StorageInlineTaskConstants.TASK_BODY_ELEMENT_NAME.length() + 5, result.length() - StorageInlineTaskConstants.TASK_BODY_ELEMENT_NAME.length() - 6);
                        resettableXmlEventReader.restoreEventPosition(position + 1);
                        continue;
                    }
                    resettableXmlEventReader.nextEvent();
                }
            }
            catch (XMLStreamException e) {
                return null;
            }
            return result;
        }

        public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
            return this.unmarshaller.handles(startElementEvent, conversionContext) || StorageInlineTaskConstants.TASK_BODY_ELEMENT.equals(startElementEvent.getName());
        }

        public InlineTaskList getAccumulatedTasks() {
            return this.innerTasks;
        }
    }
}

