/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.jms;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.apache.axis.client.Call;
import org.apache.axis.transport.jms.JMSURLConnection;

public class Handler
extends URLStreamHandler {
    static /* synthetic */ Class class$org$apache$axis$transport$jms$JMSTransport;

    protected String toExternalForm(URL url) {
        String destination = url.getPath().substring(1);
        String query = url.getQuery();
        StringBuffer jmsurl = new StringBuffer("jms:/");
        jmsurl.append(destination).append("?").append(query);
        return jmsurl.toString();
    }

    protected URLConnection openConnection(URL url) {
        return new JMSURLConnection(url);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Call.setTransportForProtocol("jms", class$org$apache$axis$transport$jms$JMSTransport == null ? (class$org$apache$axis$transport$jms$JMSTransport = Handler.class$("org.apache.axis.transport.jms.JMSTransport")) : class$org$apache$axis$transport$jms$JMSTransport);
    }
}

