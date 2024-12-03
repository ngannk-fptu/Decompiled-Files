/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tiles.TilesException
 *  org.apache.tiles.preparer.ViewPreparer
 *  org.apache.tiles.preparer.factory.PreparerFactory
 *  org.apache.tiles.request.Request
 *  org.springframework.web.context.WebApplicationContext
 */
package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.Request;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public abstract class AbstractSpringPreparerFactory
implements PreparerFactory {
    public ViewPreparer getPreparer(String name, Request context) {
        WebApplicationContext webApplicationContext = (WebApplicationContext)context.getContext("request").get(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null && (webApplicationContext = (WebApplicationContext)context.getContext("application").get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)) == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return this.getPreparer(name, webApplicationContext);
    }

    protected abstract ViewPreparer getPreparer(String var1, WebApplicationContext var2) throws TilesException;
}

