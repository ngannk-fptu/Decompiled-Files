/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.directory.Directory
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.DirectoryEntryProvider;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExportAsService(value={Directory.class})
public class DirectoryImpl
implements Directory {
    private static final Logger log = LoggerFactory.getLogger(DirectoryImpl.class);
    private final Iterable<? extends DirectoryEntryProvider> providers;

    public DirectoryImpl(Iterable<? extends DirectoryEntryProvider> providers) {
        this.providers = providers;
    }

    public Iterable<Directory.Entry<?>> getEntries(GadgetRequestContext gadgetRequestContext) {
        return this.getEntries(gadgetRequestContext, Directory.EntryScope.ALL);
    }

    public Iterable<Directory.Entry<?>> getEntries(GadgetRequestContext gadgetRequestContext, Directory.EntryScope entryScope) {
        return Iterables.concat((Iterable)Iterables.transform(this.providers, DirectoryImpl.providerEntries(gadgetRequestContext, entryScope)));
    }

    public boolean contains(URI gadgetSpecUri) {
        return Iterables.any(this.providers, DirectoryImpl.providerContains(gadgetSpecUri));
    }

    private static Function<DirectoryEntryProvider, Iterable<Directory.Entry<?>>> providerEntries(final GadgetRequestContext gadgetRequestContext, final Directory.EntryScope entryScope) {
        return new Function<DirectoryEntryProvider, Iterable<Directory.Entry<?>>>(){

            public Iterable<Directory.Entry<?>> apply(DirectoryEntryProvider provider) {
                try {
                    return provider.entries(gadgetRequestContext, entryScope);
                }
                catch (RuntimeException e) {
                    if (log.isDebugEnabled()) {
                        log.warn("Could not retrieve directory entries from " + provider, (Throwable)e);
                    } else if (log.isWarnEnabled()) {
                        log.warn("Could not retrieve directory entries from " + provider + ": " + e.getMessage());
                    }
                    return ImmutableSet.of();
                }
            }
        };
    }

    private static Predicate<DirectoryEntryProvider> providerContains(final URI gadgetSpecUri) {
        return new Predicate<DirectoryEntryProvider>(){

            public boolean apply(DirectoryEntryProvider provider) {
                try {
                    return provider.contains(gadgetSpecUri);
                }
                catch (RuntimeException e) {
                    if (log.isDebugEnabled()) {
                        log.warn("Could not determine whether " + provider + " contains " + gadgetSpecUri, (Throwable)e);
                    } else if (log.isWarnEnabled()) {
                        log.warn("Could not determine whether " + provider + " contains " + gadgetSpecUri + ": " + e.getMessage());
                    }
                    return false;
                }
            }
        };
    }
}

