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
import ginga.async.haywire.Connection;

public final class Connection$reify__10950
implements ILookupThunk,
IObj {
    final IPersistentMap __meta;
    Object gclass;
    public static final Var const__1 = RT.var("clojure.core", "class");

    public Connection$reify__10950(IPersistentMap iPersistentMap, Object object) {
        this.__meta = iPersistentMap;
        this.gclass = object;
    }

    public Connection$reify__10950(Object object) {
        this(null, object);
    }

    @Override
    public IPersistentMap meta() {
        return this.__meta;
    }

    @Override
    public IObj withMeta(IPersistentMap iPersistentMap) {
        return new Connection$reify__10950(iPersistentMap, this.gclass);
    }

    @Override
    public Object get(Object gtarget) {
        Object object;
        if (Util.identical(((IFn)const__1.getRawRoot()).invoke(gtarget), this.gclass)) {
            Object object2 = gtarget;
            gtarget = null;
            object = ((Connection)object2).alive;
        } else {
            object = this;
        }
        return object;
    }
}

