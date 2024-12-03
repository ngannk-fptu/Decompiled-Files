/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.baseconditions.AbstractConditionElementParser
 *  com.atlassian.plugin.web.baseconditions.CompositeCondition
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.baseconditions.AbstractConditionElementParser;
import com.atlassian.plugin.web.baseconditions.CompositeCondition;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.plugin.webresource.condition.DecoratingAndCompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.condition.DecoratingLegacyCondition;
import com.atlassian.plugin.webresource.condition.DecoratingOrCompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingUrlReadingCondition;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.util.PluginClassLoader;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Element;

public class UrlReadingConditionElementParser
extends AbstractConditionElementParser<DecoratingCondition> {
    private final HostContainer hostContainer;

    public UrlReadingConditionElementParser(HostContainer hostContainer) {
        this.hostContainer = hostContainer;
    }

    protected DecoratingCondition makeConditionImplementation(Plugin plugin, Element element) throws PluginParseException {
        try {
            String conditionClassName;
            String overrideConditionClassName = element.attributeValue("class2");
            String string = conditionClassName = null != overrideConditionClassName ? overrideConditionClassName : element.attributeValue("class");
            if (conditionClassName == null) {
                throw new PluginParseException("Condition element must specify a class attribute");
            }
            Map params = LoaderUtils.getParams((Element)element);
            return this.create(plugin, conditionClassName, params);
        }
        catch (ClassCastException e) {
            throw new PluginParseException("Configured condition class does not implement the Condition interface", (Throwable)e);
        }
        catch (ConditionLoadingException cle) {
            throw new PluginParseException("Unable to load the module's display conditions: " + cle.getMessage(), (Throwable)cle);
        }
    }

    private DecoratingCondition create(Plugin plugin, String className, Map<String, String> params) throws ConditionLoadingException {
        Object o = this.createObject(plugin, className);
        if (o instanceof UrlReadingCondition) {
            UrlReadingCondition urlReadingCondition = (UrlReadingCondition)o;
            HashMap<String, String> paramsCopy = new HashMap<String, String>(params);
            urlReadingCondition.init(params);
            return new DecoratingUrlReadingCondition(urlReadingCondition, paramsCopy, plugin.getKey(), className);
        }
        Condition condition = (Condition)o;
        condition.init(params);
        return new DecoratingLegacyCondition(condition, plugin.getKey(), className);
    }

    private <T> T createObject(Plugin plugin, String className) throws ConditionLoadingException {
        try {
            return PluginClassLoader.create(plugin, ((Object)((Object)this)).getClass(), this.hostContainer, className);
        }
        catch (ClassNotFoundException e) {
            throw new ConditionLoadingException("Cannot load condition class: " + className, (Throwable)e);
        }
    }

    protected DecoratingCondition invert(DecoratingCondition condition) {
        return condition.invertCondition();
    }

    protected CompositeCondition<DecoratingCondition> createAndCompositeCondition() {
        return new DecoratingAndCompositeCondition();
    }

    protected CompositeCondition<DecoratingCondition> createOrCompositeCondition() {
        return new DecoratingOrCompositeCondition();
    }
}

