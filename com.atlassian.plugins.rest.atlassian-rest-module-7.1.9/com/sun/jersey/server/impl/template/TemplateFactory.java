/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.template;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.server.impl.template.TemplateViewProcessor;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContext;
import com.sun.jersey.spi.template.TemplateContextException;
import com.sun.jersey.spi.template.TemplateProcessor;
import com.sun.jersey.spi.template.ViewProcessor;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.UriInfo;

public final class TemplateFactory
implements TemplateContext {
    private final Set<ViewProcessor> viewProcessors;

    public TemplateFactory(ProviderServices providerServices) {
        this.viewProcessors = providerServices.getProvidersAndServices(ViewProcessor.class);
        Set<TemplateProcessor> templateProcessors = providerServices.getProvidersAndServices(TemplateProcessor.class);
        for (TemplateProcessor tp : templateProcessors) {
            this.viewProcessors.add(new TemplateViewProcessor(tp));
        }
    }

    private Set<ViewProcessor> getViewProcessors() {
        return this.viewProcessors;
    }

    @Override
    public ResolvedViewable resolveViewable(Viewable v) {
        if (v.isTemplateNameAbsolute()) {
            return this.resolveAbsoluteViewable(v);
        }
        if (v.getResolvingClass() != null) {
            return this.resolveRelativeViewable(v, v.getResolvingClass());
        }
        if (v.getModel() == null) {
            throw new TemplateContextException("The model of the view MUST not be null");
        }
        return this.resolveRelativeViewable(v, v.getModel().getClass());
    }

    @Override
    public ResolvedViewable resolveViewable(Viewable v, UriInfo ui) {
        if (v.isTemplateNameAbsolute()) {
            return this.resolveAbsoluteViewable(v);
        }
        if (v.getResolvingClass() != null) {
            return this.resolveRelativeViewable(v, v.getResolvingClass());
        }
        List<Object> mrs = ui.getMatchedResources();
        if (mrs == null || mrs.size() == 0) {
            throw new TemplateContextException("There is no last matching resource available");
        }
        return this.resolveRelativeViewable(v, mrs.get(0).getClass());
    }

    @Override
    public ResolvedViewable resolveViewable(Viewable v, Class<?> resolvingClass) {
        if (v.isTemplateNameAbsolute()) {
            return this.resolveAbsoluteViewable(v);
        }
        if (v.getResolvingClass() != null) {
            return this.resolveRelativeViewable(v, v.getResolvingClass());
        }
        if (resolvingClass == null) {
            throw new TemplateContextException("Resolving class MUST not be null");
        }
        return this.resolveRelativeViewable(v, resolvingClass);
    }

    private ResolvedViewable resolveAbsoluteViewable(Viewable v) {
        for (ViewProcessor vp : this.getViewProcessors()) {
            Object resolvedTemplateObject = vp.resolve(v.getTemplateName());
            if (resolvedTemplateObject == null) continue;
            return new ResolvedViewable(vp, resolvedTemplateObject, v);
        }
        return null;
    }

    private ResolvedViewable resolveRelativeViewable(Viewable v, Class<?> resolvingClass) {
        Object resolvedTemplateObject;
        String absolutePath;
        Class<?> c;
        String path = v.getTemplateName();
        if (path == null || path.length() == 0) {
            path = "index";
        }
        for (c = resolvingClass; c != Object.class; c = c.getSuperclass()) {
            absolutePath = this.getAbsolutePath(c, path, '/');
            for (ViewProcessor vp : this.getViewProcessors()) {
                resolvedTemplateObject = vp.resolve(absolutePath);
                if (resolvedTemplateObject == null) continue;
                return new ResolvedViewable(vp, resolvedTemplateObject, v, c);
            }
        }
        for (c = resolvingClass; c != Object.class; c = c.getSuperclass()) {
            absolutePath = this.getAbsolutePath(c, path, '.');
            for (ViewProcessor vp : this.getViewProcessors()) {
                resolvedTemplateObject = vp.resolve(absolutePath);
                if (resolvedTemplateObject == null) continue;
                return new ResolvedViewable(vp, resolvedTemplateObject, v, c);
            }
        }
        return null;
    }

    private String getAbsolutePath(Class<?> resourceClass, String path, char delim) {
        return '/' + resourceClass.getName().replace('.', '/').replace('$', delim) + delim + path;
    }
}

