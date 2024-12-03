/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import java.util.Arrays;
import riddley.compiler$fn__14686$local_id__14687;

public final class compiler$fn__14686
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "atom");
    public static final Object const__1 = 0L;
    public static final Var const__2 = RT.var("riddley.compiler", "local-id");
    public static final AFn const__11 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "line"), 26, RT.keyword(null, "column"), 3, RT.keyword(null, "file"), "riddley/compiler.clj"));

    public static Object invokeStatic() {
        Object n = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Var var = const__2;
        var.setMeta((IPersistentMap)((Object)const__11));
        Object object = n;
        n = null;
        var.bindRoot(new compiler$fn__14686$local_id__14687(object));
        return var;
    }

    @Override
    public Object invoke() {
        return compiler$fn__14686.invokeStatic();
    }
}

