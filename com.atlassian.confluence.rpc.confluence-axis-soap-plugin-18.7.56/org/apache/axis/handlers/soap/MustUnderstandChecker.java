/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers.soap;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class MustUnderstandChecker
extends BasicHandler {
    private static Log log = LogFactory.getLog((class$org$apache$axis$handlers$soap$MustUnderstandChecker == null ? (class$org$apache$axis$handlers$soap$MustUnderstandChecker = MustUnderstandChecker.class$("org.apache.axis.handlers.soap.MustUnderstandChecker")) : class$org$apache$axis$handlers$soap$MustUnderstandChecker).getName());
    private SOAPService service = null;
    static /* synthetic */ Class class$org$apache$axis$handlers$soap$MustUnderstandChecker;

    public MustUnderstandChecker(SOAPService service) {
        this.service = service;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        Message msg;
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("semanticCheck00"));
        }
        if ((msg = msgContext.getCurrentMessage()) == null) {
            return;
        }
        SOAPEnvelope env = msg.getSOAPEnvelope();
        Vector headers = null;
        if (this.service != null) {
            ArrayList acts = this.service.getActors();
            headers = env.getHeadersByActor(acts);
        } else {
            headers = env.getHeaders();
        }
        Vector<SOAPHeaderElement> misunderstoodHeaders = null;
        Enumeration enumeration = headers.elements();
        while (enumeration.hasMoreElements()) {
            OperationDesc oper;
            SOAPHeaderElement header = (SOAPHeaderElement)enumeration.nextElement();
            if (msgContext != null && msgContext.getOperation() != null && (oper = msgContext.getOperation()).getParamByQName(header.getQName()) != null || !header.getMustUnderstand() || header.isProcessed()) continue;
            if (misunderstoodHeaders == null) {
                misunderstoodHeaders = new Vector<SOAPHeaderElement>();
            }
            misunderstoodHeaders.addElement(header);
        }
        SOAPConstants soapConstants = msgContext.getSOAPConstants();
        if (misunderstoodHeaders != null) {
            AxisFault fault = new AxisFault(soapConstants.getMustunderstandFaultQName(), null, null, null, null, null);
            StringBuffer whatWasMissUnderstood = new StringBuffer(256);
            if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                enumeration = misunderstoodHeaders.elements();
                while (enumeration.hasMoreElements()) {
                    SOAPHeaderElement badHeader = (SOAPHeaderElement)enumeration.nextElement();
                    QName badQName = new QName(badHeader.getNamespaceURI(), badHeader.getName());
                    if (whatWasMissUnderstood.length() != 0) {
                        whatWasMissUnderstood.append(", ");
                    }
                    whatWasMissUnderstood.append(badQName.toString());
                    SOAPHeaderElement newHeader = new SOAPHeaderElement("http://www.w3.org/2003/05/soap-envelope", "NotUnderstood");
                    newHeader.addAttribute(null, "qname", badQName);
                    fault.addHeader(newHeader);
                }
            }
            fault.setFaultString(Messages.getMessage("noUnderstand00", whatWasMissUnderstood.toString()));
            throw fault;
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

