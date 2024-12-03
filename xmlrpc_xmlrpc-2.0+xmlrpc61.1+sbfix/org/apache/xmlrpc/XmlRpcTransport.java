/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlrpc.XmlRpcClientException;

public interface XmlRpcTransport {
    public InputStream sendXmlRpc(byte[] var1) throws IOException, XmlRpcClientException;

    public void endClientRequest() throws XmlRpcClientException;
}

