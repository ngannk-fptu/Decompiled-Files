/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import org.apache.xmlrpc.XmlRpcHandlerMapping;

public interface XmlRpcContext {
    public String getUserName();

    public String getPassword();

    public XmlRpcHandlerMapping getHandlerMapping();
}

