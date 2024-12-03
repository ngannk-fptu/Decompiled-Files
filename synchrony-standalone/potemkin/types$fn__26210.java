/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$fn__26210
extends AFunction {
    public static final Var const__0 = RT.var("potemkin.types", "type-bodies");
    public static final AFn const__6 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 289, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/types.clj"));
    public static final AFn const__8 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 289, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/types.clj"));
    public static final Var const__9 = RT.var("clojure.core", "atom");

    public static Object invokeStatic() {
        Var var;
        Var v__6812__auto__26212;
        Var var2 = const__0;
        var2.setMeta((IPersistentMap)((Object)const__6));
        Var var3 = v__6812__auto__26212 = var2;
        v__6812__auto__26212 = null;
        if (var3.hasRoot()) {
            var = null;
        } else {
            Var var4 = const__0;
            var4.setMeta((IPersistentMap)((Object)const__8));
            var = var4;
            var4.bindRoot(((IFn)const__9.getRawRoot()).invoke(PersistentArrayMap.EMPTY));
        }
        return var;
    }

    @Override
    public Object invoke() {
        return types$fn__26210.invokeStatic();
    }
}

