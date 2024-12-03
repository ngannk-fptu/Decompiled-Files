/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.server.impl.wadl;

import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;

public class WadlApplicationContextInjectionProxy
implements WadlApplicationContext {
    private WadlApplicationContext wadlApplicationContext;

    public void init(WadlFactory wadlFactory) {
        this.wadlApplicationContext = wadlFactory.getWadlApplicationContext();
    }

    @Override
    public ApplicationDescription getApplication(UriInfo ui) {
        return this.getWadlApplicationContext().getApplication(ui);
    }

    @Override
    public Application getApplication(UriInfo info, AbstractResource resource, String path) {
        return this.getWadlApplicationContext().getApplication(info, resource, path);
    }

    @Override
    public JAXBContext getJAXBContext() {
        return this.getWadlApplicationContext().getJAXBContext();
    }

    @Override
    public void setWadlGenerationEnabled(boolean wadlGenerationEnabled) {
        this.getWadlApplicationContext().setWadlGenerationEnabled(wadlGenerationEnabled);
    }

    @Override
    public boolean isWadlGenerationEnabled() {
        return this.getWadlApplicationContext().isWadlGenerationEnabled();
    }

    private WadlApplicationContext getWadlApplicationContext() {
        if (this.wadlApplicationContext == null) {
            throw new IllegalStateException("WadlApplicationContext is not yet initialized.");
        }
        return this.wadlApplicationContext;
    }
}

