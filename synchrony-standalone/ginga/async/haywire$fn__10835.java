/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IPersistentMap;
import clojure.lang.Namespace;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import ginga.async.haywire$fn__10835$__GT_PingMsg__10853;
import ginga.async.haywire$fn__10835$map__GT_PingMsg__10855;
import java.util.Arrays;

public final class haywire$fn__10835
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async.haywire", "->PingMsg");
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/async/haywire.cljc"));
    public static final Var const__8 = RT.var("ginga.async.haywire", "map->PingMsg");
    public static final AFn const__9 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/async/haywire.cljc"));
    public static final AFn const__13 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "type"), Symbol.intern(null, "seq")))), RT.keyword(null, "doc"), "Positional factory function for class ginga.async.haywire.PingMsg.", RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/async/haywire.cljc"));
    public static final AFn const__15 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "m__7972__auto__")))), RT.keyword(null, "doc"), "Factory function for class ginga.async.haywire.PingMsg, taking a map of keywords to field values.", RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/async/haywire.cljc"));
    public static final Object const__16 = RT.classForName("ginga.async.haywire.PingMsg");

    public static Object invokeStatic() {
        const__0.setMeta((IPersistentMap)((Object)const__7));
        const__8.setMeta((IPersistentMap)((Object)const__9));
        ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("ginga.async.haywire.PingMsg"));
        Var var = const__0;
        var.setMeta((IPersistentMap)((Object)const__13));
        var.bindRoot(new haywire$fn__10835$__GT_PingMsg__10853());
        Var var2 = const__8;
        var2.setMeta((IPersistentMap)((Object)const__15));
        var2.bindRoot(new haywire$fn__10835$map__GT_PingMsg__10855());
        return const__16;
    }

    @Override
    public Object invoke() {
        return haywire$fn__10835.invokeStatic();
    }
}

