/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.gadgets.directory.Directory$OpenSocialDirectoryEntry
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.DirectoryEntryProvider;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractDirectoryEntryProvider<T>
implements DirectoryEntryProvider {
    private final GadgetSpecFactory gadgetSpecFactory;

    AbstractDirectoryEntryProvider(GadgetSpecFactory gadgetSpecFactory) {
        this.gadgetSpecFactory = Objects.requireNonNull(gadgetSpecFactory);
    }

    @Override
    public final Iterable<Directory.Entry<?>> entries(GadgetRequestContext gadgetRequestContext, Directory.EntryScope entryScope) {
        ImmutableList internalEntries = ImmutableList.copyOf(this.internalEntries(entryScope));
        return internalEntries.stream().map(internalEntry -> this.convertToLocalizedDirectoryEntry(gadgetRequestContext).apply(internalEntry)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected abstract Iterable<T> internalEntries(Directory.EntryScope var1);

    protected abstract Function<T, Directory.OpenSocialDirectoryEntry> convertToLocalizedDirectoryEntry(GadgetRequestContext var1);

    final GadgetSpec getGadgetSpec(URI gadgetSpecUri, GadgetRequestContext gadgetRequestContext) throws GadgetParsingException {
        return this.gadgetSpecFactory.getGadgetSpec(gadgetSpecUri, gadgetRequestContext);
    }
}

