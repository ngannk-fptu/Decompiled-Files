/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.notifications.api.template.TemplateDefinition
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.confluence.notifications.RenderContextProvider;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.notifications.api.template.TemplateDefinition;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationTemplateDescriptor
extends AbstractParticipantDescriptor<RenderContextProvider> {
    private static final Logger log = LoggerFactory.getLogger(NotificationTemplateDescriptor.class);
    protected final PluginController pluginController;
    private TemplateDefinition subjectTemplate;
    private String medium;
    private ModuleCompleteKey notificationKey;
    private ModuleDescriptor bodyDescriptor;
    private TemplateDefinition bodyTemplate;

    public NotificationTemplateDescriptor(ModuleFactory moduleFactory, PluginController pluginController) {
        this(moduleFactory, pluginController, null);
    }

    public NotificationTemplateDescriptor(ModuleFactory moduleFactory, PluginController pluginController, ModuleCompleteKey notificationKey) {
        super(moduleFactory);
        this.pluginController = pluginController;
        this.notificationKey = notificationKey;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@subject").withError(String.format("Missing attribute [subject=\"Velocity expression\"] denoting the subject for this medium, e.g. [subject=\"$content.space.moduleKey > $content.title\"], on", new Object[0]))});
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@for-medium").withError(String.format("Missing attribute [for-medium=\"moduleKey\"] denoting the moduleKey of the medium this notification template is used for, e.g. [for-medium=\"email\"], on", new Object[0]))});
        if (this.notificationKey == null) {
            pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@for-notification").withError(String.format("Missing attribute [for-notification=\"pluginKey:moduleCompleteKey\"] denoting the moduleKey of the notification that this notification template is used for, e.g. [for-notification=\"email-notification-plugin:page-created-notification\"], on", new Object[0]))});
        }
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element descriptor) throws PluginParseException {
        Element bodyDescriptorElement;
        super.init(plugin, descriptor);
        this.subjectTemplate = TemplateDefinition.vmTemplate((String)descriptor.attributeValue("subject"));
        this.medium = descriptor.attributeValue("for-medium");
        if (this.notificationKey == null) {
            try {
                this.notificationKey = new ModuleCompleteKey(descriptor.attributeValue("for-notification"));
            }
            catch (IllegalArgumentException ignored) {
                this.notificationKey = new ModuleCompleteKey(this.getPluginKey(), descriptor.attributeValue("for-notification"));
            }
        }
        if ((bodyDescriptorElement = descriptor.element("body")).attributeValue("key") == null) {
            bodyDescriptorElement.addAttribute("key", this.key + "-" + bodyDescriptorElement.getName());
        }
        this.bodyDescriptor = new WebResourceModuleDescriptor(this.moduleFactory, null);
        this.bodyDescriptor.init(plugin, bodyDescriptorElement);
        plugin.addModuleDescriptor(this.bodyDescriptor);
        this.bodyTemplate = TemplateDefinition.soyTemplate((String)this.bodyDescriptor.getCompleteKey(), (String)bodyDescriptorElement.attributeValue("use"));
    }

    public void enabled() {
        super.enabled();
        this.pluginController.enablePluginModule(this.bodyDescriptor.getCompleteKey());
    }

    @Override
    public void disabled() {
        super.disabled();
    }

    public TemplateDefinition getSubjectTemplate() {
        return this.subjectTemplate;
    }

    public String getMedium() {
        return this.medium;
    }

    public ModuleCompleteKey getNotificationKey() {
        return this.notificationKey;
    }

    public TemplateDefinition getBodyTemplate() {
        return this.bodyTemplate;
    }
}

