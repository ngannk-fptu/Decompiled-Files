/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.LazySeq;
import ginga.tree$lazy_precat_STAR_$iter__20720__20724$fn__20725;

public final class tree$lazy_precat_STAR_$iter__20720__20724
extends AFunction {
    Object with_children;
    Object children;
    Object pre;

    public tree$lazy_precat_STAR_$iter__20720__20724(Object object, Object object2, Object object3) {
        this.with_children = object;
        this.children = object2;
        this.pre = object3;
    }

    @Override
    public Object invoke(Object s__20721) {
        Object object = s__20721;
        s__20721 = null;
        return new LazySeq(new tree$lazy_precat_STAR_$iter__20720__20724$fn__20725(this.with_children, this, this.children, object, this.pre));
    }
}

