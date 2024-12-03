/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.ErrorHandlingServerResponse;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@FunctionalInterface
public interface HandlerFilterFunction<T extends ServerResponse, R extends ServerResponse> {
    public R filter(ServerRequest var1, HandlerFunction<T> var2) throws Exception;

    default public HandlerFilterFunction<T, R> andThen(HandlerFilterFunction<T, T> after) {
        Assert.notNull(after, "HandlerFilterFunction must not be null");
        return (request, next) -> {
            HandlerFunction<ServerResponse> nextHandler = handlerRequest -> after.filter(handlerRequest, next);
            return this.filter(request, nextHandler);
        };
    }

    default public HandlerFunction<R> apply(HandlerFunction<T> handler) {
        Assert.notNull(handler, "HandlerFunction must not be null");
        return request -> this.filter(request, handler);
    }

    public static <T extends ServerResponse> HandlerFilterFunction<T, T> ofRequestProcessor(Function<ServerRequest, ServerRequest> requestProcessor) {
        Assert.notNull(requestProcessor, "Function must not be null");
        return (request, next) -> next.handle((ServerRequest)requestProcessor.apply(request));
    }

    public static <T extends ServerResponse, R extends ServerResponse> HandlerFilterFunction<T, R> ofResponseProcessor(BiFunction<ServerRequest, T, R> responseProcessor) {
        Assert.notNull(responseProcessor, "Function must not be null");
        return (request, next) -> (ServerResponse)responseProcessor.apply(request, next.handle(request));
    }

    public static <T extends ServerResponse> HandlerFilterFunction<T, T> ofErrorHandler(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, T> errorHandler) {
        Assert.notNull(predicate, "Predicate must not be null");
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        return (request, next) -> {
            try {
                Object t = next.handle(request);
                if (t instanceof ErrorHandlingServerResponse) {
                    ((ErrorHandlingServerResponse)t).addErrorHandler(predicate, errorHandler);
                }
                return t;
            }
            catch (Throwable throwable) {
                if (predicate.test(throwable)) {
                    return (ServerResponse)errorHandler.apply(throwable, request);
                }
                throw throwable;
            }
        };
    }
}

