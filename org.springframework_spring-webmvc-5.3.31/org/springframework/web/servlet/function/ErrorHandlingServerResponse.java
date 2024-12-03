/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

abstract class ErrorHandlingServerResponse
implements ServerResponse {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final List<ErrorHandler<?>> errorHandlers = new ArrayList();

    ErrorHandlingServerResponse() {
    }

    protected final <T extends ServerResponse> void addErrorHandler(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, T> errorHandler) {
        Assert.notNull(predicate, (String)"Predicate must not be null");
        Assert.notNull(errorHandler, (String)"ErrorHandler must not be null");
        this.errorHandlers.add(new ErrorHandler<T>(predicate, errorHandler));
    }

    @Nullable
    protected final ModelAndView handleError(Throwable t, HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
        ServerResponse serverResponse = this.errorResponse(t, servletRequest);
        if (serverResponse != null) {
            return serverResponse.writeTo(servletRequest, servletResponse, context);
        }
        if (t instanceof ServletException) {
            throw (ServletException)t;
        }
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        throw new ServletException(t);
    }

    @Nullable
    protected final ServerResponse errorResponse(Throwable t, HttpServletRequest servletRequest) {
        for (ErrorHandler<?> errorHandler : this.errorHandlers) {
            if (!errorHandler.test(t)) continue;
            ServerRequest serverRequest = (ServerRequest)servletRequest.getAttribute(RouterFunctions.REQUEST_ATTRIBUTE);
            return errorHandler.handle(t, serverRequest);
        }
        return null;
    }

    private static class ErrorHandler<T extends ServerResponse> {
        private final Predicate<Throwable> predicate;
        private final BiFunction<Throwable, ServerRequest, T> responseProvider;

        public ErrorHandler(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, T> responseProvider) {
            Assert.notNull(predicate, (String)"Predicate must not be null");
            Assert.notNull(responseProvider, (String)"ResponseProvider must not be null");
            this.predicate = predicate;
            this.responseProvider = responseProvider;
        }

        public boolean test(Throwable t) {
            return this.predicate.test(t);
        }

        public T handle(Throwable t, ServerRequest serverRequest) {
            return (T)((ServerResponse)this.responseProvider.apply(t, serverRequest));
        }
    }
}

