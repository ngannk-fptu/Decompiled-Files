/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

public interface XmlRpcClientRequest {
    public String getMethodName();

    public int getParameterCount();

    public Object getParameter(int var1);
}

