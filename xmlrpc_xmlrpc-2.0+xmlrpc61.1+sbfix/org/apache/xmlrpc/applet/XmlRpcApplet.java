/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.applet;

import java.applet.Applet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.apache.xmlrpc.applet.SimpleXmlRpcClient;
import org.apache.xmlrpc.applet.XmlRpcException;

public class XmlRpcApplet
extends Applet {
    SimpleXmlRpcClient client;

    public void initClient() {
        int port = 80;
        String p = this.getParameter("PORT");
        if (p != null) {
            try {
                port = Integer.parseInt(p);
            }
            catch (NumberFormatException nfx) {
                System.out.println("Error parsing port: " + nfx);
            }
        }
        this.initClient(port);
    }

    public void initClient(int port) {
        String uri = this.getParameter("URI");
        if (uri == null) {
            uri = "/RPC2";
        } else if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        this.initClient(port, uri);
    }

    public void initClient(int port, String uri) {
        String host = this.getCodeBase().getHost();
        try {
            URL url = new URL("http://" + host + ":" + port + uri);
            System.out.println("XML-RPC URL: " + url);
            this.client = new SimpleXmlRpcClient(url);
        }
        catch (MalformedURLException unlikely) {
            System.out.println("Error constructing XML-RPC client for " + host + ":" + port + ": " + unlikely);
        }
    }

    public Object execute(String methodName, Vector arguments) throws XmlRpcException, IOException {
        if (this.client == null) {
            this.initClient();
        }
        Object returnValue = null;
        returnValue = this.client.execute(methodName, arguments);
        return returnValue;
    }
}

