/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.pipe.Fiber;

public interface FiberContextSwitchInterceptor {
    public <R, P> R execute(Fiber var1, P var2, Work<R, P> var3);

    public static interface Work<R, P> {
        public R execute(P var1);
    }
}

