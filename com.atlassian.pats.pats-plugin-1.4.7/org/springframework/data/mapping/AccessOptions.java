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

public class AccessOptions {
    public static SetOptions defaultSetOptions() {
        return SetOptions.DEFAULT;
    }

    public static GetOptions defaultGetOptions() {
        return GetOptions.DEFAULT;
    }

    public static class SetOptions {
        private static final SetOptions DEFAULT = new SetOptions();
        private final SetNulls nullHandling;
        private final Propagation collectionPropagation;
        private final Propagation mapPropagation;

        public SetOptions(SetNulls nullHandling, Propagation collectionPropagation, Propagation mapPropagation) {
            this.nullHandling = nullHandling;
            this.collectionPropagation = collectionPropagation;
            this.mapPropagation = mapPropagation;
        }

        public SetOptions withNullHandling(SetNulls nullHandling) {
            return this.nullHandling == nullHandling ? this : new SetOptions(nullHandling, this.collectionPropagation, this.mapPropagation);
        }

        public SetOptions withCollectionPropagation(Propagation collectionPropagation) {
            return this.collectionPropagation == collectionPropagation ? this : new SetOptions(this.nullHandling, collectionPropagation, this.mapPropagation);
        }

        public SetOptions withMapPropagation(Propagation mapPropagation) {
            return this.mapPropagation == mapPropagation ? this : new SetOptions(this.nullHandling, this.collectionPropagation, mapPropagation);
        }

        public SetNulls getNullHandling() {
            return this.nullHandling;
        }

        private SetOptions() {
            this.nullHandling = SetNulls.REJECT;
            this.collectionPropagation = Propagation.PROPAGATE;
            this.mapPropagation = Propagation.PROPAGATE;
        }

        public SetOptions skipNulls() {
            return this.withNullHandling(SetNulls.SKIP);
        }

        public SetOptions skipAndLogNulls() {
            return this.withNullHandling(SetNulls.SKIP_AND_LOG);
        }

        public SetOptions rejectNulls() {
            return this.withNullHandling(SetNulls.REJECT);
        }

        public SetOptions withCollectionAndMapPropagation(Propagation propagation) {
            Assert.notNull((Object)((Object)propagation), (String)"Propagation must not be null!");
            return this.withCollectionPropagation(propagation).withMapPropagation(propagation);
        }

        public boolean propagate(@Nullable PersistentProperty<?> property) {
            if (property == null) {
                return true;
            }
            if (property.isCollectionLike() && this.collectionPropagation.equals((Object)Propagation.SKIP)) {
                return false;
            }
            return !property.isMap() || !this.mapPropagation.equals((Object)Propagation.SKIP);
        }

        public static enum Propagation {
            SKIP,
            PROPAGATE;

        }

        public static enum SetNulls {
            REJECT,
            SKIP_AND_LOG,
            SKIP;

        }
    }

    public static class GetOptions {
        private static final GetOptions DEFAULT = new GetOptions(new HashMap(), GetNulls.REJECT);
        private final Map<PersistentProperty<?>, Function<Object, Object>> handlers;
        private final GetNulls nullValues;

        public GetOptions(Map<PersistentProperty<?>, Function<Object, Object>> handlers, GetNulls nullValues) {
            this.handlers = handlers;
            this.nullValues = nullValues;
        }

        public GetNulls getNullValues() {
            return this.nullValues;
        }

        public GetOptions withNullValues(GetNulls nullValues) {
            return this.nullValues == nullValues ? this : new GetOptions(this.handlers, nullValues);
        }

        public GetOptions registerHandler(PersistentProperty<?> property, Function<Object, Object> handler) {
            Assert.notNull(property, (String)"Property must not be null!");
            Assert.notNull(handler, (String)"Handler must not be null!");
            HashMap newHandlers = new HashMap(this.handlers);
            newHandlers.put(property, handler);
            return new GetOptions(newHandlers, this.nullValues);
        }

        public GetOptions registerCollectionHandler(PersistentProperty<?> property, Function<? super Collection<?>, Object> handler) {
            return this.registerHandler(property, Collection.class, handler);
        }

        public GetOptions registerListHandler(PersistentProperty<?> property, Function<? super List<?>, Object> handler) {
            return this.registerHandler(property, List.class, handler);
        }

        public GetOptions registerSetHandler(PersistentProperty<?> property, Function<? super Set<?>, Object> handler) {
            return this.registerHandler(property, Set.class, handler);
        }

        public GetOptions registerMapHandler(PersistentProperty<?> property, Function<? super Map<?, ?>, Object> handler) {
            return this.registerHandler(property, Map.class, handler);
        }

        public <T> GetOptions registerHandler(PersistentProperty<?> property, Class<T> type, Function<? super T, Object> handler) {
            Assert.isTrue((boolean)type.isAssignableFrom(property.getType()), () -> String.format("Cannot register a property handler for %s on a property of type %s!", type, property.getType()));
            Function<Object, Object> caster = it -> type.cast(it);
            return this.registerHandler(property, caster.andThen(handler));
        }

        @Nullable
        Object postProcess(PersistentProperty<?> property, @Nullable Object value) {
            Function<Object, Object> handler = this.handlers.get(property);
            return handler == null ? value : handler.apply(value);
        }

        public static enum GetNulls {
            REJECT,
            EARLY_RETURN;


            public SetOptions.SetNulls toNullHandling() {
                return REJECT == this ? SetOptions.SetNulls.REJECT : SetOptions.SetNulls.SKIP;
            }
        }
    }
}

