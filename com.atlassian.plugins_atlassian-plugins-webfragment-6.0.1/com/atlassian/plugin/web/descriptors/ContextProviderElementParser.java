/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.NoOpContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import org.dom4j.Element;

class ContextProviderElementParser {
    private final WebFragmentHelper webFragmentHelper;

    public ContextProviderElementParser(WebFragmentHelper webFragmentHelper) {
        this.webFragmentHelper = webFragmentHelper;
    }

    public ContextProvider makeContextProvider(Plugin plugin, Element element) throws PluginParseException {
        Assertions.notNull((String)"plugin == null", (Object)plugin);
        try {
            Element contextProviderElement = element.element("context-provider");
            if (contextProviderElement == null) {
                return new NoOpContextProvider();
            }
            ContextProvider context = this.webFragmentHelper.loadContextProvider(contextProviderElement.attributeValue("class"), plugin);
            context.init(LoaderUtils.getParams((Element)contextProviderElement));
            return context;
        }
        catch (ClassCastException e) {
            throw new PluginParseException("Configured context-provider class does not implement the ContextProvider interface", (Throwable)e);
        }
        catch (ConditionLoadingException cle) {
            throw new PluginParseException("Unable to load the module's display conditions: " + cle.getMessage(), (Throwable)cle);
        }
    }
}

