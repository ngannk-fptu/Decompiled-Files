/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  javax.servlet.DispatcherType
 *  javax.servlet.Filter
 *  org.dom4j.Element
 */
package com.atlassian.plugin.servlet.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor;
import com.atlassian.plugin.servlet.filter.FilterDispatcherCondition;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import org.dom4j.Element;

public class ServletFilterModuleDescriptor
extends BaseServletModuleDescriptor<Filter>
implements StateAware {
    @VisibleForTesting
    static final String ASYNC_DEFAULT_SYSPROP = "atlassian.plugins.filter.async.default";
    @VisibleForTesting
    static final ResettableLazyReference<Boolean> ASYNC_DEFAULT = new ResettableLazyReference<Boolean>(){

        protected Boolean create() {
            return Boolean.getBoolean(ServletFilterModuleDescriptor.ASYNC_DEFAULT_SYSPROP);
        }
    };
    @VisibleForTesting
    static final String FORCE_ASYNC_DISPATCHER_SYSPROP = "atlassian.plugins.filter.force.async.dispatcher";
    @VisibleForTesting
    static final ResettableLazyReference<Boolean> FORCE_ASYNC = new ResettableLazyReference<Boolean>(){

        protected Boolean create() {
            return Boolean.getBoolean(ServletFilterModuleDescriptor.FORCE_ASYNC_DISPATCHER_SYSPROP);
        }
    };
    static final String DEFAULT_LOCATION = FilterLocation.BEFORE_DISPATCH.name();
    static final String DEFAULT_WEIGHT = "100";
    private final Set<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST);
    private final ServletModuleManager servletModuleManager;
    private FilterLocation location;
    private int weight;
    public static final Comparator<ServletFilterModuleDescriptor> byWeight = Comparator.comparingInt(ServletFilterModuleDescriptor::getWeight);

    public ServletFilterModuleDescriptor(ModuleFactory moduleFactory, ServletModuleManager servletModuleManager) {
        super(moduleFactory);
        this.servletModuleManager = Objects.requireNonNull(servletModuleManager, "servletModuleManager");
    }

    @Override
    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        try {
            this.location = FilterLocation.parse(element.attributeValue("location", DEFAULT_LOCATION));
            this.weight = Integer.parseInt(element.attributeValue("weight", DEFAULT_WEIGHT));
        }
        catch (IllegalArgumentException ex) {
            throw new PluginParseException((Throwable)ex);
        }
        List dispatcherElements = element.elements("dispatcher");
        if (!dispatcherElements.isEmpty()) {
            this.dispatcherTypes.clear();
            for (Element dispatcher : dispatcherElements) {
                this.dispatcherTypes.add(DispatcherType.valueOf((String)dispatcher.getTextTrim()));
            }
        }
        if (((Boolean)FORCE_ASYNC.get()).booleanValue()) {
            this.dispatcherTypes.add(DispatcherType.ASYNC);
        }
    }

    @Override
    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        StringBuilder conditionRule = new StringBuilder();
        conditionRule.append("dispatcher[");
        DispatcherType[] dispatcherTypes = DispatcherType.values();
        for (int x = 0; x < dispatcherTypes.length; ++x) {
            conditionRule.append(". != '").append(dispatcherTypes[x]).append("'");
            if (x + 1 >= dispatcherTypes.length) continue;
            conditionRule.append(" and ");
        }
        conditionRule.append("]");
        pattern.rule(conditionRule.toString(), new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"dispatcher").withError("The dispatcher value must be one of the following only " + Arrays.asList(DispatcherType.values())), ValidationPattern.test((String)"@class").withError("The class is required")});
    }

    public void enabled() {
        super.enabled();
        this.servletModuleManager.addFilterModule(this);
    }

    public void disabled() {
        this.servletModuleManager.removeFilterModule(this);
        super.disabled();
    }

    public Filter getModule() {
        return (Filter)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public FilterLocation getLocation() {
        return this.location;
    }

    public int getWeight() {
        return this.weight;
    }

    @Deprecated
    public Set<FilterDispatcherCondition> getDispatcherConditions() {
        return this.dispatcherTypes.stream().map(FilterDispatcherCondition::fromDispatcherType).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Set<DispatcherType> getDispatcherTypes() {
        return this.dispatcherTypes;
    }

    @Override
    protected boolean getDefaultAsyncSupported() {
        return (Boolean)ASYNC_DEFAULT.get();
    }
}

