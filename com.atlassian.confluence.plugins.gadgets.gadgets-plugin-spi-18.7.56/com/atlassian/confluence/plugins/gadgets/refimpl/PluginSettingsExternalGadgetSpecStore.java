/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStoreException
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.refimpl;

import com.atlassian.confluence.plugins.gadgets.refimpl.ExternalGadgetSpecIdGenerator;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStoreException;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSettingsExternalGadgetSpecStore
implements ExternalGadgetSpecStore {
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsExternalGadgetSpecStore.class);
    static final String KEY_PREFIX = ExternalGadgetSpecStore.class.getName() + ":";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ExternalGadgetSpecIdGenerator gadgetSpecIdGenerator;

    public PluginSettingsExternalGadgetSpecStore(PluginSettingsFactory pluginSettingsFactory, ExternalGadgetSpecIdGenerator gadgetSpecIdGenerator) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.gadgetSpecIdGenerator = gadgetSpecIdGenerator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<ExternalGadgetSpec> entries() {
        LinkedList<ExternalGadgetSpec> gadgetSpecs = new LinkedList<ExternalGadgetSpec>();
        this.lock.readLock().lock();
        try {
            PluginSettings settings = this.getSettings();
            int length = Settings.length.getFrom(settings);
            for (int i = 0; i < length; ++i) {
                try {
                    ExternalGadgetSpecId gadgetSpecId = ExternalGadgetSpecId.valueOf((String)Settings.gadgetSpecId(i).getFrom(settings));
                    URI gadgetSpecUri = Settings.gadgetSpecUri(gadgetSpecId).getFrom(settings);
                    ExternalGadgetSpec gadgetSpec = new ExternalGadgetSpec(gadgetSpecId, gadgetSpecUri);
                    if (gadgetSpec == null) continue;
                    gadgetSpecs.add(gadgetSpec);
                    continue;
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Could not load external gadget spec entry " + i, (Throwable)e);
                        continue;
                    }
                    if (!log.isWarnEnabled()) continue;
                    log.warn("Could not load external gadget spec entry " + i + ": " + e.getMessage());
                }
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return gadgetSpecs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ExternalGadgetSpec add(URI gadgetSpecUri) throws ExternalGadgetSpecStoreException {
        gadgetSpecUri = gadgetSpecUri.normalize();
        this.lock.writeLock().lock();
        try {
            ExternalGadgetSpec gadgetSpec = this.getSpecFor(gadgetSpecUri);
            if (gadgetSpec != null) {
                ExternalGadgetSpec externalGadgetSpec = gadgetSpec;
                return externalGadgetSpec;
            }
            gadgetSpec = new ExternalGadgetSpec(this.gadgetSpecIdGenerator.newExternalGadgetSpecId(), gadgetSpecUri);
            ArrayList<ExternalGadgetSpec> specs = new ArrayList<ExternalGadgetSpec>((Collection<ExternalGadgetSpec>)this.entries());
            specs.add(gadgetSpec);
            this.store(specs);
            ExternalGadgetSpec externalGadgetSpec = gadgetSpec;
            return externalGadgetSpec;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(ExternalGadgetSpecId externalGadgetSpecId) {
        this.lock.writeLock().lock();
        try {
            Iterable gadgetSpecs = this.entries();
            ExternalGadgetSpec gadgetSpecToRemove = null;
            for (ExternalGadgetSpec gadgetSpec : gadgetSpecs) {
                if (!externalGadgetSpecId.equals((Object)gadgetSpec.getId())) continue;
                gadgetSpecToRemove = gadgetSpec;
                break;
            }
            if (gadgetSpecToRemove != null && gadgetSpecs.remove(gadgetSpecToRemove)) {
                PluginSettings settings = this.getSettings();
                if (Settings.gadgetSpecUri(gadgetSpecToRemove.getId()).getFrom(settings) != null) {
                    Settings.gadgetSpecUri(gadgetSpecToRemove.getId()).removeFrom(settings);
                }
                this.store(gadgetSpecs);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean contains(URI gadgetSpecUri) {
        gadgetSpecUri = gadgetSpecUri.normalize();
        this.lock.readLock().lock();
        try {
            boolean bl = this.getSpecFor(gadgetSpecUri) != null;
            return bl;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Iterable<ExternalGadgetSpec> store(Iterable<ExternalGadgetSpec> gadgetSpecs) {
        if (this.lock.writeLock().tryLock()) {
            try {
                PluginSettings settings = this.getSettings();
                int oldLength = Settings.length.getFrom(settings);
                HashSet<ExternalGadgetSpecId> alreadyStored = new HashSet<ExternalGadgetSpecId>();
                int length = 0;
                for (ExternalGadgetSpec gadgetSpec : gadgetSpecs) {
                    String nextStoredSpecId;
                    int nextIndex;
                    ExternalGadgetSpecId gadgetSpecId = gadgetSpec.getId();
                    if (alreadyStored.contains(gadgetSpecId)) continue;
                    if (Settings.gadgetSpecId(nextIndex = length++).getFrom(settings) != null && Settings.gadgetSpecUri(ExternalGadgetSpecId.valueOf((String)(nextStoredSpecId = Settings.gadgetSpecId(nextIndex).getFrom(settings)))).getFrom(settings) != null) {
                        Settings.gadgetSpecUri(ExternalGadgetSpecId.valueOf((String)nextStoredSpecId)).removeFrom(settings);
                    }
                    Settings.gadgetSpecId(nextIndex).putInto(settings, gadgetSpecId.value());
                    Settings.gadgetSpecUri(gadgetSpecId).putInto(settings, gadgetSpec.getSpecUri());
                    alreadyStored.add(gadgetSpecId);
                }
                Settings.length.putInto(settings, length);
                for (int i = length; i < oldLength; ++i) {
                    Settings.gadgetSpecId(i).removeFrom(settings);
                }
                Iterable iterable = this.entries();
                return iterable;
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
        throw new IllegalStateException("Callers to store(Iterable<ExternalGadgetSpec>) must hold the write lock");
    }

    private ExternalGadgetSpec getSpecFor(URI gadgetSpecUri) {
        for (ExternalGadgetSpec gadgetSpec : this.entries()) {
            if (!gadgetSpecUri.equals(gadgetSpec.getSpecUri())) continue;
            return gadgetSpec;
        }
        return null;
    }

    private PluginSettings getSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }

    private static abstract class Settings<T> {
        static final Settings<Integer> length = new Settings<Integer>(KEY_PREFIX + "length"){

            @Override
            Integer valueOf(Object value) {
                return value != null ? Integer.parseInt(value.toString()) : 0;
            }
        };
        private final String name;

        static Settings<URI> gadgetSpecUri(ExternalGadgetSpecId id) {
            return new Settings<URI>(KEY_PREFIX + "gadgetSpecUri." + id.value()){

                @Override
                URI valueOf(Object value) {
                    return value != null ? URI.create(value.toString()) : null;
                }
            };
        }

        static Settings<String> gadgetSpecId(int index) {
            return new Settings<String>(KEY_PREFIX + "gadgetSpecId." + index){

                @Override
                String valueOf(Object value) {
                    return value != null ? value.toString() : null;
                }
            };
        }

        private Settings(String name) {
            this.name = name;
        }

        T getFrom(PluginSettings settings) {
            return this.valueOf(settings.get(this.name));
        }

        T putInto(PluginSettings settings, T value) {
            return this.valueOf(settings.put(this.name, (Object)String.valueOf(value)));
        }

        T removeFrom(PluginSettings settings) {
            return this.valueOf(settings.remove(this.name));
        }

        abstract T valueOf(Object var1);
    }
}

