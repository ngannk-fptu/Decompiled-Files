/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;

class ToStringVisitor
implements RouterFunctions.Visitor,
RequestPredicates.Visitor {
    private final StringBuilder builder = new StringBuilder();
    private int indent = 0;

    ToStringVisitor() {
    }

    @Override
    public void startNested(RequestPredicate predicate) {
        this.indent();
        predicate.accept(this);
        this.builder.append(" => {\n");
        ++this.indent;
    }

    @Override
    public void endNested(RequestPredicate predicate) {
        --this.indent;
        this.indent();
        this.builder.append("}\n");
    }

    @Override
    public void route(RequestPredicate predicate, HandlerFunction<?> handlerFunction) {
        this.indent();
        predicate.accept(this);
        this.builder.append(" -> ");
        this.builder.append(handlerFunction).append('\n');
    }

    @Override
    public void resources(Function<ServerRequest, Optional<Resource>> lookupFunction) {
        this.indent();
        this.builder.append(lookupFunction).append('\n');
    }

    @Override
    public void attributes(Map<String, Object> attributes) {
    }

    @Override
    public void unknown(RouterFunction<?> routerFunction) {
        this.indent();
        this.builder.append(routerFunction);
    }

    private void indent() {
        for (int i2 = 0; i2 < this.indent; ++i2) {
            this.builder.append(' ');
        }
    }

    @Override
    public void method(Set<HttpMethod> methods) {
        if (methods.size() == 1) {
            this.builder.append((Object)methods.iterator().next());
        } else {
            this.builder.append(methods);
        }
    }

    @Override
    public void path(String pattern) {
        this.builder.append(pattern);
    }

    @Override
    public void pathExtension(String extension) {
        this.builder.append(String.format("*.%s", extension));
    }

    @Override
    public void header(String name, String value) {
        this.builder.append(String.format("%s: %s", name, value));
    }

    @Override
    public void param(String name, String value) {
        this.builder.append(String.format("?%s == %s", name, value));
    }

    @Override
    public void startAnd() {
        this.builder.append('(');
    }

    @Override
    public void and() {
        this.builder.append(" && ");
    }

    @Override
    public void endAnd() {
        this.builder.append(')');
    }

    @Override
    public void startOr() {
        this.builder.append('(');
    }

    @Override
    public void or() {
        this.builder.append(" || ");
    }

    @Override
    public void endOr() {
        this.builder.append(')');
    }

    @Override
    public void startNegate() {
        this.builder.append("!(");
    }

    @Override
    public void endNegate() {
        this.builder.append(')');
    }

    @Override
    public void unknown(RequestPredicate predicate) {
        this.builder.append(predicate);
    }

    public String toString() {
        String result = this.builder.toString();
        if (result.endsWith("\n")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}

