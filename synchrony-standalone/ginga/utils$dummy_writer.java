/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.utils$dummy_writer$fn__20402;
import ginga.utils$dummy_writer$fn__20404;
import ginga.utils$dummy_writer$fn__20406;
import ginga.utils.proxy$java.io.Writer$ff19274a;

public final class utils$dummy_writer
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "init-proxy");

    public static Object invokeStatic() {
        Writer$ff19274a p__7254__auto__20409 = new Writer$ff19274a();
        ((IFn)const__0.getRawRoot()).invoke(p__7254__auto__20409, RT.mapUniqueKeys("write", new utils$dummy_writer$fn__20402(), "flush", new utils$dummy_writer$fn__20404(), "close", new utils$dummy_writer$fn__20406()));
        Object var0 = null;
        return p__7254__auto__20409;
    }

    @Override
    public Object invoke() {
        return utils$dummy_writer.invokeStatic();
    }
}

