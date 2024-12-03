/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;

public class ErrorHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$ErrorHandler == null ? (class$org$apache$axis$handlers$ErrorHandler = ErrorHandler.class$("org.apache.axis.handlers.ErrorHandler")) : class$org$apache$axis$handlers$ErrorHandler).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$ErrorHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: ErrorHandler::invoke");
        throw new AxisFault("Server.Whatever", "ERROR", null, null);
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

