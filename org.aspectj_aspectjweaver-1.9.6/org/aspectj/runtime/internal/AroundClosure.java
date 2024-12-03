/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal;

import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AroundClosure {
    protected Object[] state;
    protected int bitflags = 0x100000;
    protected Object[] preInitializationState;

    public AroundClosure() {
    }

    public AroundClosure(Object[] state) {
        this.state = state;
    }

    public int getFlags() {
        return this.bitflags;
    }

    public Object[] getState() {
        return this.state;
    }

    public Object[] getPreInitializationState() {
        return this.preInitializationState;
    }

    public abstract Object run(Object[] var1) throws Throwable;

    public ProceedingJoinPoint linkClosureAndJoinPoint() {
        ProceedingJoinPoint jp = (ProceedingJoinPoint)this.state[this.state.length - 1];
        jp.set$AroundClosure(this);
        return jp;
    }

    public ProceedingJoinPoint linkStackClosureAndJoinPoint(int flags) {
        ProceedingJoinPoint jp = (ProceedingJoinPoint)this.state[this.state.length - 1];
        jp.stack$AroundClosure(this);
        this.bitflags = flags;
        return jp;
    }

    public ProceedingJoinPoint linkClosureAndJoinPoint(int flags) {
        ProceedingJoinPoint jp = (ProceedingJoinPoint)this.state[this.state.length - 1];
        jp.set$AroundClosure(this);
        this.bitflags = flags;
        return jp;
    }

    public void unlink() {
        ProceedingJoinPoint jp = (ProceedingJoinPoint)this.state[this.state.length - 1];
        jp.stack$AroundClosure(null);
    }
}

