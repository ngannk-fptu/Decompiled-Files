/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlWriter;

public class XmlRpcClientRequestProcessor {
    public void encodeRequest(XmlRpcClientRequest request, String encoding, OutputStream out) throws XmlRpcClientException, IOException {
        XmlWriter writer = new XmlWriter(out, encoding);
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
                throw new XmlRpcClientException("Failure writing request", e);
            }
            writer.endElement("param");
        }
        writer.endElement("params");
        writer.endElement("methodCall");
        writer.flush();
    }

    public byte[] encodeRequestBytes(XmlRpcClientRequest request, String encoding) throws XmlRpcClientException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            this.encodeRequest(request, encoding, buffer);
            return buffer.toByteArray();
        }
        catch (IOException ioe) {
            throw new XmlRpcClientException("Error occured encoding XML-RPC request", ioe);
        }
    }

    protected boolean canReUse() {
        return true;
    }
}

