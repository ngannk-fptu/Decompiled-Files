/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local_time$iter__19236__19240$fn__19241$fn__19242;
import clj_time.format$parse_local_time$iter__19236__19240$fn__19241$fn__19246;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local_time$iter__19236__19240$fn__19241
extends AFunction {
    Object s;
    Object s__19237;
    Object iter__19236;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__2 = RT.var("clojure.core", "chunk-first");
    public static final Var const__5 = RT.var("clojure.core", "chunk-buffer");
    public static final Var const__6 = RT.var("clojure.core", "chunk-cons");
    public static final Var const__7 = RT.var("clojure.core", "chunk");
    public static final Var const__8 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__9 = RT.var("clojure.core", "first");
    public static final Var const__10 = RT.var("clojure.core", "cons");
    public static final Var const__11 = RT.var("clojure.core", "rest");

    public format$parse_local_time$iter__19236__19240$fn__19241(Object object, Object object2, Object object3) {
        this.s = object;
        this.s__19237 = object2;
        this.iter__19236 = object3;
    }

    @Override
    public Object invoke() {
        Object object;
        block5: {
            Object s__19237 = this_.s__19237 = null;
            while (true) {
                Object d;
                Object f;
                format$parse_local_time$iter__19236__19240$fn__19241 this_;
                Object temp__5804__auto__19251;
                Object object2 = s__19237;
                s__19237 = null;
                Object object3 = temp__5804__auto__19251 = ((IFn)const__0.getRawRoot()).invoke(object2);
                if (object3 == null || object3 == Boolean.FALSE) break;
                Object object4 = temp__5804__auto__19251;
                temp__5804__auto__19251 = null;
                Object s__192372 = object4;
                Object object5 = ((IFn)const__1.getRawRoot()).invoke(s__192372);
                if (object5 != null && object5 != Boolean.FALSE) {
                    Object c__6371__auto__19249 = ((IFn)const__2.getRawRoot()).invoke(s__192372);
                    int size__6372__auto__19250 = RT.intCast(RT.count(c__6371__auto__19249));
                    Object b__19239 = ((IFn)const__5.getRawRoot()).invoke(size__6372__auto__19250);
                    Object object6 = c__6371__auto__19249;
                    c__6371__auto__19249 = null;
                    Object object7 = ((IFn)new format$parse_local_time$iter__19236__19240$fn__19241$fn__19242(object6, b__19239, this_.s, size__6372__auto__19250)).invoke();
                    if (object7 != null && object7 != Boolean.FALSE) {
                        Object object8 = b__19239;
                        b__19239 = null;
                        Object object9 = s__192372;
                        s__192372 = null;
                        this_ = null;
                        object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object8), ((IFn)this_.iter__19236).invoke(((IFn)const__8.getRawRoot()).invoke(object9)));
                    } else {
                        Object object10 = b__19239;
                        b__19239 = null;
                        this_ = null;
                        object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object10), null);
                    }
                    break block5;
                }
                Object object11 = f = ((IFn)const__9.getRawRoot()).invoke(s__192372);
                f = null;
                Object object12 = d = ((IFn)new format$parse_local_time$iter__19236__19240$fn__19241$fn__19246(object11, this_.s)).invoke();
                if (object12 != null && object12 != Boolean.FALSE) {
                    Object object13 = d;
                    d = null;
                    Object object14 = s__192372;
                    s__192372 = null;
                    this_ = null;
                    object = ((IFn)const__10.getRawRoot()).invoke(object13, ((IFn)this_.iter__19236).invoke(((IFn)const__11.getRawRoot()).invoke(object14)));
                    break block5;
                }
                Object object15 = s__192372;
                s__192372 = null;
                s__19237 = ((IFn)const__11.getRawRoot()).invoke(object15);
            }
            object = null;
        }
        return object;
    }
}

