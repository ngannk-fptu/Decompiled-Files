/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.web.baseconditions.BaseCondition
 *  com.atlassian.plugin.web.baseconditions.CompositeCondition
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.baseconditions;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.web.baseconditions.BaseCondition;
import com.atlassian.plugin.web.baseconditions.CompositeCondition;
import java.util.List;
import org.dom4j.Element;

public abstract class AbstractConditionElementParser<T extends BaseCondition> {
    public T makeConditions(Plugin plugin, Element element, int type) throws PluginParseException {
        Assertions.notNull((String)"plugin == null", (Object)plugin);
        List singleConditionElements = element.elements("condition");
        BaseCondition singleConditions = null;
        if (singleConditionElements != null && !singleConditionElements.isEmpty()) {
            singleConditions = (BaseCondition)this.makeConditions(plugin, singleConditionElements, type);
        }
        List nestedConditionsElements = element.elements("conditions");
        CompositeCondition<T> nestedConditions = null;
        if (nestedConditionsElements != null && !nestedConditionsElements.isEmpty()) {
            nestedConditions = this.getCompositeCondition(type);
            for (Element nestedElement : nestedConditionsElements) {
                nestedConditions.addCondition(this.makeConditions(plugin, nestedElement, CompositeType.parse(nestedElement.attributeValue("type"))));
            }
        }
        if (singleConditions != null && nestedConditions != null) {
            CompositeCondition<T> compositeCondition = this.getCompositeCondition(type);
            compositeCondition.addCondition(singleConditions);
            compositeCondition.addCondition(nestedConditions);
            return (T)compositeCondition;
        }
        if (singleConditions != null) {
            return (T)singleConditions;
        }
        if (nestedConditions != null) {
            return (T)nestedConditions;
        }
        return null;
    }

    public T makeConditions(Plugin plugin, List<Element> elements, int type) throws PluginParseException {
        if (elements.isEmpty()) {
            return null;
        }
        if (elements.size() == 1) {
            return this.makeCondition(plugin, elements.get(0));
        }
        CompositeCondition<T> compositeCondition = this.getCompositeCondition(type);
        for (Element element : elements) {
            compositeCondition.addCondition(this.makeCondition(plugin, element));
        }
        return (T)compositeCondition;
    }

    public T makeCondition(Plugin plugin, Element element) throws PluginParseException {
        T condition = this.makeConditionImplementation(plugin, element);
        if (element.attribute("invert") != null && "true".equals(element.attributeValue("invert"))) {
            return this.invert(condition);
        }
        return condition;
    }

    protected abstract T makeConditionImplementation(Plugin var1, Element var2) throws PluginParseException;

    protected abstract T invert(T var1);

    protected abstract CompositeCondition<T> createAndCompositeCondition();

    protected abstract CompositeCondition<T> createOrCompositeCondition();

    private CompositeCondition<T> getCompositeCondition(int type) throws PluginParseException {
        switch (type) {
            case 0: {
                return this.createOrCompositeCondition();
            }
            case 1: {
                return this.createAndCompositeCondition();
            }
        }
        throw new PluginParseException("Invalid condition type specified. type = " + type);
    }

    public static class CompositeType {
        public static final int OR = 0;
        public static final int AND = 1;

        public static int parse(String type) throws PluginParseException {
            if ("or".equalsIgnoreCase(type)) {
                return 0;
            }
            if ("and".equalsIgnoreCase(type)) {
                return 1;
            }
            throw new PluginParseException("Invalid condition type specified. type = " + type);
        }
    }
}

