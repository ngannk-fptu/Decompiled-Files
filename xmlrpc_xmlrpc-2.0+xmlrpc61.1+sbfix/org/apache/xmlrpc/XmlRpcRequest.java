/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcServerRequest;

public class XmlRpcRequest
implements XmlRpcServerRequest,
XmlRpcClientRequest {
    protected final String methodName;
    protected final Vector parameters;

    public XmlRpcRequest(String methodName, Vector parameters) {
        this.parameters = parameters;
        this.methodName = methodName;
    }

    public int getParameterCount() {
        return this.parameters.size();
    }

    public Vector getParameters() {
        return this.parameters;
    }

    public Object getParameter(int index) {
        return this.parameters.elementAt(index);
    }

    public String getMethodName() {
        return this.methodName;
    }
}

