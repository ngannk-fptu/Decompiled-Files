/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.mail.embed.MimeBodyPartRecorder
 *  com.atlassian.confluence.mail.embed.MimeBodyPartReference
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.activation.DataSource;

public class ResourceImageFunction
implements SoyServerFunction<String> {
    public static final ImmutableSet<Integer> VALID_ARG_SIZES = ImmutableSet.builder().add((Object[])new Integer[]{1, 2}).build();
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private final DataSourceFactory dataSourceFactory;
    private final MimeBodyPartRecorder bodyPartRecorder;

    public ResourceImageFunction(DataSourceFactory dataSourceFactory, MimeBodyPartRecorder bodyPartRecorder) {
        this.dataSourceFactory = dataSourceFactory;
        this.bodyPartRecorder = bodyPartRecorder;
    }

    public String apply(Object ... args) {
        DataSource resource;
        ModuleCompleteKey moduleCompleteKey = args[0] instanceof ModuleCompleteKey ? (ModuleCompleteKey)args[0] : new ModuleCompleteKey((String)args[0]);
        Maybe maybePluginDataSourceFactory = this.dataSourceFactory.forPlugin(moduleCompleteKey.getPluginKey());
        if (maybePluginDataSourceFactory.isEmpty()) {
            log.warnOrDebug("Plugin [%s] does not exist or is disabled.", new Object[]{moduleCompleteKey.getPluginKey()});
            return moduleCompleteKey.getCompleteKey();
        }
        PluginDataSourceFactory pluginDataSourceFactory = (PluginDataSourceFactory)maybePluginDataSourceFactory.get();
        if (args.length == 2) {
            String resourceName = (String)args[1];
            Maybe maybeResource = pluginDataSourceFactory.resourceFromModuleByName(moduleCompleteKey.getModuleKey(), resourceName);
            if (maybeResource.isEmpty()) {
                log.warnOrDebug("Either the module [%s] or a nested resource with name [%s] does not exist in plugin [%s].", new Object[]{moduleCompleteKey.getModuleKey(), resourceName, moduleCompleteKey.getPluginKey()});
                return moduleCompleteKey.getCompleteKey();
            }
            resource = (DataSource)maybeResource.get();
        } else {
            Maybe maybeResources = pluginDataSourceFactory.resourcesFromModules(moduleCompleteKey.getModuleKey());
            if (maybeResources.isEmpty()) {
                log.warnOrDebug("Resource module [%s] does not exist in plugin [%s].", new Object[]{moduleCompleteKey.getModuleKey(), moduleCompleteKey.getPluginKey()});
                return moduleCompleteKey.getCompleteKey();
            }
            try {
                resource = (DataSource)Iterables.getOnlyElement((Iterable)((Iterable)maybeResources.get()));
            }
            catch (NoSuchElementException e) {
                log.warnOrDebug("Module [%s] in plugin [%s] is not a resource and does not contain nested resources.", new Object[]{moduleCompleteKey.getModuleKey(), moduleCompleteKey.getPluginKey()});
                return moduleCompleteKey.getCompleteKey();
            }
            catch (IllegalArgumentException e) {
                log.warnOrDebug("Module [%s] in plugin [%s] is not a resource, please provide the resource name as a second argument in case it is nested inside the module.", new Object[]{moduleCompleteKey.getModuleKey(), moduleCompleteKey.getPluginKey()});
                return moduleCompleteKey.getCompleteKey();
            }
        }
        return ((MimeBodyPartReference)this.bodyPartRecorder.track(resource).get()).getLocator().toASCIIString();
    }

    public String getName() {
        return "resourceImage";
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARG_SIZES;
    }
}

