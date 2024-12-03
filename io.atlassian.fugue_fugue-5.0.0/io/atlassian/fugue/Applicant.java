/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import java.util.function.Consumer;

@FunctionalInterface
public interface Applicant<A> {
    public void forEach(Consumer<? super A> var1);
}

