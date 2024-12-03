/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class async$all$fn__10416
extends AFunction {
    Object result;
    Object result_ch;
    Object cnt;
    Object i;
    public static final Var const__0 = RT.var("clojure.core", "swap!");
    public static final Var const__1 = RT.var("clojure.core", "assoc");
    public static final Var const__3 = RT.var("clojure.core", "dec");
    public static final Var const__4 = RT.var("clojure.core", "group-by");
    public static final Var const__5 = RT.var("ginga.core", "error?");
    public static final Var const__6 = RT.var("clojure.core", "deref");
    public static final Var const__7 = RT.var("clojure.core", "seq?");
    public static final Var const__8 = RT.var("clojure.core", "next");
    public static final Var const__9 = RT.var("clojure.core", "to-array");
    public static final Var const__10 = RT.var("clojure.core", "seq");
    public static final Var const__11 = RT.var("clojure.core", "first");
    public static final Var const__13 = RT.var("clojure.core.async", "put!");
    public static final Var const__14 = RT.var("clojure.core", "ex-info");
    public static final Keyword const__15 = RT.keyword(null, "errors");

    public async$all$fn__10416(Object object, Object object2, Object object3, Object object4) {
        this.result = object;
        this.result_ch = object2;
        this.cnt = object3;
        this.i = object4;
    }

    @Override
    public Object invoke(Object p1__10402_SHARP_) {
        Object object;
        Object object2 = p1__10402_SHARP_;
        p1__10402_SHARP_ = null;
        ((IFn)const__0.getRawRoot()).invoke(this_.result, const__1.getRawRoot(), this_.i, object2);
        if (Numbers.isZero(((IFn)const__0.getRawRoot()).invoke(this_.cnt, const__3.getRawRoot()))) {
            async$all$fn__10416 this_;
            Object object3;
            Object map__10417 = ((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__6.getRawRoot()).invoke(this_.result));
            Object object4 = ((IFn)const__7.getRawRoot()).invoke(map__10417);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = ((IFn)const__8.getRawRoot()).invoke(map__10417);
                if (object5 != null && object5 != Boolean.FALSE) {
                    Object object6 = map__10417;
                    map__10417 = null;
                    object3 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__9.getRawRoot()).invoke(object6));
                } else {
                    Object object7 = ((IFn)const__10.getRawRoot()).invoke(map__10417);
                    if (object7 != null && object7 != Boolean.FALSE) {
                        Object object8 = map__10417;
                        map__10417 = null;
                        object3 = ((IFn)const__11.getRawRoot()).invoke(object8);
                    } else {
                        object3 = PersistentArrayMap.EMPTY;
                    }
                }
            } else {
                object3 = map__10417;
                map__10417 = null;
            }
            Object map__104172 = object3;
            Object errors2 = RT.get(map__104172, Boolean.TRUE);
            Object object9 = map__104172;
            map__104172 = null;
            Object vals2 = RT.get(object9, Boolean.FALSE);
            Object object10 = ((IFn)const__10.getRawRoot()).invoke(errors2);
            if (object10 != null && object10 != Boolean.FALSE) {
                Object[] objectArray = new Object[2];
                objectArray[0] = const__15;
                Object object11 = errors2;
                errors2 = null;
                objectArray[1] = object11;
                this_ = null;
                object = ((IFn)const__13.getRawRoot()).invoke(this_.result_ch, ((IFn)const__14.getRawRoot()).invoke("multiple errors", RT.mapUniqueKeys(objectArray)));
            } else {
                Object object12 = vals2;
                vals2 = null;
                this_ = null;
                object = ((IFn)const__13.getRawRoot()).invoke(this_.result_ch, object12);
            }
        } else {
            object = null;
        }
        return object;
    }
}

