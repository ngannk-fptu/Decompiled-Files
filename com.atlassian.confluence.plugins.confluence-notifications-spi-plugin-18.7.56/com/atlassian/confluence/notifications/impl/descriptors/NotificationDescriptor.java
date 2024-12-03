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
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  org.dom4j.Element
 *  org.dom4j.XPath
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.confluence.notifications.NotificationFactory;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.impl.DescriptorBasedNotificationEventFactory;
import com.atlassian.confluence.notifications.impl.DescriptorBasedNotificationFactory;
import com.atlassian.confluence.notifications.impl.NotificationEventFactory;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.confluence.notifications.impl.descriptors.AggregateModuleDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTemplateDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTransformerDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.RecipientProviderDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.dom4j.Element;
import org.dom4j.XPath;

public class NotificationDescriptor<PAYLOAD extends NotificationPayload>
extends AggregateModuleDescriptor<PAYLOAD> {
    private final ObjectMapperFactory objectMapperFactory;
    private ModuleCompleteKey notificationKey;
    private NotificationFactory<PAYLOAD> notificationFactory;
    private NotificationEventFactory<PAYLOAD> notificationEventFactory;

    public NotificationDescriptor(ModuleFactory moduleFactory, PluginController pluginController, ObjectMapperFactory objectMapperFactory) {
        super(moduleFactory, pluginController);
        this.objectMapperFactory = objectMapperFactory;
    }

    public ModuleCompleteKey getNotificationKey() {
        return this.notificationKey;
    }

    public Class<PAYLOAD> getPayloadType() {
        return this.moduleClass;
    }

    public PAYLOAD getModule() {
        throw new UnsupportedOperationException(String.format("[%s] can not produce [%s] on its own, you'll want to use the create method.", ((Object)((Object)this)).getClass().getName(), this.getPayloadType()));
    }

    @Override
    protected Map<XPath, Function<Element, ModuleDescriptor>> getDescriptorFactories() {
        this.notificationKey = new ModuleCompleteKey(this.getCompleteKey());
        try {
            this.moduleClass = this.plugin.loadClass(this.moduleClassName, null);
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException((Throwable)e);
        }
        return ImmutableMap.builder().put((Object)NotificationDescriptor.xpath("transformer"), descriptorConfiguration -> new NotificationTransformerDescriptor(this.moduleFactory, new ModuleCompleteKey(this.getCompleteKey()))).put((Object)NotificationDescriptor.xpath("recipients"), descriptorConfiguration -> new RecipientProviderDescriptor(this.moduleFactory)).put((Object)NotificationDescriptor.xpath("template"), descriptorConfiguration -> new NotificationTemplateDescriptor(this.moduleFactory, this.pluginController, new ModuleCompleteKey(this.getCompleteKey()))).build();
    }

    protected void loadClass(Plugin plugin, String payloadClass) throws PluginParseException {
        super.loadClass(plugin, payloadClass);
        this.notificationFactory = new DescriptorBasedNotificationFactory(this);
        this.notificationEventFactory = new DescriptorBasedNotificationEventFactory(this, this.objectMapperFactory);
    }

    public NotificationFactory<PAYLOAD> getNotificationFactory() {
        return this.notificationFactory;
    }

    public void setNotificationFactory(NotificationFactory<PAYLOAD> notificationFactory) {
        this.notificationFactory = notificationFactory;
    }

    public NotificationEventFactory<PAYLOAD> getNotificationEventFactory() {
        return this.notificationEventFactory;
    }
}

