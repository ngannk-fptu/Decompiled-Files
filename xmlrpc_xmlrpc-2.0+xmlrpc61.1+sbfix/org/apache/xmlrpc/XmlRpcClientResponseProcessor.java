/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.InputStream;
import java.util.Hashtable;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcException;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;

public class XmlRpcClientResponseProcessor
extends XmlRpc {
    protected Object result;
    protected boolean fault;

    public Object decodeResponse(InputStream is) throws XmlRpcClientException {
        this.result = null;
        this.fault = false;
        try {
            this.parse(is);
            if (this.fault) {
                return this.decodeException(this.result);
            }
            return this.result;
        }
        catch (Exception x) {
            throw new XmlRpcClientException("Error decoding XML-RPC response", x);
        }
    }

    protected XmlRpcException decodeException(Object result) throws XmlRpcClientException {
        try {
            Hashtable exceptionData = (Hashtable)result;
            return new XmlRpcException(Integer.parseInt(exceptionData.get("faultCode").toString()), (String)exceptionData.get("faultString"));
        }
        catch (Exception x) {
            throw new XmlRpcClientException("Error decoding XML-RPC exception response", x);
        }
    }

    protected void objectParsed(Object what) {
        this.result = what;
    }

    public void startElement(String name, AttributeList atts) throws SAXException {
        if ("fault".equals(name)) {
            this.fault = true;
        } else {
            super.startElement(name, atts);
        }
    }

    protected boolean canReUse() {
        this.result = null;
        this.fault = false;
        return true;
    }
}

