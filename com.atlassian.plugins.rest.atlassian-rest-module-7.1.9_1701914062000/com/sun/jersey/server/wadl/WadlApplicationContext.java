/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.server.wadl;

import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;

public interface WadlApplicationContext {
    public ApplicationDescription getApplication(UriInfo var1);

    public Application getApplication(UriInfo var1, AbstractResource var2, String var3);

    public JAXBContext getJAXBContext();

    public void setWadlGenerationEnabled(boolean var1);

    public boolean isWadlGenerationEnabled();
}

