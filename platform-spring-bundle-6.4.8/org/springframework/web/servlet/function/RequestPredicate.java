/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.util.Optional;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.ServerRequest;

@FunctionalInterface
public interface RequestPredicate {
    public boolean test(ServerRequest var1);

    default public RequestPredicate and(RequestPredicate other) {
        return new RequestPredicates.AndRequestPredicate(this, other);
    }

    default public RequestPredicate negate() {
        return new RequestPredicates.NegateRequestPredicate(this);
    }

    default public RequestPredicate or(RequestPredicate other) {
        return new RequestPredicates.OrRequestPredicate(this, other);
    }

    default public Optional<ServerRequest> nest(ServerRequest request) {
        return this.test(request) ? Optional.of(request) : Optional.empty();
    }

    default public void accept(RequestPredicates.Visitor visitor) {
        visitor.unknown(this);
    }
}

