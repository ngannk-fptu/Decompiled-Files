/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlWriter;

public class XmlRpcResponseProcessor {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] encodeResponse(Object responseParam, String encoding) throws IOException, UnsupportedEncodingException, XmlRpcException {
        long now = 0L;
        if (XmlRpc.debug) {
            now = System.currentTimeMillis();
        }
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            XmlWriter writer = new XmlWriter((OutputStream)buffer, encoding);
            this.writeResponse(responseParam, writer);
            writer.flush();
            byte[] byArray = buffer.toByteArray();
            Object var9_7 = null;
            if (XmlRpc.debug) {
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis encoding response");
            }
            return byArray;
        }
        catch (Throwable throwable) {
            block4: {
                Object var9_8 = null;
                if (!XmlRpc.debug) break block4;
                System.out.println("Spent " + (System.currentTimeMillis() - now) + " millis encoding response");
            }
            throw throwable;
        }
    }

    public byte[] encodeException(Exception x, String encoding, int code) {
        if (XmlRpc.debug) {
            x.printStackTrace();
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        XmlWriter writer = null;
        try {
            writer = new XmlWriter((OutputStream)buffer, encoding);
        }
        catch (UnsupportedEncodingException encx) {
            System.err.println("XmlRpcServer attempted to use unsupported encoding: " + encx);
        }
        catch (IOException iox) {
            System.err.println("XmlRpcServer experienced I/O error writing error response: " + iox);
        }
        String message = x.toString();
        try {
            this.writeError(code, message, writer);
            writer.flush();
        }
        catch (Exception e) {
            System.err.println("Unable to send error response to client: " + e);
        }
        return writer != null ? buffer.toByteArray() : EMPTY_BYTE_ARRAY;
    }

    public byte[] encodeException(Exception x, String encoding) {
        return this.encodeException(x, encoding, x instanceof XmlRpcException ? ((XmlRpcException)x).code : 0);
    }

    void writeResponse(Object param, XmlWriter writer) throws XmlRpcException, IOException {
        writer.startElement("methodResponse");
        writer.startElement("params");
        writer.startElement("param");
        writer.writeObject(param);
        writer.endElement("param");
        writer.endElement("params");
        writer.endElement("methodResponse");
    }

    void writeError(int code, String message, XmlWriter writer) throws XmlRpcException, IOException {
        Hashtable<String, Object> h = new Hashtable<String, Object>();
        h.put("faultCode", new Integer(code));
        h.put("faultString", message);
        writer.startElement("methodResponse");
        writer.startElement("fault");
        writer.writeObject(h);
        writer.endElement("fault");
        writer.endElement("methodResponse");
    }
}

