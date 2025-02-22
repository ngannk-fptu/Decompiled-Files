/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class MoreCollectors {
    private static final Collector<Object, ?, Optional<Object>> TO_OPTIONAL = Collector.of(ToOptionalState::new, ToOptionalState::add, ToOptionalState::combine, ToOptionalState::getOptional, Collector.Characteristics.UNORDERED);
    private static final Object NULL_PLACEHOLDER = new Object();
    private static final Collector<@Nullable Object, ?, @Nullable Object> ONLY_ELEMENT = Collector.of(ToOptionalState::new, (state, o) -> state.add(o == null ? NULL_PLACEHOLDER : o), ToOptionalState::combine, state -> {
        Object result = state.getElement();
        return result == NULL_PLACEHOLDER ? null : result;
    }, Collector.Characteristics.UNORDERED);

    public static <T> Collector<T, ?, Optional<T>> toOptional() {
        return TO_OPTIONAL;
    }

    public static <T> Collector<T, ?, T> onlyElement() {
        return ONLY_ELEMENT;
    }

    private MoreCollectors() {
    }

    private static final class ToOptionalState {
        static final int MAX_EXTRAS = 4;
        @CheckForNull
        Object element = null;
        List<Object> extras = Collections.emptyList();

        ToOptionalState() {
        }

        IllegalArgumentException multiples(boolean overflow) {
            StringBuilder sb = new StringBuilder().append("expected one element but was: <").append(this.element);
            for (Object o : this.extras) {
                sb.append(", ").append(o);
            }
            if (overflow) {
                sb.append(", ...");
            }
            sb.append('>');
            throw new IllegalArgumentException(sb.toString());
        }

        void add(Object o) {
            Preconditions.checkNotNull(o);
            if (this.element == null) {
                this.element = o;
            } else if (this.extras.isEmpty()) {
                this.extras = new ArrayList<Object>(4);
                this.extras.add(o);
            } else if (this.extras.size() < 4) {
                this.extras.add(o);
            } else {
                throw this.multiples(true);
            }
        }

        ToOptionalState combine(ToOptionalState other) {
            if (this.element == null) {
                return other;
            }
            if (other.element == null) {
                return this;
            }
            if (this.extras.isEmpty()) {
                this.extras = new ArrayList<Object>();
            }
            this.extras.add(other.element);
            this.extras.addAll(other.extras);
            if (this.extras.size() > 4) {
                this.extras.subList(4, this.extras.size()).clear();
                throw this.multiples(true);
            }
            return this;
        }

        Optional<Object> getOptional() {
            if (this.extras.isEmpty()) {
                return Optional.ofNullable(this.element);
            }
            throw this.multiples(false);
        }

        Object getElement() {
            if (this.element == null) {
                throw new NoSuchElementException();
            }
            if (this.extras.isEmpty()) {
                return this.element;
            }
            throw this.multiples(false);
        }
    }
}

