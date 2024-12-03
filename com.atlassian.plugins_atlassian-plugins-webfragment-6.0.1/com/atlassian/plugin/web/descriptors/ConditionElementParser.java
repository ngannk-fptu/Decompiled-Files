/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.baseconditions.CompositeCondition
 *  com.atlassian.plugin.web.conditions.AndCompositeCondition
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.plugin.web.conditions.InvertedCondition
 *  com.atlassian.plugin.web.conditions.OrCompositeCondition
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.baseconditions.AbstractConditionElementParser;
import com.atlassian.plugin.web.baseconditions.CompositeCondition;
import com.atlassian.plugin.web.conditions.AndCompositeCondition;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.plugin.web.conditions.InvertedCondition;
import com.atlassian.plugin.web.conditions.OrCompositeCondition;
import java.util.List;
import org.dom4j.Element;

public class ConditionElementParser
extends AbstractConditionElementParser<Condition> {
    private final ConditionFactory conditionFactory;

    public ConditionElementParser(ConditionFactory conditionFactory) {
        this.conditionFactory = conditionFactory;
    }

    @Override
    public Condition makeConditions(Plugin plugin, Element element, int type) throws PluginParseException {
        return (Condition)super.makeConditions(plugin, element, type);
    }

    @Override
    public Condition makeConditions(Plugin plugin, List<Element> elements, int type) throws PluginParseException {
        return (Condition)super.makeConditions(plugin, elements, type);
    }

    @Override
    public Condition makeCondition(Plugin plugin, Element element) throws PluginParseException {
        return (Condition)super.makeCondition(plugin, element);
    }

    @Override
    protected Condition makeConditionImplementation(Plugin plugin, Element element) throws PluginParseException {
        try {
            String conditionClassName = element.attributeValue("class");
            if (conditionClassName == null) {
                throw new PluginParseException("Condition element must specify a class attribute");
            }
            Condition condition = this.conditionFactory.create(conditionClassName, plugin);
            condition.init(LoaderUtils.getParams((Element)element));
            return condition;
        }
        catch (ClassCastException e) {
            throw new PluginParseException("Configured condition class does not implement the Condition interface", (Throwable)e);
        }
        catch (ConditionLoadingException cle) {
            throw new PluginParseException("Unable to load the module's display conditions: " + cle.getMessage(), (Throwable)cle);
        }
    }

    @Override
    protected Condition invert(Condition condition) {
        return new InvertedCondition(condition);
    }

    @Override
    protected CompositeCondition<Condition> createAndCompositeCondition() {
        return new AndCompositeCondition();
    }

    @Override
    protected CompositeCondition<Condition> createOrCompositeCondition() {
        return new OrCompositeCondition();
    }

    public static interface ConditionFactory {
        public Condition create(String var1, Plugin var2) throws ConditionLoadingException;
    }

    @Deprecated
    public static class CompositeType
    extends AbstractConditionElementParser.CompositeType {
    }
}

