/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IMapEntry;
import clojure.lang.IObj;
import clojure.lang.IRecord;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.walk$walk$fn__26058;

public final class walk$walk
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "list?");
    public static final Var const__1 = RT.var("clojure.core", "apply");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final Var const__3 = RT.var("clojure.core", "map");
    public static final Var const__6 = RT.var("clojure.core", "vec");
    public static final Var const__7 = RT.var("clojure.core", "seq?");
    public static final Var const__8 = RT.var("clojure.core", "doall");
    public static final Var const__10 = RT.var("clojure.core", "reduce");
    public static final Var const__11 = RT.var("clojure.core", "coll?");
    public static final Var const__12 = RT.var("clojure.core", "into");
    public static final Var const__13 = RT.var("clojure.core", "empty");
    public static final Keyword const__14 = RT.keyword(null, "else");
    public static final Var const__16 = RT.var("clojure.core", "with-meta");
    public static final Var const__17 = RT.var("clojure.core", "merge");
    public static final Var const__18 = RT.var("clojure.core", "meta");

    public static Object invokeStatic(Object inner, Object outer, Object form2) {
        Object object;
        Object x;
        Object object2;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(form2);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = outer;
            outer = null;
            Object object5 = inner;
            inner = null;
            object2 = ((IFn)object4).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(object5, form2)));
        } else if (form2 instanceof IMapEntry) {
            Object object6 = outer;
            outer = null;
            Object object7 = inner;
            inner = null;
            object2 = ((IFn)object6).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object7, form2)));
        } else {
            Object object8 = ((IFn)const__7.getRawRoot()).invoke(form2);
            if (object8 != null && object8 != Boolean.FALSE) {
                Object object9 = outer;
                outer = null;
                Object object10 = inner;
                inner = null;
                object2 = ((IFn)object9).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object10, form2)));
            } else if (form2 instanceof IRecord) {
                Object object11 = outer;
                outer = null;
                Object object12 = inner;
                inner = null;
                object2 = ((IFn)object11).invoke(((IFn)const__10.getRawRoot()).invoke(new walk$walk$fn__26058(object12), form2, form2));
            } else {
                Object object13 = ((IFn)const__11.getRawRoot()).invoke(form2);
                if (object13 != null && object13 != Boolean.FALSE) {
                    Object object14 = outer;
                    outer = null;
                    Object object15 = inner;
                    inner = null;
                    object2 = ((IFn)object14).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(form2), ((IFn)const__3.getRawRoot()).invoke(object15, form2)));
                } else {
                    Keyword keyword2 = const__14;
                    if (keyword2 != null && keyword2 != Boolean.FALSE) {
                        Object object16 = outer;
                        outer = null;
                        object2 = ((IFn)object16).invoke(form2);
                    } else {
                        object2 = x = null;
                    }
                }
            }
        }
        if (x instanceof IObj) {
            Object object17 = x;
            Object object18 = form2;
            form2 = null;
            Object object19 = x;
            x = null;
            object = ((IFn)const__16.getRawRoot()).invoke(object17, ((IFn)const__17.getRawRoot()).invoke(((IFn)const__18.getRawRoot()).invoke(object18), ((IFn)const__18.getRawRoot()).invoke(object19)));
        } else {
            object = x;
            Object var3_3 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return walk$walk.invokeStatic(object4, object5, object6);
    }
}

