/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyReader
 *  javax.ws.rs.ext.MessageBodyWriter
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.jaxrs.JacksonJsonProvider
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.SerializationConfig$Feature
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.rest.representations.ErrorResponseStatusObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Provider
@Produces(value={"application/vnd.atl.plugins.install.downloading+json", "application/vnd.atl.plugins.install.next-task+json", "application/vnd.atl.plugins.install.complete+json", "application/vnd.atl.plugins.install.installing+json", "application/vnd.atl.plugins.task.install.err+json", "application/vnd.atl.plugins.embeddedlicense.complete+json", "application/vnd.atl.plugins.task.embeddedlicense.err+json", "application/vnd.atl.plugins.embeddedlicense.installing+json", "application/vnd.atl.plugins.task.error+json", "application/vnd.atl.plugins.error+json", "application/vnd.atl.plugins.pending-task+json", "application/vnd.atl.plugins.pending-tasks+json", "application/vnd.atl.plugins.updateall.finding+json", "application/vnd.atl.plugins.updateall.downloading+json", "application/vnd.atl.plugins.updateall.updating+json", "application/vnd.atl.plugins.updateall.complete+json", "application/vnd.atl.plugins.updateall.err+json", "application/vnd.atl.plugins.disableall.finding+json", "application/vnd.atl.plugins.disableall.disabling+json", "application/vnd.atl.plugins.disableall.complete+json", "application/vnd.atl.plugins.disableall.err+json", "application/vnd.atl.plugins.installed+json", "application/vnd.atl.plugins.install.uri+json", "application/vnd.atl.plugins.compatibility+json", "application/vnd.atl.plugins.plugin+json", "application/vnd.atl.plugins.plugin.module+json", "application/vnd.atl.plugins.available.featured+json", "application/vnd.atl.plugins.available.plugin+json", "application/vnd.atl.plugins.available+json", "application/vnd.atl.plugins.updates+json", "application/vnd.atl.plugins.product.updates+json", "application/vnd.atl.plugins.popular+json", "application/vnd.atl.plugins.topgrossing+json", "application/vnd.atl.plugins.trending+json", "application/vnd.atl.plugins.byatlassian+json", "application/vnd.atl.plugins.categories+json", "application/vnd.atl.plugins.banners+json", "application/vnd.atl.plugins.safe.mode.flag+json", "application/vnd.atl.plugins.safemode.error-reenabling-plugin+json", "application/vnd.atl.plugins.safemode.error-reenabling-plugin-module+json", "application/vnd.atl.plugins.changes.requiring.restart+json", "application/vnd.atl.plugins.audit.log.entries+json", "application/vnd.atl.plugins.audit.log.max.entries+json", "application/vnd.atl.plugins.audit.log.purge.after+json", "application/vnd.atl.plugins.osgi.bundles+json", "application/vnd.atl.plugins.osgi.bundle+json", "application/vnd.atl.plugins.osgi.services+json", "application/vnd.atl.plugins.osgi.service+json", "application/vnd.atl.plugins.osgi.package+json", "application/vnd.atl.plugins.osgi.packages+json", "application/vnd.atl.plugins.build.number+json", "application/vnd.atl.plugins.cancellable.blocking+json", "application/vnd.atl.plugins.pac.base.url+json", "application/vnd.atl.plugins.pac.status+json", "application/vnd.atl.plugins.pac.disabled+json", "application/vnd.atl.plugins.pac.details+json", "application/vnd.atl.plugins+json"})
public class JsonProvider
implements MessageBodyReader<Object>,
MessageBodyWriter<Object> {
    private final JacksonJsonProvider provider = new JacksonJsonProvider();

    public JsonProvider(BaseRepresentationFactory representationFactory) {
        this.provider.setMapper((ObjectMapper)new ErrorResponseStatusObjectMapper(representationFactory));
        this.provider.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
        this.provider.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, false);
        this.provider.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
        this.provider.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.provider.getSize(value, type, genericType, annotations, mediaType);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.provider.isWriteable(type, genericType, annotations, mediaType);
    }

    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        this.provider.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.provider.isReadable(type, genericType, annotations, mediaType);
    }

    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        return this.provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}

