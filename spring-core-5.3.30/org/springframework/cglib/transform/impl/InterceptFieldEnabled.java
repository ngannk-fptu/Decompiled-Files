/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform.impl;

import org.springframework.cglib.transform.impl.InterceptFieldCallback;

public interface InterceptFieldEnabled {
    public void setInterceptFieldCallback(InterceptFieldCallback var1);

    public InterceptFieldCallback getInterceptFieldCallback();
}

