/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.util.Iterator;
import org.apache.velocity.util.introspection.AbstractChainableUberspector;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.Uberspect;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.apache.velocity.util.introspection.VelPropertySet;

public class LinkingUberspector
extends AbstractChainableUberspector {
    private Uberspect leftUberspect;
    private Uberspect rightUberspect;

    public LinkingUberspector(Uberspect left, Uberspect right) {
        this.leftUberspect = left;
        this.rightUberspect = right;
    }

    public void init() {
        this.leftUberspect.init();
        this.rightUberspect.init();
    }

    public Iterator getIterator(Object obj, Info i) throws Exception {
        Iterator it = this.leftUberspect.getIterator(obj, i);
        return it != null ? it : this.rightUberspect.getIterator(obj, i);
    }

    public VelMethod getMethod(Object obj, String methodName, Object[] args, Info i) throws Exception {
        VelMethod method = this.leftUberspect.getMethod(obj, methodName, args, i);
        return method != null ? method : this.rightUberspect.getMethod(obj, methodName, args, i);
    }

    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        VelPropertyGet getter = this.leftUberspect.getPropertyGet(obj, identifier, i);
        return getter != null ? getter : this.rightUberspect.getPropertyGet(obj, identifier, i);
    }

    public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info i) throws Exception {
        VelPropertySet setter = this.leftUberspect.getPropertySet(obj, identifier, arg, i);
        return setter != null ? setter : this.rightUberspect.getPropertySet(obj, identifier, arg, i);
    }
}

