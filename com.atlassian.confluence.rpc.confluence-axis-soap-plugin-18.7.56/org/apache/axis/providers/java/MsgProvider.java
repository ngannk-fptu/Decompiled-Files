/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.providers.java;

import java.lang.reflect.Method;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.i18n.Messages;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.JavaProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MsgProvider
extends JavaProvider {
    public void processMessage(MessageContext msgContext, SOAPEnvelope reqEnv, SOAPEnvelope resEnv, Object obj) throws Exception {
        MessageElement element;
        Vector bodyElements;
        OperationDesc operation = msgContext.getOperation();
        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getServiceDescription();
        QName opQName = null;
        if (operation == null && (bodyElements = reqEnv.getBodyElements()).size() > 0 && (element = (MessageElement)bodyElements.get(0)) != null) {
            opQName = new QName(element.getNamespaceURI(), element.getLocalName());
            operation = serviceDesc.getOperationByElementQName(opQName);
        }
        if (operation == null) {
            throw new AxisFault(Messages.getMessage("noOperationForQName", opQName == null ? "null" : opQName.toString()));
        }
        Method method = operation.getMethod();
        int methodType = operation.getMessageOperationStyle();
        if (methodType != 2) {
            Vector bodies = reqEnv.getBodyElements();
            Object[] argObjects = new Object[1];
            switch (methodType) {
                case 1: {
                    SOAPBodyElement[] bodyElements2 = new SOAPBodyElement[bodies.size()];
                    bodies.toArray(bodyElements2);
                    argObjects[0] = bodyElements2;
                    SOAPBodyElement[] bodyResult = (SOAPBodyElement[])method.invoke(obj, argObjects);
                    if (bodyResult != null) {
                        for (int i = 0; i < bodyResult.length; ++i) {
                            SOAPBodyElement bodyElement = bodyResult[i];
                            resEnv.addBodyElement(bodyElement);
                        }
                    }
                    return;
                }
                case 3: {
                    Element[] elements = new Element[bodies.size()];
                    for (int i = 0; i < elements.length; ++i) {
                        SOAPBodyElement body = (SOAPBodyElement)bodies.get(i);
                        elements[i] = body.getAsDOM();
                    }
                    argObjects[0] = elements;
                    Element[] elemResult = (Element[])method.invoke(obj, argObjects);
                    if (elemResult != null) {
                        for (int i = 0; i < elemResult.length; ++i) {
                            if (elemResult[i] == null) continue;
                            resEnv.addBodyElement(new SOAPBodyElement(elemResult[i]));
                        }
                    }
                    return;
                }
                case 4: {
                    Document doc = ((SOAPBodyElement)bodies.get(0)).getAsDocument();
                    argObjects[0] = doc;
                    Document resultDoc = (Document)method.invoke(obj, argObjects);
                    if (resultDoc != null) {
                        resEnv.addBodyElement(new SOAPBodyElement(resultDoc.getDocumentElement()));
                    }
                    return;
                }
            }
        } else {
            Object[] argObjects = new Object[]{reqEnv, resEnv};
            method.invoke(obj, argObjects);
            return;
        }
        throw new AxisFault(Messages.getMessage("badMsgMethodStyle"));
    }
}

