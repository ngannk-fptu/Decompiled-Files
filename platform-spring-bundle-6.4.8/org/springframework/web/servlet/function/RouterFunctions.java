/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.function;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.ChangePathPatternParserVisitor;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.PathResourceLookupFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.ResourceHandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctionBuilder;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.ToStringVisitor;
import org.springframework.web.util.pattern.PathPatternParser;

public abstract class RouterFunctions {
    private static final Log logger = LogFactory.getLog(RouterFunctions.class);
    public static final String REQUEST_ATTRIBUTE = RouterFunctions.class.getName() + ".request";
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = RouterFunctions.class.getName() + ".uriTemplateVariables";
    public static final String MATCHING_PATTERN_ATTRIBUTE = RouterFunctions.class.getName() + ".matchingPattern";

    public static Builder route() {
        return new RouterFunctionBuilder();
    }

    public static <T extends ServerResponse> RouterFunction<T> route(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
        return new DefaultRouterFunction<T>(predicate, handlerFunction);
    }

    public static <T extends ServerResponse> RouterFunction<T> nest(RequestPredicate predicate, RouterFunction<T> routerFunction) {
        return new DefaultNestedRouterFunction<T>(predicate, routerFunction);
    }

    public static RouterFunction<ServerResponse> resources(String pattern, Resource location) {
        return RouterFunctions.resources(RouterFunctions.resourceLookupFunction(pattern, location));
    }

    public static Function<ServerRequest, Optional<Resource>> resourceLookupFunction(String pattern, Resource location) {
        return new PathResourceLookupFunction(pattern, location);
    }

    public static RouterFunction<ServerResponse> resources(Function<ServerRequest, Optional<Resource>> lookupFunction) {
        return new ResourcesRouterFunction(lookupFunction);
    }

    public static <T extends ServerResponse> RouterFunction<T> changeParser(RouterFunction<T> routerFunction, PathPatternParser parser) {
        Assert.notNull(routerFunction, "RouterFunction must not be null");
        Assert.notNull((Object)parser, "Parser must not be null");
        ChangePathPatternParserVisitor visitor = new ChangePathPatternParserVisitor(parser);
        routerFunction.accept(visitor);
        return routerFunction;
    }

    static final class AttributesRouterFunction<T extends ServerResponse>
    extends AbstractRouterFunction<T> {
        private final RouterFunction<T> delegate;
        private final Map<String, Object> attributes;

        public AttributesRouterFunction(RouterFunction<T> delegate, Map<String, Object> attributes) {
            this.delegate = delegate;
            this.attributes = AttributesRouterFunction.initAttributes(attributes);
        }

        private static Map<String, Object> initAttributes(Map<String, Object> attributes) {
            if (attributes.isEmpty()) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(new LinkedHashMap<String, Object>(attributes));
        }

        @Override
        public Optional<HandlerFunction<T>> route(ServerRequest request) {
            return this.delegate.route(request);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.attributes(this.attributes);
            this.delegate.accept(visitor);
        }

        @Override
        public RouterFunction<T> withAttribute(String name, Object value) {
            Assert.hasLength(name, "Name must not be empty");
            Assert.notNull(value, "Value must not be null");
            LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>(this.attributes);
            attributes.put(name, value);
            return new AttributesRouterFunction<T>(this.delegate, attributes);
        }

        @Override
        public RouterFunction<T> withAttributes(Consumer<Map<String, Object>> attributesConsumer) {
            Assert.notNull(attributesConsumer, "AttributesConsumer must not be null");
            LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>(this.attributes);
            attributesConsumer.accept(attributes);
            return new AttributesRouterFunction<T>(this.delegate, attributes);
        }
    }

