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
import org.apache.axis.encoding.Base64;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class HTTPAuthHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$http$HTTPAuthHandler == null ? (class$org$apache$axis$handlers$http$HTTPAuthHandler = HTTPAuthHandler.class$("org.apache.axis.handlers.http.HTTPAuthHandler")) : class$org$apache$axis$handlers$http$HTTPAuthHandler).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$http$HTTPAuthHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: HTTPAuthHandler::invoke");
        String tmp = (String)msgContext.getProperty("Authorization");
        if (tmp != null) {
            tmp = tmp.trim();
        }
        if (tmp != null && tmp.startsWith("Basic ")) {
            String user = null;
            int i = (tmp = new String(Base64.decode(tmp.substring(6)))).indexOf(58);
            user = i == -1 ? tmp : tmp.substring(0, i);
            msgContext.setUsername(user);
            log.debug((Object)Messages.getMessage("httpUser00", user));
            if (i != -1) {
                String pwd = tmp.substring(i + 1);
                if (pwd != null && pwd.equals("")) {
                    pwd = null;
                }
                if (pwd != null) {
                    msgContext.setPassword(pwd);
                    log.debug((Object)Messages.getMessage("httpPassword00", pwd));
                }
            }
        }
        log.debug((Object)"Exit: HTTPAuthHandler::invoke");
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

