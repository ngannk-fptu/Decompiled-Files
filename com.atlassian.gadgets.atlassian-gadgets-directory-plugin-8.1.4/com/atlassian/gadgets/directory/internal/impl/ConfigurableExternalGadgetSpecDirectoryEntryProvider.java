/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.gadgets.directory.Directory$OpenSocialDirectoryEntry
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 *  com.atlassian.gadgets.event.AddGadgetEvent
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.ConfigurableExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.internal.DirectoryUrlBuilder;
import com.atlassian.gadgets.directory.internal.impl.AbstractDirectoryEntryProvider;
import com.atlassian.gadgets.directory.internal.impl.GadgetSpecDirectoryEntry;
import com.atlassian.gadgets.directory.internal.impl.UnavailableFeatureException;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.event.AddGadgetEvent;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurableExternalGadgetSpecDirectoryEntryProvider
extends AbstractDirectoryEntryProvider<ExternalGadgetSpec>
implements ConfigurableExternalGadgetSpecStore {
    private final ExternalGadgetSpecStore externalGadgetSpecStore;
    private final DirectoryUrlBuilder directoryUrlBuilder;
    private final TransactionTemplate txTemplate;
    private final EventPublisher eventPublisher;

    @Autowired
    public ConfigurableExternalGadgetSpecDirectoryEntryProvider(GadgetSpecFactory gadgetSpecFactory, @ComponentImport ExternalGadgetSpecStore externalGadgetSpecStore, DirectoryUrlBuilder directoryUrlBuilder, @ComponentImport(value="txTemplate") TransactionTemplate txTemplate, @ComponentImport EventPublisher eventPublisher) {
        super(gadgetSpecFactory);
        this.externalGadgetSpecStore = externalGadgetSpecStore;
        this.directoryUrlBuilder = directoryUrlBuilder;
        this.txTemplate = txTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean contains(URI gadgetSpecUri) {
        return this.externalGadgetSpecStore.contains(gadgetSpecUri);
    }

    @Override
    protected Iterable<ExternalGadgetSpec> internalEntries(Directory.EntryScope entryScope) {
        return Directory.EntryScope.LOCAL.equals((Object)entryScope) ? Collections.emptyList() : this.externalGadgetSpecStore.entries();
    }

    @Override
    public void add(URI gadgetSpecUri) throws GadgetParsingException {
        this.txTemplate.execute(() -> {
            if (!this.contains(gadgetSpecUri)) {
                this.eventPublisher.publish((Object)new AddGadgetEvent(gadgetSpecUri));
                this.validateGadgetSpec(gadgetSpecUri);
                this.externalGadgetSpecStore.add(gadgetSpecUri);
            }
            return null;
        });
    }

    @Override
    public void remove(ExternalGadgetSpecId gadgetSpecId) {
        this.txTemplate.execute(() -> {
            this.externalGadgetSpecStore.remove(gadgetSpecId);
            return null;
        });
    }

    @Override
    protected Function<ExternalGadgetSpec, Directory.OpenSocialDirectoryEntry> convertToLocalizedDirectoryEntry(GadgetRequestContext gadgetRequestContext) {
        return externalGadgetSpec -> {
            try {
                return new GadgetSpecDirectoryEntry(this.getGadgetSpec(externalGadgetSpec.getSpecUri(), gadgetRequestContext), true, this.getDirectoryEntryUri((ExternalGadgetSpec)externalGadgetSpec));
            }
            catch (GadgetParsingException | URISyntaxException e) {
                return null;
            }
        };
    }

    private void validateGadgetSpec(URI gadgetSpecUri) throws GadgetParsingException {
        try {
            GadgetSpec gadgetSpec = this.getGadgetSpec(gadgetSpecUri, GadgetRequestContext.NO_CURRENT_REQUEST);
            if (!Iterables.isEmpty((Iterable)gadgetSpec.getUnsupportedFeatureNames())) {
                throw new UnavailableFeatureException(gadgetSpec.getUnsupportedFeatureNames().toString());
            }
        }
        catch (GadgetParsingException e) {
            throw new GadgetParsingException((Throwable)e);
        }
    }

    private URI getDirectoryEntryUri(ExternalGadgetSpec externalGadgetSpec) throws URISyntaxException {
        return new URI(this.directoryUrlBuilder.buildDirectoryGadgetResourceUrl(externalGadgetSpec.getId()));
    }

    public String toString() {
        return "configured external gadget specs";
    }
}

