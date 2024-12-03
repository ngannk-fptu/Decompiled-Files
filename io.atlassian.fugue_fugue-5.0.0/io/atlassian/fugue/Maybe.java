/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Effect;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Maybe<A>
extends Iterable<A>,
Effect.Applicant<A> {
    public A get();

    public <B extends A> A getOrElse(B var1);

    public A getOr(Supplier<? extends A> var1);

    @Deprecated
    public A getOrElse(Supplier<? extends A> var1);

    public A getOrNull();

    public A getOrError(Supplier<String> var1);

    public <X extends Throwable> A getOrThrow(Supplier<X> var1) throws X;

    public boolean isDefined();

    public boolean isEmpty();

    public boolean exists(Predicate<? super A> var1);

    @Override
    public Iterator<A> iterator();

    public boolean forall(Predicate<? super A> var1);
}

