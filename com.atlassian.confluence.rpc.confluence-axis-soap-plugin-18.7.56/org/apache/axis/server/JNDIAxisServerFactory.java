/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.axis.server;

import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import org.apache.axis.AxisFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.server.DefaultAxisServerFactory;
import org.apache.axis.utils.Messages;

public class JNDIAxisServerFactory
extends DefaultAxisServerFactory {
    public AxisServer getServer(Map environment) throws AxisFault {
        String name;
        log.debug((Object)"Enter: JNDIAxisServerFactory::getServer");
        InitialContext context = null;
        try {
            context = new InitialContext();
        }
        catch (NamingException e) {
            log.warn((Object)Messages.getMessage("jndiNotFound00"), (Throwable)e);
        }
        ServletContext servletContext = null;
        try {
            servletContext = (ServletContext)environment.get("servletContext");
        }
        catch (ClassCastException e) {
            log.warn((Object)Messages.getMessage("servletContextWrongClass00"), (Throwable)e);
        }
        AxisServer server = null;
        if (context != null && servletContext != null && (name = servletContext.getRealPath("/WEB-INF/Server")) != null) {
            try {
                server = (AxisServer)context.lookup(name);
            }
            catch (NamingException e) {
                server = super.getServer(environment);
                try {
                    context.bind(name, (Object)server);
                }
                catch (NamingException e1) {
                    // empty catch block
                }
            }
        }
        if (server == null) {
            server = super.getServer(environment);
        }
        log.debug((Object)"Exit: JNDIAxisServerFactory::getServer");
        return server;
    }
}

