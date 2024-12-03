/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu;

import com.google.template.soy.sharedpasses.render.RenderException;

public class SoyTofuException
extends RuntimeException {
    public SoyTofuException(String message) {
        super(message);
    }

    public SoyTofuException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoyTofuException(RenderException re) {
        super(re.getMessage(), re);
        re.finalizeStackTrace();
        re.finalizeStackTrace(this);
    }
}

