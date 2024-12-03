/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.context.FacesContext
 */
package org.springframework.web.context.request;

import javax.faces.context.FacesContext;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.FacesRequestAttributes;
import org.springframework.web.context.request.RequestAttributes;

public abstract class RequestContextHolder {
    private static final boolean jsfPresent = ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());
    private static final ThreadLocal<RequestAttributes> requestAttributesHolder = new NamedThreadLocal<RequestAttributes>("Request attributes");
    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder = new NamedInheritableThreadLocal<RequestAttributes>("Request context");

    public static void resetRequestAttributes() {
        requestAttributesHolder.remove();
        inheritableRequestAttributesHolder.remove();
    }

    public static void setRequestAttributes(@Nullable RequestAttributes attributes) {
        RequestContextHolder.setRequestAttributes(attributes, false);
    }

    public static void setRequestAttributes(@Nullable RequestAttributes attributes, boolean inheritable) {
        if (attributes == null) {
            RequestContextHolder.resetRequestAttributes();
        } else if (inheritable) {
            inheritableRequestAttributesHolder.set(attributes);
            requestAttributesHolder.remove();
        } else {
            requestAttributesHolder.set(attributes);
            inheritableRequestAttributesHolder.remove();
        }
    }

    @Nullable
    public static RequestAttributes getRequestAttributes() {
        RequestAttributes attributes = requestAttributesHolder.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributesHolder.get();
        }
        return attributes;
    }

    public static RequestAttributes currentRequestAttributes() throws IllegalStateException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            if (jsfPresent) {
                attributes = FacesRequestAttributesFactory.getFacesRequestAttributes();
            }
            if (attributes == null) {
                throw new IllegalStateException("No thread-bound request found: Are you referring to request attributes outside of an actual web request, or processing a request outside of the originally receiving thread? If you are actually operating within a web request and still receive this message, your code is probably running outside of DispatcherServlet/DispatcherPortlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.");
            }
        }
        return attributes;
    }

    private static class FacesRequestAttributesFactory {
        private FacesRequestAttributesFactory() {
        }

        @Nullable
        public static RequestAttributes getFacesRequestAttributes() {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            return facesContext != null ? new FacesRequestAttributes(facesContext) : null;
        }
    }
}

