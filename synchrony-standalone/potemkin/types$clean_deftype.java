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
import potemkin.types$clean_deftype$fn__26138;

public final class types$clean_deftype
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "*clojure-version*");
    public static final Var const__1 = RT.var("clojure.core", "seq?");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "to-array");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "first");
    public static final Keyword const__7 = RT.keyword(null, "major");
    public static final Keyword const__8 = RT.keyword(null, "minor");
    public static final Keyword const__9 = RT.keyword(null, "incremental");
    public static final Var const__10 = RT.var("clojure.core", "str");
    public static final Var const__11 = RT.var("clojure.core", "remove");

    public static Object invokeStatic(Object x) {
        Object version;
        Object object;
        Object map__26137 = const__0.get();
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(map__26137);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = ((IFn)const__2.getRawRoot()).invoke(map__26137);
            if (object3 != null && object3 != Boolean.FALSE) {
                Object object4 = map__26137;
                map__26137 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__3.getRawRoot()).invoke(object4));
            } else {
                Object object5 = ((IFn)const__4.getRawRoot()).invoke(map__26137);
                if (object5 != null && object5 != Boolean.FALSE) {
                    Object object6 = map__26137;
                    map__26137 = null;
                    object = ((IFn)const__5.getRawRoot()).invoke(object6);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__26137;
            map__26137 = null;
        }
        Object map__261372 = object;
        Object major = RT.get(map__261372, const__7);
        Object minor = RT.get(map__261372, const__8);
        Object object7 = map__261372;
        map__261372 = null;
        Object incremental = RT.get(object7, const__9);
        Object object8 = major;
        major = null;
        Object object9 = minor;
        minor = null;
        Object object10 = incremental;
        incremental = null;
        Object object11 = version = ((IFn)const__10.getRawRoot()).invoke(object8, ".", object9, ".", object10);
        version = null;
        Object object12 = x;
        x = null;
        return ((IFn)const__11.getRawRoot()).invoke(new types$clean_deftype$fn__26138(object11), object12);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$clean_deftype.invokeStatic(object2);
    }
}

