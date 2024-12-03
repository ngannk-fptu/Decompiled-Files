/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Map;
import ognl.NullHandler;

public class ObjectNullHandler
implements NullHandler {
    @Override
    public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
        return null;
    }

    @Override
    public Object nullPropertyValue(Map context, Object target, Object property) {
        return null;
    }
}

