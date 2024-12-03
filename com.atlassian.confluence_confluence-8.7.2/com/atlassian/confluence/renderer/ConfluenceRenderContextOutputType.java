/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

public enum ConfluenceRenderContextOutputType {
    PAGE_GADGET("page_gadget");

    private String outputType;

    private ConfluenceRenderContextOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String toString() {
        return this.outputType;
    }
}

