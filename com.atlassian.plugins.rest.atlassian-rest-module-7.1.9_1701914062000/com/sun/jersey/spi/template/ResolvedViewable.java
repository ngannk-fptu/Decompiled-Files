/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.template;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ViewProcessor;
import java.io.IOException;
import java.io.OutputStream;

public class ResolvedViewable<T>
extends Viewable {
    private final ViewProcessor<T> vp;
    private final T templateObject;

    public ResolvedViewable(ViewProcessor<T> vp, T t, Viewable v) {
        this(vp, t, v, null);
    }

    public ResolvedViewable(ViewProcessor<T> vp, T t, Viewable v, Class<?> resolvingClass) {
        super(v.getTemplateName(), v.getModel(), resolvingClass);
        this.vp = vp;
        this.templateObject = t;
    }

    public void writeTo(OutputStream out) throws IOException {
        this.vp.writeTo(this.templateObject, this, out);
    }
}

