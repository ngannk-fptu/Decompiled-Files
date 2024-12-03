/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IPersistentMap;
import clojure.lang.Namespace;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import ginga.core$fn__8305$__GT_ErrorBox__8307;
import java.util.Arrays;

public final class core$fn__8305
extends AFunction {
    public static final Var const__0 = RT.var("ginga.core", "->ErrorBox");
    public static final AFn const__9 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "value")))), RT.keyword(null, "doc"), "Positional factory function for class ginga.core.ErrorBox.", RT.keyword(null, "line"), 309, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/core.cljc"));
    public static final Object const__10 = RT.classForName("ginga.core.ErrorBox");

    public static Object invokeStatic() {
        ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("ginga.core.ErrorBox"));
        Var var = const__0;
        var.setMeta((IPersistentMap)((Object)const__9));
        var.bindRoot(new core$fn__8305$__GT_ErrorBox__8307());
        return const__10;
    }

    @Override
    public Object invoke() {
        return core$fn__8305.invokeStatic();
    }
}

