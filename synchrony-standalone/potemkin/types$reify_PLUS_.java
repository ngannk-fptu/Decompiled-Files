/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class types$reify_PLUS_
extends RestFn {
    public static final Var const__0 = RT.var("potemkin.types", "deftype*->deftype");
    public static final Var const__1 = RT.var("potemkin.types", "expand-deftype");
    public static final Var const__2 = RT.var("potemkin.types", "clean-deftype");
    public static final Var const__3 = RT.var("clojure.core", "list*");
    public static final AFn const__4 = Symbol.intern(null, "deftype");
    public static final Var const__5 = RT.var("clojure.core", "gensym");
    public static final AFn const__6 = Symbol.intern(null, "potemkin.types.PotemkinType");
    public static final Var const__7 = RT.var("clojure.core", "seq");
    public static final Var const__8 = RT.var("clojure.core", "concat");
    public static final Var const__9 = RT.var("clojure.core", "list");
    public static final AFn const__10 = Symbol.intern("clojure.core", "reify");
    public static final Var const__11 = RT.var("clojure.core", "remove");
    public static final AFn const__14 = PersistentHashSet.create(Symbol.intern(null, "clojure.lang.IObj"), RT.classForName("clojure.lang.IObj"));
    public static final Var const__15 = RT.var("clojure.core", "drop");
    public static final Object const__16 = 3L;

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq body) {
        Object body2;
        ISeq iSeq = body;
        body = null;
        Object object = body2 = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__4, ((IFn)const__5.getRawRoot()).invoke("reify"), PersistentVector.EMPTY, const__6, iSeq))));
        body2 = null;
        return ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__10), ((IFn)const__11.getRawRoot()).invoke(const__14, ((IFn)const__15.getRawRoot()).invoke(const__16, object))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return types$reify_PLUS_.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

