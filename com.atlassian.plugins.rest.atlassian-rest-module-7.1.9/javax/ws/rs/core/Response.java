/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Response {
    protected Response() {
    }

    public abstract Object getEntity();

    public abstract int getStatus();

    public abstract MultivaluedMap<String, Object> getMetadata();

    public static ResponseBuilder fromResponse(Response response) {
        ResponseBuilder b = Response.status(response.getStatus());
        b.entity(response.getEntity());
        for (String headerName : response.getMetadata().keySet()) {
            List headerValues = (List)response.getMetadata().get(headerName);
            for (Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }

    public static ResponseBuilder status(StatusType status) {
        ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }

    public static ResponseBuilder status(Status status) {
        return Response.status((StatusType)status);
    }

    public static ResponseBuilder status(int status) {
        ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }

    public static ResponseBuilder ok() {
        ResponseBuilder b = Response.status(Status.OK);
        return b;
    }

    public static ResponseBuilder ok(Object entity) {
        ResponseBuilder b = Response.ok();
        b.entity(entity);
        return b;
    }

    public static ResponseBuilder ok(Object entity, MediaType type) {
        ResponseBuilder b = Response.ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    public static ResponseBuilder ok(Object entity, String type) {
        ResponseBuilder b = Response.ok();
        b.entity(entity);
        b.type(type);
        return b;
    }

    public static ResponseBuilder ok(Object entity, Variant variant) {
        ResponseBuilder b = Response.ok();
        b.entity(entity);
        b.variant(variant);
        return b;
    }

    public static ResponseBuilder serverError() {
        ResponseBuilder b = Response.status(Status.INTERNAL_SERVER_ERROR);
        return b;
    }

    public static ResponseBuilder created(URI location) {
        ResponseBuilder b = Response.status(Status.CREATED).location(location);
        return b;
    }

    public static ResponseBuilder noContent() {
        ResponseBuilder b = Response.status(Status.NO_CONTENT);
        return b;
    }

    public static ResponseBuilder notModified() {
        ResponseBuilder b = Response.status(Status.NOT_MODIFIED);
        return b;
    }

    public static ResponseBuilder notModified(EntityTag tag) {
        ResponseBuilder b = Response.notModified();
        b.tag(tag);
        return b;
    }

    public static ResponseBuilder notModified(String tag) {
        ResponseBuilder b = Response.notModified();
        b.tag(tag);
        return b;
    }

    public static ResponseBuilder seeOther(URI location) {
        ResponseBuilder b = Response.status(Status.SEE_OTHER).location(location);
        return b;
    }

    public static ResponseBuilder temporaryRedirect(URI location) {
        ResponseBuilder b = Response.status(Status.TEMPORARY_REDIRECT).location(location);
        return b;
    }

    public static ResponseBuilder notAcceptable(List<Variant> variants) {
        ResponseBuilder b = Response.status(Status.NOT_ACCEPTABLE).variants(variants);
        return b;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Status implements StatusType
    {
        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NO_CONTENT(204, "No Content"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable");

        private final int code;
        private final String reason;
        private Family family;

        private Status(int statusCode, String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            switch (this.code / 100) {
                case 1: {
                    this.family = Family.INFORMATIONAL;
                    break;
                }
                case 2: {
                    this.family = Family.SUCCESSFUL;
                    break;
                }
                case 3: {
                    this.family = Family.REDIRECTION;
                    break;
                }
                case 4: {
                    this.family = Family.CLIENT_ERROR;
                    break;
                }
                case 5: {
                    this.family = Family.SERVER_ERROR;
                    break;
                }
                default: {
                    this.family = Family.OTHER;
                }
            }
        }

        @Override
        public Family getFamily() {
            return this.family;
        }

        @Override
        public int getStatusCode() {
            return this.code;
        }

        @Override
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

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Family {
            INFORMATIONAL,
            SUCCESSFUL,
            REDIRECTION,
            CLIENT_ERROR,
            SERVER_ERROR,
            OTHER;

        }
    }

    public static interface StatusType {
        public int getStatusCode();

        public Status.Family getFamily();

        public String getReasonPhrase();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class ResponseBuilder {
        protected ResponseBuilder() {
        }

        protected static ResponseBuilder newInstance() {
            ResponseBuilder b = RuntimeDelegate.getInstance().createResponseBuilder();
            return b;
        }

        public abstract Response build();

        public abstract ResponseBuilder clone();

        public abstract ResponseBuilder status(int var1);

        public ResponseBuilder status(StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException();
            }
            return this.status(status.getStatusCode());
        }

        public ResponseBuilder status(Status status) {
            return this.status((StatusType)status);
        }

        public abstract ResponseBuilder entity(Object var1);

        public abstract ResponseBuilder type(MediaType var1);

        public abstract ResponseBuilder type(String var1);

        public abstract ResponseBuilder variant(Variant var1);

        public abstract ResponseBuilder variants(List<Variant> var1);

        public abstract ResponseBuilder language(String var1);

        public abstract ResponseBuilder language(Locale var1);

        public abstract ResponseBuilder location(URI var1);

        public abstract ResponseBuilder contentLocation(URI var1);

        public abstract ResponseBuilder tag(EntityTag var1);

        public abstract ResponseBuilder tag(String var1);

        public abstract ResponseBuilder lastModified(Date var1);

        public abstract ResponseBuilder cacheControl(CacheControl var1);

        public abstract ResponseBuilder expires(Date var1);

        public abstract ResponseBuilder header(String var1, Object var2);

        public abstract ResponseBuilder cookie(NewCookie ... var1);
    }
}

