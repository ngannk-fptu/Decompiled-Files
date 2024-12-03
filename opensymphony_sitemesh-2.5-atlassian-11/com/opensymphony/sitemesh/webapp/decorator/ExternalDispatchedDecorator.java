/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.webapp.decorator.DispatchedDecorator;
import javax.servlet.ServletContext;

public class ExternalDispatchedDecorator
extends DispatchedDecorator {
    private final String webApp;

    public ExternalDispatchedDecorator(String path, String webApp) {
        super(path);
        this.webApp = webApp;
    }

    protected ServletContext locateWebApp(ServletContext context) {
        ServletContext externalContext = context.getContext(this.webApp);
        if (externalContext != null) {
            return externalContext;
        }
        throw new SecurityException("Cannot obtain ServletContext for web-app : " + this.webApp);
    }
}

