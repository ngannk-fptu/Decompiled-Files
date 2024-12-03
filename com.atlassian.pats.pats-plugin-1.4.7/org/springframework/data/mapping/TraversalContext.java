/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class TraversalContext {
    private Map<PersistentProperty<?>, Function<Object, Object>> handlers = new HashMap();

    public TraversalContext registerHandler(PersistentProperty<?> property, Function<Object, Object> handler) {
        Assert.notNull(property, (String)"Property must not be null!");
        Assert.notNull(handler, (String)"Handler must not be null!");
        this.handlers.put(property, handler);
        return this;
    }

    public TraversalContext registerCollectionHandler(PersistentProperty<?> property, Function<? super Collection<?>, Object> handler) {
        return this.registerHandler(property, Collection.class, handler);
    }

    public TraversalContext registerListHandler(PersistentProperty<?> property, Function<? super List<?>, Object> handler) {
        return this.registerHandler(property, List.class, handler);
    }

    public TraversalContext registerSetHandler(PersistentProperty<?> property, Function<? super Set<?>, Object> handler) {
        return this.registerHandler(property, Set.class, handler);
    }

    public TraversalContext registerMapHandler(PersistentProperty<?> property, Function<? super Map<?, ?>, Object> handler) {
        return this.registerHandler(property, Map.class, handler);
    }

    public <T> TraversalContext registerHandler(PersistentProperty<?> property, Class<T> type, Function<? super T, Object> handler) {
        Assert.isTrue((boolean)type.isAssignableFrom(property.getType()), () -> String.format("Cannot register a property handler for %s on a property of type %s!", type, property.getType()));
        Function<Object, Object> caster = it -> type.cast(it);
        return this.registerHandler(property, caster.andThen(handler));
    }

    @Nullable
    Object postProcess(PersistentProperty<?> property, @Nullable Object value) {
        Function<Object, Object> handler = this.handlers.get(property);
        return handler == null ? value : handler.apply(value);
    }
}

