/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.law.IsEq
 */
package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.law.IsEq;
import io.atlassian.fugue.optic.PSetter;
import java.util.function.Function;

public final class SetterLaws<S, A> {
    private final PSetter<S, S, A, A> setter;

    public SetterLaws(PSetter<S, S, A, A> setter) {
        this.setter = setter;
    }

    public IsEq<S> setIdempotent(S s, A a) {
        return IsEq.isEq(this.setter.set(a).apply(this.setter.set(a).apply(s)), this.setter.set(a).apply(s));
    }

    public IsEq<S> modifyIdentity(S s) {
        return IsEq.isEq(this.setter.modify(Function.identity()).apply(s), s);
    }
}

