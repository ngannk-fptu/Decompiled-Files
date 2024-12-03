/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.gadgets.dashboard.DashboardItemModules
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.gadgets.directory.Directory$OpenSocialDirectoryEntry
 *  com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.gadgets.util.StreamUtil
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  io.atlassian.fugue.Maybe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.dashboard.DashboardItemModules;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.impl.AbstractDirectoryEntryProvider;
import com.atlassian.gadgets.directory.internal.impl.GadgetSpecDirectoryEntry;
import com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.gadgets.util.StreamUtil;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import io.atlassian.fugue.Maybe;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GadgetSpecProviderDirectoryEntryProvider
extends AbstractDirectoryEntryProvider<URI> {
    private static final Logger LOG = LoggerFactory.getLogger(GadgetSpecProviderDirectoryEntryProvider.class);
    private final Collection<GadgetSpecProvider> gadgetSpecProviders;
    private final Collection<GadgetSpecProvider> localGadgetSpecProviders;
    private final GadgetSpecProviderToURI gadgetSpecProviderToURI;

    GadgetSpecProviderDirectoryEntryProvider(GadgetSpecFactory gadgetSpecFactory, Collection<GadgetSpecProvider> gadgetSpecProviders, Collection<GadgetSpecProvider> localGadgetSpecProviders, Collection<DashboardItemModules> dashboardItemModules) {
        super(gadgetSpecFactory);
        this.gadgetSpecProviders = gadgetSpecProviders;
        this.localGadgetSpecProviders = localGadgetSpecProviders;
        this.gadgetSpecProviderToURI = new GadgetSpecProviderToURI(new DashboardItemDefinitionReplacesOpenSocialSpecPredicate(dashboardItemModules));
    }

    @Override
    public boolean contains(URI gadgetSpecUri) {
        return this.gadgetSpecProviders.stream().anyMatch(provider -> {
            try {
                return provider.contains(gadgetSpecUri);
            }
            catch (RuntimeException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Could not determine whether " + provider + " contains " + gadgetSpecUri, (Throwable)e);
                }
                return false;
            }
        });
    }

    @Override
    protected Iterable<URI> internalEntries(Directory.EntryScope entryScope) {
        switch (entryScope) {
            case ALL: {
                return this.allInternalEntries();
            }
            case LOCAL: {
                return this.localInternalEntries();
            }
            case EXTERNAL: {
                HashSet internalEntries = Sets.newHashSet(this.allInternalEntries());
                Iterables.removeAll((Iterable)internalEntries, (Collection)Sets.newHashSet(this.localInternalEntries()));
                return internalEntries;
            }
        }
        throw new IllegalArgumentException("Unsupported entry scope: " + entryScope);
    }

    private Iterable<URI> allInternalEntries() {
        return this.gadgetSpecProviders.stream().map(this.gadgetSpecProviderToURI).flatMap(StreamUtil::toStream).collect(Collectors.toList());
    }

    private Iterable<URI> localInternalEntries() {
        return this.localGadgetSpecProviders.stream().map(this.gadgetSpecProviderToURI).flatMap(StreamUtil::toStream).collect(Collectors.toList());
    }

    @Override
    protected Function<URI, Directory.OpenSocialDirectoryEntry> convertToLocalizedDirectoryEntry(GadgetRequestContext gadgetRequestContext) {
        return gadgetSpecUri -> {
            try {
                return new GadgetSpecDirectoryEntry(this.getGadgetSpec((URI)gadgetSpecUri, gadgetRequestContext), false, null);
            }
            catch (GadgetParsingException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Couldn't retrieve " + gadgetSpecUri, (Throwable)e);
                }
                return null;
            }
        };
    }

    public String toString() {
        return "application-provided gadget specs";
    }

    private static final class DashboardItemDefinitionReplacesOpenSocialSpecPredicate
    implements Predicate<URI> {
        private final Collection<DashboardItemModules> dashboardItemModulesCollection;

        private DashboardItemDefinitionReplacesOpenSocialSpecPredicate(Collection<DashboardItemModules> dashboardItemModulesCollection) {
            this.dashboardItemModulesCollection = Objects.requireNonNull(dashboardItemModulesCollection);
        }

        @Override
        public boolean test(URI specURI) {
            return this.dashboardItemModulesCollection.stream().map(DashboardItemModules::getDashboardItemsWithDirectoryDefinition).flatMap(StreamUtil::toStream).map(DashboardItemModuleDescriptor::getGadgetSpecUriToReplace).filter(Maybe::isDefined).map(Maybe::get).anyMatch(specUriToReplace -> specUriToReplace.equals(specURI.toASCIIString()));
        }
    }

    private static final class GadgetSpecProviderToURI
    implements Function<GadgetSpecProvider, Iterable<URI>> {
        private final DashboardItemDefinitionReplacesOpenSocialSpecPredicate dashboardItemDefinitionReplacesOpenSocialSpecPredicate;

        GadgetSpecProviderToURI(DashboardItemDefinitionReplacesOpenSocialSpecPredicate dashboardItemDefinitionReplacesOpenSocialSpecPredicate) {
            this.dashboardItemDefinitionReplacesOpenSocialSpecPredicate = dashboardItemDefinitionReplacesOpenSocialSpecPredicate;
        }

        @Override
        public Iterable<URI> apply(GadgetSpecProvider provider) {
            try {
                return StreamUtil.toStream((Iterable)provider.entries()).filter(uri -> !this.dashboardItemDefinitionReplacesOpenSocialSpecPredicate.test((URI)uri)).collect(Collectors.toList());
            }
            catch (RuntimeException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Could not retrieve directory entries from " + provider, (Throwable)e);
                } else if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not retrieve directory entries from " + provider + ": " + e.getMessage());
                }
                return Collections.emptySet();
            }
        }
    }
}

