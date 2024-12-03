/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.java;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Scope;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.commons.logging.Log;

public class JavaSender
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$java$JavaSender == null ? (class$org$apache$axis$transport$java$JavaSender = JavaSender.class$("org.apache.axis.transport.java.JavaSender")) : class$org$apache$axis$transport$java$JavaSender).getName());
    static /* synthetic */ Class class$org$apache$axis$transport$java$JavaSender;

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JavaSender::invoke");
        }
        SOAPService service = null;
        SOAPService saveService = msgContext.getService();
        OperationDesc saveOp = msgContext.getOperation();
        Call call = (Call)msgContext.getProperty("call_object");
        String url = call.getTargetEndpointAddress();
        String cls = url.substring(5);
        msgContext.setService(null);
        msgContext.setOperation(null);
        service = msgContext.getProperty("isMsg") == null ? new SOAPService(new RPCProvider()) : new SOAPService(new MsgProvider());
        if (cls.startsWith("//")) {
            cls = cls.substring(2);
        }
        service.setOption("className", cls);
        service.setEngine(msgContext.getAxisEngine());
        service.setOption("allowedMethods", "*");
        service.setOption("scope", Scope.DEFAULT.getName());
        service.getInitializedServiceDesc(msgContext);
        service.init();
        msgContext.setService(service);
        service.invoke(msgContext);
        msgContext.setService(saveService);
        msgContext.setOperation(saveOp);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JavaSender::invoke");
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

