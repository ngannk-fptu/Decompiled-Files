/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Effect;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.util.Iterator;

public interface Maybe<A>
extends Iterable<A>,
Effect.Applicant<A> {
    public A get();

    public <B extends A> A getOrElse(B var1);

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

