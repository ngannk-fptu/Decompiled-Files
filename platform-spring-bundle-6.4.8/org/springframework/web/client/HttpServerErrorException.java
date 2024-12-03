/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;

public class HttpServerErrorException
extends HttpStatusCodeException {
    private static final long serialVersionUID = -2915754006618138282L;

    public HttpServerErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public HttpServerErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpServerErrorException(HttpStatus statusCode, String statusText, @Nullable byte[] body2, @Nullable Charset charset) {
        super(statusCode, statusText, body2, charset);
    }

    public HttpServerErrorException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body2, @Nullable Charset charset) {
        super(statusCode, statusText, headers, body2, charset);
    }

    public HttpServerErrorException(String message, HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body2, @Nullable Charset charset) {
        super(message, statusCode, statusText, headers, body2, charset);
    }

    public static HttpServerErrorException create(HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
        return HttpServerErrorException.create(null, statusCode, statusText, headers, body2, charset);
    }

    public static HttpServerErrorException create(@Nullable String message, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
        switch (statusCode) {
            case INTERNAL_SERVER_ERROR: {
                return message != null ? new InternalServerError(message, statusText, headers, body2, charset) : new InternalServerError(statusText, headers, body2, charset);
            }
            case NOT_IMPLEMENTED: {
                return message != null ? new NotImplemented(message, statusText, headers, body2, charset) : new NotImplemented(statusText, headers, body2, charset);
            }
            case BAD_GATEWAY: {
                return message != null ? new BadGateway(message, statusText, headers, body2, charset) : new BadGateway(statusText, headers, body2, charset);
            }
            case SERVICE_UNAVAILABLE: {
                return message != null ? new ServiceUnavailable(message, statusText, headers, body2, charset) : new ServiceUnavailable(statusText, headers, body2, charset);
            }
            case GATEWAY_TIMEOUT: {
                return message != null ? new GatewayTimeout(message, statusText, headers, body2, charset) : new GatewayTimeout(statusText, headers, body2, charset);
            }
        }
        return message != null ? new HttpServerErrorException(message, statusCode, statusText, headers, body2, charset) : new HttpServerErrorException(statusCode, statusText, headers, body2, charset);
    }

    public static final class GatewayTimeout
    extends HttpServerErrorException {
        private GatewayTimeout(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.GATEWAY_TIMEOUT, statusText, headers, body2, charset);
        }

        private GatewayTimeout(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.GATEWAY_TIMEOUT, statusText, headers, body2, charset);
        }
    }

    public static final class ServiceUnavailable
    extends HttpServerErrorException {
        private ServiceUnavailable(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.SERVICE_UNAVAILABLE, statusText, headers, body2, charset);
        }

        private ServiceUnavailable(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.SERVICE_UNAVAILABLE, statusText, headers, body2, charset);
        }
    }

    public static final class BadGateway
    extends HttpServerErrorException {
        private BadGateway(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.BAD_GATEWAY, statusText, headers, body2, charset);
        }

        private BadGateway(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.BAD_GATEWAY, statusText, headers, body2, charset);
        }
    }

    public static final class NotImplemented
    extends HttpServerErrorException {
        private NotImplemented(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.NOT_IMPLEMENTED, statusText, headers, body2, charset);
        }

        private NotImplemented(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.NOT_IMPLEMENTED, statusText, headers, body2, charset);
        }
    }

    public static final class InternalServerError
    extends HttpServerErrorException {
        private InternalServerError(String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, statusText, headers, body2, charset);
        }

        private InternalServerError(String message, String statusText, HttpHeaders headers, byte[] body2, @Nullable Charset charset) {
            super(message, HttpStatus.INTERNAL_SERVER_ERROR, statusText, headers, body2, charset);
        }
    }
}

