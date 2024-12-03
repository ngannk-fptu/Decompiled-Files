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
import ginga.zip$fn__21209$__GT_VectorZipper__21238;
import ginga.zip$fn__21209$map__GT_VectorZipper__21240;
import java.util.Arrays;

public final class zip$fn__21209
extends AFunction {
    public static final Var const__0 = RT.var("ginga.zip", "->VectorZipper");
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 249, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final Var const__8 = RT.var("ginga.zip", "map->VectorZipper");
    public static final AFn const__10 = (AFn)((Object)RT.map(RT.keyword(null, "declared"), Boolean.TRUE, RT.keyword(null, "line"), 249, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final AFn const__15 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "v"), Symbol.intern(null, "i"), Symbol.intern(null, "up"), Symbol.intern(null, "subseqs"), Symbol.intern(null, "prev-i")))), RT.keyword(null, "doc"), "Positional factory function for class ginga.zip.VectorZipper.", RT.keyword(null, "line"), 249, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final AFn const__18 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "m__7972__auto__")))), RT.keyword(null, "doc"), "Factory function for class ginga.zip.VectorZipper, taking a map of keywords to field values.", RT.keyword(null, "line"), 249, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/zip.cljc"));
    public static final Object const__19 = RT.classForName("ginga.zip.VectorZipper");

    public static Object invokeStatic() {
        const__0.setMeta((IPersistentMap)((Object)const__7));
        const__8.setMeta((IPersistentMap)((Object)const__10));
        ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("ginga.zip.VectorZipper"));
        Var var = const__0;
        var.setMeta((IPersistentMap)((Object)const__15));
        var.bindRoot(new zip$fn__21209$__GT_VectorZipper__21238());
        Var var2 = const__8;
        var2.setMeta((IPersistentMap)((Object)const__18));
        var2.bindRoot(new zip$fn__21209$map__GT_VectorZipper__21240());
        return const__19;
    }

    @Override
    public Object invoke() {
        return zip$fn__21209.invokeStatic();
    }
}

