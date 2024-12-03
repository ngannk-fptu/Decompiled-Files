/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class SOAPBody
extends MessageElement
implements javax.xml.soap.SOAPBody {
    private static Log log = LogFactory.getLog((class$org$apache$axis$message$SOAPBody == null ? (class$org$apache$axis$message$SOAPBody = SOAPBody.class$("org.apache.axis.message.SOAPBody")) : class$org$apache$axis$message$SOAPBody).getName());
    private SOAPConstants soapConstants;
    private boolean disableFormatting = false;
    private boolean doSAAJEncodingCompliance = false;
    private static ArrayList knownEncodingStyles = new ArrayList();
    static /* synthetic */ Class class$org$apache$axis$message$SOAPBody;

    SOAPBody(SOAPEnvelope env, SOAPConstants soapConsts) {
        super(soapConsts.getEnvelopeURI(), "Body");
        this.soapConstants = soapConsts;
        try {
            this.setParentElement(env);
        }
        catch (SOAPException ex) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
        }
    }

    public SOAPBody(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context, SOAPConstants soapConsts) throws AxisFault {
        super(namespace, localPart, prefix, attributes, context);
        this.soapConstants = soapConsts;
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if (parent == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
        }
        try {
            SOAPEnvelope env = (SOAPEnvelope)parent;
            super.setParentElement(env);
            this.setEnvelope(env);
        }
        catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void disableFormatting() {
        this.disableFormatting = true;
    }

    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        if (encodingStyle == null) {
            encodingStyle = "";
        }
        if (this.doSAAJEncodingCompliance && !knownEncodingStyles.contains(encodingStyle)) {
            throw new IllegalArgumentException(Messages.getMessage("badEncodingStyle1", encodingStyle));
        }
        super.setEncodingStyle(encodingStyle);
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        boolean oldPretty = context.getPretty();
        if (!this.disableFormatting) {
            context.setPretty(true);
        } else {
            context.setPretty(false);
        }
        List bodyElements = this.getChildren();
        if (bodyElements == null || bodyElements.isEmpty()) {
            // empty if block
        }
        context.startElement(new QName(this.soapConstants.getEnvelopeURI(), "Body"), this.getAttributesEx());
        if (bodyElements != null) {
            Iterator e = bodyElements.iterator();
            while (e.hasNext()) {
                MessageElement body = (MessageElement)e.next();
                body.output(context);
            }
        }
        context.outputMultiRefs();
        context.endElement();
        context.setPretty(oldPretty);
    }

    Vector getBodyElements() throws AxisFault {
        this.initializeChildren();
        return new Vector(this.getChildren());
    }

    SOAPBodyElement getFirstBody() throws AxisFault {
        if (!this.hasChildNodes()) {
            return null;
        }
        return (SOAPBodyElement)this.getChildren().get(0);
    }

    void addBodyElement(SOAPBodyElement element) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("addBody00"));
        }
        try {
            this.addChildElement(element);
        }
        catch (SOAPException ex) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
        }
    }

    void removeBodyElement(SOAPBodyElement element) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("removeBody00"));
        }
        this.removeChild(element);
    }

    void clearBody() {
        this.removeContents();
    }

    SOAPBodyElement getBodyByName(String namespace, String localPart) throws AxisFault {
        QName name = new QName(namespace, localPart);
        return (SOAPBodyElement)this.getChildElement(name);
    }

    public javax.xml.soap.SOAPBodyElement addBodyElement(Name name) throws SOAPException {
        SOAPBodyElement bodyElement = new SOAPBodyElement(name);
        this.addChildElement(bodyElement);
        return bodyElement;
    }

    public javax.xml.soap.SOAPFault addFault(Name name, String s, Locale locale) throws SOAPException {
        AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        this.addChildElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPFault addFault(Name name, String s) throws SOAPException {
        AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        this.addChildElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPBodyElement addDocument(Document document) throws SOAPException {
        SOAPBodyElement bodyElement = new SOAPBodyElement(document.getDocumentElement());
        this.addChildElement(bodyElement);
        return bodyElement;
    }

    public javax.xml.soap.SOAPFault addFault() throws SOAPException {
        AxisFault af = new AxisFault(new QName("http://xml.apache.org/axis/", "Server.generalException"), "", "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        this.addChildElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPFault getFault() {
        List bodyElements = this.getChildren();
        if (bodyElements != null) {
            Iterator e = bodyElements.iterator();
            while (e.hasNext()) {
                Object element = e.next();
                if (!(element instanceof javax.xml.soap.SOAPFault)) continue;
                return (javax.xml.soap.SOAPFault)element;
            }
        }
        return null;
    }

    public boolean hasFault() {
        return this.getFault() != null;
    }

    public void addChild(MessageElement element) throws SOAPException {
        element.setEnvelope(this.getEnvelope());
        super.addChild(element);
    }

    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
        SOAPElement child = super.addChildElement(element);
        this.setDirty(true);
        return child;
    }

    public SOAPElement addChildElement(Name name) throws SOAPException {
        SOAPBodyElement child = new SOAPBodyElement(name);
        this.addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        SOAPBodyElement child = new SOAPBodyElement(this.getNamespaceURI(), localName);
        this.addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
        SOAPBodyElement child = new SOAPBodyElement(this.getNamespaceURI(prefix), localName);
        child.setPrefix(prefix);
        this.addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
        SOAPBodyElement child = new SOAPBodyElement(uri, localName);
        child.setPrefix(prefix);
        child.addNamespaceDeclaration(prefix, uri);
        this.addChildElement(child);
        return child;
    }

    public void setSAAJEncodingCompliance(boolean comply) {
        this.doSAAJEncodingCompliance = true;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        knownEncodingStyles.add("http://schemas.xmlsoap.org/soap/encoding/");
        knownEncodingStyles.add("http://www.w3.org/2003/05/soap-encoding");
        knownEncodingStyles.add("");
        knownEncodingStyles.add("http://www.w3.org/2003/05/soap-envelope/encoding/none");
    }
}

