/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Indexed;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.types$extend_protocol_PLUS_$fn__26124;
import potemkin.types$extend_protocol_PLUS_$fn__26130;
import potemkin.types$extend_protocol_PLUS_$protocol_QMARK___26111;
import potemkin.types$extend_protocol_PLUS_$this__26103;

public final class types$extend_protocol_PLUS_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "filter");
    public static final Var const__1 = RT.var("clojure.core", "remove");
    public static final Var const__2 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "first");
    public static final Var const__6 = RT.var("clojure.core", "next");
    public static final Var const__7 = RT.var("clojure.core", "resolve");
    public static final Var const__8 = RT.var("potemkin.types", "register-impl-callback");
    public static final Var const__9 = RT.var("clojure.core", "deref");
    public static final Var const__10 = RT.var("clojure.core", "seq?");
    public static final Var const__11 = RT.var("clojure.core", "to-array");
    public static final Keyword const__13 = RT.keyword(null, "on-interface");
    public static final Keyword const__14 = RT.keyword(null, "impls");
    public static final Var const__15 = RT.var("potemkin.types", "extend-implementations");
    public static final Var const__16 = RT.var("clojure.core", "cons");
    public static final Var const__17 = RT.var("clojure.core", "keys");
    public static final Var const__19 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__20 = RT.var("clojure.core", "chunk-first");
    public static final Var const__21 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__24 = RT.var("clojure.core", "concat");
    public static final Var const__25 = RT.var("clojure.core", "list");
    public static final AFn const__26 = Symbol.intern("clojure.core", "extend-protocol");
    public static final Var const__27 = RT.var("clojure.core", "apply");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object proto, ISeq body) {
        types$extend_protocol_PLUS_$this__26103 split_on_symbol;
        types$extend_protocol_PLUS_$this__26103 types$extend_protocol_PLUS_$this__26103 = split_on_symbol = new types$extend_protocol_PLUS_$this__26103();
        split_on_symbol = null;
        ISeq iSeq = body;
        body = null;
        Object decls = ((IFn)types$extend_protocol_PLUS_$this__26103).invoke(iSeq);
        types$extend_protocol_PLUS_$protocol_QMARK___26111 protocol_QMARK_2 = new types$extend_protocol_PLUS_$protocol_QMARK___26111();
        Object protos = ((IFn)const__0.getRawRoot()).invoke(protocol_QMARK_2, decls);
        types$extend_protocol_PLUS_$protocol_QMARK___26111 types$extend_protocol_PLUS_$protocol_QMARK___26111 = protocol_QMARK_2;
        protocol_QMARK_2 = null;
        Object object = decls;
        decls = null;
        Object classes = ((IFn)const__1.getRawRoot()).invoke(types$extend_protocol_PLUS_$protocol_QMARK___26111, object);
        Object object2 = protos;
        protos = null;
        Object seq_26117 = ((IFn)const__2.getRawRoot()).invoke(object2);
        Object chunk_26118 = null;
        long count_26119 = 0L;
        long i_26120 = 0L;
        while (true) {
            Object object3;
            Object vec__26127;
            Object temp__5804__auto__26135;
            if (i_26120 < count_26119) {
                Object object4;
                Object vec__26121;
                Object object5 = vec__26121 = ((Indexed)chunk_26118).nth(RT.intCast(i_26120));
                vec__26121 = null;
                Object seq__26122 = ((IFn)const__2.getRawRoot()).invoke(object5);
                Object first__26123 = ((IFn)const__5.getRawRoot()).invoke(seq__26122);
                Object object6 = seq__26122;
                seq__26122 = null;
                Object seq__261222 = ((IFn)const__6.getRawRoot()).invoke(object6);
                Object object7 = first__26123;
                first__26123 = null;
                Object target_proto = object7;
                Object object8 = seq__261222;
                seq__261222 = null;
                Object body2 = object8;
                Object object9 = target_proto;
                target_proto = null;
                Object target_proto_var = ((IFn)const__7.getRawRoot()).invoke(object9);
                ((IFn)const__8.getRawRoot()).invoke(target_proto_var, new types$extend_protocol_PLUS_$fn__26124(body2, proto));
                Object object10 = target_proto_var;
                target_proto_var = null;
                Object map__26126 = ((IFn)const__9.getRawRoot()).invoke(object10);
                Object object11 = ((IFn)const__10.getRawRoot()).invoke(map__26126);
                if (object11 != null && object11 != Boolean.FALSE) {
                    Object object12 = ((IFn)const__6.getRawRoot()).invoke(map__26126);
                    if (object12 != null && object12 != Boolean.FALSE) {
                        Object object13 = map__26126;
                        map__26126 = null;
                        object4 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__11.getRawRoot()).invoke(object13));
                    } else {
                        Object object14 = ((IFn)const__2.getRawRoot()).invoke(map__26126);
                        if (object14 != null && object14 != Boolean.FALSE) {
                            Object object15 = map__26126;
                            map__26126 = null;
                            object4 = ((IFn)const__5.getRawRoot()).invoke(object15);
                        } else {
                            object4 = PersistentArrayMap.EMPTY;
                        }
                    }
                } else {
                    object4 = map__26126;
                    map__26126 = null;
                }
                Object map__261262 = object4;
                Object on_interface = RT.get(map__261262, const__13);
                Object object16 = map__261262;
                map__261262 = null;
                Object impls = RT.get(object16, const__14);
                Object object17 = on_interface;
                on_interface = null;
                Object object18 = impls;
                impls = null;
                Object object19 = body2;
                body2 = null;
                ((IFn)const__15.getRawRoot()).invoke(proto, ((IFn)const__16.getRawRoot()).invoke(object17, ((IFn)const__17.getRawRoot()).invoke(object18)), object19);
                Object object20 = seq_26117;
                seq_26117 = null;
                Object object21 = chunk_26118;
                chunk_26118 = null;
                ++i_26120;
                chunk_26118 = object21;
                seq_26117 = object20;
                continue;
            }
            Object object22 = seq_26117;
            seq_26117 = null;
            Object object23 = temp__5804__auto__26135 = ((IFn)const__2.getRawRoot()).invoke(object22);
            if (object23 == null || object23 == Boolean.FALSE) break;
            Object object24 = temp__5804__auto__26135;
            temp__5804__auto__26135 = null;
            Object seq_261172 = object24;
            Object object25 = ((IFn)const__19.getRawRoot()).invoke(seq_261172);
            if (object25 != null && object25 != Boolean.FALSE) {
                Object c__6065__auto__26134 = ((IFn)const__20.getRawRoot()).invoke(seq_261172);
                Object object26 = seq_261172;
                seq_261172 = null;
                Object object27 = c__6065__auto__26134;
                Object object28 = c__6065__auto__26134;
                c__6065__auto__26134 = null;
                i_26120 = RT.intCast(0L);
                count_26119 = RT.intCast(RT.count(object28));
                chunk_26118 = object27;
                seq_26117 = ((IFn)const__21.getRawRoot()).invoke(object26);
                continue;
            }
            Object object29 = vec__26127 = ((IFn)const__5.getRawRoot()).invoke(seq_261172);
            vec__26127 = null;
            Object seq__26128 = ((IFn)const__2.getRawRoot()).invoke(object29);
            Object first__26129 = ((IFn)const__5.getRawRoot()).invoke(seq__26128);
            Object object30 = seq__26128;
            seq__26128 = null;
            Object seq__261282 = ((IFn)const__6.getRawRoot()).invoke(object30);
            Object object31 = first__26129;
            first__26129 = null;
            Object target_proto = object31;
            Object object32 = seq__261282;
            seq__261282 = null;
            Object body3 = object32;
            Object object33 = target_proto;
            target_proto = null;
            Object target_proto_var = ((IFn)const__7.getRawRoot()).invoke(object33);
            ((IFn)const__8.getRawRoot()).invoke(target_proto_var, new types$extend_protocol_PLUS_$fn__26130(proto, body3));
            Object object34 = target_proto_var;
            target_proto_var = null;
            Object map__26132 = ((IFn)const__9.getRawRoot()).invoke(object34);
            Object object35 = ((IFn)const__10.getRawRoot()).invoke(map__26132);
            if (object35 != null && object35 != Boolean.FALSE) {
                Object object36 = ((IFn)const__6.getRawRoot()).invoke(map__26132);
                if (object36 != null && object36 != Boolean.FALSE) {
                    Object object37 = map__26132;
                    map__26132 = null;
                    object3 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__11.getRawRoot()).invoke(object37));
                } else {
                    Object object38 = ((IFn)const__2.getRawRoot()).invoke(map__26132);
                    if (object38 != null && object38 != Boolean.FALSE) {
                        Object object39 = map__26132;
                        map__26132 = null;
                        object3 = ((IFn)const__5.getRawRoot()).invoke(object39);
                    } else {
                        object3 = PersistentArrayMap.EMPTY;
                    }
                }
            } else {
                object3 = map__26132;
                map__26132 = null;
            }
            Object map__261322 = object3;
            Object on_interface = RT.get(map__261322, const__13);
            Object object40 = map__261322;
            map__261322 = null;
            Object impls = RT.get(object40, const__14);
            Object object41 = on_interface;
            on_interface = null;
            Object object42 = impls;
            impls = null;
            Object object43 = body3;
            body3 = null;
            ((IFn)const__15.getRawRoot()).invoke(proto, ((IFn)const__16.getRawRoot()).invoke(object41, ((IFn)const__17.getRawRoot()).invoke(object42)), object43);
            Object object44 = seq_261172;
            seq_261172 = null;
            i_26120 = 0L;
            count_26119 = 0L;
            chunk_26118 = null;
            seq_26117 = ((IFn)const__6.getRawRoot()).invoke(object44);
        }
        Object object45 = proto;
        proto = null;
        Object object46 = classes;
        classes = null;
        return ((IFn)const__2.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__26), ((IFn)const__25.getRawRoot()).invoke(object45), ((IFn)const__27.getRawRoot()).invoke(const__24.getRawRoot(), object46)));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        ISeq iSeq = (ISeq)object4;
        object4 = null;
        return types$extend_protocol_PLUS_.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

