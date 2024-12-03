/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.notifications.api.template.TemplateDefinition
 *  com.atlassian.plugin.notifications.api.template.TemplatePathResolver
 *  com.atlassian.plugin.notifications.api.template.TemplatePathResolver$TemplatePath
 *  com.atlassian.plugin.notifications.spi.TemplateLocator
 *  com.atlassian.plugin.notifications.spi.TemplateParams
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.impl.NotificationDescriptorLocator;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTemplateDescriptor;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.notifications.api.template.TemplateDefinition;
import com.atlassian.plugin.notifications.api.template.TemplatePathResolver;
import com.atlassian.plugin.notifications.spi.TemplateLocator;
import com.atlassian.plugin.notifications.spi.TemplateParams;
import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceTemplateLocator
implements TemplateLocator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceTemplateLocator.class);
    private final TemplatePathResolver templatePathResolver;
    private final NotificationDescriptorLocator locate;

    public ConfluenceTemplateLocator(TemplatePathResolver templatePathResolver, NotificationDescriptorLocator locate) {
        this.templatePathResolver = templatePathResolver;
        this.locate = locate;
    }

    public TemplateDefinition getTemplate(TemplateParams params) {
        Notification notification = (Notification)params.getContext().get("originalEvent");
        Maybe<NotificationTemplateDescriptor> maybeNotificationTemplateDescriptor = this.locate.findTemplateDescriptor(notification, params.getMediumKey());
        if (params.getTemplateType().startsWith("subject")) {
            if (maybeNotificationTemplateDescriptor.isDefined()) {
                return ((NotificationTemplateDescriptor)((Object)maybeNotificationTemplateDescriptor.get())).getSubjectTemplate();
            }
            return TemplateDefinition.vmTemplate((String)"$content.space.key > $content.title");
        }
        Iterable customTemplatePaths = this.templatePathResolver.getCustomTemplatePaths(params);
        for (TemplatePathResolver.TemplatePath templatePath : customTemplatePaths) {
            File templateFile = new File(templatePath.getFullTemplatePath());
            if (!templateFile.exists() || !templateFile.canRead()) continue;
            try {
                return TemplateDefinition.vmTemplate((String)FileUtils.readFileToString((File)templateFile));
            }
            catch (IOException e) {
                log.error("Error reading template file '" + templateFile.getAbsolutePath() + "'.", (Throwable)e);
            }
        }
        if (maybeNotificationTemplateDescriptor.isDefined()) {
            return ((NotificationTemplateDescriptor)((Object)maybeNotificationTemplateDescriptor.get())).getBodyTemplate();
        }
        Iterable pluginTemplatePaths = this.templatePathResolver.getTemplatePaths(new File("templates/"), params);
        for (TemplatePathResolver.TemplatePath templatePath : pluginTemplatePaths) {
            InputStream templateStream = ClassLoaderUtils.getResourceAsStream((String)templatePath.getFullTemplatePath(), this.getClass());
            if (templateStream == null) continue;
            try {
                return TemplateDefinition.vmTemplate((String)IOUtils.toString((InputStream)templateStream));
            }
            catch (IOException e) {
                log.error("Error reading template from classpath '" + templatePath.getFullTemplatePath() + "'.", (Throwable)e);
            }
        }
        return null;
    }
}

