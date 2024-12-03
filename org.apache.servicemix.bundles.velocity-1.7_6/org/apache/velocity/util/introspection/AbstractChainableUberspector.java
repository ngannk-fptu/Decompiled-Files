/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.util.Iterator;
import org.apache.velocity.util.introspection.ChainableUberspector;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.Uberspect;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.apache.velocity.util.introspection.VelPropertySet;

public abstract class AbstractChainableUberspector
extends UberspectImpl
implements ChainableUberspector {
    protected Uberspect inner;

    public void wrap(Uberspect inner) {
        this.inner = inner;
    }

    public void init() {
        if (this.inner != null) {
            this.inner.init();
        }
    }

    public Iterator getIterator(Object obj, Info i) throws Exception {
        return this.inner != null ? this.inner.getIterator(obj, i) : null;
    }

    public VelMethod getMethod(Object obj, String methodName, Object[] args, Info i) throws Exception {
        return this.inner != null ? this.inner.getMethod(obj, methodName, args, i) : null;
    }

    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        return this.inner != null ? this.inner.getPropertyGet(obj, identifier, i) : null;
    }

    public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info i) throws Exception {
        return this.inner != null ? this.inner.getPropertySet(obj, identifier, arg, i) : null;
    }
}

