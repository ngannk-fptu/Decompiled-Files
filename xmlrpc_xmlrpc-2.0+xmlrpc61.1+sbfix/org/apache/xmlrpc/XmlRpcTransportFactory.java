/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;

public interface XmlRpcTransportFactory {
    public static final String TRANSPORT_URL = "url";
    public static final String TRANSPORT_AUTH = "auth";
    public static final Class[] CONSTRUCTOR_SIGNATURE = new Class[]{1.class$java$util$Properties == null ? (1.class$java$util$Properties = 1.class$("java.util.Properties")) : 1.class$java$util$Properties};
    public static final String CONSTRUCTOR_SIGNATURE_STRING = "(java.util.Properties properties)";

    public XmlRpcTransport createTransport() throws XmlRpcClientException;

    public void setProperty(String var1, Object var2);

    static class 1 {
        static /* synthetic */ Class class$java$util$Properties;

        static /* synthetic */ Class class$(String x0) {
            try {
                return Class.forName(x0);
            }
            catch (ClassNotFoundException x1) {
                throw new NoClassDefFoundError(x1.getMessage());
            }
        }
    }
}

