/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.BundleAccessor;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.impl.BundleImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;

public final class BundleAccessorImpl
implements BundleAccessor {
    private final PackageAccessor packageAccessor;
    private final BundleContext bundleContext;

    public BundleAccessorImpl(PackageAccessor packageAccessor, BundleContext bundleContext) {
        this.packageAccessor = Objects.requireNonNull(packageAccessor, "packageAccessor");
        this.bundleContext = bundleContext;
    }

    @Override
    public Iterable<Bundle> getBundles() {
        return BundleImpl.wrap(this.packageAccessor).fromArray((org.osgi.framework.Bundle[])this.bundleContext.getBundles());
    }

    @Override
    public Iterable<Bundle> getBundles(@Nullable String term) {
        return term == null ? this.getBundles() : ImmutableList.copyOf((Collection)StreamSupport.stream(this.getBundles().spliterator(), false).filter(BundleAccessorImpl.bundleContains(term)).collect(Collectors.toList()));
    }

    @Override
    @Nullable
    public Bundle getBundle(long bundleId) {
        return BundleImpl.wrap(this.packageAccessor).fromSingleton(this.bundleContext.getBundle(bundleId));
    }

    private static final Predicate<Bundle> bundleContains(final String term) {
        final Predicate<String> stringContains = new Predicate<String>(){
            private final String lowerCaseTerm;
            {
                this.lowerCaseTerm = term.toLowerCase();
            }

            @Override
            public boolean test(@Nullable String s) {
                return s != null && s.toLowerCase().contains(this.lowerCaseTerm);
            }
        };
        Predicate<Bundle> unparsedHeadersContain = bundle -> bundle.getUnparsedHeaders().values().stream().anyMatch(stringContains);
        Predicate<Bundle> parsedHeadersContain = new Predicate<Bundle>(){
            private final Predicate<Iterable<Bundle.HeaderClause>> parsedHeaderContains = new Predicate<Iterable<Bundle.HeaderClause>>(){
                private final Predicate<Bundle.HeaderClause> headerClauseContains = headerClause -> stringContains.test(headerClause.getPath());

                @Override
                public boolean test(@Nullable Iterable<Bundle.HeaderClause> headers) {
                    return headers != null && StreamSupport.stream(headers.spliterator(), false).anyMatch(this.headerClauseContains);
                }
            };

            @Override
            public boolean test(@Nullable Bundle bundle) {
                return bundle != null && bundle.getParsedHeaders().values().stream().anyMatch(this.parsedHeaderContains);
            }
        };
        Predicate<Bundle> servicesContain = new Predicate<Bundle>(){
            private final Predicate<Service> serviceContains = service -> StreamSupport.stream(service.getObjectClasses().spliterator(), false).anyMatch(stringContains);

            @Override
            public boolean test(@Nullable Bundle bundle) {
                return bundle != null && StreamSupport.stream(Iterables.concat(bundle.getRegisteredServices(), bundle.getServicesInUse()).spliterator(), false).anyMatch(this.serviceContains);
            }
        };
        return unparsedHeadersContain.or(parsedHeadersContain).or(servicesContain);
    }
}

