/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.jmx;

import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.jmx.WebhooksMXBeanAdapter;
import com.atlassian.webhooks.internal.publish.WebhookDispatcher;
import com.google.common.annotations.VisibleForTesting;
import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxBootstrap
implements WebhooksLifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(JmxBootstrap.class);
    private final WebhookDispatcher dispatcher;
    private final WebhookService webhookService;
    private volatile ObjectName mbeanName;

    public JmxBootstrap(WebhookDispatcher dispatcher, WebhookService webhookService) {
        this.dispatcher = dispatcher;
        this.webhookService = webhookService;
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        try {
            this.mbeanName = new ObjectName(configuration.getJmxDomain() + ":name=Webhooks");
        }
        catch (MalformedObjectNameException e) {
            log.warn("Could not determine webhooks MBean name", (Throwable)e);
        }
        MBeanServer server = this.getMBeanServer();
        if (server != null && this.mbeanName != null) {
            try {
                server.registerMBean(new WebhooksMXBeanAdapter(this.dispatcher, this.webhookService), this.mbeanName);
            }
            catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
                log.warn("Failed to register MBean {}", (Object)this.mbeanName, (Object)e);
            }
        }
    }

    @Override
    public void onStop() {
        MBeanServer server = this.getMBeanServer();
        ObjectName name = this.mbeanName;
        if (server != null && name != null) {
            try {
                server.unregisterMBean(this.mbeanName);
            }
            catch (InstanceNotFoundException instanceNotFoundException) {
            }
            catch (MBeanRegistrationException e) {
                log.info("Failed to unregister MBean {}", (Object)this.mbeanName, (Object)e);
            }
        }
        this.mbeanName = null;
    }

    @VisibleForTesting
    MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}

