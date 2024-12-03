/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.header.InBoundHeaders
 *  com.sun.jersey.core.provider.CompletableReader
 *  com.sun.jersey.core.util.ReaderWriter
 *  com.sun.jersey.spi.MessageBodyWorkers
 *  javax.ws.rs.core.EntityTag
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.NewCookie
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$Status$Family
 *  javax.ws.rs.core.Response$StatusType
 *  javax.ws.rs.ext.MessageBodyReader
 *  javax.ws.rs.ext.RuntimeDelegate
 *  javax.ws.rs.ext.RuntimeDelegate$HeaderDelegate
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.Statuses;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResourceLinkHeaders;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.provider.CompletableReader;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.RuntimeDelegate;

public class ClientResponse {
    private static final Logger LOGGER = Logger.getLogger(ClientResponse.class.getName());
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
    protected static final RuntimeDelegate.HeaderDelegate<EntityTag> entityTagDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);
    protected static final RuntimeDelegate.HeaderDelegate<Date> dateDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);
    private Map<String, Object> properties;
    private Response.StatusType statusType;
    private InBoundHeaders headers;
    private boolean isEntityBuffered;
    private InputStream entity;
    private MessageBodyWorkers workers;

    public ClientResponse(Response.StatusType statusType, InBoundHeaders headers, InputStream entity, MessageBodyWorkers workers) {
        this.statusType = statusType;
        this.headers = headers;
        this.entity = entity;
        this.workers = workers;
    }

    public ClientResponse(int statusCode, InBoundHeaders headers, InputStream entity, MessageBodyWorkers workers) {
        this(Statuses.from(statusCode), headers, entity, workers);
    }

    public Client getClient() {
        return (Client)this.getProperties().get(Client.class.getName());
    }

    public Map<String, Object> getProperties() {
        if (this.properties != null) {
            return this.properties;
        }
        this.properties = new HashMap<String, Object>();
        return this.properties;
    }

    public int getStatus() {
        return this.statusType.getStatusCode();
    }

    public void setStatus(int status) {
        this.statusType = Statuses.from(status);
    }

    public void setStatus(Response.StatusType statusType) {
        this.statusType = statusType;
    }

    @Deprecated
    public Status getClientResponseStatus() {
        return Status.fromStatusCode(this.statusType.getStatusCode());
    }

    public Response.StatusType getStatusInfo() {
        return this.statusType;
    }

    @Deprecated
    public Response.Status getResponseStatus() {
        return Response.Status.fromStatusCode((int)this.statusType.getStatusCode());
    }

    @Deprecated
    public void setResponseStatus(Response.StatusType status) {
        this.setStatus(status);
    }

    @Deprecated
    public MultivaluedMap<String, String> getMetadata() {
        return this.getHeaders();
    }

    public MultivaluedMap<String, String> getHeaders() {
        return this.headers;
    }

    public boolean hasEntity() {
        try {
            try {
                if (this.entity.available() > 0) {
                    return true;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (this.entity.markSupported()) {
                this.entity.mark(1);
                int i = this.entity.read();
                if (i == -1) {
                    return false;
                }
                this.entity.reset();
                return true;
            }
            int b = this.entity.read();
            if (b == -1) {
                return false;
            }
            if (!(this.entity instanceof PushbackInputStream)) {
                this.entity = new PushbackInputStream(this.entity, 1);
            }
            ((PushbackInputStream)this.entity).unread(b);
            return true;
        }
        catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
    }

    public InputStream getEntityInputStream() {
        return this.entity;
    }

    public void setEntityInputStream(InputStream entity) {
        this.isEntityBuffered = false;
        this.entity = entity;
    }

    public <T> T getEntity(Class<T> c) throws ClientHandlerException, UniformInterfaceException {
        return this.getEntity(c, c);
    }

    public <T> T getEntity(GenericType<T> gt) throws ClientHandlerException, UniformInterfaceException {
        return this.getEntity(gt.getRawClass(), gt.getType());
    }

    private <T> T getEntity(Class<T> c, Type type) {
        MessageBodyReader br;
        if (this.getStatus() == 204) {
            throw new UniformInterfaceException(this);
        }
        MediaType mediaType = this.getType();
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        if ((br = this.workers.getMessageBodyReader(c, type, EMPTY_ANNOTATIONS, mediaType)) == null) {
            this.close();
            String message = "A message body reader for Java class " + c.getName() + ", and Java type " + type + ", and MIME media type " + mediaType + " was not found";
            LOGGER.severe(message);
            Map m = this.workers.getReaders(mediaType);
            LOGGER.severe("The registered message body readers compatible with the MIME media type are:\n" + this.workers.readersToString(m));
            throw new ClientHandlerException(message);
        }
        try {
            Object t = br.readFrom(c, type, EMPTY_ANNOTATIONS, mediaType, (MultivaluedMap)this.headers, this.entity);
            if (br instanceof CompletableReader) {
                t = ((CompletableReader)br).complete(t);
            }
            if (!(t instanceof Closeable)) {
                this.close();
            }
            return (T)t;
        }
        catch (IOException ex) {
            this.close();
            throw new ClientHandlerException(ex);
        }
    }

    public void bufferEntity() throws ClientHandlerException {
        if (this.isEntityBuffered) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ReaderWriter.writeTo((InputStream)this.entity, (OutputStream)baos);
        }
        catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
        finally {
            this.close();
        }
        this.entity = new ByteArrayInputStream(baos.toByteArray());
        this.isEntityBuffered = true;
    }

    public void close() throws ClientHandlerException {
        try {
            this.entity.close();
        }
        catch (IOException e) {
            throw new ClientHandlerException(e);
        }
    }

    public MediaType getType() {
        String ct = (String)this.getHeaders().getFirst((Object)"Content-Type");
        return ct != null ? MediaType.valueOf((String)ct) : null;
    }

    public URI getLocation() {
        String l = (String)this.getHeaders().getFirst((Object)"Location");
        return l != null ? URI.create(l) : null;
    }

    public EntityTag getEntityTag() {
        String t = (String)this.getHeaders().getFirst((Object)"ETag");
        return t != null ? (EntityTag)entityTagDelegate.fromString(t) : null;
    }

    public Date getLastModified() {
        String d = (String)this.getHeaders().getFirst((Object)"Last-Modified");
        return d != null ? (Date)dateDelegate.fromString(d) : null;
    }

    public Date getResponseDate() {
        String d = (String)this.getHeaders().getFirst((Object)"Date");
        return d != null ? (Date)dateDelegate.fromString(d) : null;
    }

    public String getLanguage() {
        return (String)this.getHeaders().getFirst((Object)"Content-Language");
    }

    public int getLength() {
        int size = -1;
        String sizeStr = (String)this.getHeaders().getFirst((Object)"Content-Length");
        if (sizeStr == null) {
            return -1;
        }
        try {
            size = Integer.parseInt(sizeStr);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return size;
    }

    public List<NewCookie> getCookies() {
        List hs = (List)this.getHeaders().get((Object)"Set-Cookie");
        if (hs == null) {
            return Collections.emptyList();
        }
        ArrayList<NewCookie> cs = new ArrayList<NewCookie>();
        for (String h : hs) {
            cs.add(NewCookie.valueOf((String)h));
        }
        return cs;
    }

    public Set<String> getAllow() {
        String allow = (String)this.headers.getFirst("Allow");
        if (allow == null) {
            return Collections.emptySet();
        }
        HashSet<String> allowedMethods = new HashSet<String>();
        StringTokenizer tokenizer = new StringTokenizer(allow, ",");
        while (tokenizer.hasMoreTokens()) {
            String m = tokenizer.nextToken().trim();
            if (m.length() <= 0) continue;
            allowedMethods.add(m.toUpperCase());
        }
        return allowedMethods;
    }

    public WebResourceLinkHeaders getLinks() {
        return new WebResourceLinkHeaders(this.getClient(), this.getHeaders());
    }

    public String toString() {
        return "Client response status: " + this.statusType.getStatusCode();
    }

    public static enum Status implements Response.StatusType
    {
        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NON_AUTHORITIVE_INFORMATION(203, "Non-Authoritative Information"),
        NO_CONTENT(204, "No Content"),
        RESET_CONTENT(205, "Reset Content"),
        PARTIAL_CONTENT(206, "Partial Content"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        FOUND(302, "Found"),
        SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        USE_PROXY(305, "Use Proxy"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        PAYMENT_REQUIRED(402, "Payment Required"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        REQUESTED_RANGE_NOT_SATIFIABLE(416, "Requested Range Not Satisfiable"),
        EXPECTATION_FAILED(417, "Expectation Failed"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        BAD_GATEWAY(502, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

        private final int code;
        private final String reason;
        private Response.Status.Family family;

        public static Response.Status.Family getFamilyByStatusCode(int statusCode) {
            switch (statusCode / 100) {
                case 1: {
                    return Response.Status.Family.INFORMATIONAL;
                }
                case 2: {
                    return Response.Status.Family.SUCCESSFUL;
                }
                case 3: {
                    return Response.Status.Family.REDIRECTION;
                }
                case 4: {
                    return Response.Status.Family.CLIENT_ERROR;
                }
                case 5: {
                    return Response.Status.Family.SERVER_ERROR;
                }
            }
            return Response.Status.Family.OTHER;
        }

        private Status(int statusCode, String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            this.family = Status.getFamilyByStatusCode(this.code);
        }

        public Response.Status.Family getFamily() {
            return this.family;
        }

        public int getStatusCode() {
            return this.code;
        }

        public String getReasonPhrase() {
            return this.toString();
        }

        public String toString() {
            return this.reason;
        }

        public static Status fromStatusCode(int statusCode) {
            for (Status s : Status.values()) {
                if (s.code != statusCode) continue;
                return s;
            }
            return null;
        }
    }
}

