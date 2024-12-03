/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import java.net.MalformedURLException;
import java.util.Iterator;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Call;
import org.apache.axis.utils.Messages;

public class SOAPConnectionImpl
extends SOAPConnection {
    private boolean closed = false;
    private Integer timeout = null;
    static /* synthetic */ Class class$javax$xml$soap$SOAPMessage;

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public SOAPMessage call(SOAPMessage request, Object endpoint) throws SOAPException {
        if (this.closed) {
            throw new SOAPException(Messages.getMessage("connectionClosed00"));
        }
        try {
            String soapActionURI;
            Call call = new Call(endpoint.toString());
            ((Message)request).setMessageContext(call.getMessageContext());
            Attachments attachments = ((Message)request).getAttachmentsImpl();
            if (attachments != null) {
                Iterator iterator = attachments.getAttachments().iterator();
                while (iterator.hasNext()) {
                    Object attachment = iterator.next();
                    call.addAttachmentPart(attachment);
                }
            }
            if ((soapActionURI = this.checkForSOAPActionHeader(request)) != null) {
                call.setSOAPActionURI(soapActionURI);
            }
            call.setTimeout(this.timeout);
            call.setReturnClass(class$javax$xml$soap$SOAPMessage == null ? (class$javax$xml$soap$SOAPMessage = SOAPConnectionImpl.class$("javax.xml.soap.SOAPMessage")) : class$javax$xml$soap$SOAPMessage);
            call.setProperty("call.CheckMustUnderstand", Boolean.FALSE);
            call.invoke((Message)request);
            return call.getResponseMessage();
        }
        catch (MalformedURLException mue) {
            throw new SOAPException(mue);
        }
        catch (AxisFault af) {
            throw new SOAPException(af);
        }
    }

    private String checkForSOAPActionHeader(SOAPMessage request) {
        String[] saHdrs;
        MimeHeaders hdrs = request.getMimeHeaders();
        if (hdrs != null && (saHdrs = hdrs.getHeader("SOAPAction")) != null && saHdrs.length > 0) {
            return saHdrs[0];
        }
        return null;
    }

    public void close() throws SOAPException {
        if (this.closed) {
            throw new SOAPException(Messages.getMessage("connectionClosed00"));
        }
        this.closed = true;
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

