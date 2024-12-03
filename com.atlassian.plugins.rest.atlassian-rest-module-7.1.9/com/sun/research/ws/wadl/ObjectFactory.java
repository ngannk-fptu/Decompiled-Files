/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRegistry
 */
package com.sun.research.ws.wadl;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Grammars;
import com.sun.research.ws.wadl.Include;
import com.sun.research.ws.wadl.Link;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Option;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.ResourceType;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public Grammars createGrammars() {
        return new Grammars();
    }

    public Application createApplication() {
        return new Application();
    }

    public Param createParam() {
        return new Param();
    }

    public Include createInclude() {
        return new Include();
    }

    public Request createRequest() {
        return new Request();
    }

    public Response createResponse() {
        return new Response();
    }

    public Resource createResource() {
        return new Resource();
    }

    public Link createLink() {
        return new Link();
    }

    public Resources createResources() {
        return new Resources();
    }

    public Method createMethod() {
        return new Method();
    }

    public Doc createDoc() {
        return new Doc();
    }

    public Option createOption() {
        return new Option();
    }

    public Representation createRepresentation() {
        return new Representation();
    }

    public ResourceType createResourceType() {
        return new ResourceType();
    }
}

