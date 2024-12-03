/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Absent;
import com.google.common.base.AbstractIterator;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Present;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.DoNotMock;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.CheckForNull;

@DoNotMock(value="Use Optional.of(value) or Optional.absent()")
@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true)
public abstract class Optional<T>
implements Serializable {
    private static final long serialVersionUID = 0L;

    public static <T> Optional<T> absent() {
        return Absent.withType();
    }

    public static <T> Optional<T> of(T reference) {
        return new Present<T>(Preconditions.checkNotNull(reference));
    }

    public static <T> Optional<T> fromNullable(@CheckForNull T nullableReference) {
        return nullableReference == null ? Optional.absent() : new Present<T>(nullableReference);
    }

    @CheckForNull
    public static <T> Optional<T> fromJavaUtil(@CheckForNull java.util.Optional<T> javaUtilOptional) {
        return javaUtilOptional == null ? null : Optional.fromNullable(javaUtilOptional.orElse(null));
    }

    @CheckForNull
    public static <T> java.util.Optional<T> toJavaUtil(@CheckForNull Optional<T> googleOptional) {
        return googleOptional == null ? null : googleOptional.toJavaUtil();
    }

    public java.util.Optional<T> toJavaUtil() {
        return java.util.Optional.ofNullable(this.orNull());
    }

    Optional() {
    }

    public abstract boolean isPresent();

    public abstract T get();

    public abstract T or(T var1);

    public abstract Optional<T> or(Optional<? extends T> var1);

    public abstract T or(Supplier<? extends T> var1);

    @CheckForNull
    public abstract T orNull();

    public abstract Set<T> asSet();

    public abstract <V> Optional<V> transform(Function<? super T, V> var1);

    public abstract boolean equals(@CheckForNull Object var1);

    public abstract int hashCode();

    public abstract String toString();

    public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> optionals) {
        Preconditions.checkNotNull(optionals);
        return new Iterable<T>(){

            @Override
            public Iterator<T> iterator() {
                return new AbstractIterator<T>(){
                    private final Iterator<? extends Optional<? extends T>> iterator;
                    {
                        this.iterator = Preconditions.checkNotNull(optionals.iterator());
                    }

                    @Override
                    @CheckForNull
                    protected T computeNext() {
                        while (this.iterator.hasNext()) {
                            Optional optional = this.iterator.next();
                            if (!optional.isPresent()) continue;
                            return optional.get();
                        }
                        return this.endOfData();
                    }
                };
            }
        };
    }
}

