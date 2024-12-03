/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api;

import com.sun.jersey.api.JResponseAsResponse;
import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.spi.factory.ResponseImpl;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

public class JResponse<E> {
    private final Response.StatusType statusType;
    private final E entity;
    private final OutBoundHeaders headers;

    public JResponse(Response.StatusType statusType, OutBoundHeaders headers, E entity) {
        this.statusType = statusType;
        this.entity = entity;
        this.headers = headers;
    }

    public JResponse(int status, OutBoundHeaders headers, E entity) {
        this(ResponseImpl.toStatusType(status), headers, entity);
    }

    public JResponse(JResponse<E> that) {
        this(that.statusType, that.headers != null ? new OutBoundHeaders(that.headers) : null, that.entity);
    }

    protected JResponse(AJResponseBuilder<E, ?> b) {
        this.statusType = b.getStatusType();
        this.entity = b.getEntity();
        this.headers = b.getMetadata();
    }

    public JResponseAsResponse toResponse() {
        return new JResponseAsResponse(this);
    }

    public JResponseAsResponse toResponse(Type type) {
        return new JResponseAsResponse(this, type);
    }

    public Response.StatusType getStatusType() {
        return this.statusType;
    }

    public int getStatus() {
        return this.statusType.getStatusCode();
    }

    public OutBoundHeaders getMetadata() {
        return this.headers;
    }

    public E getEntity() {
        return this.entity;
    }

