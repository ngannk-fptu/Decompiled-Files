/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.snapshot;

import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Deprecation;
import com.atlassian.plugin.webresource.impl.snapshot.RootPage;
import com.atlassian.plugin.webresource.impl.snapshot.WebResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class Snapshot {
    public final Config config;
    final Map<WebResource, CachedTransformers> webResourcesTransformations;
    final Map<WebResource, CachedCondition> webResourcesCondition;
    final Map<String, Deprecation> webResourceDeprecationWarnings;
    final Set<WebResource> webResourcesWithLegacyConditions;
    final Set<WebResource> webResourcesWithLegacyTransformers;
    @Deprecated
    final Set<WebResource> webResourcesWithDisabledMinification;
    private final Map<String, Bundle> cachedBundles;
    private final Map<String, RootPage> rootPages;

    public Snapshot(Config config) {
        this(config, new HashMap<String, Bundle>(), new HashMap<String, RootPage>(), new HashMap<WebResource, CachedTransformers>(), new HashMap<WebResource, CachedCondition>(), new HashSet<WebResource>(), new HashSet<WebResource>(), new HashSet<WebResource>());
    }

    public Snapshot(Config config, Map<String, Bundle> cachedBundles, Map<String, RootPage> rootPages, Map<WebResource, CachedTransformers> webResourcesTransformations, Map<WebResource, CachedCondition> webResourcesCondition, Set<WebResource> webResourcesWithLegacyConditions, Set<WebResource> webResourcesWithLegacyTransformers, Set<WebResource> webResourcesWithDisabledMinification) {
        this(config, cachedBundles, rootPages, webResourcesTransformations, webResourcesCondition, new HashMap<String, Deprecation>(), webResourcesWithLegacyConditions, webResourcesWithLegacyTransformers, webResourcesWithDisabledMinification);
    }

    public Snapshot(Config config, Map<String, Bundle> cachedBundles, Map<String, RootPage> rootPages, Map<WebResource, CachedTransformers> webResourcesTransformations, Map<WebResource, CachedCondition> webResourcesCondition, Map<String, Deprecation> webResourceDeprecationWarnings, Set<WebResource> webResourcesWithLegacyConditions, Set<WebResource> webResourcesWithLegacyTransformers, Set<WebResource> webResourcesWithDisabledMinification) {
        this.cachedBundles = cachedBundles;
        this.rootPages = rootPages;
        this.webResourcesTransformations = webResourcesTransformations;
        this.webResourcesCondition = webResourcesCondition;
        this.webResourcesWithLegacyConditions = webResourcesWithLegacyConditions;
        this.webResourcesWithLegacyTransformers = webResourcesWithLegacyTransformers;
        this.webResourcesWithDisabledMinification = webResourcesWithDisabledMinification;
        this.webResourceDeprecationWarnings = webResourceDeprecationWarnings;
        this.config = config;
    }

    public List<Bundle> toBundles(Iterable<String> keys) {
        ArrayList<Bundle> bundles = new ArrayList<Bundle>();
        for (String key : keys) {
            Bundle bundle = this.get(key);
            if (bundle == null) continue;
            bundles.add(bundle);
        }
        return bundles;
    }

    public void forEachBundle(Consumer<Bundle> consumer) {
        this.cachedBundles.values().forEach(consumer);
    }

    public Bundle get(String key) {
        return this.cachedBundles.get(key);
    }

    @Nonnull
    public RootPage getRootPage(String key) {
        return Optional.ofNullable(this.rootPages.get(key)).orElseThrow(() -> new IllegalArgumentException("Root page '" + key + "' does not exist!"));
    }

    public Iterable<RootPage> getAllRootPages() {
        return this.rootPages.values();
    }

    public Iterable<CachedCondition> conditions() {
        return this.webResourcesCondition.values();
    }

    public Iterable<CachedTransformers> transformers() {
        return this.webResourcesTransformations.values();
    }

    public BundleFinder find() {
        return new BundleFinder(this);
    }

    public Set<WebResource> getWebResourcesWithLegacyConditions() {
        return this.webResourcesWithLegacyConditions;
    }

    public Set<WebResource> getWebResourcesWithLegacyTransformers() {
        return this.webResourcesWithLegacyTransformers;
    }
}

