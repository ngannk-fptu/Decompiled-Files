/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import net.sf.cglib.proxy.Callback;

public interface NoOp
extends Callback {
    public static final NoOp INSTANCE = new NoOp(){};
}

