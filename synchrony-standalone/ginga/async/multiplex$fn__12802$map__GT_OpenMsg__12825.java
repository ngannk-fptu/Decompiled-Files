/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.MapEquivalence;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async.multiplex.OpenMsg;

public final class multiplex$fn__12802$map__GT_OpenMsg__12825
extends AFunction {
    public static final Var const__2 = RT.var("clojure.core", "into");

    @Override
    public Object invoke(Object m__7972__auto__) {
        Object object;
        if (m__7972__auto__ instanceof MapEquivalence) {
            object = m__7972__auto__;
            m__7972__auto__ = null;
        } else {
            Object object2 = m__7972__auto__;
            m__7972__auto__ = null;
            object = ((IFn)const__2.getRawRoot()).invoke(PersistentArrayMap.EMPTY, object2);
        }
        multiplex$fn__12802$map__GT_OpenMsg__12825 this_ = null;
        return OpenMsg.create((IPersistentMap)object);
    }
}

