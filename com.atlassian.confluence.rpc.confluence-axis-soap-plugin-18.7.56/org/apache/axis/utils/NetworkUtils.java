/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class NetworkUtils {
    public static final String LOCALHOST = "127.0.0.1";
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$NetworkUtils == null ? (class$org$apache$axis$utils$NetworkUtils = NetworkUtils.class$("org.apache.axis.utils.NetworkUtils")) : class$org$apache$axis$utils$NetworkUtils).getName());
    static /* synthetic */ Class class$org$apache$axis$utils$NetworkUtils;

    private NetworkUtils() {
    }

    public static String getLocalHostname() {
        String hostname;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostname = address.getHostName();
            if (hostname == null || hostname.length() == 0) {
                hostname = address.toString();
            }
        }
        catch (UnknownHostException noIpAddrException) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Failed to lookup local IP address", (Throwable)noIpAddrException);
            }
            hostname = LOCALHOST;
        }
        return hostname;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

