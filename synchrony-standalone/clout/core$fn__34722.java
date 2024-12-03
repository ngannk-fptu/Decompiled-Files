/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$fn__34722
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "str");

    public static Object invokeStatic() {
        String letter;
        String string2 = letter = "\\p{L}";
        String string3 = letter;
        String string4 = letter;
        letter = null;
        return ((IFn)const__0.getRawRoot()).invoke("route    = (scheme / part) part*\n       scheme   = #'(https?:)?//'\n       <part>   = literal | escaped | wildcard | param\n       literal  = #'(:[^", string2, "_*{}\\\\]|[^:*{}\\\\])+'\n       escaped  = #'\\\\.'\n       wildcard = '*'\n       param    = key pattern?\n       key      = <':'> #'([", string3, "_][", string4, "_0-9-]*)'\n       pattern  = '{' (#'(?:[^{}\\\\]|\\\\.)+' | pattern)* '}'");
    }

    @Override
    public Object invoke() {
        return core$fn__34722.invokeStatic();
    }
}

