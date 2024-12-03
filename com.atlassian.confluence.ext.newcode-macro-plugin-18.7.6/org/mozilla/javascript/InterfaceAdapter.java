/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ClassCache;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.VMBridge;
import org.mozilla.javascript.WrapFactory;

public class InterfaceAdapter {
    private final Object proxyHelper;

    static Object create(Context cx, Class<?> cl, ScriptableObject object) {
        if (!cl.isInterface()) {
            throw new IllegalArgumentException();
        }
        Scriptable topScope = ScriptRuntime.getTopCallScope(cx);
        ClassCache cache = ClassCache.get(topScope);
        InterfaceAdapter adapter = (InterfaceAdapter)cache.getInterfaceAdapter(cl);
        ContextFactory cf = cx.getFactory();
        if (adapter == null) {
            if (object instanceof Callable) {
                boolean canConvert;
                Method[] methods = cl.getMethods();
                HashSet<String> functionalMethodNames = new HashSet<String>();
                HashSet<String> defaultMethodNames = new HashSet<String>();
                for (Method method : methods) {
                    if (InterfaceAdapter.isFunctionalMethodCandidate(method)) {
                        functionalMethodNames.add(method.getName());
                        if (functionalMethodNames.size() <= 1) continue;
                        break;
                    }
                    defaultMethodNames.add(method.getName());
                }
                boolean bl = canConvert = functionalMethodNames.size() == 1 || functionalMethodNames.isEmpty() && defaultMethodNames.size() == 1;
                if (!canConvert) {
                    if (functionalMethodNames.isEmpty() && defaultMethodNames.isEmpty()) {
                        throw Context.reportRuntimeErrorById("msg.no.empty.interface.conversion", cl.getName());
                    }
                    throw Context.reportRuntimeErrorById("msg.no.function.interface.conversion", cl.getName());
                }
            }
            adapter = new InterfaceAdapter(cf, cl);
            cache.cacheInterfaceAdapter(cl, adapter);
        }
        return VMBridge.instance.newInterfaceProxy(adapter.proxyHelper, cf, adapter, object, topScope);
    }

    private static boolean isFunctionalMethodCandidate(Method method) {
        if (method.getName().equals("equals") || method.getName().equals("hashCode") || method.getName().equals("toString")) {
            return false;
        }
        return Modifier.isAbstract(method.getModifiers());
    }

    private InterfaceAdapter(ContextFactory cf, Class<?> cl) {
        this.proxyHelper = VMBridge.instance.getInterfaceProxyHelper(cf, new Class[]{cl});
    }

    public Object invoke(ContextFactory cf, Object target, Scriptable topScope, Object thisObject, Method method, Object[] args) {
        return cf.call(cx -> this.invokeImpl(cx, target, topScope, thisObject, method, args));
    }

    Object invokeImpl(Context cx, Object target, Scriptable topScope, Object thisObject, Method method, Object[] args) {
        Callable function;
        if (target instanceof Callable) {
            function = (Callable)target;
        } else {
            Scriptable s = (Scriptable)target;
            String methodName = method.getName();
            Object value = ScriptableObject.getProperty(s, methodName);
            if (value == Scriptable.NOT_FOUND) {
                Context.reportWarning(ScriptRuntime.getMessageById("msg.undefined.function.interface", methodName));
                Class<?> resultType = method.getReturnType();
                if (resultType == Void.TYPE) {
                    return null;
                }
                return Context.jsToJava(null, resultType);
            }
            if (!(value instanceof Callable)) {
                throw Context.reportRuntimeErrorById("msg.not.function.interface", methodName);
            }
            function = (Callable)value;
        }
        WrapFactory wf = cx.getWrapFactory();
        if (args == null) {
            args = ScriptRuntime.emptyArgs;
        } else {
            int N = args.length;
            for (int i = 0; i != N; ++i) {
                Object arg = args[i];
                if (arg instanceof String || arg instanceof Number || arg instanceof Boolean) continue;
                args[i] = wf.wrap(cx, topScope, arg, null);
            }
        }
        Scriptable thisObj = wf.wrapAsJavaObject(cx, topScope, thisObject, null);
        Object result = function.call(cx, topScope, thisObj, args);
        Class<?> javaResultType = method.getReturnType();
        result = javaResultType == Void.TYPE ? null : Context.jsToJava(result, javaResultType);
        return result;
    }
}

