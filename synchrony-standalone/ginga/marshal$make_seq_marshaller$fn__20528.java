/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class marshal$make_seq_marshaller$fn__20528
extends AFunction {
    Object v__GT_seq;
    Object tag;
    public static final Var const__0 = RT.var("ginga.marshal", "marshal-from-seq");

    public marshal$make_seq_marshaller$fn__20528(Object object, Object object2) {
        this.v__GT_seq = object;
        this.tag = object2;
    }

    @Override
    public Object invoke(Object v, Object marshaller) {
        Object object = v;
        v = null;
        Object object2 = marshaller;
        marshaller = null;
        marshal$make_seq_marshaller$fn__20528 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.tag, ((IFn)this_.v__GT_seq).invoke(object), object2);
    }
}

