/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  io.atlassian.fugue.Option
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import io.atlassian.fugue.Option;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

final class MeteredBandanaPersister
implements BandanaPersister {
    private final BandanaPersister delegate;
    private final MeterRegistry micrometer;

    public MeteredBandanaPersister(BandanaPersister delegate, MeterRegistry micrometer) {
        this.delegate = delegate;
        this.micrometer = micrometer;
    }

    public Object retrieve(BandanaContext context, String key) {
        this.incrementCounter(context, key, "retrieve");
        return this.delegate.retrieve(context, key);
    }

    public Map<String, Object> retrieve(BandanaContext context) {
        this.incrementCounter(context, null, "retrieve");
        return this.delegate.retrieve(context);
    }

    public Iterable<String> retrieveKeys(BandanaContext context) {
        this.incrementCounter(context, null, "retrieveKeys");
        return this.delegate.retrieveKeys(context);
    }

    public void store(BandanaContext context, String key, Object value) {
        this.incrementCounter(context, key, "store");
        this.delegate.store(context, key, value);
    }

    public void flushCaches() {
        this.delegate.flushCaches();
    }

    public void remove(BandanaContext context) {
        this.incrementCounter(context, null, "remove");
        this.delegate.remove(context);
    }

    public void remove(BandanaContext context, String key) {
        this.incrementCounter(context, key, "remove");
        this.delegate.remove(context, key);
    }

    private void incrementCounter(BandanaContext context, @Nullable String key, String retrieve) {
        this.micrometer.counter("bandana.persister", MeteredBandanaPersister.getTags(context, key, retrieve)).increment();
    }

    private static Iterable<Tag> getTags(BandanaContext context, @Nullable String key, String operation) {
        return Stream.of(Option.some((Object)Tag.of((String)"operation", (String)operation)), MeteredBandanaPersister.getKeyTag(key, context), MeteredBandanaPersister.getContextKeyTag(context)).flatMap(Option::toStream).collect(Collectors.toList());
    }

    private static Option<Tag> getKeyTag(@Nullable String key, BandanaContext context) {
        if (key != null && MeteredBandanaPersister.isGlobalContext(context)) {
            return Option.some((Object)Tag.of((String)"key", (String)key));
        }
        return Option.none();
    }

    private static boolean isGlobalContext(BandanaContext context) {
        return context instanceof ConfluenceBandanaContext && ((ConfluenceBandanaContext)context).isGlobal();
    }

    private static Option<Tag> getContextKeyTag(BandanaContext context) {
        return context instanceof KeyedBandanaContext ? Option.some((Object)Tag.of((String)"contextKey", (String)((KeyedBandanaContext)context).getContextKey())) : Option.none();
    }
}

