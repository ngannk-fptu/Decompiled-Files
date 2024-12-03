/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers.http;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class HTTPActionHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$http$HTTPActionHandler == null ? (class$org$apache$axis$handlers$http$HTTPActionHandler = HTTPActionHandler.class$("org.apache.axis.handlers.http.HTTPActionHandler")) : class$org$apache$axis$handlers$http$HTTPActionHandler).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$http$HTTPActionHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: HTTPActionHandler::invoke");
        if (msgContext.getService() == null) {
            String action = msgContext.getSOAPActionURI();
            log.debug((Object)("  HTTP SOAPAction: " + action));
            if (action == null) {
                throw new AxisFault("Server.NoHTTPSOAPAction", Messages.getMessage("noSOAPAction00"), null, null);
            }
            if ((action = action.trim()).length() > 0 && action.charAt(0) == '\"') {
                action = action.equals("\"\"") ? "" : action.substring(1, action.length() - 1);
            }
            if (action.length() > 0) {
                msgContext.setTargetService(action);
            }
        }
        log.debug((Object)"Exit: HTTPActionHandler::invoke");
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

