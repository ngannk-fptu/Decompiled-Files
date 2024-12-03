/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import org.codehaus.groovy.runtime.ExceptionUtils;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class BooleanReturningMethodInvoker {
    private final CallSiteArray csa;

    public BooleanReturningMethodInvoker() {
        this(null);
    }

    public BooleanReturningMethodInvoker(String methodName) {
        this.csa = new CallSiteArray(BooleanReturningMethodInvoker.class, new String[]{methodName, "asBoolean"});
    }

    public boolean invoke(Object receiver, Object ... args) {
        try {
            Object ret = this.csa.array[0].call(receiver, args);
            return this.convertToBoolean(ret);
        }
        catch (Throwable t) {
            ExceptionUtils.sneakyThrow(t);
            return false;
        }
    }

    public boolean convertToBoolean(Object arg) {
        if (arg == null) {
            return false;
        }
        if (arg instanceof Boolean) {
            return (Boolean)arg;
        }
        try {
            arg = this.csa.array[1].call(arg, CallSiteArray.NOPARAM);
        }
        catch (Throwable t) {
            ExceptionUtils.sneakyThrow(t);
        }
        return (Boolean)arg;
    }
}

