/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.view;

import com.atlassian.gadgets.GadgetState;

public class GadgetRenderingException
extends RuntimeException {
    private final GadgetState gadget;

    public GadgetRenderingException(String message, GadgetState gadget, Throwable cause) {
        super(message, cause);
        this.gadget = gadget;
    }

    public GadgetRenderingException(String message, GadgetState gadget) {
        super(message);
        this.gadget = gadget;
    }

    public GadgetRenderingException(GadgetState gadget, Throwable cause) {
        super(cause);
        this.gadget = gadget;
    }

    public GadgetState getGadgetState() {
        return this.gadget;
    }
}

