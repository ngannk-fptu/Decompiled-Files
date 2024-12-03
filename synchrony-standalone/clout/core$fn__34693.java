/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IPersistentMap;
import clojure.lang.Namespace;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import clout.core$fn__34693$__GT_CompiledRoute__34717;
import clout.core$fn__34693$map__GT_CompiledRoute__34719;
import java.util.Arrays;

public final class core$fn__34693
extends AFunction {
    public static final Var const__0 = RT.var("clout.core", "->CompiledRoute");
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 56, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "clout/core.cljc"));
    public static final Var const__8 = RT.var("clout.core", "map->CompiledRoute");
    public static final AFn const__9 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 56, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "clout/core.cljc"));
    public static final AFn const__13 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "source"), Symbol.intern(null, "re"), Symbol.intern(null, "keys"), Symbol.intern(null, "absolute?")))), RT.keyword(null, "doc"), "Positional factory function for class clout.core.CompiledRoute.", RT.keyword(null, "line"), 56, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "clout/core.cljc"));
    public static final AFn const__15 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "m__7972__auto__")))), RT.keyword(null, "doc"), "Factory function for class clout.core.CompiledRoute, taking a map of keywords to field values.", RT.keyword(null, "line"), 56, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "clout/core.cljc"));
    public static final Object const__16 = RT.classForName("clout.core.CompiledRoute");

    public static Object invokeStatic() {
        const__0.setMeta((IPersistentMap)((Object)const__7));
        const__8.setMeta((IPersistentMap)((Object)const__9));
        ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clout.core.CompiledRoute"));
        Var var = const__0;
        var.setMeta((IPersistentMap)((Object)const__13));
        var.bindRoot(new core$fn__34693$__GT_CompiledRoute__34717());
        Var var2 = const__8;
        var2.setMeta((IPersistentMap)((Object)const__15));
        var2.bindRoot(new core$fn__34693$map__GT_CompiledRoute__34719());
        return const__16;
    }

    @Override
    public Object invoke() {
        return core$fn__34693.invokeStatic();
    }
}

