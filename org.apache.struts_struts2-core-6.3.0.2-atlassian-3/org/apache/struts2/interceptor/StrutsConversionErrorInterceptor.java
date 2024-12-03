/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ConversionErrorInterceptor;
import com.opensymphony.xwork2.util.ValueStack;

public class StrutsConversionErrorInterceptor
extends ConversionErrorInterceptor {
    private static final long serialVersionUID = 2759744840082921602L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object getOverrideExpr(ActionInvocation invocation, Object value) {
        ValueStack stack = invocation.getStack();
        try {
            stack.push(value);
            String string = this.escape(stack.findString("top"));
            return string;
        }
        finally {
            stack.pop();
        }
    }

    @Override
    protected boolean shouldAddError(String propertyName, Object value) {
        if (value == null) {
            return false;
        }
        if ("".equals(value)) {
            return false;
        }
        if (value instanceof String[]) {
            String[] array = (String[])value;
            if (array.length == 0) {
                return false;
            }
            if (array.length > 1) {
                return true;
            }
            String str = array[0];
            if ("".equals(str)) {
                return false;
            }
        }
        return true;
    }
}

