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
import ginga.zip$fn__21147$__GT_Zipper__21175;
import ginga.zip$fn__21147$map__GT_Zipper__21177;
import java.util.Arrays;

public final class zip$fn__21147
extends AFunction {
    public static final Var const__0 = RT.var("ginga.zip", "->Zipper");
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 107, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final Var const__8 = RT.var("ginga.zip", "map->Zipper");
    public static final AFn const__9 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 107, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final AFn const__13 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "prevs"), Symbol.intern(null, "nexts"), Symbol.intern(null, "up"), Symbol.intern(null, "m")))), RT.keyword(null, "doc"), "Positional factory function for class ginga.zip.Zipper.", RT.keyword(null, "line"), 107, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final AFn const__15 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "m__7972__auto__")))), RT.keyword(null, "doc"), "Factory function for class ginga.zip.Zipper, taking a map of keywords to field values.", RT.keyword(null, "line"), 107, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final Object const__16 = RT.classForName("ginga.zip.Zipper");

    public static Object invokeStatic() {
        const__0.setMeta((IPersistentMap)((Object)const__7));
        const__8.setMeta((IPersistentMap)((Object)const__9));
        ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("ginga.zip.Zipper"));
        Var var = const__0;
        var.setMeta((IPersistentMap)((Object)const__13));
        var.bindRoot(new zip$fn__21147$__GT_Zipper__21175());
        Var var2 = const__8;
        var2.setMeta((IPersistentMap)((Object)const__15));
        var2.bindRoot(new zip$fn__21147$map__GT_Zipper__21177());
        return const__16;
    }

    @Override
    public Object invoke() {
        return zip$fn__21147.invokeStatic();
    }
}

