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

public final class types$deftype_PLUS_
extends RestFn {
    public static final Var const__0 = RT.var("potemkin.types", "deftype*->deftype");
    public static final Var const__1 = RT.var("potemkin.types", "expand-deftype");
    public static final Var const__2 = RT.var("potemkin.types", "clean-deftype");
    public static final Var const__3 = RT.var("clojure.core", "list*");
    public static final AFn const__4 = Symbol.intern(null, "deftype");
    public static final AFn const__5 = Symbol.intern(null, "potemkin.types.PotemkinType");
    public static final Var const__6 = RT.var("clojure.core", "with-meta");
    public static final Var const__7 = RT.var("clojure.core", "symbol");
    public static final Var const__8 = RT.var("clojure.core", "str");
    public static final Var const__9 = RT.var("clojure.core", "namespace-munge");
    public static final Var const__10 = RT.var("clojure.core", "*ns*");
    public static final Var const__11 = RT.var("clojure.core", "meta");
    public static final Var const__12 = RT.var("clojure.core", "class?");
    public static final Var const__13 = RT.var("clojure.core", "ns-resolve");
    public static final Var const__14 = RT.var("clojure.core", "deref");
    public static final Var const__15 = RT.var("potemkin.types", "type-bodies");
    public static final Var const__16 = RT.var("potemkin.macros", "equivalent?");
    public static final Var const__17 = RT.var("potemkin.types", "transform-deftype*");
    public static final Var const__18 = RT.var("clojure.core", "identity");
    public static final Var const__19 = RT.var("clojure.core", "swap!");
    public static final Var const__20 = RT.var("clojure.core", "assoc");
    public static final Var const__21 = RT.var("riddley.walk", "macroexpand-all");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, Object params2, ISeq body) {
        Object object;
        Object object2;
        Object prev_body;
        Object and__5579__auto__26214;
        Object object3 = params2;
        params2 = null;
        ISeq iSeq = body;
        body = null;
        Object body2 = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__4, name2, object3, const__5, iSeq))));
        Object classname = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__10.get()), ".", name2)), ((IFn)const__11.getRawRoot()).invoke(name2));
        Object object4 = name2;
        name2 = null;
        Object object5 = ((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(const__10.get(), object4));
        Object object6 = and__5579__auto__26214 = (prev_body = object5 != null && object5 != Boolean.FALSE ? ((IFn)((IFn)const__14.getRawRoot()).invoke(const__15.getRawRoot())).invoke(classname) : null);
        if (object6 != null && object6 != Boolean.FALSE) {
            Object object7 = prev_body;
            prev_body = null;
            object2 = ((IFn)const__16.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), object7), ((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), body2));
        } else {
            object2 = and__5579__auto__26214;
            and__5579__auto__26214 = null;
        }
        if (object2 != null && object2 != Boolean.FALSE) {
            object = null;
        } else {
            Object object8 = classname;
            classname = null;
            ((IFn)const__19.getRawRoot()).invoke(const__15.getRawRoot(), const__20.getRawRoot(), object8, ((IFn)const__21.getRawRoot()).invoke(body2));
            object = body2;
            body2 = null;
        }
        return object;
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
        return types$deftype_PLUS_.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

