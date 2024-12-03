/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class FaultableHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$FaultableHandler == null ? (class$org$apache$axis$FaultableHandler = FaultableHandler.class$("org.apache.axis.FaultableHandler")) : class$org$apache$axis$FaultableHandler).getName());
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    protected Handler workHandler;
    static /* synthetic */ Class class$org$apache$axis$FaultableHandler;

    public FaultableHandler(Handler workHandler) {
        this.workHandler = workHandler;
    }

    public void init() {
        this.workHandler.init();
    }

    public void cleanup() {
        this.workHandler.cleanup();
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: FaultableHandler::invoke");
        try {
            this.workHandler.invoke(msgContext);
        }
        catch (Exception e) {
            entLog.info((Object)Messages.getMessage("toAxisFault00"), (Throwable)e);
            AxisFault fault = AxisFault.makeFault(e);
            Handler faultHandler = null;
            Hashtable options = this.getOptions();
            if (options != null) {
                Enumeration enumeration = options.keys();
                while (enumeration.hasMoreElements()) {
                    String s = (String)enumeration.nextElement();
                    if (!s.equals("fault-" + fault.getFaultCode().getLocalPart())) continue;
                    faultHandler = (Handler)options.get(s);
                }
            }
            if (faultHandler != null) {
                faultHandler.invoke(msgContext);
            }
            throw fault;
        }
        log.debug((Object)"Exit: FaultableHandler::invoke");
    }

    public void onFault(MessageContext msgContext) {
        log.debug((Object)"Enter: FaultableHandler::onFault");
        this.workHandler.onFault(msgContext);
        log.debug((Object)"Exit: FaultableHandler::onFault");
    }

    public boolean canHandleBlock(QName qname) {
        return this.workHandler.canHandleBlock(qname);
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

