/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$register_impl_callback$fn__26097
extends AFunction {
    Object callback;
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "impls");
    public static final Var const__7 = RT.var("clojure.set", "difference");
    public static final Var const__8 = RT.var("clojure.core", "set");
    public static final Var const__9 = RT.var("clojure.core", "keys");

    public types$register_impl_callback$fn__26097(Object object) {
        this.callback = object;
    }

    @Override
    public Object invoke(Object _2, Object proto_var, Object p__26095, Object p__26096) {
        Object new_impls;
        Object map__26099;
        Object object;
        Object map__26098;
        Object object2;
        Object object3 = p__26095;
        p__26095 = null;
        Object map__260982 = object3;
        Object object4 = ((IFn)const__0.getRawRoot()).invoke(map__260982);
        if (object4 != null && object4 != Boolean.FALSE) {
            Object object5 = ((IFn)const__1.getRawRoot()).invoke(map__260982);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object object6 = map__260982;
                map__260982 = null;
                object2 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object6));
            } else {
                Object object7 = ((IFn)const__3.getRawRoot()).invoke(map__260982);
                if (object7 != null && object7 != Boolean.FALSE) {
                    Object object8 = map__260982;
                    map__260982 = null;
                    object2 = ((IFn)const__4.getRawRoot()).invoke(object8);
                } else {
                    object2 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object2 = map__260982;
            map__260982 = null;
        }
        Object object9 = map__26098 = object2;
        map__26098 = null;
        Object old_impls = RT.get(object9, const__6);
        Object object10 = p__26096;
        p__26096 = null;
        Object map__260992 = object10;
        Object object11 = ((IFn)const__0.getRawRoot()).invoke(map__260992);
        if (object11 != null && object11 != Boolean.FALSE) {
            Object object12 = ((IFn)const__1.getRawRoot()).invoke(map__260992);
            if (object12 != null && object12 != Boolean.FALSE) {
                Object object13 = map__260992;
                map__260992 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object13));
            } else {
                Object object14 = ((IFn)const__3.getRawRoot()).invoke(map__260992);
                if (object14 != null && object14 != Boolean.FALSE) {
                    Object object15 = map__260992;
                    map__260992 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object15);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__260992;
            map__260992 = null;
        }
        Object object16 = map__26099 = object;
        map__26099 = null;
        Object object17 = new_impls = RT.get(object16, const__6);
        new_impls = null;
        Object object18 = old_impls;
        old_impls = null;
        types$register_impl_callback$fn__26097 this_ = null;
        return ((IFn)this_.callback).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(object17)), ((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(object18))));
    }
}

