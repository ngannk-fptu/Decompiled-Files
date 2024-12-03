/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.pattern.PathPatternParser;

class ChangePathPatternParserVisitor
implements RouterFunctions.Visitor {
    private final PathPatternParser parser;

    public ChangePathPatternParserVisitor(PathPatternParser parser) {
        Assert.notNull((Object)parser, "Parser must not be null");
        this.parser = parser;
    }

    @Override
    public void startNested(RequestPredicate predicate) {
        this.changeParser(predicate);
    }

    @Override
    public void endNested(RequestPredicate predicate) {
    }

    @Override
    public void route(RequestPredicate predicate, HandlerFunction<?> handlerFunction) {
        this.changeParser(predicate);
    }

    @Override
    public void resources(Function<ServerRequest, Optional<Resource>> lookupFunction) {
    }

    @Override
    public void attributes(Map<String, Object> attributes) {
    }

    @Override
    public void unknown(RouterFunction<?> routerFunction) {
    }

    private void changeParser(RequestPredicate predicate) {
        if (predicate instanceof Target) {
            Target target = (Target)((Object)predicate);
            target.changeParser(this.parser);
        }
    }

    public static interface Target {
        public void changeParser(PathPatternParser var1);
    }
}

