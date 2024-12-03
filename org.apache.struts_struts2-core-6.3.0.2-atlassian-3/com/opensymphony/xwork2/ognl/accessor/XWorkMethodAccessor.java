/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MethodFailedException
 *  ognl.ObjectMethodAccessor
 *  ognl.OgnlContext
 *  ognl.OgnlRuntime
 *  ognl.PropertyAccessor
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import ognl.MethodFailedException;
import ognl.ObjectMethodAccessor;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XWorkMethodAccessor
extends ObjectMethodAccessor {
    private static final Logger LOG = LogManager.getLogger(XWorkMethodAccessor.class);

    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {
        boolean e;
        Boolean exec;
        if (objects.length == 1 && context instanceof OgnlContext) {
            try {
                PropertyDescriptor descriptor;
                Class<?> propertyType;
                OgnlContext ogContext = (OgnlContext)context;
                if (OgnlRuntime.hasSetProperty((OgnlContext)ogContext, (Object)object, (Object)string) && Collection.class.isAssignableFrom(propertyType = (descriptor = OgnlRuntime.getPropertyDescriptor(object.getClass(), (String)string)).getPropertyType())) {
                    Object propVal = OgnlRuntime.getProperty((OgnlContext)ogContext, (Object)object, (Object)string);
                    PropertyAccessor accessor = OgnlRuntime.getPropertyAccessor(Collection.class);
                    ReflectionContextState.setGettingByKeyProperty((Map<String, Object>)ogContext, true);
                    return accessor.getProperty((Map)ogContext, propVal, objects[0]);
                }
            }
            catch (Exception oe) {
                LOG.error("An unexpected exception occurred", (Throwable)oe);
            }
        }
        if (objects.length == 2 && string.startsWith("set") || objects.length == 1 && string.startsWith("get")) {
            boolean e2;
            exec = (Boolean)context.get("xwork.IndexedPropertyAccessor.denyMethodExecution");
            boolean bl = e2 = exec == null ? false : exec;
            if (!e2) {
                return this.callMethodWithDebugInfo(context, object, string, objects);
            }
        }
        boolean bl = e = (exec = Boolean.valueOf(ReflectionContextState.isDenyMethodExecution(context))) != null && exec != false;
        if (!e) {
            return this.callMethodWithDebugInfo(context, object, string, objects);
        }
        return null;
    }

    private Object callMethodWithDebugInfo(Map context, Object object, String methodName, Object[] objects) throws MethodFailedException {
        try {
            return super.callMethod(context, object, methodName, objects);
        }
        catch (MethodFailedException e) {
            if (LOG.isDebugEnabled() && !(e.getReason() instanceof NoSuchMethodException)) {
                LOG.debug("Error calling method through OGNL: object: [{}] method: [{}] args: [{}]", (Object)e.getReason(), (Object)object.toString(), (Object)methodName, (Object)Arrays.toString(objects));
            }
            throw e;
        }
    }

    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        boolean e;
        Boolean exec = ReflectionContextState.isDenyMethodExecution(context);
        boolean bl = e = exec == null ? false : exec;
        if (!e) {
            return this.callStaticMethodWithDebugInfo(context, aClass, string, objects);
        }
        return null;
    }

    private Object callStaticMethodWithDebugInfo(Map context, Class aClass, String methodName, Object[] objects) throws MethodFailedException {
        try {
            return super.callStaticMethod(context, aClass, methodName, objects);
        }
        catch (MethodFailedException e) {
            if (LOG.isDebugEnabled() && !(e.getReason() instanceof NoSuchMethodException)) {
                LOG.debug("Error calling method through OGNL, class: [{}] method: [{}] args: [{}]", (Object)e.getReason(), (Object)aClass.getName(), (Object)methodName, (Object)Arrays.toString(objects));
            }
            throw e;
        }
    }
}

