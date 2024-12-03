/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimaps
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.notification.AbstractTaskNotificationContextFactory;
import com.atlassian.confluence.plugins.tasklist.notification.TaskRenderService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HipChatTaskNotificationContextFactory
extends AbstractTaskNotificationContextFactory {
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-inline-tasks:inline-task-mail-resources";
    private static final String CALENDAR = "inline-task-calendar-icon";
    private final WebResourceUrlProvider webResourceUrlProvider;

    public HipChatTaskNotificationContextFactory(UserAccessor userAccessor, I18NBeanFactory beanFactory, LocaleManager localeManager, TaskRenderService taskRenderService, ContentService contentService, WebResourceUrlProvider webResourceUrlProvider, NotificationUserService notificationUserService) {
        super(userAccessor, beanFactory, localeManager, taskRenderService, contentService, notificationUserService);
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    protected ListMultimap<TaskModfication.Operation, TaskModfication> renderTask(Iterable<TaskModfication> tasks, Content content, ConfluenceUser recipientUser) {
        return Multimaps.index((Iterable)Iterables.transform(this.getTaskRenderService().renderTasksOnPage(tasks, content), input -> {
            Object dateTimeElement2;
            Document doc = Jsoup.parse((String)input.getHtmlContent());
            Element parsed = (Element)doc.getElementsByTag("body").get(0);
            Elements dateTimeElements = parsed.getElementsByTag("time");
            for (Object dateTimeElement2 : dateTimeElements) {
                dateTimeElement2.before("<span class='atTag'><img src='" + this.webResourceUrlProvider.getStaticPluginResourceUrl(PLUGIN_KEY, CALENDAR, UrlMode.ABSOLUTE) + "'/> &nbsp;" + dateTimeElement2.text() + "</span>");
                dateTimeElement2.remove();
            }
            Elements userInfos = parsed.getElementsByAttributeValue("data-linked-resource-type", "userinfo");
            dateTimeElement2 = userInfos.iterator();
            while (dateTimeElement2.hasNext()) {
                Element userInfo;
                String userName = (userInfo = (Element)dateTimeElement2.next()).attr("data-username");
                userInfo.before("<span class='atTag " + (userName.equals(recipientUser.getName()) ? "atTagMe" : "") + "'>&nbsp;@" + HtmlUtil.htmlEncode((String)userName) + "</span>");
                userInfo.remove();
            }
            String bodyStripped = parsed.html();
            input.setHtmlContent(bodyStripped);
            return input;
        }), this.byOperation());
    }
}

