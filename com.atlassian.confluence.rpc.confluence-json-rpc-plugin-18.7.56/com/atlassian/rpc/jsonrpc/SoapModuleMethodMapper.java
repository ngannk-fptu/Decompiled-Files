/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.rpc.jsonrpc;

import com.atlassian.voorhees.ApplicationException;
import com.atlassian.voorhees.RpcMethodMapper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoapModuleMethodMapper
implements RpcMethodMapper {
    private Object mappedObject;
    private Class publishedInterface;
    private boolean authenticated;
    private Map<String, List<Method>> methodCache;

    public SoapModuleMethodMapper(Object mappedObject, Class publishedInterface, boolean authenticated) {
        this.mappedObject = mappedObject;
        this.publishedInterface = publishedInterface;
        this.authenticated = authenticated;
        this.initMethodCache();
    }

    @Override
    public boolean methodExists(String methodName) {
        if (this.authenticated && methodName.equals("login")) {
            return false;
        }
        return this.methodCache.containsKey(methodName);
    }

    @Override
    public boolean methodExists(String methodName, int arity) {
        if (!this.methodExists(methodName)) {
            return false;
        }
        if (this.authenticated) {
            ++arity;
        }
        for (Method method : this.methodCache.get(methodName)) {
            if (method.getParameterTypes().length != arity) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<Class[]> getPossibleArgumentTypes(String methodName, int arity) {
        if (!this.methodExists(methodName)) {
            throw new IllegalStateException("No method exists with name " + methodName);
        }
        if (this.authenticated) {
            ++arity;
        }
        ArrayList<Class[]> possibleArgumentTypes = new ArrayList<Class[]>();
        for (Method method : this.methodCache.get(methodName)) {
            if (method.getParameterTypes().length != arity) continue;
            possibleArgumentTypes.add(this.filterIfAuthenticated(this.authenticated, method.getParameterTypes()));
        }
        if (possibleArgumentTypes.size() == 0) {
            throw new IllegalStateException("No method exists with name " + methodName + " and arity " + arity);
        }
        return possibleArgumentTypes;
    }

    private Class[] filterIfAuthenticated(boolean authenticated, Class[] parameterTypes) {
        if (authenticated) {
            Class[] replacement = new Class[parameterTypes.length - 1];
            if (replacement.length > 0) {
                System.arraycopy(parameterTypes, 1, replacement, 0, replacement.length);
            }
            parameterTypes = replacement;
        }
        return parameterTypes;
    }

    @Override
    public Object call(String methodName, Class[] argumentTypes, Object[] arguments) throws Exception {
        Object[] replacementArguments;
        if (this.authenticated) {
            Class[] replacementTypes = new Class[argumentTypes.length + 1];
            replacementTypes[0] = String.class;
            System.arraycopy(argumentTypes, 0, replacementTypes, 1, argumentTypes.length);
            argumentTypes = replacementTypes;
            replacementArguments = new Object[arguments.length + 1];
            replacementArguments[0] = "";
            System.arraycopy(arguments, 0, replacementArguments, 1, arguments.length);
            arguments = replacementArguments;
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.publishedInterface.getClassLoader());
            replacementArguments = this.publishedInterface.getMethod(methodName, argumentTypes).invoke(this.mappedObject, arguments);
            return replacementArguments;
        }
        catch (InvocationTargetException e) {
            throw new ApplicationException(e.getCause());
        }
        finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }

    private void initMethodCache() {
        this.methodCache = new HashMap<String, List<Method>>();
        for (Method method : this.publishedInterface.getMethods()) {
            if (!this.methodCache.containsKey(method.getName())) {
                this.methodCache.put(method.getName(), new ArrayList());
            }
            this.methodCache.get(method.getName()).add(method);
        }
    }
}

