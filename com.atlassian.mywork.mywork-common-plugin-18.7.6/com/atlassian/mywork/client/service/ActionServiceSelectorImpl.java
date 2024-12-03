/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 */
package com.atlassian.mywork.client.service;

import com.atlassian.mywork.service.ActionService;
import com.atlassian.mywork.service.ActionServiceSelector;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class ActionServiceSelectorImpl
implements ActionServiceSelector {
    private final PluginAccessor pluginAccessor;

    public ActionServiceSelectorImpl(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public ActionService get(final String application) {
        return (ActionService)Iterables.find((Iterable)this.pluginAccessor.getEnabledModulesByClass(ActionService.class), (Predicate)new Predicate<ActionService>(){

            public boolean apply(ActionService service) {
                return service.getApplication().equals(application);
            }
        });
    }
}

