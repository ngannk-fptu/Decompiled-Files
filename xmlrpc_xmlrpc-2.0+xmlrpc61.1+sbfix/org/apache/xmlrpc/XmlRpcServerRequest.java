/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Vector;

public interface XmlRpcServerRequest {
    public Vector getParameters();

    public Object getParameter(int var1);

    public String getMethodName();
}

