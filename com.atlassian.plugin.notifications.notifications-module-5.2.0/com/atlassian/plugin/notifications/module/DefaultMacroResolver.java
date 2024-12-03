/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.TextUtil;
import com.atlassian.plugin.notifications.api.macros.Macro;
import com.atlassian.plugin.notifications.api.macros.MacroResolver;
import com.atlassian.plugin.notifications.module.macros.NotificationMacroModuleDescriptor;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.log4j.Logger;

public class DefaultMacroResolver
implements MacroResolver,
TextUtil.MacroKeyReplacer {
    private static final Logger log = Logger.getLogger(DefaultMacroResolver.class);
    private final PluginModuleTracker<Macro, NotificationMacroModuleDescriptor> macroTracker;

    public DefaultMacroResolver(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.macroTracker = DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, NotificationMacroModuleDescriptor.class);
    }

    @Override
    public String resolveAll(String text, Map<String, Object> context) {
        return TextUtil.replaceMacroKeys(text, context, this);
    }

    @Override
    public String replace(final String macroKey, Map<String, Object> context) {
        Macro macro = (Macro)Iterables.find((Iterable)this.macroTracker.getModules(), (Predicate)new Predicate<Macro>(){

            public boolean apply(@Nullable Macro input) {
                return input != null && input.getName().equals(macroKey);
            }
        }, null);
        if (macro == null) {
            return macroKey;
        }
        try {
            return macro.resolve(context);
        }
        catch (RuntimeException e) {
            log.error((Object)("Could not load macro with key '" + macroKey + "'"));
            return macroKey;
        }
    }
}

