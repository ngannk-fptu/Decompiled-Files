/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local$iter__19198__19202$fn__19203$fn__19204;
import clj_time.format$parse_local$iter__19198__19202$fn__19203$fn__19208;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local$iter__19198__19202$fn__19203
extends AFunction {
    Object iter__19198;
    Object s;
    Object s__19199;
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

    public format$parse_local$iter__19198__19202$fn__19203(Object object, Object object2, Object object3) {
        this.iter__19198 = object;
        this.s = object2;
        this.s__19199 = object3;
    }

    @Override
    public Object invoke() {
        Object object;
        block5: {
            Object s__19199 = this_.s__19199 = null;
            while (true) {
                Object d;
                Object f;
                format$parse_local$iter__19198__19202$fn__19203 this_;
                Object temp__5804__auto__19213;
                Object object2 = s__19199;
                s__19199 = null;
                Object object3 = temp__5804__auto__19213 = ((IFn)const__0.getRawRoot()).invoke(object2);
                if (object3 == null || object3 == Boolean.FALSE) break;
                Object object4 = temp__5804__auto__19213;
                temp__5804__auto__19213 = null;
                Object s__191992 = object4;
                Object object5 = ((IFn)const__1.getRawRoot()).invoke(s__191992);
                if (object5 != null && object5 != Boolean.FALSE) {
                    Object c__6371__auto__19211 = ((IFn)const__2.getRawRoot()).invoke(s__191992);
                    int size__6372__auto__19212 = RT.intCast(RT.count(c__6371__auto__19211));
                    Object b__19201 = ((IFn)const__5.getRawRoot()).invoke(size__6372__auto__19212);
                    Object object6 = c__6371__auto__19211;
                    c__6371__auto__19211 = null;
                    Object object7 = ((IFn)new format$parse_local$iter__19198__19202$fn__19203$fn__19204(b__19201, this_.s, size__6372__auto__19212, object6)).invoke();
                    if (object7 != null && object7 != Boolean.FALSE) {
                        Object object8 = b__19201;
                        b__19201 = null;
                        Object object9 = s__191992;
                        s__191992 = null;
                        this_ = null;
                        object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object8), ((IFn)this_.iter__19198).invoke(((IFn)const__8.getRawRoot()).invoke(object9)));
                    } else {
                        Object object10 = b__19201;
                        b__19201 = null;
                        this_ = null;
                        object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object10), null);
                    }
                    break block5;
                }
                Object object11 = f = ((IFn)const__9.getRawRoot()).invoke(s__191992);
                f = null;
                Object object12 = d = ((IFn)new format$parse_local$iter__19198__19202$fn__19203$fn__19208(object11, this_.s)).invoke();
                if (object12 != null && object12 != Boolean.FALSE) {
                    Object object13 = d;
                    d = null;
                    Object object14 = s__191992;
                    s__191992 = null;
                    this_ = null;
                    object = ((IFn)const__10.getRawRoot()).invoke(object13, ((IFn)this_.iter__19198).invoke(((IFn)const__11.getRawRoot()).invoke(object14)));
                    break block5;
                }
                Object object15 = s__191992;
                s__191992 = null;
                s__19199 = ((IFn)const__11.getRawRoot()).invoke(object15);
            }
            object = null;
        }
        return object;
    }
}

