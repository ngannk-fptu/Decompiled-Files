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
import clojure.lang.Tuple;
import clojure.lang.Var;
import potemkin.utils$try_STAR_$fn__26245;
import potemkin.utils$try_STAR_$ignore_retry__26248;

public final class utils$try_STAR_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "take-while");
    public static final Var const__1 = RT.var("clojure.core", "drop");
    public static final Var const__3 = RT.var("clojure.core", "update-in");
    public static final Var const__4 = RT.var("clojure.core", "zipmap");
    public static final Var const__5 = RT.var("clojure.core", "map");
    public static final Var const__6 = RT.var("clojure.core", "second");
    public static final AFn const__8 = (AFn)((Object)Tuple.create(Symbol.intern(null, "Throwable")));
    public static final AFn const__10 = (AFn)((Object)Tuple.create(Symbol.intern(null, "Error")));
    public static final Var const__11 = RT.var("clojure.core", "seq");
    public static final Var const__12 = RT.var("clojure.core", "concat");
    public static final Var const__13 = RT.var("clojure.core", "list");
    public static final AFn const__14 = Symbol.intern(null, "try");
    public static final Var const__15 = RT.var("clojure.core", "remove");
    public static final Var const__16 = RT.var("clojure.core", "nil?");
    public static final Var const__17 = RT.var("clojure.core", "vals");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq body_PLUS_catch) {
        Object body = ((IFn)const__0.getRawRoot()).invoke(new utils$try_STAR_$fn__26245(), body_PLUS_catch);
        ISeq iSeq = body_PLUS_catch;
        body_PLUS_catch = null;
        Object object = ((IFn)const__1.getRawRoot()).invoke(RT.count(body), iSeq);
        utils$try_STAR_$ignore_retry__26248 ignore_retry = new utils$try_STAR_$ignore_retry__26248();
        Object object2 = ((IFn)const__5.getRawRoot()).invoke(const__6.getRawRoot(), object);
        Object object3 = object;
        object = null;
        Object object4 = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(object2, object3), const__8, ignore_retry);
        utils$try_STAR_$ignore_retry__26248 utils$try_STAR_$ignore_retry__26248 = ignore_retry;
        ignore_retry = null;
        Object class__GT_clause = ((IFn)const__3.getRawRoot()).invoke(object4, const__10, utils$try_STAR_$ignore_retry__26248);
        Object object5 = body;
        body = null;
        Object object6 = class__GT_clause;
        class__GT_clause = null;
        return ((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(const__14), object5, ((IFn)const__15.getRawRoot()).invoke(const__16.getRawRoot(), ((IFn)const__17.getRawRoot()).invoke(object6))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return utils$try_STAR_.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

