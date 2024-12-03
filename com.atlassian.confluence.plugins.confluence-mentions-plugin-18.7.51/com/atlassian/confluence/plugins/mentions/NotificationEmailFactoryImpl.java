/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.storage.InlineTasksUtils
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory$FilterByType
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem$Builder
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.content.render.xhtml.storage.InlineTasksUtils;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.mentions.NotificationEmailFactory;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Optional;
import java.util.function.Predicate;
import javax.activation.DataSource;

public class NotificationEmailFactoryImpl
implements NotificationEmailFactory {
    private DataSourceFactory imageDataSourceFactory;

    public NotificationEmailFactoryImpl(DataSourceFactory imageDataSourceFactory) {
        this.imageDataSourceFactory = imageDataSourceFactory;
    }

    @Override
    public PreRenderedMailNotificationQueueItem create(ContentEntityObject contentEntityObject, ConfluenceUser recipient, ConfluenceUser sender, String templateLocation, String templateName, String subject, NotificationContext context) {
        PreRenderedMailNotificationQueueItem.Builder builder = PreRenderedMailNotificationQueueItem.with((User)recipient, (String)templateName, (String)subject).andSender((User)sender).andTemplateLocation(templateLocation).andContext(context.getMap());
        Optional pluginFactory = this.imageDataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-mentions-plugin");
        if (pluginFactory.isPresent()) {
            Optional mentionIcon = ((PluginDataSourceFactory)pluginFactory.get()).getResourceFromModuleByName("mention-icon", "mention-icon");
            mentionIcon.ifPresent(arg_0 -> ((NotificationContext)context).addTemplateImage(arg_0));
        }
        String mentionExcerpt = (String)context.get("contentHtml");
        for (DataSource resource : InlineTasksUtils.getRequiredResources((DataSourceFactory)this.imageDataSourceFactory, (String)mentionExcerpt)) {
            context.addTemplateImage(resource);
        }
        builder.andRelatedBodyParts((Iterable)context.getTemplateImageDataSources()).andRelatedBodyParts(this.imagesUsedByChromeTemplate());
        return builder.render();
    }

    private Iterable<DataSource> imagesUsedByChromeTemplate() {
        return (Iterable)((PluginDataSourceFactory)this.imageDataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-email-resources").get()).getResourcesFromModules("chrome-template", (Predicate)PluginDataSourceFactory.FilterByType.IMAGE).get();
    }
}

