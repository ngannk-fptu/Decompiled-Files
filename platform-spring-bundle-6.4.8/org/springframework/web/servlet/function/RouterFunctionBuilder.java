/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

class RouterFunctionBuilder
implements RouterFunctions.Builder {
    private final List<RouterFunction<ServerResponse>> routerFunctions = new ArrayList<RouterFunction<ServerResponse>>();
    private final List<HandlerFilterFunction<ServerResponse, ServerResponse>> filterFunctions = new ArrayList<HandlerFilterFunction<ServerResponse, ServerResponse>>();
    private final List<HandlerFilterFunction<ServerResponse, ServerResponse>> errorHandlers = new ArrayList<HandlerFilterFunction<ServerResponse, ServerResponse>>();

    RouterFunctionBuilder() {
    }

    @Override
    public RouterFunctions.Builder add(RouterFunction<ServerResponse> routerFunction) {
        Assert.notNull(routerFunction, "RouterFunction must not be null");
        this.routerFunctions.add(routerFunction);
        return this;
    }

    private RouterFunctions.Builder add(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        this.routerFunctions.add(RouterFunctions.route(predicate, handlerFunction));
        return this;
    }

    @Override
    public RouterFunctions.Builder GET(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.GET), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder GET(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.GET).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder GET(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.GET(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder GET(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.GET(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder HEAD(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.HEAD), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder HEAD(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.HEAD).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder HEAD(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.HEAD(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder HEAD(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.HEAD(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder POST(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.POST), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder POST(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.POST).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder POST(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.POST(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder POST(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.POST(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PUT(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.PUT), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PUT(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.PUT).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PUT(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.PUT(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PUT(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.PUT(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PATCH(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.PATCH), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PATCH(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.PATCH).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PATCH(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.PATCH(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder PATCH(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.PATCH(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder DELETE(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.DELETE), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder DELETE(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.DELETE).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder DELETE(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.DELETE(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder DELETE(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.DELETE(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder OPTIONS(HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.OPTIONS), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder OPTIONS(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.method(HttpMethod.OPTIONS).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder OPTIONS(String pattern, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.OPTIONS(pattern), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder OPTIONS(String pattern, RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RequestPredicates.OPTIONS(pattern).and(predicate), handlerFunction);
    }

    @Override
    public RouterFunctions.Builder route(RequestPredicate predicate, HandlerFunction<ServerResponse> handlerFunction) {
        return this.add(RouterFunctions.route(predicate, handlerFunction));
    }

    @Override
    public RouterFunctions.Builder resources(String pattern, Resource location) {
        return this.add(RouterFunctions.resources(pattern, location));
    }

    @Override
    public RouterFunctions.Builder resources(Function<ServerRequest, Optional<Resource>> lookupFunction) {
        return this.add(RouterFunctions.resources(lookupFunction));
    }

    @Override
    public RouterFunctions.Builder nest(RequestPredicate predicate, Consumer<RouterFunctions.Builder> builderConsumer) {
        Assert.notNull(builderConsumer, "Consumer must not be null");
        RouterFunctionBuilder nestedBuilder = new RouterFunctionBuilder();
        builderConsumer.accept(nestedBuilder);
        RouterFunction<ServerResponse> nestedRoute = nestedBuilder.build();
        this.routerFunctions.add(RouterFunctions.nest(predicate, nestedRoute));
        return this;
    }

    @Override
    public RouterFunctions.Builder nest(RequestPredicate predicate, Supplier<RouterFunction<ServerResponse>> routerFunctionSupplier) {
        Assert.notNull(routerFunctionSupplier, "RouterFunction Supplier must not be null");
        RouterFunction<ServerResponse> nestedRoute = routerFunctionSupplier.get();
        this.routerFunctions.add(RouterFunctions.nest(predicate, nestedRoute));
        return this;
    }

    @Override
    public RouterFunctions.Builder path(String pattern, Consumer<RouterFunctions.Builder> builderConsumer) {
        return this.nest(RequestPredicates.path(pattern), builderConsumer);
    }

    @Override
    public RouterFunctions.Builder path(String pattern, Supplier<RouterFunction<ServerResponse>> routerFunctionSupplier) {
        return this.nest(RequestPredicates.path(pattern), routerFunctionSupplier);
    }

    @Override
    public RouterFunctions.Builder filter(HandlerFilterFunction<ServerResponse, ServerResponse> filterFunction) {
        Assert.notNull(filterFunction, "HandlerFilterFunction must not be null");
        this.filterFunctions.add(filterFunction);
        return this;
    }

    @Override
    public RouterFunctions.Builder before(Function<ServerRequest, ServerRequest> requestProcessor) {
        Assert.notNull(requestProcessor, "RequestProcessor must not be null");
        return this.filter(HandlerFilterFunction.ofRequestProcessor(requestProcessor));
    }

    @Override
    public RouterFunctions.Builder after(BiFunction<ServerRequest, ServerResponse, ServerResponse> responseProcessor) {
        Assert.notNull(responseProcessor, "ResponseProcessor must not be null");
        return this.filter(HandlerFilterFunction.ofResponseProcessor(responseProcessor));
    }

    @Override
    public RouterFunctions.Builder onError(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, ServerResponse> responseProvider) {
        Assert.notNull(predicate, "Predicate must not be null");
        Assert.notNull(responseProvider, "ResponseProvider must not be null");
        this.errorHandlers.add(0, HandlerFilterFunction.ofErrorHandler(predicate, responseProvider));
        return this;
    }

    @Override
    public RouterFunctions.Builder onError(Class<? extends Throwable> exceptionType, BiFunction<Throwable, ServerRequest, ServerResponse> responseProvider) {
        Assert.notNull(exceptionType, "ExceptionType must not be null");
        Assert.notNull(responseProvider, "ResponseProvider must not be null");
        return this.onError(exceptionType::isInstance, responseProvider);
    }

    @Override
    public RouterFunctions.Builder withAttribute(String name, Object value) {
        Assert.hasLength(name, "Name must not be empty");
        Assert.notNull(value, "Value must not be null");
        if (this.routerFunctions.isEmpty()) {
            throw new IllegalStateException("attributes can only be called after any other method (GET, path, etc.)");
        }
        int lastIdx = this.routerFunctions.size() - 1;
        RouterFunction<ServerResponse> attributed = this.routerFunctions.get(lastIdx).withAttribute(name, value);
        this.routerFunctions.set(lastIdx, attributed);
        return this;
    }

    @Override
    public RouterFunctions.Builder withAttributes(Consumer<Map<String, Object>> attributesConsumer) {
        Assert.notNull(attributesConsumer, "AttributesConsumer must not be null");
        if (this.routerFunctions.isEmpty()) {
            throw new IllegalStateException("attributes can only be called after any other method (GET, path, etc.)");
        }
        int lastIdx = this.routerFunctions.size() - 1;
        RouterFunction<ServerResponse> attributed = this.routerFunctions.get(lastIdx).withAttributes(attributesConsumer);
        this.routerFunctions.set(lastIdx, attributed);
        return this;
    }

    @Override
    public RouterFunction<ServerResponse> build() {
        if (this.routerFunctions.isEmpty()) {
            throw new IllegalStateException("No routes registered. Register a route with GET(), POST(), etc.");
        }
        BuiltRouterFunction result = new BuiltRouterFunction(this.routerFunctions);
        if (this.filterFunctions.isEmpty() && this.errorHandlers.isEmpty()) {
            return result;
        }
        HandlerFilterFunction filter2 = (HandlerFilterFunction)Stream.concat(this.filterFunctions.stream(), this.errorHandlers.stream()).reduce(HandlerFilterFunction::andThen).orElseThrow(IllegalStateException::new);
        return result.filter(filter2);
    }

    private static class BuiltRouterFunction
    extends RouterFunctions.AbstractRouterFunction<ServerResponse> {
        private final List<RouterFunction<ServerResponse>> routerFunctions;

        public BuiltRouterFunction(List<RouterFunction<ServerResponse>> routerFunctions) {
            Assert.notEmpty(routerFunctions, "RouterFunctions must not be empty");
            this.routerFunctions = new ArrayList<RouterFunction<ServerResponse>>(routerFunctions);
        }

        @Override
        public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
            for (RouterFunction<ServerResponse> routerFunction : this.routerFunctions) {
                Optional<HandlerFunction<ServerResponse>> result = routerFunction.route(request);
                if (!result.isPresent()) continue;
                return result;
            }
            return Optional.empty();
        }

        @Override
        public void accept(RouterFunctions.Visitor visitor) {
            this.routerFunctions.forEach(routerFunction -> routerFunction.accept(visitor));
        }
    }
}

