/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.LazySeq;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$extend_protocol_PLUS_$this__26103$fn__26107;

public final class types$extend_protocol_PLUS_$this__26103
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "empty?");

    @Override
    public Object invoke(Object p__26102) {
        LazySeq lazySeq;
        Object s2;
        Object object = p__26102;
        p__26102 = null;
        Object vec__26104 = object;
        Object seq__26105 = ((IFn)const__0.getRawRoot()).invoke(vec__26104);
        Object first__26106 = ((IFn)const__1.getRawRoot()).invoke(seq__26105);
        Object object2 = seq__26105;
        seq__26105 = null;
        Object seq__261052 = ((IFn)const__2.getRawRoot()).invoke(object2);
        Object object3 = first__26106;
        first__26106 = null;
        Object sym = object3;
        Object object4 = seq__261052;
        seq__261052 = null;
        Object rest = object4;
        Object object5 = vec__26104;
        vec__26104 = null;
        Object object6 = s2 = object5;
        s2 = null;
        Object object7 = ((IFn)const__3.getRawRoot()).invoke(object6);
        if (object7 != null && object7 != Boolean.FALSE) {
            lazySeq = null;
        } else {
            rest = null;
            sym = null;
            lazySeq = new LazySeq(new types$extend_protocol_PLUS_$this__26103$fn__26107(rest, this, sym));
        }
        return lazySeq;
    }
}

