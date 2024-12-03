/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xmlrpc.XmlRpc
 *  org.apache.xmlrpc.XmlRpcException
 *  org.apache.xmlrpc.XmlRpcResponseProcessor
 *  org.apache.xmlrpc.XmlWriter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xmlrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import org.apache.xmlrpc.SurrogatePairCapableXmlWriter;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcResponseProcessor;
import org.apache.xmlrpc.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurrogatePairCapableXmlRpcResponseProcessor
extends XmlRpcResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(SurrogatePairCapableXmlRpcResponseProcessor.class);
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
            SurrogatePairCapableXmlWriter writer = new SurrogatePairCapableXmlWriter(buffer, encoding);
            this.writeResponse(responseParam, writer);
            writer.flush();
            byte[] byArray = buffer.toByteArray();
            return byArray;
        }
        finally {
            if (XmlRpc.debug) {
                log.debug("Spent {} millis encoding response", (Object)(System.currentTimeMillis() - now));
            }
        }
    }

    public byte[] encodeException(Exception x, String encoding, int code) {
        if (XmlRpc.debug) {
            log.debug("", (Throwable)x);
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SurrogatePairCapableXmlWriter writer = null;
        try {
            writer = new SurrogatePairCapableXmlWriter(buffer, encoding);
        }
        catch (UnsupportedEncodingException encx) {
            log.error("XmlRpcServer attempted to use unsupported encoding", (Throwable)encx);
        }
        String message = x.toString();
        try {
            this.writeError(code, message, writer);
            writer.flush();
        }
        catch (Exception e) {
            log.error("Unable to send error response to client", (Throwable)e);
        }
        return writer != null ? buffer.toByteArray() : EMPTY_BYTE_ARRAY;
    }

    public byte[] encodeException(Exception x, String encoding) {
        return this.encodeException(x, encoding, x instanceof XmlRpcException ? ((XmlRpcException)((Object)x)).code : 0);
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

