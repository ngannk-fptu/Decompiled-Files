/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import java.util.function.Consumer;

@Deprecated
@FunctionalInterface
public interface Effect<A>
extends Consumer<A> {
    public void apply(A var1);

    @Override
    default public void accept(A a) {
        this.apply(a);
    }

    @Deprecated
    @FunctionalInterface
    public static interface Applicant<A> {
        @Deprecated
        public void foreach(Effect<? super A> var1);
    }
}

