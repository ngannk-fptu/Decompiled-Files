/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;

public final class multiplex$accept_inc$fn__13049
extends AFunction {
    Object id;
    Object upstream;
    Object task_ch;
    public static final Var const__0 = RT.var("ginga.async.multiplex", "schedule-put");
    public static final Var const__1 = RT.var("ginga.async.multiplex", "win-msg");
    public static final Keyword const__2 = RT.keyword(null, "id");
    public static final Keyword const__3 = RT.keyword(null, "cnt");
    public static final Keyword const__4 = RT.keyword(null, "size");

    public multiplex$accept_inc$fn__13049(Object object, Object object2, Object object3) {
        this.id = object;
        this.upstream = object2;
        this.task_ch = object3;
    }

    @Override
    public Object invoke(Object cnt, Object size2) {
        Object[] objectArray = new Object[6];
        objectArray[0] = const__2;
        objectArray[1] = this_.id;
        objectArray[2] = const__3;
        Object object = cnt;
        cnt = null;
        objectArray[3] = object;
        objectArray[4] = const__4;
        Object object2 = size2;
        size2 = null;
        objectArray[5] = object2;
        multiplex$accept_inc$fn__13049 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.task_ch, this_.upstream, ((IFn)const__1.getRawRoot()).invoke(RT.mapUniqueKeys(objectArray)));
    }
}

