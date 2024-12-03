/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.plugin.webresource.ResourceBatchingConfiguration
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.atlassian.plugin.webresource.impl.Globals
 *  com.atlassian.plugin.webresource.impl.RequestState
 *  com.atlassian.plugin.webresource.impl.UrlBuildingStrategy
 *  com.atlassian.plugin.webresource.impl.discovery.BundleFinder
 *  com.atlassian.plugin.webresource.transformer.TransformerCache
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformerModuleDescriptor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.LinkedListMultimap
 *  com.google.common.collect.Lists
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.one.zero;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.transformer.TransformerCache;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerModuleDescriptor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@AnonymousAllowed
@Path(value="deprecatedDescriptors")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class DeprecatedDescriptors {
    private final PluginAccessor pluginAccessor;
    private final TransformerCache transformerCache;
    private final Globals globals;

    public DeprecatedDescriptors(PluginAccessor pluginAccessor, PluginResourceLocator pluginResourceLocator, WebResourceIntegration wri, ResourceBatchingConfiguration batchingConfiguration) {
        this.pluginAccessor = pluginAccessor;
        this.transformerCache = pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt().getConfig().getTransformerCache();
        this.globals = pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt();
    }

    private static ArrayList<String> nullIfEmpty(Set<String> conditions) {
        return conditions.isEmpty() ? null : new ArrayList<String>(conditions);
    }

    @GET
    public Result getInfo() {
        return new Result(this.condition1s(), this.transform1s(), this.listWebresourceDescriptors());
    }

    private Iterable<WebresourceDescriptor> listWebresourceDescriptors() {
        RequestState requestState = new RequestState(this.globals, UrlBuildingStrategy.normal());
        List superbatch = new BundleFinder(requestState.getSnapshot()).included("_context:_super").end();
        LinkedHashSet superbatchAsSet = new LinkedHashSet(superbatch);
        LinkedListMultimap wr2contexts = LinkedListMultimap.create();
        for (WebResourceModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebResourceModuleDescriptor.class)) {
            String key = descriptor.getCompleteKey();
            HashSet ctx = new HashSet(descriptor.getContexts());
            ctx.remove(key);
            wr2contexts.putAll((Object)key, ctx);
            List dependencies = new BundleFinder(requestState.getSnapshot()).included(key).excludedResolved(superbatchAsSet).end();
            for (String depKey : dependencies) {
                wr2contexts.putAll((Object)depKey, ctx);
            }
        }
        ArrayList<WebresourceDescriptor> descriptors = new ArrayList<WebresourceDescriptor>();
        for (WebResourceModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebResourceModuleDescriptor.class)) {
            Set conditions = descriptor.getDeprecatedConditionKeys();
            Set transforms = descriptor.getDeprecatedTransformKeys(this.transformerCache);
            if (conditions.isEmpty() && transforms.isEmpty()) continue;
            String completeKey = descriptor.getCompleteKey();
            WebresourceDescriptor d = new WebresourceDescriptor(completeKey);
            d.condition1keys = DeprecatedDescriptors.nullIfEmpty(conditions);
            d.transform1keys = DeprecatedDescriptors.nullIfEmpty(transforms);
            Collection contexts = wr2contexts.get((Object)completeKey);
            if (contexts != null && !contexts.isEmpty()) {
                d.context = new ArrayList<String>(new HashSet(contexts));
            }
            if (superbatch.contains(completeKey)) {
                d.superbatch = true;
            }
            descriptors.add(d);
        }
        return descriptors;
    }

    private Iterable<String> transform1s() {
        return Iterables.transform((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebResourceTransformerModuleDescriptor.class), (Function)new Function<WebResourceTransformerModuleDescriptor, String>(){

            public String apply(WebResourceTransformerModuleDescriptor moduleDescriptor) {
                return moduleDescriptor.getCompleteKey();
            }
        });
    }

    private Iterable<String> condition1s() {
        TreeSet<String> conditionKeys = new TreeSet<String>();
        for (WebResourceModuleDescriptor wrmd : this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebResourceModuleDescriptor.class)) {
            conditionKeys.addAll(wrmd.getDeprecatedConditionKeys());
        }
        return conditionKeys;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    @JsonPropertyOrder(alphabetic=true)
    private static class Result {
        @JsonProperty
        public final List<String> condition1s;
        @JsonProperty
        public final List<String> transform1s;
        @JsonProperty
        public final Iterable<WebresourceDescriptor> webresources;

        @JsonCreator
        public Result(Iterable<String> condition1s, Iterable<String> transform1s, Iterable<WebresourceDescriptor> webresources) {
            this.condition1s = Lists.newArrayList(condition1s);
            this.transform1s = Lists.newArrayList(transform1s);
            this.webresources = webresources;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    @JsonPropertyOrder(alphabetic=true)
    public static class WebresourceDescriptor {
        @JsonProperty
        public final String moduleKey;
        @JsonProperty
        public List<String> condition1keys;
        @JsonProperty
        public List<String> transform1keys;
        @JsonProperty
        public List<String> context;
        @JsonProperty
        public Boolean superbatch;

        public WebresourceDescriptor(String moduleKey) {
            this.moduleKey = moduleKey;
        }
    }
}

