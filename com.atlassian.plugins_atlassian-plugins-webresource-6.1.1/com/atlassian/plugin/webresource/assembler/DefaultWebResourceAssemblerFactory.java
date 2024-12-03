/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.analytics.EventFiringHelper;
import com.atlassian.plugin.webresource.analytics.events.ServerResourceCacheInvalidationCause;
import com.atlassian.plugin.webresource.analytics.events.ServerResourceCacheInvalidationEvent;
import com.atlassian.plugin.webresource.assembler.DefaultWebResourceAssemblerBuilder;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssemblerBuilder;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssemblerFactory;
import com.atlassian.plugin.webresource.transformer.StaticTransformers;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.collections.CollectionUtils;

public class DefaultWebResourceAssemblerFactory
implements PrebakeWebResourceAssemblerFactory {
    private final Globals globals;

    public DefaultWebResourceAssemblerFactory(Globals globals) {
        this.globals = globals;
    }

    public DefaultWebResourceAssemblerFactory(PluginResourceLocator pluginResourceLocator) {
        this(pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt());
    }

    @Override
    public PrebakeWebResourceAssemblerBuilder create() {
        return new DefaultWebResourceAssemblerBuilder(this.globals);
    }

    @Override
    public Dimensions computeDimensions() {
        Snapshot snapshot = this.globals.getSnapshot();
        Dimensions d = Dimensions.empty();
        for (CachedCondition condition : snapshot.conditions()) {
            d = d.product(condition.computeDimensions());
        }
        for (CachedTransformers transformer : snapshot.transformers()) {
            d = d.product(transformer.computeDimensions(this.globals.getConfig().getTransformerCache()));
        }
        StaticTransformers staticTransformers = this.globals.getConfig().getStaticTransformers();
        d = d.product(staticTransformers.computeDimensions());
        return d;
    }

    @Override
    public Dimensions computeBundleDimensions(Bundle bundle) {
        if (bundle == null) {
            return Dimensions.empty();
        }
        return this.computeBundleDimensionsRecursively(Stream.of(bundle));
    }

    private Dimensions computeBundleDimensionsRecursively(Stream<Bundle> bundles) {
        return bundles.map(b -> {
            List<String> dependencies;
            CachedTransformers transformers;
            Dimensions d = Dimensions.empty();
            Config config = this.globals.getConfig();
            CachedCondition condition = b.getCondition();
            if (condition != null) {
                d = d.product(condition.computeDimensions());
            }
            if ((transformers = b.getTransformers()) != null) {
                d = d.product(transformers.computeDimensions(config.getTransformerCache()));
            }
            if (CollectionUtils.isNotEmpty(dependencies = b.getDependencies())) {
                d = d.product(this.computeBundleDimensionsRecursively(this.globals.getSnapshot().toBundles(dependencies).stream()));
            }
            d = d.product(config.getStaticTransformers().computeBundleDimensions((Bundle)b));
            return d;
        }).reduce(Dimensions::product).orElse(Dimensions.empty());
    }

    @Override
    public String computeGlobalStateHash() {
        return this.globals.getConfig().computeGlobalStateHash();
    }

    @Deprecated
    public void clearCache() {
        this.globals.triggerStateChange();
        EventFiringHelper.publishIfEventPublisherNonNull(this.globals.getEventPublisher(), new ServerResourceCacheInvalidationEvent(ServerResourceCacheInvalidationCause.PROGRAMMATIC_TRIGGER));
    }
}

