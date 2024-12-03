/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xmlrpc.XmlRpcClient
 *  org.apache.xmlrpc.XmlRpcClientWorker
 */
package org.apache.xmlrpc;

import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.xmlrpc.SurrogatePairCapableXmlRpcClientRequestProcessor;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientWorker;

public class SurrogatePairCapableXmlRpcClient
extends XmlRpcClient {
    public SurrogatePairCapableXmlRpcClient(String baseUrl) throws MalformedURLException {
        super(baseUrl);
    }

    synchronized XmlRpcClientWorker getWorker(boolean async) throws IOException {
        XmlRpcClientWorker worker = super.getWorker(async);
        worker.requestProcessor = new SurrogatePairCapableXmlRpcClientRequestProcessor();
        return worker;
    }
}

