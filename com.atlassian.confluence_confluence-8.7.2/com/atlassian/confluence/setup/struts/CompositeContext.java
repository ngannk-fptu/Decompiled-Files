/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.velocity.context.Context
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup.struts;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.StreamSupport;
import org.apache.velocity.context.Context;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CompositeContext
implements Context {
    private final Iterable<? extends Context> contexts;

    static Context composite(@NonNull Iterable<? extends Context> contexts) {
        return new CompositeContext(contexts);
    }

    static Context reverseComposite(@NonNull Iterable<? extends Context> contexts) {
        return CompositeContext.composite((Iterable<? extends Context>)ImmutableList.copyOf(contexts).reverse());
    }

    private CompositeContext(Iterable<? extends Context> contexts) {
        this.contexts = Objects.requireNonNull(contexts);
    }

    public boolean containsKey(Object o) {
        return StreamSupport.stream(this.contexts.spliterator(), false).anyMatch(context -> context.containsKey(o));
    }

    public Object get(String key) {
        for (Context context : this.contexts) {
            Object result = context.get(key);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public Object[] getKeys() {
        return StreamSupport.stream(this.contexts.spliterator(), false).map(Context::getKeys).flatMap(Arrays::stream).distinct().toArray();
    }

    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }
}

