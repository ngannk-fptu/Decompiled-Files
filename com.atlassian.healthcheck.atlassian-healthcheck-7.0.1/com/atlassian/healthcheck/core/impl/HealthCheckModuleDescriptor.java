/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.healthcheck.core.impl;

import com.atlassian.healthcheck.core.HealthCheck;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckModuleDescriptor
extends AbstractModuleDescriptor<HealthCheck> {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckModuleDescriptor.class);
    public static final int DEFAULT_TIMEOUT_MILLIS = Integer.getInteger("atlassian.healthcheck.timeout-ms", (int)TimeUnit.SECONDS.toMillis(5L));
    public static final int MAX_TIMEOUT_MILLIS = Integer.getInteger("atlassian.healthcheck.max-timeout-ms", (int)TimeUnit.SECONDS.toMillis(20L));
    private String tag;
    private int timeOut;

    public HealthCheckModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public HealthCheck getModule() {
        return (HealthCheck)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.initializeTimeout(element);
        this.tag = element.attributeValue("tag");
    }

    public int getTimeOut() {
        return this.timeOut;
    }

    public String getTag() {
        return this.tag;
    }

    private void initializeTimeout(Element element) {
        String timeoutString = element.attributeValue("time-out");
        if (timeoutString == null) {
            this.timeOut = DEFAULT_TIMEOUT_MILLIS;
        } else {
            try {
                int customTimeOut = Integer.parseInt(timeoutString);
                if (customTimeOut > MAX_TIMEOUT_MILLIS) {
                    this.timeOut = MAX_TIMEOUT_MILLIS;
                    log.warn("Custom timeout of {} ms exceeds maximum timeout of {} ms, timeout is now set to max timeout", (Object)customTimeOut, (Object)MAX_TIMEOUT_MILLIS);
                } else {
                    this.timeOut = customTimeOut;
                }
            }
            catch (NumberFormatException exception) {
                this.timeOut = DEFAULT_TIMEOUT_MILLIS;
                log.warn("Error parsing custom timeout, timeout is now set to a default of {} ms", (Object)DEFAULT_TIMEOUT_MILLIS);
            }
        }
    }
}

