/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.notification.TaskRenderService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import javax.xml.stream.XMLStreamException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl
implements TaskRenderService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final XhtmlContent xhtmlContent;
    private final ContentEntityManager contentEntityManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public NotificationServiceImpl(XhtmlContent xhtmlContent, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, TransactionTemplate transactionTemplate) {
        this.xhtmlContent = xhtmlContent;
        this.contentEntityManager = contentEntityManager;
        this.transactionTemplate = transactionTemplate;
    }

    private Function<TaskModfication, TaskModfication> renderTask(DefaultConversionContext conversionContext) {
        return taskModification -> {
            taskModification.setHtmlContent(this.getTaskView(taskModification.getTask(), conversionContext));
            return taskModification;
        };
    }

    @Override
    public Iterable<TaskModfication> renderTasksOnPage(Iterable<TaskModfication> tasks, Content content) {
        return (Iterable)this.transactionTemplate.execute(() -> {
            PageContext contentEntity = this.contentEntityManager.getById(content.getId().asLong()).toPageContext();
            DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)contentEntity);
            return ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)tasks, this.renderTask(conversionContext)));
        });
    }

    private String getTaskView(Task task, DefaultConversionContext conversionContext) {
        String html;
        try {
            html = this.xhtmlContent.convertStorageToView(task.getBody(), (ConversionContext)conversionContext);
            Document doc = Jsoup.parse((String)html);
            doc.getElementsByTag("br").remove();
            html = doc.html();
        }
        catch (XhtmlException | XMLStreamException e) {
            log.error("Unable to convert task from storage to view format for email.", e);
            return "";
        }
        return html;
    }
}

