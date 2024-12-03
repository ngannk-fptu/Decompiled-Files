/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.List;
import java.util.Map;
import ognl.MethodAccessor;
import ognl.MethodFailedException;
import ognl.OgnlContext;
import ognl.OgnlRuntime;

public class ObjectMethodAccessor
implements MethodAccessor {
    @Override
    public Object callStaticMethod(Map context, Class targetClass, String methodName, Object[] args) throws MethodFailedException {
        List methods = OgnlRuntime.getMethods(targetClass, methodName, true);
        return OgnlRuntime.callAppropriateMethod((OgnlContext)context, targetClass, null, methodName, null, methods, args);
    }

    @Override
    public Object callMethod(Map context, Object target, String methodName, Object[] args) throws MethodFailedException {
        Class<?> targetClass = target == null ? null : target.getClass();
        List methods = OgnlRuntime.getMethods(targetClass, methodName, false);
        if (methods == null || methods.size() == 0) {
            methods = OgnlRuntime.getMethods(targetClass, methodName, true);
        }
        return OgnlRuntime.callAppropriateMethod((OgnlContext)context, target, target, methodName, null, methods, args);
    }
}

