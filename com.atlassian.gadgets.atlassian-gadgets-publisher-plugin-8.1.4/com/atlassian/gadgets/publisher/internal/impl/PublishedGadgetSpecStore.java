/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 *  com.atlassian.gadgets.LocalGadgetSpecProvider
 *  com.atlassian.gadgets.Vote
 *  com.atlassian.gadgets.plugins.GadgetLocationTranslator
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec$Key
 *  com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener
 *  com.atlassian.gadgets.publisher.spi.PluginGadgetSpecProviderPermission
 *  com.atlassian.gadgets.util.GadgetSpecUrlBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ComputationException
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;
import com.atlassian.gadgets.LocalGadgetSpecProvider;
import com.atlassian.gadgets.Vote;
import com.atlassian.gadgets.plugins.GadgetLocationTranslator;
import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener;
import com.atlassian.gadgets.publisher.internal.GadgetProcessor;
import com.atlassian.gadgets.publisher.internal.GadgetSpecValidator;
import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecNotFoundException;
import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecWriter;
import com.atlassian.gadgets.publisher.internal.impl.ProcessedGadgetSpecsCache;
import com.atlassian.gadgets.publisher.spi.PluginGadgetSpecProviderPermission;
import com.atlassian.gadgets.util.GadgetSpecUrlBuilder;
import com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.ComputationException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={GadgetSpecProvider.class, LocalGadgetSpecProvider.class, PluginGadgetSpecEventListener.class, LifecycleAware.class})
public class PublishedGadgetSpecStore
implements LocalGadgetSpecProvider,
PluginGadgetSpecEventListener,
PublishedGadgetSpecWriter,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(PublishedGadgetSpecStore.class);
    private final PluginGadgetSpecStore pluginGadgetSpecStore;
    private final Map<PluginGadgetSpec.Key, PluginGadgetSpec> unprocessedSpecs = new ConcurrentHashMap<PluginGadgetSpec.Key, PluginGadgetSpec>();
    private volatile boolean pluginSystemStarted = false;
    private final GadgetSpecUrlBuilder gadgetSpecUrlBuilder;
    private final PluginGadgetSpecProviderPermission pluginGadgetSpecProviderPermission;
    private final GadgetLocationTranslator gadgetLocationTranslator;

    @Autowired
    public PublishedGadgetSpecStore(@ComponentImport ApplicationProperties applicationProperties, GadgetLocationTranslator gadgetLocationTranslator, GadgetProcessor gadgetProcessor, @ClasspathComponent GadgetSpecUrlBuilder gadgetSpecUrlBuilder, GadgetSpecValidator gadgetSpecValidator, @Qualifier(value="aggregatePermissions") PluginGadgetSpecProviderPermission pluginGadgetSpecProviderPermission) {
        this.gadgetSpecUrlBuilder = Objects.requireNonNull(gadgetSpecUrlBuilder, "gadgetSpecUrlBuilder");
        this.pluginGadgetSpecProviderPermission = Objects.requireNonNull(pluginGadgetSpecProviderPermission, "pluginGadgetSpecProviderPermission");
        this.gadgetLocationTranslator = Objects.requireNonNull(gadgetLocationTranslator, "gadgetLocationTranslator");
        this.pluginGadgetSpecStore = new PluginGadgetSpecStore(Objects.requireNonNull(applicationProperties, "applicationProperties"), Objects.requireNonNull(gadgetProcessor, "gadgetProcessor"), Objects.requireNonNull(gadgetSpecValidator, "gadgetSpecValidator"));
    }

    public synchronized void onStart() {
        this.pluginSystemStarted = true;
        for (PluginGadgetSpec spec : this.unprocessedSpecs.values()) {
            try {
                this.addToStore(spec);
            }
            catch (Exception e) {
                this.warn("Gadget spec " + spec + " could not be added to " + this + ", ignoring", e);
            }
        }
        this.unprocessedSpecs.clear();
    }

    private void warn(String message, Throwable t) {
        if (log.isDebugEnabled()) {
            log.warn(message, t);
        } else {
            log.warn(message);
        }
    }

    Collection<PluginGadgetSpec> getAll() {
        return Collections2.filter(this.pluginGadgetSpecStore.specs(), spec -> this.evaluateEnabledCondition((PluginGadgetSpec)spec));
    }

    public boolean contains(URI gadgetSpecUri) {
        try {
            return this.getIfAllowed(gadgetSpecUri) != null;
        }
        catch (GadgetParsingException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not determine if store contains gadgetSpecUri " + gadgetSpecUri, (Throwable)e);
            }
            return false;
        }
        catch (GadgetSpecUriNotAllowedException e) {
            if (log.isDebugEnabled()) {
                log.debug("GadgetSpecIri is not allowed in this store: " + gadgetSpecUri, (Throwable)e);
            }
            return false;
        }
    }

    private PluginGadgetSpec getIfAllowed(URI gadgetSpecUri) {
        PluginGadgetSpec gadgetSpec = this.getFromStore(this.gadgetSpecUrlBuilder.parseGadgetSpecUrl(gadgetSpecUri.toASCIIString()));
        if (gadgetSpec == null || !this.allowed(gadgetSpec)) {
            return null;
        }
        return gadgetSpec;
    }

    public Iterable<URI> entries() {
        return Iterables.transform((Iterable)Iterables.filter(this.pluginGadgetSpecStore.specs(), this.allowed()), this.toUri());
    }

    private Function<PluginGadgetSpec, URI> toUri() {
        return new PluginGadgetSpecToUri();
    }

    private Predicate<PluginGadgetSpec> allowed() {
        return new AllowedPluginGadgetSpec();
    }

    private boolean allowed(PluginGadgetSpec spec) {
        return this.evaluateEnabledCondition(spec) && this.pluginGadgetSpecProviderPermission.voteOn(spec) != Vote.DENY;
    }

    private boolean evaluateEnabledCondition(PluginGadgetSpec spec) {
        return spec.getEnabledCondition().shouldDisplay((Map)ImmutableMap.of((Object)"gadget", (Object)spec));
    }

    private URI getUri(PluginGadgetSpec pluginGadgetSpec) {
        URI gadgetUri = URI.create(this.gadgetSpecUrlBuilder.buildGadgetSpecUrl(pluginGadgetSpec));
        if (gadgetUri.isAbsolute()) {
            throw new GadgetParsingException("Expected relative URI but got " + gadgetUri);
        }
        return gadgetUri;
    }

    @Override
    public void writeGadgetSpecTo(String pluginKey, String location, OutputStream output) throws IOException {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(output, "output");
        PluginGadgetSpec.Key key = new PluginGadgetSpec.Key(pluginKey, location);
        this.writeGadgetSpecTo(key, output);
    }

    public void writeGadgetSpecTo(URI gadgetSpecUri, OutputStream output) throws IOException {
        Objects.requireNonNull(gadgetSpecUri, "gadgetSpecUri");
        Objects.requireNonNull(output, "output");
        PluginGadgetSpec.Key key = this.gadgetSpecUrlBuilder.parseGadgetSpecUrl(gadgetSpecUri.toASCIIString());
        this.writeGadgetSpecTo(key, output);
    }

    public Date getLastModified(URI gadgetSpecUri) {
        PluginGadgetSpec pluginGadgetSpec = this.getIfAllowed(gadgetSpecUri);
        if (gadgetSpecUri == null) {
            throw new GadgetSpecUriNotAllowedException("Gadget at '" + gadgetSpecUri + "' does not exist or access is not allowed");
        }
        return pluginGadgetSpec.getDateLoaded();
    }

    private void writeGadgetSpecTo(PluginGadgetSpec.Key key, OutputStream output) throws IOException {
        PluginGadgetSpec pluginGadgetSpec = this.getFromStore(key);
        if (pluginGadgetSpec == null) {
            throw new PublishedGadgetSpecNotFoundException(String.format("Could not find gadget spec: %s", key));
        }
        if (!this.evaluateEnabledCondition(pluginGadgetSpec)) {
            throw new PublishedGadgetSpecNotFoundException(String.format("Gadget spec (%s) was made unavailable due to failed conditions.", key));
        }
        this.write(pluginGadgetSpec, output);
    }

    private void write(PluginGadgetSpec pluginGadgetSpec, OutputStream output) throws IOException {
        output.write(this.pluginGadgetSpecStore.getProcessedGadgetSpec(pluginGadgetSpec));
    }

    public synchronized void pluginGadgetSpecEnabled(PluginGadgetSpec pluginGadgetSpec) throws GadgetParsingException {
        Objects.requireNonNull(pluginGadgetSpec, "pluginGadgetSpec");
        if (this.pluginSystemStarted) {
            this.addToStore(pluginGadgetSpec);
        } else {
            this.unprocessedSpecs.put(pluginGadgetSpec.getKey(), pluginGadgetSpec);
        }
    }

    private void addToStore(PluginGadgetSpec pluginGadgetSpec) throws GadgetParsingException {
        if (pluginGadgetSpec.isHostedExternally()) {
            return;
        }
        this.pluginGadgetSpecStore.put(pluginGadgetSpec.getKey(), pluginGadgetSpec);
    }

    private PluginGadgetSpec getFromStore(PluginGadgetSpec.Key key) {
        if (key == null) {
            return null;
        }
        PluginGadgetSpec.Key translatedKey = this.gadgetLocationTranslator.translate(key);
        if (!translatedKey.equals((Object)key) && this.pluginGadgetSpecStore.get(key) != null) {
            log.warn("Multiple gadget specs found with the key (" + key + "). Returning gadget with key ( " + translatedKey + ").");
        }
        return this.pluginGadgetSpecStore.get(translatedKey);
    }

    public synchronized void pluginGadgetSpecDisabled(PluginGadgetSpec pluginGadgetSpec) {
        Objects.requireNonNull(pluginGadgetSpec, "pluginGadgetSpec");
        if (this.pluginSystemStarted) {
            if (pluginGadgetSpec.isHostedExternally()) {
                return;
            }
            this.pluginGadgetSpecStore.remove(pluginGadgetSpec.getKey());
        } else {
            this.unprocessedSpecs.remove(pluginGadgetSpec.getKey());
        }
    }

    public String toString() {
        return "plugin-provided gadget spec store";
    }

    private static final class PluginGadgetSpecStore {
        private final Map<PluginGadgetSpec.Key, Supplier<Entry>> store = new ConcurrentHashMap<PluginGadgetSpec.Key, Supplier<Entry>>();
        private final ApplicationProperties applicationProperties;
        private final GadgetProcessor gadgetProcessor;
        private final GadgetSpecValidator gadgetSpecValidator;

        public PluginGadgetSpecStore(@ComponentImport ApplicationProperties applicationProperties, GadgetProcessor gadgetProcessor, GadgetSpecValidator gadgetSpecValidator) {
            this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
            this.gadgetProcessor = Objects.requireNonNull(gadgetProcessor, "gadgetProcessor");
            this.gadgetSpecValidator = Objects.requireNonNull(gadgetSpecValidator, "gadgetSpecValidator");
        }

        public void remove(PluginGadgetSpec.Key key) {
            this.store.remove(key);
        }

        public void put(PluginGadgetSpec.Key key, PluginGadgetSpec pluginGadgetSpec) {
            Entry entry = new Entry(pluginGadgetSpec, new ProcessedGadgetSpecsCache(this.gadgetProcessor, pluginGadgetSpec));
            this.store.put(key, (Supplier<Entry>)Suppliers.memoize(() -> this.validate(entry)));
        }

        public byte[] getProcessedGadgetSpec(PluginGadgetSpec pluginGadgetSpec) throws IOException {
            Entry entry = (Entry)this.store.get(pluginGadgetSpec.getKey()).get();
            return entry.get(this.applicationProperties.getBaseUrl());
        }

        public Collection<PluginGadgetSpec> specs() {
            return Collections2.transform(this.store.values(), this.toSpecs());
        }

        private Function<Supplier<Entry>, PluginGadgetSpec> toSpecs() {
            return input -> EntryToGadgetSpec.FUNCTION.apply((Entry)input.get());
        }

        public PluginGadgetSpec get(PluginGadgetSpec.Key key) {
            if (key == null) {
                return null;
            }
            Supplier<Entry> entry = this.store.get(key);
            if (entry == null) {
                return null;
            }
            return ((Entry)entry.get()).pluginGadgetSpec;
        }

        private Entry validate(Entry entry) {
            ByteArrayInputStream bais;
            try {
                bais = new ByteArrayInputStream(entry.get(this.applicationProperties.getBaseUrl()));
            }
            catch (IOException e) {
                throw new GadgetParsingException((Throwable)e);
            }
            if (!this.gadgetSpecValidator.isValid(bais)) {
                throw new GadgetParsingException("plugin gadget '" + entry.pluginGadgetSpec.getKey() + "' failed validation");
            }
            return entry;
        }

        private static final class Entry {
            private final PluginGadgetSpec pluginGadgetSpec;
            private final ProcessedGadgetSpecsCache processedGadgetSpecsCache;

            public Entry(PluginGadgetSpec pluginGadgetSpec, ProcessedGadgetSpecsCache processedGadgetSpecsCache) {
                this.pluginGadgetSpec = pluginGadgetSpec;
                this.processedGadgetSpecsCache = processedGadgetSpecsCache;
            }

            public byte[] get(String baseUrl) throws IOException {
                try {
                    return this.processedGadgetSpecsCache.get(baseUrl);
                }
                catch (ComputationException ce) {
                    if (ce.getCause() instanceof IOException) {
                        throw (IOException)ce.getCause();
                    }
                    if (ce.getCause() instanceof RuntimeException) {
                        throw (RuntimeException)ce.getCause();
                    }
                    throw ce;
                }
            }
        }

        private static enum EntryToGadgetSpec implements Function<Entry, PluginGadgetSpec>
        {
            FUNCTION;


            public PluginGadgetSpec apply(Entry entry) {
                return entry.pluginGadgetSpec;
            }
        }
    }

    private class AllowedPluginGadgetSpec
    implements Predicate<PluginGadgetSpec> {
        private AllowedPluginGadgetSpec() {
        }

        public boolean apply(PluginGadgetSpec spec) {
            return PublishedGadgetSpecStore.this.allowed(spec);
        }
    }

    private class PluginGadgetSpecToUri
    implements Function<PluginGadgetSpec, URI> {
        private PluginGadgetSpecToUri() {
        }

        public URI apply(PluginGadgetSpec from) {
            return PublishedGadgetSpecStore.this.getUri(from);
        }
    }
}

