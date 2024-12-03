/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.commons.logging.Log;

public class LogMessage
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$LogMessage == null ? (class$org$apache$axis$handlers$LogMessage = LogMessage.class$("org.apache.axis.handlers.LogMessage")) : class$org$apache$axis$handlers$LogMessage).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$LogMessage;

    public void invoke(MessageContext context) {
        String msg = (String)this.getOption("message");
        if (msg != null) {
            log.info((Object)msg);
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

