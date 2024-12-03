/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.descriptors.ModuleDescriptors$EqualsBuilder
 *  com.atlassian.plugin.descriptors.ModuleDescriptors$HashCodeBuilder
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.plugin.web.model.WebParam
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.descriptors.ModuleDescriptors;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.plugin.web.descriptors.ConditionElementParser;
import com.atlassian.plugin.web.descriptors.ContextProviderElementParser;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WeightElementParser;
import com.atlassian.plugin.web.model.DefaultWebLabel;
import com.atlassian.plugin.web.model.DefaultWebParam;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.plugin.web.model.WebParam;
import java.util.List;
import org.dom4j.Element;

public abstract class AbstractWebFragmentModuleDescriptor<T>
extends AbstractModuleDescriptor<T>
implements StateAware,
WebFragmentModuleDescriptor<T> {
    protected WebInterfaceManager webInterfaceManager;
    protected Element element;
    protected int weight;
    protected Condition condition;
    protected ContextProvider contextProvider;
    protected DefaultWebLabel label;
    protected DefaultWebLabel tooltip;
    protected WebParam params;
    private ConditionElementParser conditionElementParser;
    private ContextProviderElementParser contextProviderElementParser;

    protected AbstractWebFragmentModuleDescriptor(WebInterfaceManager webInterfaceManager) {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
        this.setWebInterfaceManager(webInterfaceManager);
    }

    public AbstractWebFragmentModuleDescriptor() {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
    }

    public AbstractWebFragmentModuleDescriptor(ModuleFactory moduleClassFactory, WebInterfaceManager webInterfaceManager) {
        super(moduleClassFactory);
        this.setWebInterfaceManager(webInterfaceManager);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.element = element;
        this.weight = WeightElementParser.getWeight(element);
    }

    protected Condition makeConditions(Element element, int type) throws PluginParseException {
        return this.getRequiredConditionElementParser().makeConditions(this.plugin, element, type);
    }

    protected Condition makeConditions(List elements, int type) throws PluginParseException {
        return this.getRequiredConditionElementParser().makeConditions(this.plugin, (List<Element>)elements, type);
    }

    protected Condition makeCondition(Element element) throws PluginParseException {
        return this.getRequiredConditionElementParser().makeCondition(this.plugin, element);
    }

    protected ContextProvider makeContextProvider(Element element) throws PluginParseException {
        return this.contextProviderElementParser.makeContextProvider(this.plugin, element.getParent());
    }

    private ConditionElementParser getRequiredConditionElementParser() {
        if (this.conditionElementParser == null) {
            throw new IllegalStateException("ModuleDescriptorHelper not available because the WebInterfaceManager has not been injected.");
        }
        return this.conditionElementParser;
    }

    public void enabled() {
        super.enabled();
        try {
            this.contextProvider = this.contextProviderElementParser.makeContextProvider(this.plugin, this.element);
            if (this.element.element("label") != null) {
                this.label = new DefaultWebLabel(this.element.element("label"), this.webInterfaceManager.getWebFragmentHelper(), this.contextProvider, (WebFragmentModuleDescriptor)this);
            }
            if (this.element.element("tooltip") != null) {
                this.tooltip = new DefaultWebLabel(this.element.element("tooltip"), this.webInterfaceManager.getWebFragmentHelper(), this.contextProvider, (WebFragmentModuleDescriptor)this);
            }
            if (this.getParams() != null) {
                this.params = new DefaultWebParam(this.getParams(), this.webInterfaceManager.getWebFragmentHelper(), this.contextProvider, (WebFragmentModuleDescriptor)this);
            }
            this.condition = this.makeConditions(this.element, 1);
        }
        catch (PluginParseException e) {
            throw new RuntimeException("Unable to enable web fragment", e);
        }
        this.webInterfaceManager.refresh();
    }

    public void disabled() {
        this.condition = null;
        this.webInterfaceManager.refresh();
        super.disabled();
    }

    public int getWeight() {
        return this.weight;
    }

    public WebLabel getWebLabel() {
        return this.label;
    }

    public WebLabel getTooltip() {
        return this.tooltip;
    }

    public void setWebInterfaceManager(final WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
        this.conditionElementParser = new ConditionElementParser(new ConditionElementParser.ConditionFactory(){

            @Override
            public Condition create(String className, Plugin plugin) throws ConditionLoadingException {
                return webInterfaceManager.getWebFragmentHelper().loadCondition(className, plugin);
            }
        });
        this.contextProviderElementParser = new ContextProviderElementParser(webInterfaceManager.getWebFragmentHelper());
    }

    public Condition getCondition() {
        return this.condition;
    }

    public ContextProvider getContextProvider() {
        return this.contextProvider;
    }

    public WebParam getWebParams() {
        return this.params;
    }

    public boolean equals(Object obj) {
        return new ModuleDescriptors.EqualsBuilder().descriptor((ModuleDescriptor)this).isEqualTo(obj);
    }

    public int hashCode() {
        return new ModuleDescriptors.HashCodeBuilder().descriptor((ModuleDescriptor)this).toHashCode();
    }
}

