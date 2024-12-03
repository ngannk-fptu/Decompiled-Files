/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.template;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.TemplateProcessor;
import com.sun.jersey.spi.template.ViewProcessor;
import java.io.IOException;
import java.io.OutputStream;

public final class TemplateViewProcessor
implements ViewProcessor<String> {
    private final TemplateProcessor tp;

    public TemplateViewProcessor(TemplateProcessor tp) {
        this.tp = tp;
    }

    @Override
    public String resolve(String name) {
        return this.tp.resolve(name);
    }

    @Override
    public void writeTo(String t, Viewable viewable, OutputStream out) throws IOException {
        this.tp.writeTo(t, viewable.getModel(), out);
    }
}

