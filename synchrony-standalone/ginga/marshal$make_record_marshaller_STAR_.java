/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import ginga.marshal$make_record_marshaller_STAR_$fn__20597;
import ginga.marshal$make_record_marshaller_STAR_$iter__20600__20604;

public final class marshal$make_record_marshaller_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "tag");
    public static final Keyword const__7 = RT.keyword(null, "fields");
    public static final Var const__8 = RT.var("clojure.core", "mapv");
    public static final Var const__9 = RT.var("clojure.core", "repeatedly");
    public static final Var const__11 = RT.var("clojure.core", "gensym");
    public static final Var const__12 = RT.var("clojure.core", "concat");
    public static final Var const__13 = RT.var("clojure.core", "list");
    public static final AFn const__14 = Symbol.intern("clojure.core", "let");
    public static final Var const__15 = RT.var("clojure.core", "apply");
    public static final Var const__16 = RT.var("clojure.core", "vector");
    public static final AFn const__17 = Symbol.intern(null, "tag__20594__auto__");
    public static final Var const__18 = RT.var("clojure.core", "interleave");
    public static final AFn const__19 = Symbol.intern("clojure.core", "fn");

    public static Object invokeStatic(Object p__20595) {
        marshal$make_record_marshaller_STAR_$iter__20600__20604 iter__6373__auto__20614;
        Object fields;
        Object object;
        Object object2 = p__20595;
        p__20595 = null;
        Object map__20596 = object2;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(map__20596);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__1.getRawRoot()).invoke(map__20596);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = map__20596;
                map__20596 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object5));
            } else {
                Object object6 = ((IFn)const__3.getRawRoot()).invoke(map__20596);
                if (object6 != null && object6 != Boolean.FALSE) {
                    Object object7 = map__20596;
                    map__20596 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object7);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__20596;
            map__20596 = null;
        }
        Object map__205962 = object;
        Object tag2 = RT.get(map__205962, const__6);
        Object object8 = map__205962;
        map__205962 = null;
        Object object9 = fields = RT.get(object8, const__7);
        fields = null;
        Object fns = ((IFn)const__8.getRawRoot()).invoke(new marshal$make_record_marshaller_STAR_$fn__20597(), object9);
        Object fn_syms = ((IFn)const__9.getRawRoot()).invoke(RT.count(fns), const__11.getRawRoot());
        Object record_sym = ((IFn)const__11.getRawRoot()).invoke();
        Object marshaller_sym = ((IFn)const__11.getRawRoot()).invoke();
        Object object10 = tag2;
        tag2 = null;
        Object object11 = fns;
        fns = null;
        Object object12 = ((IFn)const__13.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__16.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(const__17), ((IFn)const__13.getRawRoot()).invoke(object10), ((IFn)const__18.getRawRoot()).invoke(fn_syms, object11)))));
        Object object13 = ((IFn)const__13.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__16.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(record_sym), ((IFn)const__13.getRawRoot()).invoke(marshaller_sym)))));
        Object object14 = record_sym;
        record_sym = null;
        Object object15 = marshaller_sym;
        marshaller_sym = null;
        marshal$make_record_marshaller_STAR_$iter__20600__20604 marshal$make_record_marshaller_STAR_$iter__20600__20604 = iter__6373__auto__20614 = new marshal$make_record_marshaller_STAR_$iter__20600__20604(object14, object15);
        iter__6373__auto__20614 = null;
        Object object16 = fn_syms;
        fn_syms = null;
        return ((IFn)const__3.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(const__14), object12, ((IFn)const__13.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(const__19), object13, ((IFn)const__13.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__16.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(const__17), ((IFn)marshal$make_record_marshaller_STAR_$iter__20600__20604).invoke(object16))))))))));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return marshal$make_record_marshaller_STAR_.invokeStatic(object2);
    }
}

