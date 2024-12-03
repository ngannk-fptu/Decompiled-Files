/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.template;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContextException;
import javax.ws.rs.core.UriInfo;

public interface TemplateContext {
    public ResolvedViewable resolveViewable(Viewable var1) throws TemplateContextException;

    public ResolvedViewable resolveViewable(Viewable var1, UriInfo var2) throws TemplateContextException;

    public ResolvedViewable resolveViewable(Viewable var1, Class<?> var2) throws TemplateContextException;
}

