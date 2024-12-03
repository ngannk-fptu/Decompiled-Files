/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentHashSet;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.collections$reify_map_type$fn__26523;
import potemkin.collections$reify_map_type$fn__26525;

public final class collections$reify_map_type
extends RestFn {
    public static final AFn const__0 = (AFn)((Object)RT.map(Symbol.intern(null, "get"), Symbol.intern(null, "get*"), Symbol.intern(null, "assoc"), Symbol.intern(null, "assoc*"), Symbol.intern(null, "dissoc"), Symbol.intern(null, "dissoc*"), Symbol.intern(null, "keys"), Symbol.intern(null, "keys*"), Symbol.intern(null, "empty"), Symbol.intern(null, "empty*")));
    public static final AFn const__1 = PersistentHashSet.create(Symbol.intern(null, "meta"), Symbol.intern(null, "withMeta"));
    public static final Var const__2 = RT.var("clojure.core", "remove");
    public static final Var const__3 = RT.var("clojure.core", "macroexpand");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "concat");
    public static final Var const__6 = RT.var("clojure.core", "list");
    public static final AFn const__7 = Symbol.intern("potemkin.types", "reify+");
    public static final AFn const__8 = Symbol.intern("potemkin.collections", "AbstractMap");
    public static final Var const__9 = RT.var("clojure.core", "map");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq body) {
        AFn elide_QMARK_;
        AFn fns = const__0;
        AFn aFn = elide_QMARK_ = const__1;
        elide_QMARK_ = null;
        AFn aFn2 = fns;
        fns = null;
        ISeq iSeq = body;
        body = null;
        return ((IFn)const__2.getRawRoot()).invoke(new collections$reify_map_type$fn__26523(aFn), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__7), ((IFn)const__6.getRawRoot()).invoke(const__8), ((IFn)const__9.getRawRoot()).invoke(new collections$reify_map_type$fn__26525(aFn2), iSeq)))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return collections$reify_map_type.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

