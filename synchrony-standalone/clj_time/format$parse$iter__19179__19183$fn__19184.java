/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse$iter__19179__19183$fn__19184$fn__19185;
import clj_time.format$parse$iter__19179__19183$fn__19184$fn__19189;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse$iter__19179__19183$fn__19184
extends AFunction {
    Object s;
    Object s__19180;
    Object iter__19179;
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

    public format$parse$iter__19179__19183$fn__19184(Object object, Object object2, Object object3) {
        this.s = object;
        this.s__19180 = object2;
        this.iter__19179 = object3;
    }

    @Override
    public Object invoke() {
        Object object;
        block5: {
            Object s__19180 = this_.s__19180 = null;
            while (true) {
                Object d;
                Object f;
                format$parse$iter__19179__19183$fn__19184 this_;
                Object temp__5804__auto__19194;
                Object object2 = s__19180;
                s__19180 = null;
                Object object3 = temp__5804__auto__19194 = ((IFn)const__0.getRawRoot()).invoke(object2);
                if (object3 == null || object3 == Boolean.FALSE) break;
                Object object4 = temp__5804__auto__19194;
                temp__5804__auto__19194 = null;
                Object s__191802 = object4;
                Object object5 = ((IFn)const__1.getRawRoot()).invoke(s__191802);
                if (object5 != null && object5 != Boolean.FALSE) {
                    Object c__6371__auto__19192 = ((IFn)const__2.getRawRoot()).invoke(s__191802);
                    int size__6372__auto__19193 = RT.intCast(RT.count(c__6371__auto__19192));
                    Object b__19182 = ((IFn)const__5.getRawRoot()).invoke(size__6372__auto__19193);
                    Object object6 = c__6371__auto__19192;
                    c__6371__auto__19192 = null;
                    Object object7 = ((IFn)new format$parse$iter__19179__19183$fn__19184$fn__19185(object6, size__6372__auto__19193, this_.s, b__19182)).invoke();
                    if (object7 != null && object7 != Boolean.FALSE) {
                        Object object8 = b__19182;
                        b__19182 = null;
                        Object object9 = s__191802;
                        s__191802 = null;
                        this_ = null;
                        object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object8), ((IFn)this_.iter__19179).invoke(((IFn)const__8.getRawRoot()).invoke(object9)));
                    } else {
                        Object object10 = b__19182;
                        b__19182 = null;
                        this_ = null;
                        object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object10), null);
                    }
                    break block5;
                }
                Object object11 = f = ((IFn)const__9.getRawRoot()).invoke(s__191802);
                f = null;
                Object object12 = d = ((IFn)new format$parse$iter__19179__19183$fn__19184$fn__19189(this_.s, object11)).invoke();
                if (object12 != null && object12 != Boolean.FALSE) {
                    Object object13 = d;
                    d = null;
                    Object object14 = s__191802;
                    s__191802 = null;
                    this_ = null;
                    object = ((IFn)const__10.getRawRoot()).invoke(object13, ((IFn)this_.iter__19179).invoke(((IFn)const__11.getRawRoot()).invoke(object14)));
                    break block5;
                }
                Object object15 = s__191802;
                s__191802 = null;
                s__19180 = ((IFn)const__11.getRawRoot()).invoke(object15);
            }
            object = null;
        }
        return object;
    }
}

