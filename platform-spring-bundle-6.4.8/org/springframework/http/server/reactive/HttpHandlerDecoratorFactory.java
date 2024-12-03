/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.util.function.Function;
import org.springframework.http.server.reactive.HttpHandler;

public interface HttpHandlerDecoratorFactory
extends Function<HttpHandler, HttpHandler> {
}

