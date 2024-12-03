/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 */
package com.atlassian.audit.core.ecosystem;

import com.atlassian.audit.core.ecosystem.BundleDetector;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;

public class AllowlistedBundleDetector
implements BundleDetector {
    private final BundleDetector delegate;
    private final Set<String> allowlisted;

    public AllowlistedBundleDetector(BundleDetector delegate, @Nonnull Collection<String> allowlistedPluginKeys) {
        this.delegate = delegate;
        this.allowlisted = Sets.union((Set)ImmutableSet.copyOf(allowlistedPluginKeys), Collections.singleton("com.atlassian.audit.atlassian-audit-plugin"));
    }

    @Override
    public boolean isInternal(@Nonnull Bundle bundle) {
        return this.allowlisted.contains(bundle.getSymbolicName()) || this.delegate.isInternal(bundle);
    }
}

