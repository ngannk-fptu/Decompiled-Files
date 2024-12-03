/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import java.lang.reflect.InvocationTargetException;

public class PrepareInterceptor
extends MethodFilterInterceptor {
    private static final long serialVersionUID = -5216969014510719786L;
    private static final String PREPARE_PREFIX = "prepare";
    private static final String ALT_PREPARE_PREFIX = "prepareDo";
    private boolean alwaysInvokePrepare = true;
    private boolean firstCallPrepareDo = false;

    public void setAlwaysInvokePrepare(String alwaysInvokePrepare) {
        this.alwaysInvokePrepare = Boolean.parseBoolean(alwaysInvokePrepare);
    }

    public void setFirstCallPrepareDo(String firstCallPrepareDo) {
        this.firstCallPrepareDo = Boolean.parseBoolean(firstCallPrepareDo);
    }

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (action instanceof Preparable) {
            try {
                String[] prefixes = this.firstCallPrepareDo ? new String[]{ALT_PREPARE_PREFIX, PREPARE_PREFIX} : new String[]{PREPARE_PREFIX, ALT_PREPARE_PREFIX};
                PrefixMethodInvocationUtil.invokePrefixMethod(invocation, prefixes);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw e;
            }
            if (this.alwaysInvokePrepare) {
                ((Preparable)action).prepare();
            }
        }
        return invocation.invoke();
    }
}

