/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

public abstract class ResponseEntityExceptionHandler {
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    protected static final Log pageNotFoundLogger = LogFactory.getLog((String)"org.springframework.web.servlet.PageNotFound");
    protected final Log logger = LogFactory.getLog(this.getClass());

    @ExceptionHandler(value={HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class, MissingPathVariableException.class, MissingServletRequestParameterException.class, ServletRequestBindingException.class, ConversionNotSupportedException.class, TypeMismatchException.class, HttpMessageNotReadableException.class, HttpMessageNotWritableException.class, MethodArgumentNotValidException.class, MissingServletRequestPartException.class, BindException.class, NoHandlerFoundException.class, AsyncRequestTimeoutException.class})
    @Nullable
    public final ResponseEntity<Object> handleException(Exception ex, WebRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
            return this.handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException)((Object)ex), headers, status, request);
        }
        if (ex instanceof HttpMediaTypeNotSupportedException) {
            HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            return this.handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException)((Object)ex), headers, status, request);
        }
        if (ex instanceof HttpMediaTypeNotAcceptableException) {
            HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
            return this.handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException)((Object)ex), headers, status, request);
        }
        if (ex instanceof MissingPathVariableException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return this.handleMissingPathVariable((MissingPathVariableException)((Object)ex), headers, status, request);
        }
        if (ex instanceof MissingServletRequestParameterException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleMissingServletRequestParameter((MissingServletRequestParameterException)((Object)ex), headers, status, request);
        }
        if (ex instanceof ServletRequestBindingException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleServletRequestBindingException((ServletRequestBindingException)((Object)ex), headers, status, request);
        }
        if (ex instanceof ConversionNotSupportedException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return this.handleConversionNotSupported((ConversionNotSupportedException)ex, headers, status, request);
        }
        if (ex instanceof TypeMismatchException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleTypeMismatch((TypeMismatchException)ex, headers, status, request);
        }
        if (ex instanceof HttpMessageNotReadableException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleHttpMessageNotReadable((HttpMessageNotReadableException)ex, headers, status, request);
        }
        if (ex instanceof HttpMessageNotWritableException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return this.handleHttpMessageNotWritable((HttpMessageNotWritableException)ex, headers, status, request);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleMethodArgumentNotValid((MethodArgumentNotValidException)ex, headers, status, request);
        }
        if (ex instanceof MissingServletRequestPartException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleMissingServletRequestPart((MissingServletRequestPartException)((Object)ex), headers, status, request);
        }
        if (ex instanceof BindException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return this.handleBindException((BindException)ex, headers, status, request);
        }
        if (ex instanceof NoHandlerFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return this.handleNoHandlerFoundException((NoHandlerFoundException)((Object)ex), headers, status, request);
        }
        if (ex instanceof AsyncRequestTimeoutException) {
            HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
            return this.handleAsyncRequestTimeoutException((AsyncRequestTimeoutException)ex, headers, status, request);
        }
        throw ex;
    }

    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        pageNotFoundLogger.warn((Object)ex.getMessage());
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            ServletWebRequest servletWebRequest;
            headers.setAccept(mediaTypes);
            if (request instanceof ServletWebRequest && HttpMethod.PATCH.equals((Object)(servletWebRequest = (ServletWebRequest)request).getHttpMethod())) {
                headers.setAcceptPatch(mediaTypes);
            }
        }
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleExceptionInternal((Exception)((Object)ex), null, headers, status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        ServletWebRequest servletWebRequest;
        HttpServletResponse response;
        if (webRequest instanceof ServletWebRequest && (response = (servletWebRequest = (ServletWebRequest)webRequest).getResponse()) != null && response.isCommitted()) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn((Object)"Async request timed out");
            }
            return null;
        }
        return this.handleExceptionInternal(ex, null, headers, status, webRequest);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body2, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals((Object)status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }
        return new ResponseEntity<Object>(body2, (MultiValueMap<String, String>)headers, status);
    }
}

