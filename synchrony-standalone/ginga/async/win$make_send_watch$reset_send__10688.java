/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class win$make_send_watch$reset_send__10688
extends AFunction {
    Object send_window;
    public static final Var const__0 = RT.var("ginga.core", "reset-return-prev!");
    public static final AFn const__2 = (AFn)((Object)Tuple.create(0L, 0L));
    public static final Var const__5 = RT.var("ginga.async.win", "open?");

    public win$make_send_watch$reset_send__10688(Object object) {
        this.send_window = object;
    }

    @Override
    public Object invoke(Object stats2) {
        Object object;
        Object stats3;
        Object object2 = stats2;
        stats2 = null;
        Object vec__10689 = ((IFn)const__0.getRawRoot()).invoke(object2, const__2);
        Object cnt = RT.nth(vec__10689, RT.intCast(0L), null);
        Object size2 = RT.nth(vec__10689, RT.intCast(1L), null);
        Object object3 = vec__10689;
        vec__10689 = null;
        Object object4 = stats3 = object3;
        stats3 = null;
        Object object5 = ((IFn)const__5.getRawRoot()).invoke(object4);
        if (object5 != null && object5 != Boolean.FALSE) {
            Object object6 = cnt;
            cnt = null;
            Object object7 = size2;
            size2 = null;
            win$make_send_watch$reset_send__10688 this_ = null;
            object = ((IFn)this_.send_window).invoke(object6, object7);
        } else {
            object = null;
        }
        return object;
    }
}

