/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import org.apache.xmlrpc.XmlRpcContext;
import org.apache.xmlrpc.XmlRpcHandlerMapping;

public class DefaultXmlRpcContext
implements XmlRpcContext {
    protected String userName;
    protected String password;
    protected XmlRpcHandlerMapping handlerMapping;

    public DefaultXmlRpcContext(String userName, String password, XmlRpcHandlerMapping handlerMapping) {
        this.userName = userName;
        this.password = password;
        this.handlerMapping = handlerMapping;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public XmlRpcHandlerMapping getHandlerMapping() {
        return this.handlerMapping;
    }
}

