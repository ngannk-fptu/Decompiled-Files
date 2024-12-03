/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.util.concurrent.NotNull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.notifications.api.NotificationHandler;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.util.concurrent.NotNull;
import org.dom4j.Element;

public class NotificationHandlerModuleDescriptor
extends AbstractModuleDescriptor<NotificationHandler> {
    private String notificationClass;

    public NotificationHandlerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("The notification handler class is required")});
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@notification").withError("The notification class is required")});
    }

    public void init(@NotNull Plugin plugin, @NotNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.notificationClass = element.attributeValue("notification", "");
    }

    public NotificationHandler getModule() {
        return (NotificationHandler)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public String getNotificationClass() {
        return this.notificationClass;
    }
}

