/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.proxy;

import org.springframework.cglib.proxy.Callback;

public interface LazyLoader
extends Callback {
    public Object loadObject() throws Exception;
}

