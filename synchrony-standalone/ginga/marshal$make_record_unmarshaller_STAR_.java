/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import ginga.marshal$make_record_unmarshaller_STAR_$fn__20620;
import ginga.marshal$make_record_unmarshaller_STAR_$fn__20623;
import ginga.marshal$make_record_unmarshaller_STAR_$fn__20625;
import ginga.marshal$make_record_unmarshaller_STAR_$iter__20628__20632;
import java.util.Arrays;

public final class marshal$make_record_unmarshaller_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "tag");
    public static final Keyword const__7 = RT.keyword(null, "pos->");
    public static final Keyword const__8 = RT.keyword(null, "map->");
    public static final Keyword const__9 = RT.keyword(null, "fields");
    public static final Keyword const__10 = RT.keyword(null, "defaults");
    public static final Var const__11 = RT.var("clojure.core", "mapv");
    public static final Var const__13 = RT.var("clojure.core", "gensym");
    public static final Var const__14 = RT.var("clojure.core", "zipmap");
    public static final Var const__15 = RT.var("clojure.core", "repeatedly");
    public static final Var const__16 = RT.var("clojure.core", "mapcat");
    public static final Var const__17 = RT.var("clojure.core", "range");
    public static final Var const__18 = RT.var("clojure.core", "integer?");
    public static final Var const__19 = RT.var("clojure.core", "str");
    public static final Var const__20 = RT.var("clojure.core", "pr-str");
    public static final Object const__21 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "integer?"), Symbol.intern(null, "tag"))))).withMeta(RT.map(RT.keyword(null, "line"), 295, RT.keyword(null, "column"), 13));
    public static final Var const__22 = RT.var("clojure.core", "every?");
    public static final Var const__23 = RT.var("clojure.core", "keyword?");
    public static final Object const__24 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "every?"), Symbol.intern(null, "keyword?"), Symbol.intern(null, "kws"))))).withMeta(RT.map(RT.keyword(null, "line"), 296, RT.keyword(null, "column"), 13));
    public static final Var const__25 = RT.var("clojure.core", "concat");
    public static final Var const__26 = RT.var("clojure.core", "list");
    public static final AFn const__27 = Symbol.intern("clojure.core", "let");
    public static final Var const__28 = RT.var("clojure.core", "apply");
    public static final Var const__29 = RT.var("clojure.core", "vector");
    public static final AFn const__30 = Symbol.intern(null, "constructor__20616__auto__");
    public static final AFn const__31 = Symbol.intern("clojure.core", "fn");
    public static final AFn const__32 = Symbol.intern("clojure.core", "let");
    public static final AFn const__33 = Symbol.intern(null, "constructor__20617__auto__");
    public static final AFn const__34 = Symbol.intern("clojure.core", "fn");

    public static Object invokeStatic(Object p__20618) {
        Object object;
        marshal$make_record_unmarshaller_STAR_$iter__20628__20632 iter__6373__auto__20644;
        Object object2;
        Object object3 = p__20618;
        p__20618 = null;
        Object map__20619 = object3;
        Object object4 = ((IFn)const__0.getRawRoot()).invoke(map__20619);
        if (object4 != null && object4 != Boolean.FALSE) {
            Object object5 = ((IFn)const__1.getRawRoot()).invoke(map__20619);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object object6 = map__20619;
                map__20619 = null;
                object2 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object6));
            } else {
                Object object7 = ((IFn)const__3.getRawRoot()).invoke(map__20619);
                if (object7 != null && object7 != Boolean.FALSE) {
                    Object object8 = map__20619;
                    map__20619 = null;
                    object2 = ((IFn)const__4.getRawRoot()).invoke(object8);
                } else {
                    object2 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object2 = map__20619;
            map__20619 = null;
        }
        Object map__206192 = object2;
        Object tag2 = RT.get(map__206192, const__6);
        Object pos__GT_ = RT.get(map__206192, const__7);
        Object map__GT_ = RT.get(map__206192, const__8);
        Object fields = RT.get(map__206192, const__9);
        Object object9 = map__206192;
        map__206192 = null;
        Object defaults = RT.get(object9, const__10);
        Object object10 = fields;
        fields = null;
        Object kws = ((IFn)const__11.getRawRoot()).invoke(new marshal$make_record_unmarshaller_STAR_$fn__20620(), object10);
        int num_fields = RT.count(kws);
        Object data_sym = ((IFn)const__13.getRawRoot()).invoke("data");
        Object unmarshaller_sym = ((IFn)const__13.getRawRoot()).invoke("unmarshaller");
        Object default_syms = ((IFn)const__14.getRawRoot()).invoke(kws, ((IFn)const__15.getRawRoot()).invoke(num_fields, new marshal$make_record_unmarshaller_STAR_$fn__20623()));
        Object default_exprs = ((IFn)const__16.getRawRoot()).invoke(new marshal$make_record_unmarshaller_STAR_$fn__20625(default_syms, defaults), kws);
        Object object11 = default_syms;
        default_syms = null;
        Object object12 = defaults;
        defaults = null;
        marshal$make_record_unmarshaller_STAR_$iter__20628__20632 marshal$make_record_unmarshaller_STAR_$iter__20628__20632 = iter__6373__auto__20644 = new marshal$make_record_unmarshaller_STAR_$iter__20628__20632(object11, data_sym, object12, kws, unmarshaller_sym);
        iter__6373__auto__20644 = null;
        Object value_exprs = ((IFn)marshal$make_record_unmarshaller_STAR_$iter__20628__20632).invoke(((IFn)const__17.getRawRoot()).invoke(num_fields));
        Object object13 = tag2;
        tag2 = null;
        Object object14 = ((IFn)const__18.getRawRoot()).invoke(object13);
        if (object14 == null || object14 == Boolean.FALSE) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__19.getRawRoot()).invoke("Assert failed: ", ((IFn)const__20.getRawRoot()).invoke(const__21))));
        }
        Object object15 = ((IFn)const__22.getRawRoot()).invoke(const__23.getRawRoot(), kws);
        if (object15 == null || object15 == Boolean.FALSE) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__19.getRawRoot()).invoke("Assert failed: ", ((IFn)const__20.getRawRoot()).invoke(const__24))));
        }
        Object object16 = map__GT_;
        if (object16 != null && object16 != Boolean.FALSE) {
            Object object17 = map__GT_;
            map__GT_ = null;
            Object object18 = default_exprs;
            default_exprs = null;
            Object object19 = data_sym;
            data_sym = null;
            Object object20 = unmarshaller_sym;
            unmarshaller_sym = null;
            Object object21 = kws;
            kws = null;
            Object object22 = value_exprs;
            value_exprs = null;
            object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__27), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__28.getRawRoot()).invoke(const__29.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__30), ((IFn)const__26.getRawRoot()).invoke(object17), object18)))), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__31), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__28.getRawRoot()).invoke(const__29.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(object19), ((IFn)const__26.getRawRoot()).invoke(object20))))), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__30), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(object21, object22))))))))));
        } else {
            Object object23 = pos__GT_;
            if (object23 != null && object23 != Boolean.FALSE) {
                Object object24 = pos__GT_;
                pos__GT_ = null;
                Object object25 = default_exprs;
                default_exprs = null;
                Object object26 = data_sym;
                data_sym = null;
                Object object27 = unmarshaller_sym;
                unmarshaller_sym = null;
                Object object28 = value_exprs;
                value_exprs = null;
                object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__32), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__28.getRawRoot()).invoke(const__29.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__33), ((IFn)const__26.getRawRoot()).invoke(object24), object25)))), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__34), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__28.getRawRoot()).invoke(const__29.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(object26), ((IFn)const__26.getRawRoot()).invoke(object27))))), ((IFn)const__26.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__33), object28))))))));
            } else {
                object = null;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return marshal$make_record_unmarshaller_STAR_.invokeStatic(object2);
    }
}

