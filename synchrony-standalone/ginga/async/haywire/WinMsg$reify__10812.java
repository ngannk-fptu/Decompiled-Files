/*
 * Decompiled with CFR 0.152.
 */
package ginga.async.haywire;

import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.IObj;
import clojure.lang.IPersistentMap;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.async.haywire.WinMsg;

public final class WinMsg$reify__10812
implements ILookupThunk,
IObj {
    final IPersistentMap __meta;
    Object gclass;
    public static final Var const__1 = RT.var("clojure.core", "class");

    public WinMsg$reify__10812(IPersistentMap iPersistentMap, Object object) {
        this.__meta = iPersistentMap;
        this.gclass = object;
    }

    public WinMsg$reify__10812(Object object) {
        this(null, object);
    }

    @Override
    public IPersistentMap meta() {
        return this.__meta;
    }

    @Override
    public IObj withMeta(IPersistentMap iPersistentMap) {
        return new WinMsg$reify__10812(iPersistentMap, this.gclass);
    }

    @Override
    public Object get(Object gtarget) {
        Object object;
        if (Util.identical(((IFn)const__1.getRawRoot()).invoke(gtarget), this.gclass)) {
            Object object2 = gtarget;
            gtarget = null;
            object = ((WinMsg)object2).cnt;
        } else {
            object = this;
        }
        return object;
    }
}

