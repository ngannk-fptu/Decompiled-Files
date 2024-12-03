/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ContainerAccessor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.BeanCreationException
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerAccessor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import com.atlassian.troubleshooting.healthcheck.conditions.MinimumApplicationVersionCondition;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

@ParametersAreNonnullByDefault
public class SupportHealthCheckModuleDescriptor
extends AbstractModuleDescriptor<SupportHealthCheck> {
    public static final String ATST_PLUGIN_PREFIX = "com.atlassian.troubleshooting.plugin-";
    private static final Logger LOG = LoggerFactory.getLogger(SupportHealthCheckModuleDescriptor.class);
    private static final int DEFAULT_DEFAULT_TIMEOUT = (int)TimeUnit.SECONDS.toMillis(20L);
    @VisibleForTesting
    static final int DEFAULT_TIMEOUT_MILLIS = Integer.getInteger("atlassian.healthcheck.timeout-ms", DEFAULT_DEFAULT_TIMEOUT);
    private static final int DEFAULT_MAX_TIMEOUT = (int)TimeUnit.SECONDS.toMillis(120L);
    @VisibleForTesting
    static final int MAX_TIMEOUT_MILLIS = Integer.getInteger("atlassian.healthcheck.max-timeout-ms", DEFAULT_MAX_TIMEOUT);
    private final ApplicationContext atstApplicationContext;
    private final ApplicationProperties applicationProperties;
    private final List<SupportHealthCheckCondition> conditions = new ArrayList<SupportHealthCheckCondition>();
    private int timeOut;
    private String tag;
    private String helpPathKey;
    private String className;

    public SupportHealthCheckModuleDescriptor(ModuleFactory moduleFactory, ApplicationContext atstApplicationContext, ApplicationProperties applicationProperties) {
        super(moduleFactory);
        this.atstApplicationContext = Objects.requireNonNull(atstApplicationContext);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
    }

    public SupportHealthCheck getModule() {
        return (SupportHealthCheck)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void init(Plugin plugin, Element healthCheckElement) {
        super.init(plugin, healthCheckElement);
        this.className = healthCheckElement.attributeValue("class");
        this.helpPathKey = healthCheckElement.attributeValue("help-path");
        this.tag = healthCheckElement.attributeValue("tag");
        this.timeOut = SupportHealthCheckModuleDescriptor.parseTimeout(healthCheckElement.attributeValue("time-out"));
        this.parseMinimumProductVersionCondition(healthCheckElement).ifPresent(this.conditions::add);
        this.conditions.addAll(this.parseChildConditions(healthCheckElement.elements("condition")));
    }

    private Collection<SupportHealthCheckCondition> parseChildConditions(List<Element> conditionElements) {
        return conditionElements.stream().map(this::parseCondition).collect(Collectors.toList());
    }

    private Optional<SupportHealthCheckCondition> parseMinimumProductVersionCondition(@Nonnull Element element) {
        return Optional.of(element).map(el -> el.attributeValue("application-minimum-version")).filter(StringUtils::isNotBlank).map(minimumVersion -> new MinimumApplicationVersionCondition(this.applicationProperties, (String)minimumVersion));
    }

    private SupportHealthCheckCondition parseCondition(Element conditionElement) {
        String conditionClassName = conditionElement.attributeValue("class");
        return Optional.ofNullable(StringUtils.trimToNull((String)conditionClassName)).flatMap(this::getConditionClass).map(this::instantiateCondition).orElseThrow(() -> new PluginParseException("Cannot instantiate '" + conditionClassName + "'"));
    }

    private Optional<Class<SupportHealthCheckCondition>> getConditionClass(String conditionClassName) {
        try {
            return Optional.ofNullable(this.plugin.loadClass(conditionClassName, this.plugin.getClass()));
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException("Could not parse health check condition.", (Throwable)e);
        }
    }

    private SupportHealthCheckCondition instantiateCondition(Class<SupportHealthCheckCondition> conditionClass) {
        if (StringUtils.startsWith((CharSequence)this.getPluginKey(), (CharSequence)ATST_PLUGIN_PREFIX)) {
            return (SupportHealthCheckCondition)Objects.requireNonNull(this.atstApplicationContext.getBean(conditionClass), () -> "ATST does not contain a bean of type " + conditionClass.getName());
        }
        return this.getContainerAccessorOfDeclaringPlugin().map(containerAccessor -> (SupportHealthCheckCondition)containerAccessor.createBean(conditionClass)).orElseThrow(() -> this.getConditionCreationException(conditionClass));
    }

    private BeanCreationException getConditionCreationException(Class<SupportHealthCheckCondition> conditionClass) {
        return new BeanCreationException(String.format("Could not create bean of type %s using ContainerAccessor of plugin %s", conditionClass.getName(), this.plugin.getKey()));
    }

    private Optional<ContainerAccessor> getContainerAccessorOfDeclaringPlugin() {
        return Optional.ofNullable(this.plugin).filter(ContainerManagedPlugin.class::isInstance).map(ContainerManagedPlugin.class::cast).map(ContainerManagedPlugin::getContainerAccessor);
    }

    public int getTimeOut() {
        return this.timeOut;
    }

    public String getHelpPathKey() {
        return this.helpPathKey;
    }

    public String getClassName() {
        return this.className;
    }

    @VisibleForTesting
    static int parseTimeout(@Nullable String timeoutString) {
        if (StringUtils.isBlank((CharSequence)timeoutString)) {
            return DEFAULT_TIMEOUT_MILLIS;
        }
        try {
            int customTimeOut = Integer.parseInt(timeoutString.trim());
            if (customTimeOut > MAX_TIMEOUT_MILLIS) {
                LOG.warn("Custom timeout of {} ms exceeds maximum timeout of {} ms, timeout is now set to max timeout", (Object)customTimeOut, (Object)MAX_TIMEOUT_MILLIS);
                return MAX_TIMEOUT_MILLIS;
            }
            return customTimeOut;
        }
        catch (NumberFormatException exception) {
            LOG.warn("Error parsing custom timeout, timeout is now set to a default of {} ms", (Object)DEFAULT_TIMEOUT_MILLIS);
            return DEFAULT_TIMEOUT_MILLIS;
        }
    }

    public String getTag() {
        return this.tag;
    }

    public List<SupportHealthCheckCondition> getConditions() {
        return this.conditions;
    }
}

