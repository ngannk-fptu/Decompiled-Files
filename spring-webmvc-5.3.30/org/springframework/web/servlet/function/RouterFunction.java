/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.function;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@FunctionalInterface
public interface RouterFunction<T extends ServerResponse> {
    public Optional<HandlerFunction<T>> route(ServerRequest var1);

    default public RouterFunction<T> and(RouterFunction<T> other) {
        return new RouterFunctions.SameComposedRouterFunction<T>(this, other);
    }

    default public RouterFunction<?> andOther(RouterFunction<?> other) {
        return new RouterFunctions.DifferentComposedRouterFunction(this, other);
    }

    default public RouterFunction<T> andRoute(RequestPredicate predicate, HandlerFunction<T> handlerFunction) {
        return this.and(RouterFunctions.route(predicate, handlerFunction));
    }

    default public RouterFunction<T> andNest(RequestPredicate predicate, RouterFunction<T> routerFunction) {
        return this.and(RouterFunctions.nest(predicate, routerFunction));
    }

    default public <S extends ServerResponse> RouterFunction<S> filter(HandlerFilterFunction<T, S> filterFunction) {
        return new RouterFunctions.FilteredRouterFunction<T, S>(this, filterFunction);
    }

    default public void accept(RouterFunctions.Visitor visitor) {
        visitor.unknown(this);
    }

    default public RouterFunction<T> withAttribute(String name, Object value) {
        Assert.hasLength((String)name, (String)"Name must not be empty");
        Assert.notNull((Object)value, (String)"Value must not be null");
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributes.put(name, value);
        return new RouterFunctions.AttributesRouterFunction(this, attributes);
    }

    default public RouterFunction<T> withAttributes(Consumer<Map<String, Object>> attributesConsumer) {
        Assert.notNull(attributesConsumer, (String)"AttributesConsumer must not be null");
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributesConsumer.accept(attributes);
        return new RouterFunctions.AttributesRouterFunction(this, attributes);
    }
}

