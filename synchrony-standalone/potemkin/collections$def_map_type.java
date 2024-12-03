/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.collections$def_map_type$fn__26518;

public final class collections$def_map_type
extends RestFn {
    public static final AFn const__0 = (AFn)((Object)RT.map(Symbol.intern(null, "get"), Symbol.intern(null, "get*"), Symbol.intern(null, "assoc"), Symbol.intern(null, "assoc*"), Symbol.intern(null, "dissoc"), Symbol.intern(null, "dissoc*"), Symbol.intern(null, "keys"), Symbol.intern(null, "keys*"), Symbol.intern(null, "empty"), Symbol.intern(null, "empty*"), Symbol.intern(null, "with-meta"), Symbol.intern(null, "with-meta*"), Symbol.intern(null, "meta"), Symbol.intern(null, "meta*")));
    public static final Var const__1 = RT.var("clojure.core", "with-meta");
    public static final Var const__2 = RT.var("clojure.core", "symbol");
    public static final Var const__3 = RT.var("clojure.core", "str");
    public static final Var const__4 = RT.var("clojure.core", "namespace-munge");
    public static final Var const__5 = RT.var("clojure.core", "*ns*");
    public static final Var const__6 = RT.var("clojure.core", "meta");
    public static final Var const__7 = RT.var("potemkin.macros", "unify-gensyms");
    public static final Var const__8 = RT.var("clojure.core", "seq");
    public static final Var const__9 = RT.var("clojure.core", "concat");
    public static final Var const__10 = RT.var("clojure.core", "list");
    public static final AFn const__11 = Symbol.intern(null, "do");
    public static final AFn const__12 = Symbol.intern("potemkin.types", "deftype+");
    public static final AFn const__13 = Symbol.intern("potemkin.collections", "AbstractMap");
    public static final Var const__14 = RT.var("clojure.core", "map");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, Object params2, ISeq body) {
        AFn fns = const__0;
        Object classname = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.get()), ".", name2)), ((IFn)const__6.getRawRoot()).invoke(name2));
        Object object = name2;
        name2 = null;
        Object object2 = params2;
        params2 = null;
        AFn aFn = fns;
        fns = null;
        ISeq iSeq = body;
        body = null;
        Object object3 = classname;
        classname = null;
        return ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(const__11), ((IFn)const__10.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(const__12), ((IFn)const__10.getRawRoot()).invoke(object), ((IFn)const__10.getRawRoot()).invoke(object2), ((IFn)const__10.getRawRoot()).invoke(const__13), ((IFn)const__14.getRawRoot()).invoke(new collections$def_map_type$fn__26518(aFn), iSeq)))), ((IFn)const__10.getRawRoot()).invoke(object3))));
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
        return collections$def_map_type.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

