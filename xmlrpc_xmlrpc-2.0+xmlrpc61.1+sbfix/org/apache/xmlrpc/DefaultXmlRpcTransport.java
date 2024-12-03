/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.util.HttpUtil;

public class DefaultXmlRpcTransport
implements XmlRpcTransport {
    protected URL url;
    protected String auth;
    protected URLConnection con;

    public DefaultXmlRpcTransport(URL url, String auth) {
        this.url = url;
        this.auth = auth;
    }

    public DefaultXmlRpcTransport(URL url) {
        this(url, null);
    }

    public InputStream sendXmlRpc(byte[] request) throws IOException {
        this.con = this.url.openConnection();
        this.con.setDoInput(true);
        this.con.setDoOutput(true);
        this.con.setUseCaches(false);
        this.con.setAllowUserInteraction(false);
        this.con.setRequestProperty("Content-Length", Integer.toString(request.length));
        this.con.setRequestProperty("Content-Type", "text/xml");
        if (this.auth != null) {
            this.con.setRequestProperty("Authorization", "Basic " + this.auth);
        }
        OutputStream out = this.con.getOutputStream();
        out.write(request);
        out.flush();
        out.close();
        return this.con.getInputStream();
    }

    public void setBasicAuthentication(String user, String password) {
        this.auth = HttpUtil.encodeBasicAuthentication(user, password);
    }

    public void endClientRequest() throws XmlRpcClientException {
        try {
            this.con.getInputStream().close();
        }
        catch (Exception e) {
            throw new XmlRpcClientException("Exception closing URLConnection", e);
        }
    }
}

