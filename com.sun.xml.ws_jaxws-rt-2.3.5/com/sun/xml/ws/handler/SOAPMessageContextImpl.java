/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.handler.soap.SOAPMessageContext
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageContextImpl
extends MessageUpdatableContext
implements SOAPMessageContext {
    private Set<String> roles;
    private SOAPMessage soapMsg = null;
    private WSBinding binding;

    public SOAPMessageContextImpl(WSBinding binding, Packet packet, Set<String> roles) {
        super(packet);
        this.binding = binding;
        this.roles = roles;
    }

    public SOAPMessage getMessage() {
        if (this.soapMsg == null) {
            try {
                Message m = this.packet.getMessage();
                this.soapMsg = m != null ? m.readAsSOAPMessage() : null;
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
        return this.soapMsg;
    }

    public void setMessage(SOAPMessage soapMsg) {
        try {
            this.soapMsg = soapMsg;
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    void setPacketMessage(Message newMessage) {
        if (newMessage != null) {
            this.packet.setMessage(newMessage);
            this.soapMsg = null;
        }
    }

    @Override
    protected void updateMessage() {
        if (this.soapMsg != null) {
            this.packet.setMessage(SAAJFactory.create(this.soapMsg));
            this.soapMsg = null;
        }
    }

    public Object[] getHeaders(QName header, JAXBContext jaxbContext, boolean allRoles) {
        SOAPVersion soapVersion = this.binding.getSOAPVersion();
        ArrayList beanList = new ArrayList();
        try {
            Iterator<Header> itr = this.packet.getMessage().getHeaders().getHeaders(header, false);
            if (allRoles) {
                while (itr.hasNext()) {
                    beanList.add(itr.next().readAsJAXB(jaxbContext.createUnmarshaller()));
                }
            } else {
                while (itr.hasNext()) {
                    Header soapHeader = itr.next();
                    String role = soapHeader.getRole(soapVersion);
                    if (!this.getRoles().contains(role)) continue;
                    beanList.add(soapHeader.readAsJAXB(jaxbContext.createUnmarshaller()));
                }
            }
            return beanList.toArray();
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    public Set<String> getRoles() {
        return this.roles;
    }
}

