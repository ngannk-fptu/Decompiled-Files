/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.utils$condp_case$fn__26259;

public final class utils$condp_case
extends RestFn {
    public static final Var const__0 = RT.var("potemkin.macros", "unify-gensyms");
    public static final Var const__1 = RT.var("clojure.core", "seq");
    public static final Var const__2 = RT.var("clojure.core", "concat");
    public static final Var const__3 = RT.var("clojure.core", "list");
    public static final AFn const__4 = Symbol.intern("clojure.core", "let");
    public static final Var const__5 = RT.var("clojure.core", "apply");
    public static final Var const__6 = RT.var("clojure.core", "vector");
    public static final AFn const__7 = Symbol.intern(null, "val#__26256__auto__");
    public static final AFn const__8 = Symbol.intern(null, "pred#__26257__auto__");
    public static final AFn const__9 = Symbol.intern("clojure.core", "cond");
    public static final Var const__10 = RT.var("clojure.core", "map");
    public static final Var const__11 = RT.var("clojure.core", "partition");
    public static final Object const__12 = 2L;
    public static final Keyword const__13 = RT.keyword(null, "else");
    public static final Var const__14 = RT.var("clojure.core", "even?");
    public static final AFn const__16 = Symbol.intern(null, "throw");
    public static final AFn const__17 = Symbol.intern(null, "java.lang.IllegalArgumentException.");
    public static final AFn const__18 = Symbol.intern("clojure.core", "str");
    public static final AFn const__19 = Symbol.intern("clojure.core", "pr-str");
    public static final AFn const__20 = Symbol.intern(null, "val#__26255__auto__");
    public static final Var const__21 = RT.var("clojure.core", "last");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object predicate, Object value, ISeq cases2) {
        Object object;
        IFn iFn = (IFn)const__0.getRawRoot();
        IFn iFn2 = (IFn)const__1.getRawRoot();
        IFn iFn3 = (IFn)const__2.getRawRoot();
        Object object2 = ((IFn)const__3.getRawRoot()).invoke(const__4);
        Object object3 = value;
        value = null;
        Object object4 = predicate;
        predicate = null;
        Object object5 = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__6.getRawRoot(), ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__7), ((IFn)const__3.getRawRoot()).invoke(object3), ((IFn)const__3.getRawRoot()).invoke(const__8), ((IFn)const__3.getRawRoot()).invoke(object4)))));
        IFn iFn4 = (IFn)const__3.getRawRoot();
        IFn iFn5 = (IFn)const__1.getRawRoot();
        IFn iFn6 = (IFn)const__2.getRawRoot();
        Object object6 = ((IFn)const__3.getRawRoot()).invoke(const__9);
        Object object7 = ((IFn)const__5.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__10.getRawRoot()).invoke(new utils$condp_case$fn__26259(), ((IFn)const__11.getRawRoot()).invoke(const__12, cases2)));
        Object object8 = ((IFn)const__3.getRawRoot()).invoke(const__13);
        IFn iFn7 = (IFn)const__3.getRawRoot();
        Object object9 = ((IFn)const__14.getRawRoot()).invoke(RT.count(cases2));
        if (object9 != null && object9 != Boolean.FALSE) {
            object = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__16), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__17), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__18), ((IFn)const__3.getRawRoot()).invoke("no matching clause for "), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__19), ((IFn)const__3.getRawRoot()).invoke(const__20))))))))))));
        } else {
            ISeq iSeq = cases2;
            cases2 = null;
            object = ((IFn)const__21.getRawRoot()).invoke(iSeq);
        }
        return iFn.invoke(iFn2.invoke(iFn3.invoke(object2, object5, iFn4.invoke(iFn5.invoke(iFn6.invoke(object6, object7, object8, iFn7.invoke(object)))))));
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
        return utils$condp_case.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

