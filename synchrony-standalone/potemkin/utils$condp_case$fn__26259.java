/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.utils$condp_case$fn__26259$fn__26263;

public final class utils$condp_case$fn__26259
extends AFunction {
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "concat");
    public static final Var const__5 = RT.var("clojure.core", "list");
    public static final Var const__6 = RT.var("clojure.core", "sequential?");
    public static final AFn const__7 = Symbol.intern("clojure.core", "or");
    public static final Var const__8 = RT.var("clojure.core", "map");
    public static final AFn const__9 = Symbol.intern(null, "pred#__26253__auto__");
    public static final AFn const__10 = Symbol.intern(null, "val#__26254__auto__");

    @Override
    public Object invoke(Object p__26258) {
        Object object;
        Object object2 = p__26258;
        p__26258 = null;
        Object vec__26260 = object2;
        Object vals2 = RT.nth(vec__26260, RT.intCast(0L), null);
        Object object3 = vec__26260;
        vec__26260 = null;
        Object expr = RT.nth(object3, RT.intCast(1L), null);
        IFn iFn = (IFn)const__3.getRawRoot();
        IFn iFn2 = (IFn)const__4.getRawRoot();
        IFn iFn3 = (IFn)const__5.getRawRoot();
        Object object4 = ((IFn)const__6.getRawRoot()).invoke(vals2);
        if (object4 != null && object4 != Boolean.FALSE) {
            Object object5 = vals2;
            vals2 = null;
            object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__7), ((IFn)const__8.getRawRoot()).invoke(new utils$condp_case$fn__26259$fn__26263(), object5)));
        } else {
            Object object6 = vals2;
            vals2 = null;
            object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__9), ((IFn)const__5.getRawRoot()).invoke(const__10), ((IFn)const__5.getRawRoot()).invoke(object6)));
        }
        Object object7 = expr;
        expr = null;
        utils$condp_case$fn__26259 this_ = null;
        return iFn.invoke(iFn2.invoke(iFn3.invoke(object), ((IFn)const__5.getRawRoot()).invoke(object7)));
    }
}

