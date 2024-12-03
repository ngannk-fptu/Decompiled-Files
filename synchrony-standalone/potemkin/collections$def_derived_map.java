/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.collections$def_derived_map$fn__26548;
import potemkin.collections$def_derived_map$fn__26550;
import potemkin.collections$def_derived_map$fn__26553;
import potemkin.collections$def_derived_map$fn__26558;

public final class collections$def_derived_map
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Var const__5 = RT.var("clojure.core", "symbol");
    public static final Var const__6 = RT.var("clojure.core", "str");
    public static final Var const__7 = RT.var("clojure.core", "map");
    public static final Var const__8 = RT.var("clojure.core", "range");
    public static final Var const__10 = RT.var("clojure.core", "set");
    public static final Var const__11 = RT.var("clojure.core", "keys");
    public static final Var const__12 = RT.var("potemkin.macros", "unify-gensyms");
    public static final Var const__13 = RT.var("clojure.core", "concat");
    public static final Var const__14 = RT.var("clojure.core", "list");
    public static final AFn const__15 = Symbol.intern(null, "do");
    public static final AFn const__16 = Symbol.intern("clojure.core", "definterface");
    public static final AFn const__17 = Symbol.intern("potemkin.collections", "def-map-type");
    public static final Var const__18 = RT.var("clojure.core", "vec");
    public static final Var const__19 = RT.var("clojure.core", "conj");
    public static final AFn const__20 = Symbol.intern(null, "key-set#__26530__auto__");
    public static final AFn const__21 = Symbol.intern(null, "added#__26531__auto__");
    public static final AFn const__22 = Symbol.intern(null, "meta#__26532__auto__");
    public static final Var const__23 = RT.var("clojure.core", "vector");
    public static final Var const__24 = RT.var("clojure.core", "vals");
    public static final AFn const__25 = Symbol.intern(null, "meta");
    public static final Var const__26 = RT.var("clojure.core", "apply");
    public static final AFn const__27 = Symbol.intern("potemkin.collections", "_");
    public static final AFn const__28 = Symbol.intern(null, "meta#__26535__auto__");
    public static final AFn const__29 = Symbol.intern(null, "with-meta");
    public static final AFn const__30 = Symbol.intern("potemkin.collections", "_");
    public static final AFn const__31 = Symbol.intern(null, "x__26536__auto__");
    public static final AFn const__32 = Symbol.intern(null, "new");
    public static final AFn const__33 = Symbol.intern(null, "key-set#__26537__auto__");
    public static final AFn const__34 = Symbol.intern(null, "added#__26538__auto__");
    public static final AFn const__35 = Symbol.intern(null, "get");
    public static final AFn const__36 = Symbol.intern(null, "this#__26539__auto__");
    public static final AFn const__37 = Symbol.intern(null, "key__26540__auto__");
    public static final AFn const__38 = Symbol.intern(null, "default-value__26541__auto__");
    public static final AFn const__39 = Symbol.intern("clojure.core", "if-let");
    public static final AFn const__40 = Symbol.intern(null, "e__26542__auto__");
    public static final AFn const__41 = Symbol.intern("clojure.core", "find");
    public static final AFn const__42 = Symbol.intern("clojure.core", "val");
    public static final AFn const__43 = Symbol.intern(null, "if");
    public static final AFn const__44 = Symbol.intern("clojure.core", "contains?");
    public static final AFn const__45 = Symbol.intern("clojure.core", "case");
    public static final Var const__46 = RT.var("clojure.core", "interleave");
    public static final AFn const__47 = Symbol.intern(null, "keys");
    public static final AFn const__48 = Symbol.intern(null, "this__26543__auto__");
    public static final AFn const__49 = Symbol.intern(null, "assoc");
    public static final AFn const__50 = Symbol.intern(null, "value__26544__auto__");
    public static final AFn const__51 = Symbol.intern(null, "new");
    public static final AFn const__52 = Symbol.intern("clojure.core", "conj");
    public static final AFn const__53 = Symbol.intern("clojure.core", "assoc");
    public static final AFn const__54 = Symbol.intern(null, "dissoc");
    public static final AFn const__55 = Symbol.intern(null, "new");
    public static final AFn const__56 = Symbol.intern("clojure.core", "disj");
    public static final AFn const__57 = Symbol.intern("clojure.core", "dissoc");
    public static final AFn const__58 = Symbol.intern("clojure.core", "let");
    public static final AFn const__59 = Symbol.intern(null, "key-set__26545__auto__");
    public static final AFn const__60 = Symbol.intern("clojure.core", "defn");
    public static final AFn const__61 = Symbol.intern(null, "new");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, Object params2, ISeq p__26546) {
        ISeq map__26547;
        Object object;
        ISeq iSeq = p__26546;
        p__26546 = null;
        ISeq map__265472 = iSeq;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(map__265472);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = ((IFn)const__1.getRawRoot()).invoke(map__265472);
            if (object3 != null && object3 != Boolean.FALSE) {
                ISeq iSeq2 = map__265472;
                map__265472 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(iSeq2));
            } else {
                Object object4 = ((IFn)const__3.getRawRoot()).invoke(map__265472);
                if (object4 != null && object4 != Boolean.FALSE) {
                    ISeq iSeq3 = map__265472;
                    map__265472 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(iSeq3);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__265472;
            map__265472 = null;
        }
        ISeq iSeq4 = map__26547 = object;
        map__26547 = null;
        ISeq m4 = iSeq4;
        Object object5 = ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke("ILookup", name2));
        Object methods2 = ((IFn)const__7.getRawRoot()).invoke(new collections$def_derived_map$fn__26548(), ((IFn)const__8.getRawRoot()).invoke(RT.count(m4)));
        Object key_set = ((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(m4));
        Object object6 = ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__16), ((IFn)const__14.getRawRoot()).invoke(object5), ((IFn)const__7.getRawRoot()).invoke(new collections$def_derived_map$fn__26550(), methods2))));
        Object object7 = object5;
        object5 = null;
        Object object8 = ((IFn)const__7.getRawRoot()).invoke(new collections$def_derived_map$fn__26553(), ((IFn)const__7.getRawRoot()).invoke(const__23.getRawRoot(), methods2, ((IFn)const__24.getRawRoot()).invoke(m4)));
        ISeq iSeq5 = m4;
        m4 = null;
        Object object9 = methods2;
        methods2 = null;
        Object object10 = ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__17), ((IFn)const__14.getRawRoot()).invoke(name2), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__18.getRawRoot()).invoke(((IFn)const__19.getRawRoot()).invoke(params2, const__20, const__21, const__22))), ((IFn)const__14.getRawRoot()).invoke(object7), object8, ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__25), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__27))))), ((IFn)const__14.getRawRoot()).invoke(const__28)))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__29), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__30), ((IFn)const__14.getRawRoot()).invoke(const__31))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__32), ((IFn)const__14.getRawRoot()).invoke(name2), params2, ((IFn)const__14.getRawRoot()).invoke(const__33), ((IFn)const__14.getRawRoot()).invoke(const__34), ((IFn)const__14.getRawRoot()).invoke(const__31))))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__35), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__36), ((IFn)const__14.getRawRoot()).invoke(const__37), ((IFn)const__14.getRawRoot()).invoke(const__38))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__39), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__40), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__41), ((IFn)const__14.getRawRoot()).invoke(const__34), ((IFn)const__14.getRawRoot()).invoke(const__37)))))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__42), ((IFn)const__14.getRawRoot()).invoke(const__40)))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__43), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__44), ((IFn)const__14.getRawRoot()).invoke(const__33), ((IFn)const__14.getRawRoot()).invoke(const__37)))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__45), ((IFn)const__14.getRawRoot()).invoke(const__37), ((IFn)const__46.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(iSeq5), ((IFn)const__7.getRawRoot()).invoke(new collections$def_derived_map$fn__26558(), object9)), ((IFn)const__14.getRawRoot()).invoke(const__38)))), ((IFn)const__14.getRawRoot()).invoke(const__38)))))))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__47), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__48))))), ((IFn)const__14.getRawRoot()).invoke(const__33)))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__49), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__48), ((IFn)const__14.getRawRoot()).invoke(const__37), ((IFn)const__14.getRawRoot()).invoke(const__50))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__51), ((IFn)const__14.getRawRoot()).invoke(name2), params2, ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__52), ((IFn)const__14.getRawRoot()).invoke(const__33), ((IFn)const__14.getRawRoot()).invoke(const__37)))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__53), ((IFn)const__14.getRawRoot()).invoke(const__34), ((IFn)const__14.getRawRoot()).invoke(const__37), ((IFn)const__14.getRawRoot()).invoke(const__50)))), ((IFn)const__14.getRawRoot()).invoke(const__28))))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__54), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__48), ((IFn)const__14.getRawRoot()).invoke(const__37))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__55), ((IFn)const__14.getRawRoot()).invoke(name2), params2, ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__56), ((IFn)const__14.getRawRoot()).invoke(const__33), ((IFn)const__14.getRawRoot()).invoke(const__37)))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__57), ((IFn)const__14.getRawRoot()).invoke(const__34), ((IFn)const__14.getRawRoot()).invoke(const__37)))), ((IFn)const__14.getRawRoot()).invoke(const__28))))))))));
        Object object11 = key_set;
        key_set = null;
        Object object12 = ((IFn)const__14.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke("->", name2)));
        Object object13 = ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(params2))));
        Object object14 = name2;
        name2 = null;
        Object object15 = params2;
        params2 = null;
        return ((IFn)const__12.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__15), object6, object10, ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__58), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__26.getRawRoot()).invoke(const__23.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__59), ((IFn)const__14.getRawRoot()).invoke(object11))))), ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__60), object12, object13, ((IFn)const__14.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(const__61), ((IFn)const__14.getRawRoot()).invoke(object14), object15, ((IFn)const__14.getRawRoot()).invoke(const__59), ((IFn)const__14.getRawRoot()).invoke(null), ((IFn)const__14.getRawRoot()).invoke(null)))))))))))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        ISeq iSeq = (ISeq)object5;
        object5 = null;
        return collections$def_derived_map.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

