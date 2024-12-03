/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class chash$replication_keys$iter__14644__14648$fn__14649$fn__14650
extends AFunction {
    int size__6372__auto__;
    Object key;
    Object c__6371__auto__;
    Object b__14647;
    public static final Var const__3 = RT.var("clojure.core", "chunk-append");
    public static final Var const__4 = RT.var("ginga.chash", "b64-hash");
    public static final Var const__5 = RT.var("clojure.core", "str");
    public static final Object const__6 = Character.valueOf('|');

    public chash$replication_keys$iter__14644__14648$fn__14649$fn__14650(int n, Object object, Object object2, Object object3) {
        this.size__6372__auto__ = n;
        this.key = object;
        this.c__6371__auto__ = object2;
        this.b__14647 = object3;
    }

    @Override
    public Object invoke() {
        for (long i__14646 = (long)RT.intCast(0L); i__14646 < (long)this.size__6372__auto__; ++i__14646) {
            Object i;
            Object object = i = ((Indexed)this.c__6371__auto__).nth(RT.intCast(i__14646));
            i = null;
            ((IFn)const__3.getRawRoot()).invoke(this.b__14647, ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(object, const__6, this.key)));
        }
        return Boolean.TRUE;
    }
}

