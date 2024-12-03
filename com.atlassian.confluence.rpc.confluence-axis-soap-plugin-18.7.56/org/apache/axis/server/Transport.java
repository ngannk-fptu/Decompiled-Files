/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.server;

import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class Transport
extends SimpleTargetedChain {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$server$Transport == null ? (class$org$apache$axis$server$Transport = Transport.class$("org.apache.axis.server.Transport")) : class$org$apache$axis$server$Transport).getName());
    static /* synthetic */ Class class$org$apache$axis$server$Transport;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

