/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xmlrpc.XmlRpcHandlerMapping
 *  org.apache.xmlrpc.XmlRpcServer
 *  org.apache.xmlrpc.XmlRpcWorker
 */
package org.apache.xmlrpc;

import org.apache.xmlrpc.SurrogatePairCapableXmlRpcResponseProcessor;
import org.apache.xmlrpc.XmlRpcHandlerMapping;
import org.apache.xmlrpc.XmlRpcServer;
import org.apache.xmlrpc.XmlRpcWorker;

public class SurrogatePairCapableXmlRpcServer
extends XmlRpcServer {
    protected XmlRpcWorker createWorker() {
        return new SurrogatePairCapableXmlRpcWorker(this.getHandlerMapping());
    }

    private static class SurrogatePairCapableXmlRpcWorker
    extends XmlRpcWorker {
        public SurrogatePairCapableXmlRpcWorker(XmlRpcHandlerMapping handlerMapping) {
            super(handlerMapping);
            this.responseProcessor = new SurrogatePairCapableXmlRpcResponseProcessor();
        }
    }
}

