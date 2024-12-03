/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class DebugHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$DebugHandler == null ? (class$org$apache$axis$handlers$DebugHandler = DebugHandler.class$("org.apache.axis.handlers.DebugHandler")) : class$org$apache$axis$handlers$DebugHandler).getName());
    public static final String NS_URI_DEBUG = "http://xml.apache.org/axis/debug";
    static /* synthetic */ Class class$org$apache$axis$handlers$DebugHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: DebugHandler::invoke");
        try {
            Message msg = msgContext.getRequestMessage();
            SOAPEnvelope message = msg.getSOAPEnvelope();
            SOAPHeaderElement header = message.getHeaderByName(NS_URI_DEBUG, "Debug");
            if (header != null) {
                Integer i = (Integer)header.getValueAsType(Constants.XSD_INT);
                if (i == null) {
                    throw new AxisFault(Messages.getMessage("cantConvert03"));
                }
                int debugVal = i;
                log.debug((Object)Messages.getMessage("debugLevel00", "" + debugVal));
                header.setProcessed(true);
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
        log.debug((Object)"Exit: DebugHandler::invoke");
    }

    public void onFault(MessageContext msgContext) {
        log.debug((Object)"Enter: DebugHandler::onFault");
        log.debug((Object)"Exit: DebugHandler::onFault");
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

