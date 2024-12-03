/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface HostResolver {
    public InetAddress[] resolve(String var1) throws UnknownHostException;
}

