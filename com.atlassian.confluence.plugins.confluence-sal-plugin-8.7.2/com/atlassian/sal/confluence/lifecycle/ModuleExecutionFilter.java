/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.confluence.lifecycle.ServiceExecutionStrategy;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Objects;
import java.util.Set;
import org.osgi.framework.ServiceReference;

public class ModuleExecutionFilter<S>
implements ServiceExecutionStrategy<S> {
    private final Function<ServiceReference, Option<ModuleCompleteKey>> moduleReferenceParser;
    private final ServiceExecutionStrategy<S> delegate;
    private final Set<ModuleCompleteKey> moduleWhiteList;
    private static final Function<String, ModuleCompleteKey> MODULE_COMPLETE_KEY_FACTORY = ModuleCompleteKey::new;

    public ModuleExecutionFilter(Function<ServiceReference, Option<ModuleCompleteKey>> moduleReferenceParser, ServiceExecutionStrategy<S> delegate, Set<String> moduleWhiteList) {
        this.moduleReferenceParser = moduleReferenceParser;
        this.delegate = delegate;
        this.moduleWhiteList = ImmutableSet.copyOf((Iterable)Iterables.transform(moduleWhiteList, MODULE_COMPLETE_KEY_FACTORY));
    }

    @Override
    public boolean add(ServiceReference serviceReference, Function<S, ?> serviceCallback) {
        Option maybeModuleKey = Objects.requireNonNull((Option)this.moduleReferenceParser.apply((Object)serviceReference));
        if (maybeModuleKey.isEmpty()) {
            return false;
        }
        if (!this.moduleWhiteList.contains(maybeModuleKey.get())) {
            return false;
        }
        return this.delegate.add(serviceReference, serviceCallback);
    }

    @Override
    public void trigger() {
        this.delegate.trigger();
    }
}

