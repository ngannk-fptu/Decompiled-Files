/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.zip.Edit;
import ginga.zip.Traverse;

public final class zip$replace_down
extends AFunction {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    public static final Var const__0;
    public static final Var const__1;

    /*
     * Unable to fully structure code
     */
    public static Object invokeStatic(Object z, Object node) {
        v0 = z;
        z = null;
        v1 = v0;
        if (Util.classOf(v0) == zip$replace_down.__cached_class__0) ** GOTO lbl8
        if (!(v1 instanceof Edit)) {
            v1 = v1;
            zip$replace_down.__cached_class__0 = Util.classOf(v1);
lbl8:
            // 2 sources

            v2 = node;
            node = null;
            v3 = zip$replace_down.const__1.getRawRoot().invoke(v1, v2);
        } else {
            v4 = node;
            node = null;
            v3 = v5 = ((Edit)v1).replace(v4);
        }
        if (Util.classOf(v3) == zip$replace_down.__cached_class__1) ** GOTO lbl19
        if (!(v5 instanceof Traverse)) {
            v5 = v5;
            zip$replace_down.__cached_class__1 = Util.classOf(v5);
lbl19:
            // 2 sources

            v6 = zip$replace_down.const__0.getRawRoot().invoke(v5);
        } else {
            v6 = ((Traverse)v5).down();
        }
        return v6;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return zip$replace_down.invokeStatic(object3, object4);
    }

    static {
        const__0 = RT.var("ginga.zip", "down");
        const__1 = RT.var("ginga.zip", "replace");
    }
}

