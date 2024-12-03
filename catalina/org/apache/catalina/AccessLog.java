/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

public interface AccessLog {
    public static final String REMOTE_ADDR_ATTRIBUTE = "org.apache.catalina.AccessLog.RemoteAddr";
    public static final String REMOTE_HOST_ATTRIBUTE = "org.apache.catalina.AccessLog.RemoteHost";
    public static final String PROTOCOL_ATTRIBUTE = "org.apache.catalina.AccessLog.Protocol";
    public static final String SERVER_NAME_ATTRIBUTE = "org.apache.catalina.AccessLog.ServerName";
    public static final String SERVER_PORT_ATTRIBUTE = "org.apache.catalina.AccessLog.ServerPort";

    public void log(Request var1, Response var2, long var3);

    public void setRequestAttributesEnabled(boolean var1);

    public boolean getRequestAttributesEnabled();
}

