/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 */
package com.atlassian.confluence.plugins.gadgets.gadgetspecstore;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.plugins.gadgets.refimpl.ExternalGadgetSpecIdGenerator;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultClusterSafeExternalGadgetSpecStore
implements ExternalGadgetSpecStore {
    private final ClusterLockService clusterLockService;
    private final BandanaManager bandanaManager;
    private final ExternalGadgetSpecIdGenerator externalGadgetSpecIdGenerator;
    private static final String BANDANA_KEY = "confluence.ExternalGadgetSpecStore.specs";

    public DefaultClusterSafeExternalGadgetSpecStore(ClusterLockService clusterLockService, BandanaManager bandanaManager, ExternalGadgetSpecIdGenerator externalGadgetSpecIdGenerator) {
        this.clusterLockService = clusterLockService;
        this.bandanaManager = bandanaManager;
        this.externalGadgetSpecIdGenerator = externalGadgetSpecIdGenerator;
    }

    private List<ExternalGadgetSpec> getEntries() {
        List existingEntries = (List)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BANDANA_KEY);
        GadgetSpecsHolder gadgetSpecsHolder = existingEntries != null ? new GadgetSpecsHolder(existingEntries) : new GadgetSpecsHolder();
        return gadgetSpecsHolder.entries();
    }

    public Iterable<ExternalGadgetSpec> entries() {
        return this.getEntries();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ExternalGadgetSpec add(URI gadgetSpecUri) {
        if (gadgetSpecUri == null) {
            throw new IllegalArgumentException("Cannot add null gadgetSpecUri");
        }
        ClusterLock lock = this.clusterLockService.getLockForName(DefaultClusterSafeExternalGadgetSpecStore.class.getName() + ".executionlock");
        lock.lock();
        try {
            List<ExternalGadgetSpec> entries = this.getEntries();
            for (ExternalGadgetSpec externalGadgetSpec : entries) {
                if (!externalGadgetSpec.getSpecUri().equals(gadgetSpecUri)) continue;
                ExternalGadgetSpec externalGadgetSpec2 = externalGadgetSpec;
                return externalGadgetSpec2;
            }
            ExternalGadgetSpec gadgetSpec = new ExternalGadgetSpec(this.externalGadgetSpecIdGenerator.newExternalGadgetSpecId(), gadgetSpecUri);
            entries.add(gadgetSpec);
            this.saveSpecs(entries);
            ExternalGadgetSpec externalGadgetSpec = gadgetSpec;
            return externalGadgetSpec;
        }
        finally {
            lock.unlock();
        }
    }

    private void saveSpecs(List<ExternalGadgetSpec> list) {
        GadgetSpecsHolder holder = new GadgetSpecsHolder();
        for (ExternalGadgetSpec externalGadgetSpec : list) {
            holder.addSpec(externalGadgetSpec);
        }
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BANDANA_KEY, holder.getValues());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(ExternalGadgetSpecId externalGadgetSpecId) {
        if (externalGadgetSpecId == null) {
            throw new IllegalArgumentException("externalGadgetSpecId cannot be null");
        }
        ClusterLock lock = this.clusterLockService.getLockForName(DefaultClusterSafeExternalGadgetSpecStore.class.getName() + ".executionlock");
        lock.lock();
        try {
            List<ExternalGadgetSpec> list = this.getEntries();
            for (ExternalGadgetSpec externalGadgetSpec : list) {
                if (!externalGadgetSpec.getId().equals((Object)externalGadgetSpecId)) continue;
                list.remove(externalGadgetSpec);
                this.saveSpecs(list);
                return;
            }
        }
        finally {
            lock.unlock();
        }
    }

    public boolean contains(URI gadgetSpecUri) {
        for (ExternalGadgetSpec externalGadgetSpec : this.getEntries()) {
            if (!externalGadgetSpec.getSpecUri().equals(gadgetSpecUri)) continue;
            return true;
        }
        return false;
    }

    private class GadgetSpecsHolder {
        private List<Map<String, Object>> values;
        private static final String ID_KEY = "id";
        private static final String SPEC_URI = "specUri";

        private GadgetSpecsHolder(List<Map<String, Object>> values) {
            this.values = values;
        }

        private GadgetSpecsHolder() {
            this.values = new ArrayList<Map<String, Object>>();
        }

        public List<Map<String, Object>> getValues() {
            return this.values;
        }

        public void addSpec(ExternalGadgetSpec spec) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(ID_KEY, spec.getId().value());
            map.put(SPEC_URI, spec.getSpecUri());
            this.values.add(map);
        }

        public List<ExternalGadgetSpec> entries() {
            ArrayList<ExternalGadgetSpec> entries = new ArrayList<ExternalGadgetSpec>(this.values.size());
            for (Map<String, Object> value : this.values) {
                ExternalGadgetSpecId gadgetSpecId = ExternalGadgetSpecId.valueOf((String)((String)value.get(ID_KEY)));
                entries.add(new ExternalGadgetSpec(gadgetSpecId, (URI)value.get(SPEC_URI)));
            }
            return entries;
        }
    }
}

