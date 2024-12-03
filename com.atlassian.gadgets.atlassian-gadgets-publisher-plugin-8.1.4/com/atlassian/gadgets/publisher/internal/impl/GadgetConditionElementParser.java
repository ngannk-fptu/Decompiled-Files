/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.baseconditions.AbstractConditionElementParser$CompositeType
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.plugin.web.descriptors.ConditionElementParser
 *  com.atlassian.plugin.web.descriptors.ConditionElementParser$ConditionFactory
 *  org.dom4j.Element
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.baseconditions.AbstractConditionElementParser;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.plugin.web.descriptors.ConditionElementParser;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GadgetConditionElementParser {
    private final ConditionElementParser conditionElementParser;

    @Autowired
    public GadgetConditionElementParser(final @ComponentImport WebInterfaceManager webInterfaceManager) {
        this.conditionElementParser = new ConditionElementParser(new ConditionElementParser.ConditionFactory(){

            public Condition create(String className, Plugin plugin) throws ConditionLoadingException {
                WebFragmentHelper webFragmentHelper = webInterfaceManager.getWebFragmentHelper();
                return webFragmentHelper == null ? null : webFragmentHelper.loadCondition(className, plugin);
            }
        });
    }

    public Condition makeGadgetConditions(Element element, GadgetConditionScope scope, Plugin plugin) {
        Element scopedCondition = element.element(scope.elementName());
        if (scopedCondition == null) {
            return null;
        }
        int compositeType = this.getCompositeConditionType(scopedCondition);
        return this.conditionElementParser.makeConditions(plugin, scopedCondition, compositeType);
    }

    public Condition makeDashboardItemCondition(Element element, Plugin plugin) {
        return this.conditionElementParser.makeCondition(plugin, element);
    }

    public Condition makeDashboardItemConditions(Element element, Plugin plugin) {
        int compositeType = this.getCompositeConditionType(element);
        return this.conditionElementParser.makeConditions(plugin, element, compositeType);
    }

    private int getCompositeConditionType(Element element) {
        String type = element.attributeValue("type");
        return type == null ? 1 : AbstractConditionElementParser.CompositeType.parse((String)type);
    }

    public static enum GadgetConditionScope {
        ENABLED,
        LOCAL;


        public String elementName() {
            return this.name().toLowerCase() + "-conditions";
        }
    }
}

