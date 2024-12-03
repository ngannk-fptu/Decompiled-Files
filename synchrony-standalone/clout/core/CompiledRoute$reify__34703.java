/*
 * Decompiled with CFR 0.152.
 */
package clout.core;

import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.IObj;
import clojure.lang.IPersistentMap;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import clout.core.CompiledRoute;

public final class CompiledRoute$reify__34703
implements ILookupThunk,
IObj {
    final IPersistentMap __meta;
    Object gclass;
    public static final Var const__1 = RT.var("clojure.core", "class");

    public CompiledRoute$reify__34703(IPersistentMap iPersistentMap, Object object) {
        this.__meta = iPersistentMap;
        this.gclass = object;
    }

    public CompiledRoute$reify__34703(Object object) {
        this(null, object);
    }

    @Override
    public IPersistentMap meta() {
        return this.__meta;
    }

    @Override
    public IObj withMeta(IPersistentMap iPersistentMap) {
        return new CompiledRoute$reify__34703(iPersistentMap, this.gclass);
    }

    @Override
    public Object get(Object gtarget) {
        Object object;
        if (Util.identical(((IFn)const__1.getRawRoot()).invoke(gtarget), this.gclass)) {
            Object object2 = gtarget;
            gtarget = null;
            object = ((CompiledRoute)object2).keys;
        } else {
            object = this;
        }
        return object;
    }
}

