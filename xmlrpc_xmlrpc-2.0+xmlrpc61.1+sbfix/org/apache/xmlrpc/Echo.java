/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.util.Vector;
import org.apache.xmlrpc.XmlRpcHandler;

public class Echo
implements XmlRpcHandler {
    public Object execute(String method, Vector parameters) throws Exception {
        return parameters;
    }
}

