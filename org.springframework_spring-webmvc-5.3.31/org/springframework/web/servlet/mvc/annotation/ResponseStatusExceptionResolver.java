/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.i18n.LocaleContextHolder
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.web.bind.annotation.ResponseStatus
 *  org.springframework.web.server.ResponseStatusException
 */
package org.springframework.web.servlet.mvc.annotation;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

public class ResponseStatusExceptionResolver
extends AbstractHandlerExceptionResolver
implements MessageSourceAware {
    @Nullable
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @Nullable
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        block5: {
            try {
                if (ex instanceof ResponseStatusException) {
                    return this.resolveResponseStatusException((ResponseStatusException)ex, request, response, handler);
                }
                ResponseStatus status = (ResponseStatus)AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
                if (status != null) {
                    return this.resolveResponseStatus(status, request, response, handler, ex);
                }
                if (ex.getCause() instanceof Exception) {
                    return this.doResolveException(request, response, handler, (Exception)ex.getCause());
                }
            }
            catch (Exception resolveEx) {
                if (!this.logger.isWarnEnabled()) break block5;
                this.logger.warn((Object)("Failure while trying to resolve exception [" + ex.getClass().getName() + "]"), (Throwable)resolveEx);
            }
        }
        return null;
    }

    protected ModelAndView resolveResponseStatus(ResponseStatus responseStatus, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) throws Exception {
        int statusCode = responseStatus.code().value();
        String reason = responseStatus.reason();
        return this.applyStatusAndReason(statusCode, reason, response);
    }

    protected ModelAndView resolveResponseStatusException(ResponseStatusException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws Exception {
        ex.getResponseHeaders().forEach((name, values) -> values.forEach(value -> response.addHeader(name, value)));
        return this.applyStatusAndReason(ex.getRawStatusCode(), ex.getReason(), response);
    }

    protected ModelAndView applyStatusAndReason(int statusCode, @Nullable String reason, HttpServletResponse response) throws IOException {
        if (!StringUtils.hasLength((String)reason)) {
            response.sendError(statusCode);
        } else {
            String resolvedReason = this.messageSource != null ? this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale()) : reason;
            response.sendError(statusCode, resolvedReason);
        }
        return new ModelAndView();
    }
}

