/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.secure;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.secure.SecurityTool;

public class SecureXmlRpcClient
extends XmlRpcClient {
    static /* synthetic */ Class class$org$apache$xmlrpc$secure$SecureXmlRpcClient;

    public SecureXmlRpcClient(URL url) {
        super(url);
    }

    public SecureXmlRpcClient(String url) throws MalformedURLException {
        super(url);
    }

    public SecureXmlRpcClient(String hostname, int port) throws MalformedURLException {
        super("https://" + hostname + ':' + port + "/RPC2");
    }

    public void setup() throws Exception {
        SecurityTool.setup();
    }

    public static void main(String[] args) throws Exception {
        try {
            String url = args[0];
            String method = args[1];
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
            SecureXmlRpcClient client = new SecureXmlRpcClient(url);
            try {
                System.err.println(client.execute(method, v));
            }
            catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
        catch (Exception x) {
            System.err.println(x);
            System.err.println("Usage: java " + (class$org$apache$xmlrpc$secure$SecureXmlRpcClient == null ? (class$org$apache$xmlrpc$secure$SecureXmlRpcClient = SecureXmlRpcClient.class$("org.apache.xmlrpc.secure.SecureXmlRpcClient")) : class$org$apache$xmlrpc$secure$SecureXmlRpcClient).getName() + " <url> <method> [args]");
            System.err.println("Arguments are sent as integers or strings.");
        }
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

