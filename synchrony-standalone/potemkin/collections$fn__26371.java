/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class collections$fn__26371
extends AFunction {
    public static final Var const__0 = RT.var("potemkin.collections", "PotemkinMap");
    public static final AFn const__6 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 5, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/collections.clj"));
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 5, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/collections.clj"));

    public static Object invokeStatic() {
        Var var;
        Var v__6812__auto__26373;
        Var var2 = const__0;
        var2.setMeta((IPersistentMap)((Object)const__6));
        Var var3 = v__6812__auto__26373 = var2;
        v__6812__auto__26373 = null;
        if (var3.hasRoot()) {
            var = null;
        } else {
            Var var4 = const__0;
            var4.setMeta((IPersistentMap)((Object)const__7));
            var = var4;
            var4.bindRoot(PersistentArrayMap.EMPTY);
        }
        return var;
    }

    @Override
    public Object invoke() {
        return collections$fn__26371.invokeStatic();
    }
}

