/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.monitor.SOAPMonitorService;

public class SOAPMonitorHandler
extends BasicHandler {
    private static long next_message_id = 1L;

    public void invoke(MessageContext messageContext) throws AxisFault {
        Message message;
        Integer type;
        Long id;
        String target = messageContext.getTargetService();
        if (target == null) {
            target = "";
        }
        if (!messageContext.getPastPivot()) {
            id = this.assignMessageId(messageContext);
            type = new Integer(0);
            message = messageContext.getRequestMessage();
        } else {
            id = this.getMessageId(messageContext);
            type = new Integer(1);
            message = messageContext.getResponseMessage();
        }
        String soap = null;
        if (message != null) {
            soap = ((SOAPPart)message.getSOAPPart()).getAsString();
        }
        if (id != null && soap != null) {
            SOAPMonitorService.publishMessage(id, type, target, soap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Long assignMessageId(MessageContext messageContext) {
        Long id = null;
        String string = "SOAPMonitorId";
        synchronized ("SOAPMonitorId") {
            id = new Long(next_message_id);
            ++next_message_id;
            // ** MonitorExit[var3_3] (shouldn't be in output)
            messageContext.setProperty("SOAPMonitorId", id);
            return id;
        }
    }

    private Long getMessageId(MessageContext messageContext) {
        Long id = null;
        id = (Long)messageContext.getProperty("SOAPMonitorId");
        return id;
    }
}