    private static class ResourcesRouterFunction
    extends AbstractRouterFunction<ServerResponse> {
        private final Function<ServerRequest, Optional<Resource>> lookupFunction;

        public ResourcesRouterFunction(Function<ServerRequest, Optional<Resource>> lookupFunction) {
            Assert.notNull(lookupFunction, "Function must not be null");
            this.lookupFunction = lookupFunction;
        }

        @Override
        public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
            return this.lookupFunction.apply(request).map(ResourceHandlerFunction::new);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.resources(this.lookupFunction);
        }
    }

    private static final class DefaultNestedRouterFunction<T extends ServerResponse>
    extends AbstractRouterFunction<T> {
        private final RequestPredicate predicate;
        private final RouterFunction<T> routerFunction;

        public DefaultNestedRouterFunction(RequestPredicate predicate, RouterFunction<T> routerFunction) {
            Assert.notNull((Object)predicate, "Predicate must not be null");
            Assert.notNull(routerFunction, "RouterFunction must not be null");
            this.predicate = predicate;
            this.routerFunction = routerFunction;
        }

        @Override
        public Optional<HandlerFunction<T>> route(ServerRequest serverRequest) {
            return this.predicate.nest(serverRequest).map(nestedRequest -> {
                Optional<HandlerFunction<T>> result;
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)String.format("Nested predicate \"%s\" matches against \"%s\"", this.predicate, serverRequest));
                }
                if ((result = this.routerFunction.route((ServerRequest)nestedRequest)).isPresent() && nestedRequest != serverRequest) {
                    serverRequest.attributes().clear();
                    serverRequest.attributes().putAll(nestedRequest.attributes());
                }
                return result;
            }).orElseGet(Optional::empty);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.startNested(this.predicate);
            this.routerFunction.accept(visitor);
            visitor.endNested(this.predicate);
        }
    }

    private static final class DefaultRouterFunction<T extends ServerResponse>
    extends AbstractRouterFunction<T> {
        private final RequestPredicate predicate;
        private final HandlerFunction<T> handlerFunction;

        public DefaultRouterFunction(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
            Assert.notNull((Object)predicate, "Predicate must not be null");
            Assert.notNull(handlerFunction, "HandlerFunction must not be null");
            this.predicate = predicate;
            this.handlerFunction = handlerFunction;
        }

        @Override
        public Optional<HandlerFunction<T>> route(ServerRequest request) {
            if (this.predicate.test(request)) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)String.format("Predicate \"%s\" matches against \"%s\"", this.predicate, request));
                }
                return Optional.of(this.handlerFunction);
            }
            return Optional.empty();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.route(this.predicate, this.handlerFunction);
        }
    }

    static final class FilteredRouterFunction<T extends ServerResponse, S extends ServerResponse>
    implements RouterFunction<S> {
        private final RouterFunction<T> routerFunction;
        private final HandlerFilterFunction<T, S> filterFunction;

        public FilteredRouterFunction(RouterFunction<T> routerFunction, HandlerFilterFunction<T, S> filterFunction) {
            this.routerFunction = routerFunction;
            this.filterFunction = filterFunction;
        }

        @Override
        public Optional<HandlerFunction<S>> route(ServerRequest request) {
            return this.routerFunction.route(request).map(this.filterFunction::apply);
        }

        @Override
        public void accept(Visitor visitor) {
            this.routerFunction.accept(visitor);
        }

        public String toString() {
            return this.routerFunction.toString();
        }
    }

    static final class DifferentComposedRouterFunction
    extends AbstractRouterFunction<ServerResponse> {
        private final RouterFunction<?> first;
        private final RouterFunction<?> second;

        public DifferentComposedRouterFunction(RouterFunction<?> first, RouterFunction<?> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public Optional<HandlerFunction<ServerResponse>> route(ServerRequest request) {
            Optional<HandlerFunction<ServerResponse>> firstRoute = this.first.route(request);
            if (firstRoute.isPresent()) {
                return firstRoute;
            }
            Optional<HandlerFunction<ServerResponse>> secondRoute = this.second.route(request);
            return secondRoute;
        }

        @Override
        public void accept(Visitor visitor) {
            this.first.accept(visitor);
            this.second.accept(visitor);
        }
    }

    static final class SameComposedRouterFunction<T extends ServerResponse>
    extends AbstractRouterFunction<T> {
        private final RouterFunction<T> first;
        private final RouterFunction<T> second;

        public SameComposedRouterFunction(RouterFunction<T> first, RouterFunction<T> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public Optional<HandlerFunction<T>> route(ServerRequest request) {
            Optional<HandlerFunction<T>> firstRoute = this.first.route(request);
            if (firstRoute.isPresent()) {
                return firstRoute;
            }
            return this.second.route(request);
        }

        @Override
        public void accept(Visitor visitor) {
            this.first.accept(visitor);
            this.second.accept(visitor);
        }
    }

    static abstract class AbstractRouterFunction<T extends ServerResponse>
    implements RouterFunction<T> {
        AbstractRouterFunction() {
        }

        public String toString() {
            ToStringVisitor visitor = new ToStringVisitor();
            this.accept(visitor);
            return visitor.toString();
        }
    }

    public static interface Visitor {
        public void startNested(RequestPredicate var1);

        public void endNested(RequestPredicate var1);

        public void route(RequestPredicate var1, HandlerFunction<?> var2);

        public void resources(Function<ServerRequest, Optional<Resource>> var1);

        public void attributes(Map<String, Object> var1);

        public void unknown(RouterFunction<?> var1);
    }

    public static interface Builder {
        public Builder GET(HandlerFunction<ServerResponse> var1);

        public Builder GET(String var1, HandlerFunction<ServerResponse> var2);

        public Builder GET(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder GET(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder HEAD(HandlerFunction<ServerResponse> var1);

        public Builder HEAD(String var1, HandlerFunction<ServerResponse> var2);

        public Builder HEAD(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder HEAD(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder POST(HandlerFunction<ServerResponse> var1);

        public Builder POST(String var1, HandlerFunction<ServerResponse> var2);

        public Builder POST(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder POST(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder PUT(HandlerFunction<ServerResponse> var1);

        public Builder PUT(String var1, HandlerFunction<ServerResponse> var2);

        public Builder PUT(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder PUT(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder PATCH(HandlerFunction<ServerResponse> var1);

        public Builder PATCH(String var1, HandlerFunction<ServerResponse> var2);

        public Builder PATCH(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder PATCH(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder DELETE(HandlerFunction<ServerResponse> var1);

        public Builder DELETE(String var1, HandlerFunction<ServerResponse> var2);

        public Builder DELETE(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder DELETE(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder OPTIONS(HandlerFunction<ServerResponse> var1);

        public Builder OPTIONS(String var1, HandlerFunction<ServerResponse> var2);

        public Builder OPTIONS(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder OPTIONS(String var1, RequestPredicate var2, HandlerFunction<ServerResponse> var3);

        public Builder route(RequestPredicate var1, HandlerFunction<ServerResponse> var2);

        public Builder add(RouterFunction<ServerResponse> var1);

        public Builder resources(String var1, Resource var2);

        public Builder resources(Function<ServerRequest, Optional<Resource>> var1);

        public Builder nest(RequestPredicate var1, Supplier<RouterFunction<ServerResponse>> var2);

        public Builder nest(RequestPredicate var1, Consumer<Builder> var2);

        public Builder path(String var1, Supplier<RouterFunction<ServerResponse>> var2);

        public Builder path(String var1, Consumer<Builder> var2);

        public Builder filter(HandlerFilterFunction<ServerResponse, ServerResponse> var1);

        public Builder before(Function<ServerRequest, ServerRequest> var1);

        public Builder after(BiFunction<ServerRequest, ServerResponse, ServerResponse> var1);

        public Builder onError(Predicate<Throwable> var1, BiFunction<Throwable, ServerRequest, ServerResponse> var2);

        public Builder onError(Class<? extends Throwable> var1, BiFunction<Throwable, ServerRequest, ServerResponse> var2);

        public Builder withAttribute(String var1, Object var2);

        public Builder withAttributes(Consumer<Map<String, Object>> var1);

        public RouterFunction<ServerResponse> build();
    }
}

