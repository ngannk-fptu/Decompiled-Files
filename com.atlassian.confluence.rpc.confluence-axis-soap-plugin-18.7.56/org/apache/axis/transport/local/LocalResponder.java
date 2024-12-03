/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.local;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;

public class LocalResponder
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$local$LocalResponder == null ? (class$org$apache$axis$transport$local$LocalResponder = LocalResponder.class$("org.apache.axis.transport.local.LocalResponder")) : class$org$apache$axis$transport$local$LocalResponder).getName());
    static /* synthetic */ Class class$org$apache$axis$transport$local$LocalResponder;

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: LocalResponder::invoke");
        }
        String msgStr = msgContext.getResponseMessage().getSOAPPartAsString();
        if (log.isDebugEnabled()) {
            log.debug((Object)msgStr);
            log.debug((Object)"Exit: LocalResponder::invoke");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

