/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface DnsResolver {
    public InetAddress[] resolve(String var1) throws UnknownHostException;

    public String resolveCanonicalHostname(String var1) throws UnknownHostException;
}

