/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class compiler$loading__6789__auto____14678
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");

    @Override
    public Object invoke() {
        Class clazz;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clojure.lang.Var"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clojure.lang.Compiler"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clojure.lang.Compiler$ObjMethod"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clojure.lang.Compiler$ObjExpr"));
            clazz = ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("riddley.Util"));
        }
        finally {
            Var.popThreadBindings();
        }
        return clazz;
    }
}

