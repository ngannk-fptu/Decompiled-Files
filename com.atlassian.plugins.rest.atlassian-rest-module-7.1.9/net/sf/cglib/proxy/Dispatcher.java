/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import net.sf.cglib.proxy.Callback;

public interface Dispatcher
extends Callback {
    public Object loadObject() throws Exception;
}

