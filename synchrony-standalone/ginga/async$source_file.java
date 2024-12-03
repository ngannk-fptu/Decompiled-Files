/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Var;

public final class async$source_file
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "*file*");

    public static Object invokeStatic() {
        return const__0.get();
    }

    @Override
    public Object invoke() {
        return async$source_file.invokeStatic();
    }
}

