/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.util.GadgetSpecUrlBuilder
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 */
package com.atlassian.gadgets.publisher;

import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.publisher.internal.impl.GadgetConditionElementParser;
import com.atlassian.gadgets.util.GadgetSpecUrlBuilder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugin.web.Condition;
import com.google.common.base.Preconditions;
import org.dom4j.Element;

public final class GadgetModuleDescriptor
extends AbstractModuleDescriptor<PluginGadgetSpec> {
    private GadgetConditionElementParser gadgetConditionElementParser;
    private Element element;
    private PluginGadgetSpec pluginGadgetSpec;

    public GadgetModuleDescriptor(GadgetConditionElementParser gadgetConditionElementParser, ModuleFactory moduleFactory) {
        super(moduleFactory);
        this.gadgetConditionElementParser = gadgetConditionElementParser;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init((Plugin)Preconditions.checkNotNull((Object)plugin, (Object)"plugin"), (Element)Preconditions.checkNotNull((Object)element, (Object)"element"));
        this.element = element;
        this.checkPublishUrlMatchesPattern(element.attributeValue("publish-location"));
    }

    public void enabled() {
        super.enabled();
        String location = this.element.attributeValue("location");
        String publishLocation = this.element.attributeValue("publish-location");
        Condition enabledCondition = this.makeCondition(GadgetConditionElementParser.GadgetConditionScope.ENABLED);
        Condition localCondition = this.makeCondition(GadgetConditionElementParser.GadgetConditionScope.LOCAL);
        this.pluginGadgetSpec = PluginGadgetSpec.builder().enabledCondition(enabledCondition).localCondition(localCondition).location(location).moduleKey(this.getKey()).params(this.getParams()).publishLocation(publishLocation).plugin(this.plugin).build();
    }

    private void checkPublishUrlMatchesPattern(String publishLocation) {
        if (publishLocation != null && !GadgetSpecUrlBuilder.GADGET_SPEC_URL_PATTERN.matcher(publishLocation).matches()) {
            throw new PluginParseException("Gadget publish location (" + publishLocation + ") does not match the pattern of \"[context]/[location]\"");
        }
    }

    public PluginGadgetSpec getModule() {
        return this.pluginGadgetSpec;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@location").withError("The location is required")});
    }

    private Condition makeCondition(GadgetConditionElementParser.GadgetConditionScope scope) {
        return this.gadgetConditionElementParser.makeGadgetConditions(this.element, scope, this.plugin);
    }
}

