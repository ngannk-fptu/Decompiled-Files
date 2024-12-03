/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.mvc.support;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

public class DefaultHandlerExceptionResolver
extends AbstractHandlerExceptionResolver {
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    protected static final Log pageNotFoundLogger = LogFactory.getLog((String)"org.springframework.web.servlet.PageNotFound");

    public DefaultHandlerExceptionResolver() {
        this.setOrder(Integer.MAX_VALUE);
        this.setWarnLogCategory(this.getClass().getName());
    }

    @Override
    @Nullable
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        block17: {
            try {
                if (ex instanceof HttpRequestMethodNotSupportedException) {
                    return this.handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException)((Object)ex), request, response, handler);
                }
                if (ex instanceof HttpMediaTypeNotSupportedException) {
                    return this.handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException)((Object)ex), request, response, handler);
                }
                if (ex instanceof HttpMediaTypeNotAcceptableException) {
                    return this.handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException)((Object)ex), request, response, handler);
                }
                if (ex instanceof MissingPathVariableException) {
                    return this.handleMissingPathVariable((MissingPathVariableException)((Object)ex), request, response, handler);
                }
                if (ex instanceof MissingServletRequestParameterException) {
                    return this.handleMissingServletRequestParameter((MissingServletRequestParameterException)((Object)ex), request, response, handler);
                }
                if (ex instanceof ServletRequestBindingException) {
                    return this.handleServletRequestBindingException((ServletRequestBindingException)((Object)ex), request, response, handler);
                }
                if (ex instanceof ConversionNotSupportedException) {
                    return this.handleConversionNotSupported((ConversionNotSupportedException)ex, request, response, handler);
                }
                if (ex instanceof TypeMismatchException) {
                    return this.handleTypeMismatch((TypeMismatchException)ex, request, response, handler);
                }
                if (ex instanceof HttpMessageNotReadableException) {
                    return this.handleHttpMessageNotReadable((HttpMessageNotReadableException)ex, request, response, handler);
                }
                if (ex instanceof HttpMessageNotWritableException) {
                    return this.handleHttpMessageNotWritable((HttpMessageNotWritableException)ex, request, response, handler);
                }
                if (ex instanceof MethodArgumentNotValidException) {
                    return this.handleMethodArgumentNotValidException((MethodArgumentNotValidException)ex, request, response, handler);
                }
                if (ex instanceof MissingServletRequestPartException) {
                    return this.handleMissingServletRequestPartException((MissingServletRequestPartException)((Object)ex), request, response, handler);
                }
                if (ex instanceof BindException) {
                    return this.handleBindException((BindException)ex, request, response, handler);
                }
                if (ex instanceof NoHandlerFoundException) {
                    return this.handleNoHandlerFoundException((NoHandlerFoundException)((Object)ex), request, response, handler);
                }
                if (ex instanceof AsyncRequestTimeoutException) {
                    return this.handleAsyncRequestTimeoutException((AsyncRequestTimeoutException)ex, request, response, handler);
                }
            }
            catch (Exception handlerEx) {
                if (!this.logger.isWarnEnabled()) break block17;
                this.logger.warn((Object)("Failure while trying to resolve exception [" + ex.getClass().getName() + "]"), (Throwable)handlerEx);
            }
        }
        return null;
    }

    protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        Object[] supportedMethods = ex.getSupportedMethods();
        if (supportedMethods != null) {
            response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
        }
        response.sendError(405, ex.getMessage());
        return new ModelAndView();
    }

    protected ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            response.setHeader("Accept", MediaType.toString(mediaTypes));
            if (request.getMethod().equals("PATCH")) {
                response.setHeader("Accept-Patch", MediaType.toString(mediaTypes));
            }
        }
        response.sendError(415);
        return new ModelAndView();
    }

    protected ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(406);
        return new ModelAndView();
    }

    protected ModelAndView handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(500, ex.getMessage());
        return new ModelAndView();
    }

    protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400, ex.getMessage());
        return new ModelAndView();
    }

    protected ModelAndView handleServletRequestBindingException(ServletRequestBindingException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400, ex.getMessage());
        return new ModelAndView();
    }

    protected ModelAndView handleConversionNotSupported(ConversionNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        this.sendServerError(ex, request, response);
        return new ModelAndView();
    }

    protected ModelAndView handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected ModelAndView handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        this.sendServerError(ex, request, response);
        return new ModelAndView();
    }

    protected ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected ModelAndView handleMissingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400, ex.getMessage());
        return new ModelAndView();
    }

    protected ModelAndView handleBindException(BindException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(400);
        return new ModelAndView();
    }

    protected ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        pageNotFoundLogger.warn((Object)ex.getMessage());
        response.sendError(404);
        return new ModelAndView();
    }

    protected ModelAndView handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        if (!response.isCommitted()) {
            response.sendError(503);
        } else {
            this.logger.warn((Object)"Async request timed out");
        }
        return new ModelAndView();
    }

    protected void sendServerError(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute("javax.servlet.error.exception", (Object)ex);
        response.sendError(500);
    }
}