    public Type getType() {
        return JResponse.getSuperclassTypeParameter(this.getClass());
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            return Object.class;
        }
        ParameterizedType parameterized = (ParameterizedType)superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public static <E> JResponseBuilder<E> fromResponse(Response response) {
        JResponseBuilder<Object> b = JResponse.status(response.getStatus());
        b.entity(response.getEntity());
        for (String headerName : response.getMetadata().keySet()) {
            List headerValues = (List)response.getMetadata().get(headerName);
            for (Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }

    public static <E> JResponseBuilder<E> fromResponse(JResponse<E> response) {
        JResponseBuilder<E> b = JResponse.status(response.getStatus());
        b.entity(response.getEntity());
        for (String headerName : response.getMetadata().keySet()) {
            List headerValues = (List)response.getMetadata().get(headerName);
            for (Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }

    public static <E> JResponseBuilder<E> status(Response.StatusType status) {
        JResponseBuilder b = new JResponseBuilder();
        b.status(status);
        return b;
    }

    public static <E> JResponseBuilder<E> status(Response.Status status) {
        return JResponse.status((Response.StatusType)status);
    }

    public static <E> JResponseBuilder<E> status(int status) {
        JResponseBuilder b = new JResponseBuilder();
        b.status(status);
        return b;
    }

    public static <E> JResponseBuilder<E> ok() {
        JResponseBuilder<E> b = JResponse.status(Response.Status.OK);
        return b;
    }

    public static <E> JResponseBuilder<E> ok(E entity) {
        JResponseBuilder<E> b = JResponse.ok();
        b.entity(entity);
        return b;
    }

    public static <E> JResponseBuilder<E> ok(E entity, MediaType type) {
        JResponseBuilder<E> b = JResponse.ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    public static <E> JResponseBuilder<E> ok(E entity, String type) {
        JResponseBuilder<E> b = JResponse.ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    public static <E> JResponseBuilder<E> ok(E entity, Variant variant) {
        JResponseBuilder<E> b = JResponse.ok();
        b.entity(entity);
        b.variant(variant);
        return b;
    }

    public static <E> JResponseBuilder<E> serverError() {
        JResponseBuilder<E> b = JResponse.status(Response.Status.INTERNAL_SERVER_ERROR);
        return b;
    }

    public static <E> JResponseBuilder<E> created(URI location) {
        JResponseBuilder b = (JResponseBuilder)JResponse.status(Response.Status.CREATED).location(location);
        return b;
    }

    public static <E> JResponseBuilder<E> noContent() {
        JResponseBuilder<E> b = JResponse.status(Response.Status.NO_CONTENT);
        return b;
    }

    public static <E> JResponseBuilder<E> notModified() {
        JResponseBuilder<E> b = JResponse.status(Response.Status.NOT_MODIFIED);
        return b;
    }

    public static <E> JResponseBuilder<E> notModified(EntityTag tag) {
        JResponseBuilder<E> b = JResponse.notModified();
        b.tag(tag);
        return b;
    }

    public static <E> JResponseBuilder<E> notModified(String tag) {
        JResponseBuilder<E> b = JResponse.notModified();
        b.tag(tag);
        return b;
    }

    public static <E> JResponseBuilder<E> seeOther(URI location) {
        JResponseBuilder b = (JResponseBuilder)JResponse.status(Response.Status.SEE_OTHER).location(location);
        return b;
    }

    public static <E> JResponseBuilder<E> temporaryRedirect(URI location) {
        JResponseBuilder b = (JResponseBuilder)JResponse.status(Response.Status.TEMPORARY_REDIRECT).location(location);
        return b;
    }

    public static <E> JResponseBuilder<E> notAcceptable(List<Variant> variants) {
        JResponseBuilder b = (JResponseBuilder)JResponse.status(Response.Status.NOT_ACCEPTABLE).variants(variants);
        return b;
    }

    public static abstract class AJResponseBuilder<E, B extends AJResponseBuilder> {
        protected Response.StatusType statusType = Response.Status.NO_CONTENT;
        protected OutBoundHeaders headers;
        protected E entity;

        protected AJResponseBuilder() {
        }

        protected AJResponseBuilder(AJResponseBuilder<E, ?> that) {
            this.statusType = that.statusType;
            this.entity = that.entity;
            this.headers = that.headers != null ? new OutBoundHeaders(that.headers) : null;
        }

        protected void reset() {
            this.statusType = Response.Status.NO_CONTENT;
            this.entity = null;
            this.headers = null;
        }

        protected Response.StatusType getStatusType() {
            return this.statusType;
        }

        protected int getStatus() {
            return this.statusType.getStatusCode();
        }

        protected OutBoundHeaders getMetadata() {
            if (this.headers == null) {
                this.headers = new OutBoundHeaders();
            }
            return this.headers;
        }

        protected E getEntity() {
            return this.entity;
        }

        public B status(int status) {
            return this.status(ResponseImpl.toStatusType(status));
        }

        public B status(Response.StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException();
            }
            this.statusType = status;
            return (B)this;
        }

        public B status(Response.Status status) {
            return this.status((Response.StatusType)status);
        }

        public B entity(E entity) {
            this.entity = entity;
            return (B)this;
        }

        public B type(MediaType type) {
            this.headerSingle("Content-Type", type);
            return (B)this;
        }

        public B type(String type) {
            return this.type(type == null ? null : MediaType.valueOf(type));
        }

        public B variant(Variant variant) {
            if (variant == null) {
                this.type((MediaType)null);
                this.language((String)null);
                this.encoding(null);
                return (B)this;
            }
            this.type(variant.getMediaType());
            this.language(variant.getLanguage());
            this.encoding(variant.getEncoding());
            return (B)this;
        }

        public B variants(List<Variant> variants) {
            if (variants == null) {
                this.header("Vary", null);
                return (B)this;
            }
            if (variants.isEmpty()) {
                return (B)this;
            }
            MediaType accept = variants.get(0).getMediaType();
            boolean vAccept = false;
            Locale acceptLanguage = variants.get(0).getLanguage();
            boolean vAcceptLanguage = false;
            String acceptEncoding = variants.get(0).getEncoding();
            boolean vAcceptEncoding = false;
            for (Variant v : variants) {
                vAccept |= !vAccept && this.vary(v.getMediaType(), accept);
                vAcceptLanguage |= !vAcceptLanguage && this.vary(v.getLanguage(), acceptLanguage);
                vAcceptEncoding |= !vAcceptEncoding && this.vary(v.getEncoding(), acceptEncoding);
            }
            StringBuilder vary = new StringBuilder();
            this.append(vary, vAccept, "Accept");
            this.append(vary, vAcceptLanguage, "Accept-Language");
            this.append(vary, vAcceptEncoding, "Accept-Encoding");
            if (vary.length() > 0) {
                this.header("Vary", vary.toString());
            }
            return (B)this;
        }

        private boolean vary(MediaType v, MediaType vary) {
            return v != null && !v.equals(vary);
        }

        private boolean vary(Locale v, Locale vary) {
            return v != null && !v.equals(vary);
        }

        private boolean vary(String v, String vary) {
            return v != null && !v.equalsIgnoreCase(vary);
        }

        private void append(StringBuilder sb, boolean v, String s) {
            if (v) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
        }

        public B language(String language) {
            this.headerSingle("Content-Language", language);
            return (B)this;
        }

        public B language(Locale language) {
            this.headerSingle("Content-Language", language);
            return (B)this;
        }

        public B location(URI location) {
            this.headerSingle("Location", location);
            return (B)this;
        }

        public B contentLocation(URI location) {
            this.headerSingle("Content-Location", location);
            return (B)this;
        }

        public B encoding(String encoding) {
            this.headerSingle("Content-Encoding", encoding);
            return (B)this;
        }

        public B tag(EntityTag tag) {
            this.headerSingle("ETag", tag);
            return (B)this;
        }

        public B tag(String tag) {
            return this.tag(tag == null ? null : new EntityTag(tag));
        }

        public B lastModified(Date lastModified) {
            this.headerSingle("Last-Modified", lastModified);
            return (B)this;
        }

        public B cacheControl(CacheControl cacheControl) {
            this.headerSingle("Cache-Control", cacheControl);
            return (B)this;
        }

        public B expires(Date expires) {
            this.headerSingle("Expires", expires);
            return (B)this;
        }

        public B cookie(NewCookie ... cookies) {
            if (cookies != null) {
                for (NewCookie cookie : cookies) {
                    this.header("Set-Cookie", cookie);
                }
            } else {
                this.header("Set-Cookie", null);
            }
            return (B)this;
        }

        public B header(String name, Object value) {
            return this.header(name, value, false);
        }

        public B headerSingle(String name, Object value) {
            return this.header(name, value, true);
        }

        public B header(String name, Object value, boolean single) {
            if (value != null) {
                if (single) {
                    this.getMetadata().putSingle(name, value);
                } else {
                    this.getMetadata().add(name, value);
                }
            } else {
                this.getMetadata().remove(name);
            }
            return (B)this;
        }
    }

    public static final class JResponseBuilder<E>
    extends AJResponseBuilder<E, JResponseBuilder<E>> {
        public JResponseBuilder() {
        }

        public JResponseBuilder(JResponseBuilder<E> that) {
            super(that);
        }

        public JResponseBuilder<E> clone() {
            return new JResponseBuilder<E>(this);
        }

        public JResponse<E> build() {
            JResponse r = new JResponse(this);
            this.reset();
            return r;
        }
    }
}

