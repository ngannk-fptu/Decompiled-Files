/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.event.events.plugin.XStreamStateChangeEvent;
import com.atlassian.confluence.impl.xstream.security.XStreamSecurityConfigurator;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class XStreamSecurityModuleDescriptor
extends AbstractModuleDescriptor {
    private final XStreamSecurityConfigurator xStreamSecurityConfigurator;
    private final EventPublisher eventPublisher;
    private Element element;
    private String pluginKey;

    public XStreamSecurityModuleDescriptor(ModuleFactory moduleFactory, XStreamSecurityConfigurator xStreamSecurityConfigurator, EventPublisher eventPublisher) {
        super(moduleFactory);
        this.xStreamSecurityConfigurator = xStreamSecurityConfigurator;
        this.eventPublisher = eventPublisher;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.pluginKey = plugin.getKey();
        this.element = element;
    }

    public void enabled() {
        super.enabled();
        List typeElements = this.element.elements("type");
        List typesByRegExpElements = this.element.elements("regex");
        List wildcardElements = this.element.elements("wildcard");
        Set<String> types = this.getChildren(typeElements);
        Set<String> typesByRegExps = this.getChildren(typesByRegExpElements);
        Set<String> wildcards = this.getChildren(wildcardElements);
        this.xStreamSecurityConfigurator.addAllowTypes(this.pluginKey, types);
        this.xStreamSecurityConfigurator.addAllowTypesByRegExps(this.pluginKey, typesByRegExps);
        this.xStreamSecurityConfigurator.addAllowTypesByWildcard(this.pluginKey, wildcards);
        this.eventPublisher.publish((Object)new XStreamStateChangeEvent((Object)this));
    }

    public void disabled() {
        super.disabled();
        this.xStreamSecurityConfigurator.clearPluginSecurityData(this.pluginKey);
        this.eventPublisher.publish((Object)new XStreamStateChangeEvent((Object)this));
    }

    public Object getModule() {
        return null;
    }

    private Set<String> getChildren(List<Element> elements) {
        return elements.stream().map(Element::getText).collect(Collectors.toSet());
    }
}

