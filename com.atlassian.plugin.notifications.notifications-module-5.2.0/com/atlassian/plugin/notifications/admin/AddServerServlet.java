/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.admin;

import com.atlassian.plugin.notifications.admin.AbstractAdminServlet;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.plugin.notifications.spi.salext.GroupManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;

public class AddServerServlet
extends AbstractAdminServlet {
    private final NotificationMediumManager notificationMediumManager;
    private final I18nResolver i18n;
    private final GroupManager groupManager;

    public AddServerServlet(WebSudoManager webSudoManager, TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager, NotificationMediumManager notificationMediumManager, @Qualifier(value="i18nResolver") I18nResolver i18n, GroupManager groupManager) {
        super(webSudoManager, renderer, userManager, loginUriProvider, webResourceManager);
        this.notificationMediumManager = notificationMediumManager;
        this.i18n = i18n;
        this.groupManager = groupManager;
    }

    @Override
    protected void requireResource(WebResourceManager webResourceManager) {
        webResourceManager.requireResource("com.atlassian.plugin.notifications.notifications-module:notification-server");
    }

    @Override
    protected void renderResponse(TemplateRenderer renderer, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ArrayList notificationMediums = Lists.newArrayList((Iterable)Iterables.filter(this.notificationMediumManager.getNotificationMediums(), (Predicate)new Predicate<NotificationMedium>(){

            public boolean apply(@Nullable NotificationMedium medium) {
                return !medium.getStaticConfiguration().isDefined();
            }
        }));
        Collections.sort(notificationMediums, new Comparator<NotificationMedium>(){

            @Override
            public int compare(NotificationMedium medium1, NotificationMedium medium2) {
                String medium1Name = AddServerServlet.this.notificationMediumManager.getI18nizedMediumName(AddServerServlet.this.i18n, medium1.getKey());
                String medium2Name = AddServerServlet.this.notificationMediumManager.getI18nizedMediumName(AddServerServlet.this.i18n, medium2.getKey());
                return medium1Name.compareTo(medium2Name);
            }
        });
        HashMap context = Maps.newHashMap();
        context.put("notificationMediums", notificationMediums);
        context.put("groups", Lists.newArrayList(this.groupManager.getGroups()));
        context.put("notificationMediumManager", this.notificationMediumManager);
        renderer.render("templates/admin/addnotificationserver.vm", (Map)context, (Writer)response.getWriter());
    }
}

