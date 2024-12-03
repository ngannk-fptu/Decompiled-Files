/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import java.util.Map;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionalBandanaPersister
implements BandanaPersister {
    private final BandanaPersister delegate;

    public TransactionalBandanaPersister(BandanaPersister delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Transactional(readOnly=true)
    public Object retrieve(BandanaContext context, String key) {
        return this.delegate.retrieve(context, key);
    }

    @Transactional(readOnly=true)
    public Map<String, Object> retrieve(BandanaContext context) {
        return this.delegate.retrieve(context);
    }

    @Transactional(readOnly=true)
    public Iterable<String> retrieveKeys(BandanaContext context) {
        return this.delegate.retrieveKeys(context);
    }

    public void store(BandanaContext context, String key, Object value) {
        this.delegate.store(context, key, value);
    }

    @Transactional(readOnly=true)
    public void flushCaches() {
        this.delegate.flushCaches();
    }

    public void remove(BandanaContext context) {
        this.delegate.remove(context);
    }

    public void remove(BandanaContext context, String key) {
        this.delegate.remove(context, key);
    }
}

