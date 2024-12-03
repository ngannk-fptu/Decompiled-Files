/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal;

import java.util.Stack;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.runtime.CFlow;
import org.aspectj.runtime.internal.CFlowPlusState;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl11;

public class CFlowStack {
    private static ThreadStackFactory tsFactory;
    private ThreadStack stackProxy = tsFactory.getNewThreadStack();

    private Stack getThreadStack() {
        return this.stackProxy.getThreadStack();
    }

    public void push(Object obj) {
        this.getThreadStack().push(obj);
    }

    public void pushInstance(Object obj) {
        this.getThreadStack().push(new CFlow(obj));
    }

    public void push(Object[] obj) {
        this.getThreadStack().push(new CFlowPlusState(obj));
    }

    public void pop() {
        Stack s = this.getThreadStack();
        s.pop();
        if (s.isEmpty()) {
            this.stackProxy.removeThreadStack();
        }
    }

    public Object peek() {
        Stack stack = this.getThreadStack();
        if (stack.isEmpty()) {
            throw new NoAspectBoundException();
        }
        return stack.peek();
    }

    public Object get(int index) {
        CFlow cf = this.peekCFlow();
        return null == cf ? null : cf.get(index);
    }

    public Object peekInstance() {
        CFlow cf = this.peekCFlow();
        if (cf != null) {
            return cf.getAspect();
        }
        throw new NoAspectBoundException();
    }

    public CFlow peekCFlow() {
        Stack stack = this.getThreadStack();
        if (stack.isEmpty()) {
            return null;
        }
        return (CFlow)stack.peek();
    }

    public CFlow peekTopCFlow() {
        Stack stack = this.getThreadStack();
        if (stack.isEmpty()) {
            return null;
        }
        return (CFlow)stack.elementAt(0);
    }

    public boolean isValid() {
        return !this.getThreadStack().isEmpty();
    }

    private static ThreadStackFactory getThreadLocalStackFactory() {
        return new ThreadStackFactoryImpl();
    }

    private static ThreadStackFactory getThreadLocalStackFactoryFor11() {
        return new ThreadStackFactoryImpl11();
    }

    private static void selectFactoryForVMVersion() {
        String v;
        String override = CFlowStack.getSystemPropertyWithoutSecurityException("aspectj.runtime.cflowstack.usethreadlocal", "unspecified");
        boolean useThreadLocalImplementation = false;
        useThreadLocalImplementation = override.equals("unspecified") ? (v = System.getProperty("java.class.version", "0.0")).compareTo("46.0") >= 0 : override.equals("yes") || override.equals("true");
        tsFactory = useThreadLocalImplementation ? CFlowStack.getThreadLocalStackFactory() : CFlowStack.getThreadLocalStackFactoryFor11();
    }

    private static String getSystemPropertyWithoutSecurityException(String aPropertyName, String aDefaultValue) {
        try {
            return System.getProperty(aPropertyName, aDefaultValue);
        }
        catch (SecurityException ex) {
            return aDefaultValue;
        }
    }

    public static String getThreadStackFactoryClassName() {
        return tsFactory.getClass().getName();
    }

    static {
        CFlowStack.selectFactoryForVMVersion();
    }
}

