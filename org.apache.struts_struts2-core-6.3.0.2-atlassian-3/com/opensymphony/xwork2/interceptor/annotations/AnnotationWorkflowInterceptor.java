/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.reflect.MethodUtils
 */
package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.interceptor.annotations.After;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.opensymphony.xwork2.interceptor.annotations.BeforeResult;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.struts2.StrutsException;

public class AnnotationWorkflowInterceptor
extends AbstractInterceptor
implements PreResultListener {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        invocation.addPreResultListener(this);
        ArrayList methods = new ArrayList(MethodUtils.getMethodsListWithAnnotation(action.getClass(), Before.class, (boolean)true, (boolean)true));
        if (methods.size() > 0) {
            Collections.sort(methods, new Comparator<Method>(){

                @Override
                public int compare(Method method1, Method method2) {
                    return AnnotationWorkflowInterceptor.comparePriorities(((Before)MethodUtils.getAnnotation((Method)method1, Before.class, (boolean)true, (boolean)true)).priority(), ((Before)MethodUtils.getAnnotation((Method)method2, Before.class, (boolean)true, (boolean)true)).priority());
                }
            });
            for (Method m : methods) {
                String resultCode = (String)MethodUtils.invokeMethod((Object)action, (boolean)true, (String)m.getName());
                if (resultCode == null) continue;
                return resultCode;
            }
        }
        String invocationResult = invocation.invoke();
        methods = new ArrayList(MethodUtils.getMethodsListWithAnnotation(action.getClass(), After.class, (boolean)true, (boolean)true));
        if (methods.size() > 0) {
            Collections.sort(methods, new Comparator<Method>(){

                @Override
                public int compare(Method method1, Method method2) {
                    return AnnotationWorkflowInterceptor.comparePriorities(((After)MethodUtils.getAnnotation((Method)method1, After.class, (boolean)true, (boolean)true)).priority(), ((After)MethodUtils.getAnnotation((Method)method2, After.class, (boolean)true, (boolean)true)).priority());
                }
            });
            for (Method m : methods) {
                MethodUtils.invokeMethod((Object)action, (boolean)true, (String)m.getName());
            }
        }
        return invocationResult;
    }

    protected static int comparePriorities(int val1, int val2) {
        if (val2 < val1) {
            return -1;
        }
        if (val2 > val1) {
            return 1;
        }
        return 0;
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        Object action = invocation.getAction();
        ArrayList methods = new ArrayList(MethodUtils.getMethodsListWithAnnotation(action.getClass(), BeforeResult.class, (boolean)true, (boolean)true));
        if (methods.size() > 0) {
            Collections.sort(methods, new Comparator<Method>(){

                @Override
                public int compare(Method method1, Method method2) {
                    return AnnotationWorkflowInterceptor.comparePriorities(((BeforeResult)MethodUtils.getAnnotation((Method)method1, BeforeResult.class, (boolean)true, (boolean)true)).priority(), ((BeforeResult)MethodUtils.getAnnotation((Method)method2, BeforeResult.class, (boolean)true, (boolean)true)).priority());
                }
            });
            for (Method m : methods) {
                try {
                    MethodUtils.invokeMethod((Object)action, (boolean)true, (String)m.getName());
                }
                catch (Exception e) {
                    throw new StrutsException(e);
                }
            }
        }
    }
}

