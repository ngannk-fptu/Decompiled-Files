/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xmlrpc.XmlRpcClientException
 *  org.apache.xmlrpc.XmlRpcClientRequest
 *  org.apache.xmlrpc.XmlRpcClientRequestProcessor
 *  org.apache.xmlrpc.XmlRpcException
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlrpc.SurrogatePairCapableXmlWriter;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcClientRequestProcessor;
import org.apache.xmlrpc.XmlRpcException;

public class SurrogatePairCapableXmlRpcClientRequestProcessor
extends XmlRpcClientRequestProcessor {
    public void encodeRequest(XmlRpcClientRequest request, String encoding, OutputStream out) throws XmlRpcClientException, IOException {
        SurrogatePairCapableXmlWriter writer = new SurrogatePairCapableXmlWriter(out, encoding);
        writer.startElement("methodCall");
        writer.startElement("methodName");
        writer.write(request.getMethodName());
        writer.endElement("methodName");
        writer.startElement("params");
        int l = request.getParameterCount();
        for (int i = 0; i < l; ++i) {
            writer.startElement("param");
            try {
                writer.writeObject(request.getParameter(i));
            }
            catch (XmlRpcException e) {
                throw new XmlRpcClientException("Failure writing request", (Throwable)e);
            }
            writer.endElement("param");
        }
        writer.endElement("params");
        writer.endElement("methodCall");
        writer.flush();
    }
}

