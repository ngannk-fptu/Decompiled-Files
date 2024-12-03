/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.applet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.apache.xmlrpc.applet.XmlRpcException;
import org.apache.xmlrpc.applet.XmlRpcSupport;

public class SimpleXmlRpcClient {
    URL url;

    public SimpleXmlRpcClient(URL url) {
        this.url = url;
    }

    public SimpleXmlRpcClient(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public SimpleXmlRpcClient(String hostname, int port) throws MalformedURLException {
        this.url = new URL("http://" + hostname + ":" + port + "/RPC2");
    }

    public Object execute(String method, Vector params) throws XmlRpcException, IOException {
        return new XmlRpcSupport(this.url).execute(method, params);
    }
}

