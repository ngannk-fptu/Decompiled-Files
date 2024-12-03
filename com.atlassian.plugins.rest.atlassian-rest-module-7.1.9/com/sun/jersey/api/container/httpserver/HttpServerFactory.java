/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container.httpserver;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

public final class HttpServerFactory {
    private HttpServerFactory() {
    }

    public static HttpServer create(String u) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return HttpServerFactory.create(URI.create(u));
    }

    public static HttpServer create(URI u) throws IOException, IllegalArgumentException {
        return HttpServerFactory.create(u, ContainerFactory.createContainer(HttpHandler.class));
    }

    public static HttpServer create(String u, ResourceConfig rc) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return HttpServerFactory.create(URI.create(u), rc);
    }

    public static HttpServer create(URI u, ResourceConfig rc) throws IOException, IllegalArgumentException {
        return HttpServerFactory.create(u, ContainerFactory.createContainer(HttpHandler.class, rc));
    }

    public static HttpServer create(String u, ResourceConfig rc, IoCComponentProviderFactory factory) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return HttpServerFactory.create(URI.create(u), rc, factory);
    }

    public static HttpServer create(URI u, ResourceConfig rc, IoCComponentProviderFactory factory) throws IOException, IllegalArgumentException {
        return HttpServerFactory.create(u, ContainerFactory.createContainer(HttpHandler.class, rc, factory));
    }

    public static HttpServer create(String u, HttpHandler handler) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return HttpServerFactory.create(URI.create(u), handler);
    }

    public static HttpServer create(URI u, HttpHandler handler) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        String scheme = u.getScheme();
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            throw new IllegalArgumentException("The URI scheme, of the URI " + u + ", must be equal (ignoring case) to 'http' or 'https'");
        }
        String path = u.getPath();
        if (path == null) {
            throw new IllegalArgumentException("The URI path, of the URI " + u + ", must be non-null");
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException("The URI path, of the URI " + u + ", must be present");
        }
        if (path.charAt(0) != '/') {
            throw new IllegalArgumentException("The URI path, of the URI " + u + ". must start with a '/'");
        }
        int port = u.getPort() == -1 ? 80 : u.getPort();
        HttpServer server = scheme.equalsIgnoreCase("http") ? HttpServer.create(new InetSocketAddress(port), 0) : HttpsServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext(path, handler);
        return server;
    }
}

