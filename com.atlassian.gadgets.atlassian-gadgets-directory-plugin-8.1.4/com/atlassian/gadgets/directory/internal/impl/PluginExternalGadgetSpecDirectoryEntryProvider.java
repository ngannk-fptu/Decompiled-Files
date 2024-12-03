/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.gadgets.directory.Directory$OpenSocialDirectoryEntry
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.impl.AbstractDirectoryEntryProvider;
import com.atlassian.gadgets.directory.internal.impl.GadgetSpecDirectoryEntry;
import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginGadgetSpecEventListener.class})
public class PluginExternalGadgetSpecDirectoryEntryProvider
extends AbstractDirectoryEntryProvider<URI>
implements PluginGadgetSpecEventListener {
    private final Set<URI> entries = new HashSet<URI>();
    private final ReadWriteLock entriesLock = new ReentrantReadWriteLock();

    @Autowired
    public PluginExternalGadgetSpecDirectoryEntryProvider(@ComponentImport GadgetSpecFactory gadgetSpecFactory) {
        super((GadgetSpecFactory)Preconditions.checkNotNull((Object)gadgetSpecFactory, (Object)"gadgetSpecFactory"));
    }

    @Override
    public boolean contains(URI gadgetSpecUri) {
        this.entriesLock.readLock().lock();
        try {
            boolean bl = this.entries.contains(gadgetSpecUri);
            return bl;
        }
        finally {
            this.entriesLock.readLock().unlock();
        }
    }

    public void pluginGadgetSpecEnabled(PluginGadgetSpec pluginGadgetSpec) throws GadgetParsingException {
        if (pluginGadgetSpec.isHostedExternally()) {
            this.add(URI.create(pluginGadgetSpec.getLocation()));
        }
    }

    public void pluginGadgetSpecDisabled(PluginGadgetSpec pluginGadgetSpec) {
        if (pluginGadgetSpec.isHostedExternally()) {
            this.remove(URI.create(pluginGadgetSpec.getLocation()));
        }
    }

    @Override
    protected Iterable<URI> internalEntries(Directory.EntryScope entryScope) {
        if (Directory.EntryScope.LOCAL.equals((Object)entryScope)) {
            return Collections.emptyList();
        }
        this.entriesLock.readLock().lock();
        try {
            ImmutableSet immutableSet = ImmutableSet.copyOf(this.entries);
            return immutableSet;
        }
        finally {
            this.entriesLock.readLock().unlock();
        }
    }

    @Override
    protected Function<URI, Directory.OpenSocialDirectoryEntry> convertToLocalizedDirectoryEntry(GadgetRequestContext gadgetRequestContext) {
        return gadgetSpecUri -> {
            try {
                return new GadgetSpecDirectoryEntry(this.getGadgetSpec((URI)gadgetSpecUri, gadgetRequestContext), false, null);
            }
            catch (GadgetParsingException e) {
                return null;
            }
        };
    }

    private void add(URI gadgetSpecUri) throws GadgetParsingException {
        this.entriesLock.writeLock().lock();
        try {
            if (!this.entries.contains(gadgetSpecUri)) {
                this.validateGadgetSpec(gadgetSpecUri);
                this.entries.add(gadgetSpecUri);
            }
        }
        finally {
            this.entriesLock.writeLock().unlock();
        }
    }

    private void remove(URI gadgetSpecUri) {
        this.entriesLock.writeLock().lock();
        try {
            this.entries.remove(gadgetSpecUri);
        }
        finally {
            this.entriesLock.writeLock().unlock();
        }
    }

    private void validateGadgetSpec(URI gadgetSpecUri) throws GadgetParsingException {
        this.getGadgetSpec(gadgetSpecUri, GadgetRequestContext.NO_CURRENT_REQUEST);
    }

    public String toString() {
        return "plugin-provided external gadget specs";
    }
}

