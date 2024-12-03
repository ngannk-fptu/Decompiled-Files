/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.Namespace;
import clojure.lang.RT;

public final class types$fn__26086
extends AFunction {
    public static final Object const__0 = RT.classForName("potemkin.types.PotemkinType");

    public static Object invokeStatic() {
        return ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("potemkin.types.PotemkinType"));
    }

    @Override
    public Object invoke() {
        return types$fn__26086.invokeStatic();
    }
}

