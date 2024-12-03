/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebPanelFunction
implements SoyServerFunction<List<String>>,
SoyClientFunction {
    private static final Logger LOG = LoggerFactory.getLogger(WebPanelFunction.class);
    private static final Set<Integer> VALID_ARG_SIZES = ImmutableSet.of((Object)1, (Object)2);
    private final WebInterfaceManager webInterfaceManager;

    public WebPanelFunction(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    public String getName() {
        return "getWebPanels";
    }

    public List<String> apply(Object ... args) {
        String location = (String)args[0];
        ConfluenceActionSupport dummyAction = new ConfluenceActionSupport();
        ContainerManager.autowireComponent((Object)dummyAction);
        ImmutableMap.Builder contextBuilder = ImmutableMap.builder();
        contextBuilder.putAll(Maps.filterValues((Map)dummyAction.getContext(), (Predicate)Predicates.notNull()));
        if (args.length == 2) {
            contextBuilder.putAll((Map)args[1]);
        }
        ImmutableMap context = contextBuilder.build();
        ArrayList<String> webPanels = new ArrayList<String>();
        for (WebPanelModuleDescriptor webPanelModuleDescriptor : this.webInterfaceManager.getDisplayableWebPanelDescriptors(location, (Map)context)) {
            try {
                webPanels.add(((WebPanel)webPanelModuleDescriptor.getModule()).getHtml((Map)context));
            }
            catch (RuntimeException e) {
                LOG.warn(String.format("An error occurred rendering %s. Ignoring", webPanelModuleDescriptor.getCompleteKey()), (Throwable)e);
            }
        }
        return webPanels;
    }

    public JsExpression generate(JsExpression ... jsExpressions) {
        return new JsExpression("'Client-side web panels are not plugged in yet'");
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARG_SIZES;
    }
}

