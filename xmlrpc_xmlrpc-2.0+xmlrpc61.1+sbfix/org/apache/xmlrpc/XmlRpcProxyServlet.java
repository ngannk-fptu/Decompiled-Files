/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClientLite;
import org.apache.xmlrpc.XmlRpcServer;

public class XmlRpcProxyServlet
extends HttpServlet {
    private XmlRpcServer xmlrpc;

    public void init(ServletConfig config) throws ServletException {
        if ("true".equalsIgnoreCase(config.getInitParameter("debug"))) {
            XmlRpc.setDebug(true);
        }
        String url = config.getInitParameter("url");
        this.xmlrpc = new XmlRpcServer();
        try {
            this.xmlrpc.addHandler("$default", new XmlRpcClientLite(url));
        }
        catch (Exception x) {
            throw new ServletException("Invalid URL: " + url + " (" + x.toString() + ")");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        byte[] result = this.xmlrpc.execute((InputStream)req.getInputStream());
        res.setContentType("text/xml");
        res.setContentLength(result.length);
        ServletOutputStream output = res.getOutputStream();
        output.write(result);
        output.flush();
    }
}

