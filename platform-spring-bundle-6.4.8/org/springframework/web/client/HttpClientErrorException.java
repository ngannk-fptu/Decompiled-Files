/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;

public class HttpClientErrorException
extends HttpStatusCodeException {
    private static final long serialVersionUID = 5177019431887513952L;

    public HttpClientErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText, @Nullable byte[] body2, @Nullable Charset responseCharset) {
        super(statusCode, statusText, body2, responseCharset);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body2, @Nullable Charset responseCharset) {
        super(statusCode, statusText, headers, body2, responseCharset);
    }

    public HttpClientErrorException(String message, HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body2, @Nullable Charset responseCharset) {
        super(message, statusCode, statusText, headers, body2, responseCharset);
    }

    public static HttpClientErrorException create(HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
        return HttpClientErrorException.create(null, statusCode, statusText, headers, body2, charset);
    }

    public static HttpClientErrorException create(@Nullable String message, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
        switch (statusCode) {
            case BAD_REQUEST: {
                return message != null ? new BadRequest(message, statusText, headers, body2, charset) : new BadRequest(statusText, headers, body2, charset);
            }
            case UNAUTHORIZED: {
                return message != null ? new Unauthorized(message, statusText, headers, body2, charset) : new Unauthorized(statusText, headers, body2, charset);
            }
            case FORBIDDEN: {
                return message != null ? new Forbidden(message, statusText, headers, body2, charset) : new Forbidden(statusText, headers, body2, charset);
            }
            case NOT_FOUND: {
                return message != null ? new NotFound(message, statusText, headers, body2, charset) : new NotFound(statusText, headers, body2, charset);
            }
            case METHOD_NOT_ALLOWED: {
                return message != null ? new MethodNotAllowed(message, statusText, headers, body2, charset) : new MethodNotAllowed(statusText, headers, body2, charset);
            }
            case NOT_ACCEPTABLE: {
                return message != null ? new NotAcceptable(message, statusText, headers, body2, charset) : new NotAcceptable(statusText, headers, body2, charset);
            }
            case CONFLICT: {
                return message != null ? new Conflict(message, statusText, headers, body2, charset) : new Conflict(statusText, headers, body2, charset);
            }
            case GONE: {
                return message != null ? new Gone(message, statusText, headers, body2, charset) : new Gone(statusText, headers, body2, charset);
            }
            case UNSUPPORTED_MEDIA_TYPE: {
                return message != null ? new UnsupportedMediaType(message, statusText, headers, body2, charset) : new UnsupportedMediaType(statusText, headers, body2, charset);
            }
            case TOO_MANY_REQUESTS: {
                return message != null ? new TooManyRequests(message, statusText, headers, body2, charset) : new TooManyRequests(statusText, headers, body2, charset);
            }
            case UNPROCESSABLE_ENTITY: {
                return message != null ? new UnprocessableEntity(message, statusText, headers, body2, charset) : new UnprocessableEntity(statusText, headers, body2, charset);
            }
        }
        return message != null ? new HttpClientErrorException(message, statusCode, statusText, headers, body2, charset) : new HttpClientErrorException(statusCode, statusText, headers, body2, charset);
    }

    public static final class TooManyRequests
    extends HttpClientErrorException {
        private TooManyRequests(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.TOO_MANY_REQUESTS, statusText, headers, body2, charset);
        }

        private TooManyRequests(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.TOO_MANY_REQUESTS, statusText, headers, body2, charset);
        }
    }

    public static final class UnprocessableEntity
    extends HttpClientErrorException {
        private UnprocessableEntity(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.UNPROCESSABLE_ENTITY, statusText, headers, body2, charset);
        }

        private UnprocessableEntity(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.UNPROCESSABLE_ENTITY, statusText, headers, body2, charset);
        }
    }

    public static final class UnsupportedMediaType
    extends HttpClientErrorException {
        private UnsupportedMediaType(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusText, headers, body2, charset);
        }

        private UnsupportedMediaType(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusText, headers, body2, charset);
        }
    }

    public static final class Gone
    extends HttpClientErrorException {
        private Gone(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.GONE, statusText, headers, body2, charset);
        }

        private Gone(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.GONE, statusText, headers, body2, charset);
        }
    }

    public static final class Conflict
    extends HttpClientErrorException {
        private Conflict(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.CONFLICT, statusText, headers, body2, charset);
        }

        private Conflict(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.CONFLICT, statusText, headers, body2, charset);
        }
    }

    public static final class NotAcceptable
    extends HttpClientErrorException {
        private NotAcceptable(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.NOT_ACCEPTABLE, statusText, headers, body2, charset);
        }

        private NotAcceptable(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.NOT_ACCEPTABLE, statusText, headers, body2, charset);
        }
    }

    public static final class MethodNotAllowed
    extends HttpClientErrorException {
        private MethodNotAllowed(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.METHOD_NOT_ALLOWED, statusText, headers, body2, charset);
        }

        private MethodNotAllowed(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.METHOD_NOT_ALLOWED, statusText, headers, body2, charset);
        }
    }

    public static final class NotFound
    extends HttpClientErrorException {
        private NotFound(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.NOT_FOUND, statusText, headers, body2, charset);
        }

        private NotFound(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.NOT_FOUND, statusText, headers, body2, charset);
        }
    }

    public static final class Forbidden
    extends HttpClientErrorException {
        private Forbidden(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.FORBIDDEN, statusText, headers, body2, charset);
        }

        private Forbidden(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.FORBIDDEN, statusText, headers, body2, charset);
        }
    }

    public static final class Unauthorized
    extends HttpClientErrorException {
        private Unauthorized(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.UNAUTHORIZED, statusText, headers, body2, charset);
        }

        private Unauthorized(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.UNAUTHORIZED, statusText, headers, body2, charset);
        }
    }

    public static final class BadRequest
    extends HttpClientErrorException {
        private BadRequest(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.BAD_REQUEST, statusText, headers, body2, charset);
        }

        private BadRequest(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.BAD_REQUEST, statusText, headers, body2, charset);
        }
    }
}

