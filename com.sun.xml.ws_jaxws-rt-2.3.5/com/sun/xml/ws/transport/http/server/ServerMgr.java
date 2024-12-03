/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.ws.server.ServerRtException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ServerMgr {
    private static final ServerMgr serverMgr = new ServerMgr();
    private static final Logger LOGGER = Logger.getLogger("com.sun.xml.ws.server.http");
    private final Map<InetSocketAddress, ServerState> servers = new HashMap<InetSocketAddress, ServerState>();

    private ServerMgr() {
    }

    static ServerMgr getInstance() {
        return serverMgr;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    HttpContext createContext(String address) {
        try {
            ServerState state;
            URL url = new URL(address);
            int port = url.getPort();
            if (port == -1) {
                port = url.getDefaultPort();
            }
            InetSocketAddress inetAddress = new InetSocketAddress(url.getHost(), port);
            Map<InetSocketAddress, ServerState> map = this.servers;
            synchronized (map) {
                state = this.servers.get(inetAddress);
                if (state == null) {
                    ServerState free = null;
                    for (ServerState ss : this.servers.values()) {
                        if (port != ss.getServer().getAddress().getPort()) continue;
                        free = ss;
                        break;
                    }
                    if (inetAddress.getAddress().isAnyLocalAddress() && free != null) {
                        state = free;
                    } else {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("Creating new HTTP Server at " + inetAddress);
                        }
                        HttpServer server = HttpServer.create(inetAddress, 0);
                        server.setExecutor(Executors.newCachedThreadPool());
                        String path = url.toURI().getPath();
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("Creating HTTP Context at = " + path);
                        }
                        HttpContext context = server.createContext(path);
                        server.start();
                        inetAddress = server.getAddress();
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("HTTP server started = " + inetAddress);
                        }
                        state = new ServerState(server, path);
                        this.servers.put(inetAddress, state);
                        return context;
                    }
                }
            }
            HttpServer server = state.getServer();
            if (state.getPaths().contains(url.getPath())) {
                String err = "Context with URL path " + url.getPath() + " already exists on the server " + server.getAddress();
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(err);
                }
                throw new IllegalArgumentException(err);
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Creating HTTP Context at = " + url.getPath());
            }
            HttpContext context = server.createContext(url.getPath());
            state.oneMoreContext(url.getPath());
            return context;
        }
        catch (Exception e) {
            throw new ServerRtException("server.rt.err", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeContext(HttpContext context) {
        InetSocketAddress inetAddress = context.getServer().getAddress();
        Map<InetSocketAddress, ServerState> map = this.servers;
        synchronized (map) {
            ServerState state = this.servers.get(inetAddress);
            int instances = state.noOfContexts();
            if (instances < 2) {
                ((ExecutorService)state.getServer().getExecutor()).shutdown();
                state.getServer().stop(0);
                this.servers.remove(inetAddress);
            } else {
                state.getServer().removeContext(context);
                state.oneLessContext(context.getPath());
            }
        }
    }

    private static final class ServerState {
        private final HttpServer server;
        private int instances;
        private final Set<String> paths = new HashSet<String>();

        ServerState(HttpServer server, String path) {
            this.server = server;
            this.instances = 1;
            this.paths.add(path);
        }

        public HttpServer getServer() {
            return this.server;
        }

        public void oneMoreContext(String path) {
            ++this.instances;
            this.paths.add(path);
        }

        public void oneLessContext(String path) {
            --this.instances;
            this.paths.remove(path);
        }

        public int noOfContexts() {
            return this.instances;
        }

        public Set<String> getPaths() {
            return this.paths;
        }
    }
}

