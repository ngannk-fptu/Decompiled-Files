/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.SocketUtils
 */
package io.netty.resolver;

import io.netty.resolver.InetNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.SocketUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class DefaultNameResolver
extends InetNameResolver {
    public DefaultNameResolver(EventExecutor executor) {
        super(executor);
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        try {
            promise.setSuccess((Object)SocketUtils.addressByName((String)inetHost));
        }
        catch (UnknownHostException e) {
            promise.setFailure((Throwable)e);
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        try {
            promise.setSuccess(Arrays.asList(SocketUtils.allAddressesByName((String)inetHost)));
        }
        catch (UnknownHostException e) {
            promise.setFailure((Throwable)e);
        }
    }
}

