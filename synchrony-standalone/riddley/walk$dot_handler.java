/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class walk$dot_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "list*");
    public static final AFn const__4 = Symbol.intern(null, ".");
    public static final Var const__5 = RT.var("clojure.core", "seq?");
    public static final Var const__6 = RT.var("clojure.core", "doall");
    public static final Var const__7 = RT.var("clojure.core", "map");
    public static final Var const__8 = RT.var("clojure.core", "rest");

    public static Object invokeStatic(Object f, Object x) {
        Object object;
        Object vec__14816;
        Object object2 = x;
        x = null;
        Object object3 = vec__14816 = object2;
        vec__14816 = null;
        Object seq__14817 = ((IFn)const__0.getRawRoot()).invoke(object3);
        Object first__14818 = ((IFn)const__1.getRawRoot()).invoke(seq__14817);
        Object object4 = seq__14817;
        seq__14817 = null;
        Object seq__148172 = ((IFn)const__2.getRawRoot()).invoke(object4);
        first__14818 = null;
        Object first__148182 = ((IFn)const__1.getRawRoot()).invoke(seq__148172);
        Object object5 = seq__148172;
        seq__148172 = null;
        Object seq__148173 = ((IFn)const__2.getRawRoot()).invoke(object5);
        Object object6 = first__148182;
        first__148182 = null;
        Object hostexpr = object6;
        Object first__148183 = ((IFn)const__1.getRawRoot()).invoke(seq__148173);
        Object object7 = seq__148173;
        seq__148173 = null;
        Object seq__148174 = ((IFn)const__2.getRawRoot()).invoke(object7);
        Object object8 = first__148183;
        first__148183 = null;
        Object mem_or_meth = object8;
        Object object9 = seq__148174;
        seq__148174 = null;
        Object remainder = object9;
        IFn iFn = (IFn)const__3.getRawRoot();
        Object object10 = hostexpr;
        hostexpr = null;
        Object object11 = ((IFn)f).invoke(object10);
        Object object12 = ((IFn)const__5.getRawRoot()).invoke(mem_or_meth);
        if (object12 != null && object12 != Boolean.FALSE) {
            Object object13 = ((IFn)const__1.getRawRoot()).invoke(mem_or_meth);
            Object object14 = mem_or_meth;
            mem_or_meth = null;
            object = ((IFn)const__3.getRawRoot()).invoke(object13, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(f, ((IFn)const__8.getRawRoot()).invoke(object14))));
        } else {
            Object object15 = mem_or_meth;
            mem_or_meth = null;
            object = ((IFn)f).invoke(object15);
        }
        Object object16 = f;
        f = null;
        Object object17 = remainder;
        remainder = null;
        return iFn.invoke(const__4, object11, object, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object16, object17)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$dot_handler.invokeStatic(object3, object4);
    }
}

