/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Vector;
import org.apache.xmlrpc.AuthenticatedXmlRpcHandler;
import org.apache.xmlrpc.XmlRpcException;

public class AuthDemo
implements AuthenticatedXmlRpcHandler {
    public Object execute(String method, Vector v, String user, String password) throws Exception {
        if (user == null || user.startsWith("bad")) {
            throw new XmlRpcException(5, "Sorry, you're not allowed in here!");
        }
        return "Hello " + user;
    }
}

