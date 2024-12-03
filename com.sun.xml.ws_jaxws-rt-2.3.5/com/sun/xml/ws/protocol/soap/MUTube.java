/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.SOAPBinding
 *  javax.xml.ws.soap.SOAPFaultException
 */
package com.sun.xml.ws.protocol.soap;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.message.DOMHeader;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

abstract class MUTube
extends AbstractFilterTubeImpl {
    private static final String MU_FAULT_DETAIL_LOCALPART = "NotUnderstood";
    private static final QName MU_HEADER_DETAIL = new QName(SOAPVersion.SOAP_12.nsUri, "NotUnderstood");
    protected static final Logger logger = Logger.getLogger("com.sun.xml.ws.soap.decoder");
    private static final String MUST_UNDERSTAND_FAULT_MESSAGE_STRING = "One or more mandatory SOAP header blocks not understood";
    protected final SOAPVersion soapVersion;
    protected SOAPBindingImpl binding;

    protected MUTube(WSBinding binding, Tube next) {
        super(next);
        if (!(binding instanceof SOAPBinding)) {
            throw new WebServiceException("MUPipe should n't be used for bindings other than SOAP.");
        }
        this.binding = (SOAPBindingImpl)binding;
        this.soapVersion = binding.getSOAPVersion();
    }

    protected MUTube(MUTube that, TubeCloner cloner) {
        super(that, cloner);
        this.binding = that.binding;
        this.soapVersion = that.soapVersion;
    }

    public final Set<QName> getMisUnderstoodHeaders(MessageHeaders headers, Set<String> roles, Set<QName> handlerKnownHeaders) {
        return headers.getNotUnderstoodHeaders(roles, handlerKnownHeaders, this.binding);
    }

    final SOAPFaultException createMUSOAPFaultException(Set<QName> notUnderstoodHeaders) {
        try {
            SOAPFault fault = this.soapVersion.getSOAPFactory().createFault(MUST_UNDERSTAND_FAULT_MESSAGE_STRING, this.soapVersion.faultCodeMustUnderstand);
            fault.setFaultString("MustUnderstand headers:" + notUnderstoodHeaders + " are not understood");
            return new SOAPFaultException(fault);
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    final Message createMUSOAPFaultMessage(Set<QName> notUnderstoodHeaders) {
        try {
            String faultString = MUST_UNDERSTAND_FAULT_MESSAGE_STRING;
            if (this.soapVersion == SOAPVersion.SOAP_11) {
                faultString = "MustUnderstand headers:" + notUnderstoodHeaders + " are not understood";
            }
            Message muFaultMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, faultString, this.soapVersion.faultCodeMustUnderstand);
            if (this.soapVersion == SOAPVersion.SOAP_12) {
                MUTube.addHeader(muFaultMessage, notUnderstoodHeaders);
            }
            return muFaultMessage;
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static void addHeader(Message m, Set<QName> notUnderstoodHeaders) throws SOAPException {
        for (QName qname : notUnderstoodHeaders) {
            SOAPElement soapEl = SOAPVersion.SOAP_12.getSOAPFactory().createElement(MU_HEADER_DETAIL);
            soapEl.addNamespaceDeclaration("abc", qname.getNamespaceURI());
            soapEl.setAttribute("qname", "abc:" + qname.getLocalPart());
            DOMHeader<SOAPElement> header = new DOMHeader<SOAPElement>(soapEl);
            m.getHeaders().add(header);
        }
    }
}

