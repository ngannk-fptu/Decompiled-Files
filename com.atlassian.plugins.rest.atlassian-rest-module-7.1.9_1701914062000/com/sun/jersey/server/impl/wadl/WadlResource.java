/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.Marshaller
 */
package com.sun.jersey.server.impl.wadl;

import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.research.ws.wadl.Application;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.Marshaller;

@Singleton
public final class WadlResource {
    public static final String HTTPDATEFORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final Logger LOGGER = Logger.getLogger(WadlResource.class.getName());
    private WadlApplicationContext wadlContext;
    private URI lastBaseUri;
    private byte[] cachedWadl;
    private String lastModified;
    private Variant lastVariant;
    private ApplicationDescription applicationDescription;

    public WadlResource(@Context WadlApplicationContext wadlContext) {
        this.wadlContext = wadlContext;
        this.lastModified = new SimpleDateFormat(HTTPDATEFORMAT).format(new Date());
    }

    @Produces(value={"application/vnd.sun.wadl+xml", "application/vnd.sun.wadl+json", "application/xml"})
    @GET
    public synchronized Response getWadl(@Context Request request, @Context UriInfo uriInfo, @Context Providers providers) {
        if (!this.wadlContext.isWadlGenerationEnabled()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Variant> vl = Variant.mediaTypes(MediaTypes.WADL, MediaTypes.WADL_JSON, MediaType.APPLICATION_XML_TYPE).add().build();
        Variant v = request.selectVariant(vl);
        if (v == null) {
            return Response.notAcceptable(vl).build();
        }
        if (this.applicationDescription == null || this.lastBaseUri != null && !this.lastBaseUri.equals(uriInfo.getBaseUri()) && !this.lastVariant.equals(v)) {
            this.lastBaseUri = uriInfo.getBaseUri();
            this.lastModified = new SimpleDateFormat(HTTPDATEFORMAT).format(new Date());
            this.lastVariant = v;
            this.applicationDescription = this.wadlContext.getApplication(uriInfo);
            Application application = this.applicationDescription.getApplication();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (v.getMediaType().equals(MediaTypes.WADL)) {
                try {
                    Marshaller marshaller = this.wadlContext.getJAXBContext().createMarshaller();
                    marshaller.setProperty("jaxb.formatted.output", (Object)true);
                    marshaller.marshal((Object)application, (OutputStream)os);
                    this.cachedWadl = os.toByteArray();
                    os.close();
                }
                catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Could not marshal wadl Application.", e);
                    return Response.serverError().build();
                }
            }
            MessageBodyWriter<Application> messageBodyWriter = providers.getMessageBodyWriter(Application.class, null, new Annotation[0], v.getMediaType());
            if (messageBodyWriter == null) {
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            }
            try {
                messageBodyWriter.writeTo(application, Application.class, null, new Annotation[0], v.getMediaType(), null, os);
                this.cachedWadl = os.toByteArray();
                os.close();
            }
            catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not serialize wadl Application.", e);
                return Response.serverError().build();
            }
        }
        return Response.ok(new ByteArrayInputStream(this.cachedWadl)).header("Last-modified", this.lastModified).build();
    }

    @Produces(value={"*/*"})
    @GET
    @Path(value="{path}")
    public synchronized Response geExternalGramar(@Context UriInfo uriInfo, @PathParam(value="path") String path) {
        if (!this.wadlContext.isWadlGenerationEnabled()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ApplicationDescription applicationDescription = this.wadlContext.getApplication(uriInfo);
        ApplicationDescription.ExternalGrammar externalMetadata = applicationDescription.getExternalGrammar(path);
        if (externalMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().type(externalMetadata.getType()).entity(externalMetadata.getContent()).build();
    }
}

