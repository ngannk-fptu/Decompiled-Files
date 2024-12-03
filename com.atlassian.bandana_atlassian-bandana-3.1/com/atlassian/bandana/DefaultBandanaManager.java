/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.bandana.BandanaPersister;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultBandanaManager
implements BandanaManager {
    private final BandanaPersister persister;

    public DefaultBandanaManager(BandanaPersister persister) {
        this.persister = persister;
    }

    @Override
    public void init() {
    }

    @Override
    public Object getValue(BandanaContext context, String key) {
        return this.getValue(context, key, true);
    }

    @Override
    public Object getValue(BandanaContext context, String key, boolean lookup) {
        if (context == null) {
            return null;
        }
        Object value = this.persister.retrieve(context, key);
        if (value != null) {
            return value;
        }
        if (lookup && context.hasParentContext()) {
            return this.getValue(context.getParentContext(), key, true);
        }
        return null;
    }

    @Override
    public void setValue(BandanaContext context, String key, Object value) {
        this.persister.store(context, key, value);
    }

    @Override
    public Iterable<String> getKeys(BandanaContext context) {
        return this.persister.retrieveKeys(context);
    }

    @Override
    public void removeValue(BandanaContext context, String key) {
        this.persister.remove(context, key);
    }
}

