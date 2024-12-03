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
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;

public class URLMapper
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$http$URLMapper == null ? (class$org$apache$axis$handlers$http$URLMapper = URLMapper.class$("org.apache.axis.handlers.http.URLMapper")) : class$org$apache$axis$handlers$http$URLMapper).getName());
    static /* synthetic */ Class class$org$apache$axis$handlers$http$URLMapper;

    public void invoke(MessageContext msgContext) throws AxisFault {
        String path;
        log.debug((Object)"Enter: URLMapper::invoke");
        if (msgContext.getService() == null && (path = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO)) != null && path.length() >= 1) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            msgContext.setTargetService(path);
        }
        log.debug((Object)"Exit: URLMapper::invoke");
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        this.invoke(msgContext);
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

