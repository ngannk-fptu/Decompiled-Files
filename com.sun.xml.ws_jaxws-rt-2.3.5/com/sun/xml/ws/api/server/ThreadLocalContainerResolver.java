/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.server;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import java.util.concurrent.Executor;

public class ThreadLocalContainerResolver
extends ContainerResolver {
    private ThreadLocal<Container> containerThreadLocal = new ThreadLocal<Container>(){

        @Override
        protected Container initialValue() {
            return Container.NONE;
        }
    };

    @Override
    public Container getContainer() {
        return this.containerThreadLocal.get();
    }

    public Container enterContainer(Container container) {
        Container old = this.containerThreadLocal.get();
        this.containerThreadLocal.set(container);
        return old;
    }

    public void exitContainer(Container old) {
        this.containerThreadLocal.set(old);
    }

    public Executor wrapExecutor(final Container container, final Executor ex) {
        if (ex == null) {
            return null;
        }
        return new Executor(){

            @Override
            public void execute(final Runnable command) {
                ex.execute(new Runnable(){

                    @Override
                    public void run() {
                        Container old = ThreadLocalContainerResolver.this.enterContainer(container);
                        try {
                            command.run();
                        }
                        finally {
                            ThreadLocalContainerResolver.this.exitContainer(old);
                        }
                    }
                });
            }
        };
    }
}

