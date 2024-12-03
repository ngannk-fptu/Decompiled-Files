/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.map.ObjectMapper
 */
package org.codehaus.jackson.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

@Provider
@Consumes(value={"application/json", "text/json"})
@Produces(value={"application/json", "text/json"})
public class JacksonJaxbJsonProvider
extends JacksonJsonProvider {
    public static final Annotations[] DEFAULT_ANNOTATIONS = new Annotations[]{Annotations.JACKSON, Annotations.JAXB};

    public JacksonJaxbJsonProvider() {
        this((ObjectMapper)null, DEFAULT_ANNOTATIONS);
    }

    public JacksonJaxbJsonProvider(Annotations ... annotationsToUse) {
        this((ObjectMapper)null, annotationsToUse);
    }

    public JacksonJaxbJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
    }
}

