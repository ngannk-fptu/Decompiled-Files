/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.transport.MultiPointSender
 *  org.apache.catalina.tribes.transport.ReplicationTransmitter
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.transport.ReplicationTransmitter;

public class SenderSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aSender, StoreDescription parentDesc) throws Exception {
        ReplicationTransmitter transmitter;
        MultiPointSender transport;
        if (aSender instanceof ReplicationTransmitter && (transport = (transmitter = (ReplicationTransmitter)aSender).getTransport()) != null) {
            this.storeElement(aWriter, indent, transport);
        }
    }
}

