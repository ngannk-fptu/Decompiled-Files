/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Tuple;

public class Tuple2<T1, T2>
extends Tuple {
    public Tuple2(T1 first, T2 second) {
        super(new Object[]{first, second});
    }

    public T1 getFirst() {
        return (T1)this.get(0);
    }

    public T2 getSecond() {
        return (T2)this.get(1);
    }
}

