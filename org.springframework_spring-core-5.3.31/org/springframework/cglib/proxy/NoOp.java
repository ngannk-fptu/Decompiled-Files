/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.proxy;

import org.springframework.cglib.proxy.Callback;

public interface NoOp
extends Callback {
    public static final NoOp INSTANCE = new NoOp(){};
}

