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
import potemkin.namespaces$import_vars$fn__26047;
import potemkin.namespaces$import_vars$unravel__26042;

public final class namespaces$import_vars
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "mapcat");
    public static final Var const__1 = RT.var("clojure.core", "seq");
    public static final Var const__2 = RT.var("clojure.core", "concat");
    public static final Var const__3 = RT.var("clojure.core", "list");
    public static final AFn const__4 = Symbol.intern(null, "do");
    public static final Var const__5 = RT.var("clojure.core", "map");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq syms) {
        Object syms2;
        namespaces$import_vars$unravel__26042 unravel;
        namespaces$import_vars$unravel__26042 namespaces$import_vars$unravel__26042 = unravel = new namespaces$import_vars$unravel__26042();
        unravel = null;
        ISeq iSeq = syms;
        syms = null;
        Object object = syms2 = ((IFn)const__0.getRawRoot()).invoke(namespaces$import_vars$unravel__26042, iSeq);
        syms2 = null;
        return ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__4), ((IFn)const__5.getRawRoot()).invoke(new namespaces$import_vars$fn__26047(), object)));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return namespaces$import_vars.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

