/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.LazySeq;
import ginga.chash$replication_keys$iter__14644__14648$fn__14649;

public final class chash$replication_keys$iter__14644__14648
extends AFunction {
    Object key;

    public chash$replication_keys$iter__14644__14648(Object object) {
        this.key = object;
    }

    @Override
    public Object invoke(Object s__14645) {
        Object object = s__14645;
        s__14645 = null;
        return new LazySeq(new chash$replication_keys$iter__14644__14648$fn__14649(this.key, object, this));
    }
}

