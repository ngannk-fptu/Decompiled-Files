/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultImpl
extends SOAPElement
implements SOAPFault,
OMConstants {
    protected Exception e;

    protected SOAPFaultImpl(OMNamespace ns, SOAPFactory factory) {
        super("Fault", ns, factory);
    }

    public SOAPFaultImpl(SOAPBody parent, Exception e, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Fault", true, factory);
        this.setException(e);
    }

    public void setException(Exception e) {
        this.e = e;
        this.putExceptionToSOAPFault(e);
    }

    public SOAPFaultImpl(SOAPBody parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Fault", true, factory);
    }

    public SOAPFaultImpl(SOAPBody parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, "Fault", builder, factory);
    }

    protected abstract SOAPFaultDetail getNewSOAPFaultDetail(SOAPFault var1) throws SOAPProcessingException;

    public void setCode(SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        this.setNewElement(this.getCode(), soapFaultCode);
    }

    public void setReason(SOAPFaultReason reason) throws SOAPProcessingException {
        this.setNewElement(this.getReason(), reason);
    }

    public void setNode(SOAPFaultNode node) throws SOAPProcessingException {
        this.setNewElement(this.getNode(), node);
    }

    public void setRole(SOAPFaultRole role) throws SOAPProcessingException {
        this.setNewElement(this.getRole(), role);
    }

    public void setDetail(SOAPFaultDetail detail) throws SOAPProcessingException {
        this.setNewElement(this.getDetail(), detail);
    }

    public Exception getException() throws OMException {
        SOAPFaultDetail detail = this.getDetail();
        if (detail == null) {
            return null;
        }
        OMElement exceptionElement = this.getDetail().getFirstChildWithName(new QName("Exception"));
        if (exceptionElement != null && exceptionElement.getText() != null) {
            return new Exception(exceptionElement.getText());
        }
        return null;
    }

    protected void putExceptionToSOAPFault(Exception e) throws SOAPProcessingException {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        sw.flush();
        SOAPFaultDetail detail = this.getDetail();
        if (detail == null) {
            detail = this.getNewSOAPFaultDetail(this);
            this.setDetail(detail);
        }
        OMElementImpl faultDetailEnty = new OMElementImpl(detail, "Exception", null, null, this.factory, true);
        faultDetailEnty.setText(sw.getBuffer().toString());
    }

    protected void setNewElement(OMElement myElement, OMElement newElement) {
        if (myElement != null) {
            myElement.discard();
        }
        if (newElement != null && newElement.getParent() != null) {
            newElement.discard();
        }
        this.addChild(newElement);
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        SOAPFaultDetail faultDetail;
        SOAPFaultReason faultReason;
        this.registerContentHandler(writer);
        this.build();
        OMSerializerUtil.serializeStartpart(this, writer);
        SOAPFaultCode faultCode = this.getCode();
        if (faultCode != null) {
            ((OMNodeEx)((Object)faultCode)).internalSerialize(writer, true);
        }
        if ((faultReason = this.getReason()) != null) {
            ((OMNodeEx)((Object)faultReason)).internalSerialize(writer, true);
        }
        this.serializeFaultNode(writer);
        SOAPFaultRole faultRole = this.getRole();
        if (faultRole != null && faultRole.getText() != null && !"".equals(faultRole.getText())) {
            ((OMNodeEx)((Object)faultRole)).internalSerialize(writer, true);
        }
        if ((faultDetail = this.getDetail()) != null) {
            ((OMNodeEx)((Object)faultDetail)).internalSerialize(writer, true);
        }
        OMSerializerUtil.serializeEndpart(writer);
    }

    protected abstract void serializeFaultNode(XMLStreamWriter var1) throws XMLStreamException;

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return this.e == null ? ((SOAPFactory)this.factory).createSOAPFault((SOAPBody)targetParent) : ((SOAPFactory)this.factory).createSOAPFault((SOAPBody)targetParent, this.e);
    }
}

