/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface DnsResolver {
    public InetAddress[] resolve(String var1) throws UnknownHostException;
}

