/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.context.ExternalContext
 *  javax.faces.context.FacesContext
 */
package org.springframework.web.jsf;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

public abstract class FacesContextUtils {
    @Nullable
    public static WebApplicationContext getWebApplicationContext(FacesContext fc) {
        Assert.notNull((Object)fc, "FacesContext must not be null");
        Object attr = fc.getExternalContext().getApplicationMap().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException)attr;
        }
        if (attr instanceof Error) {
            throw (Error)attr;
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Root context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext)attr;
    }

    public static WebApplicationContext getRequiredWebApplicationContext(FacesContext fc) throws IllegalStateException {
        WebApplicationContext wac = FacesContextUtils.getWebApplicationContext(fc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }

    @Nullable
    public static Object getSessionMutex(FacesContext fc) {
        Assert.notNull((Object)fc, "FacesContext must not be null");
        ExternalContext ec = fc.getExternalContext();
        Object mutex = ec.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = ec.getSession(true);
        }
        return mutex;
    }
}

