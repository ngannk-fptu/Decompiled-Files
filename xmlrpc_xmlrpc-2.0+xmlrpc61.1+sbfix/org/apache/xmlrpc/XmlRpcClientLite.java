/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.apache.xmlrpc.LiteXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcTransport;

public class XmlRpcClientLite
extends XmlRpcClient {
    public XmlRpcClientLite(URL url) {
        super(url);
    }

    public XmlRpcClientLite(String url) throws MalformedURLException {
        super(url);
    }

    public XmlRpcClientLite(String hostname, int port) throws MalformedURLException {
        super(hostname, port);
    }

    protected XmlRpcTransport createTransport() {
        return new LiteXmlRpcTransport(this.url);
    }

    public static void main(String[] args) throws Exception {
        try {
            String url = args[0];
            String method = args[1];
            XmlRpcClientLite client = new XmlRpcClientLite(url);
            Vector<Object> v = new Vector<Object>();
            for (int i = 2; i < args.length; ++i) {
                try {
                    v.addElement(new Integer(Integer.parseInt(args[i])));
                    continue;
                }
                catch (NumberFormatException nfx) {
                    v.addElement(args[i]);
                }
            }
            try {
                System.out.println(client.execute(method, v));
            }
            catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
        catch (Exception x) {
            System.err.println(x);
            System.err.println("Usage: java org.apache.xmlrpc.XmlRpcClient <url> <method> <arg> ....");
            System.err.println("Arguments are sent as integers or strings.");
        }
    }
}

