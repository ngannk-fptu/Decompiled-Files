/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util;

import java.util.Vector;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RPCUtils {
    protected static Vector<Object> makeParams(Object p1) {
        Vector<Object> params = new Vector<Object>();
        params.add(p1);
        return params;
    }

    protected static Vector<Object> makeParams(Object p1, Object p2) {
        Vector<Object> params = RPCUtils.makeParams(p1);
        params.add(p2);
        return params;
    }

    protected static Vector<Object> makeParams(Object p1, Object p2, Object p3) {
        Vector<Object> params = RPCUtils.makeParams(p1, p2);
        params.add(p3);
        return params;
    }

    protected static Vector<Object> makeParams(Object p1, Object p2, Object p3, Object p4) {
        Vector<Object> params = RPCUtils.makeParams(p1, p2, p3);
        params.add(p4);
        return params;
    }

    protected static Vector<Object> makeParams(Object p1, Object p2, Object p3, Object p4, Object p5) {
        Vector<Object> params = RPCUtils.makeParams(p1, p2, p3, p4);
        params.add(p5);
        return params;
    }
}

