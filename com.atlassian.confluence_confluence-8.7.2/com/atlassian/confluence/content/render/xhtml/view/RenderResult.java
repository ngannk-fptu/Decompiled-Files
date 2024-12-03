/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view;

public class RenderResult {
    private final String render;
    private final boolean successful;

    public static RenderResult success(String result) {
        return new RenderResult(result, true);
    }

    public static RenderResult failure(String errorRender) {
        return new RenderResult(errorRender, false);
    }

    private RenderResult(String render, boolean successful) {
        this.render = render;
        this.successful = successful;
    }

    public String getRender() {
        return this.render;
    }

    public boolean isSuccessful() {
        return this.successful;
    }
}

