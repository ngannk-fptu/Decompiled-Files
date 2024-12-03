/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NodeImpl;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

public class SOAPHeader
extends MessageElement
implements javax.xml.soap.SOAPHeader {
    private static Log log = LogFactory.getLog((class$org$apache$axis$message$SOAPHeader == null ? (class$org$apache$axis$message$SOAPHeader = SOAPHeader.class$("org.apache.axis.message.SOAPHeader")) : class$org$apache$axis$message$SOAPHeader).getName());
    private SOAPConstants soapConstants;
    static /* synthetic */ Class class$org$apache$axis$message$SOAPHeader;

    SOAPHeader(SOAPEnvelope env, SOAPConstants soapConsts) {
        super("Header", "soapenv", soapConsts != null ? soapConsts.getEnvelopeURI() : Constants.DEFAULT_SOAP_VERSION.getEnvelopeURI());
        this.soapConstants = soapConsts != null ? soapConsts : Constants.DEFAULT_SOAP_VERSION;
        try {
            this.setParentElement(env);
        }
        catch (SOAPException ex) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
        }
    }

    public SOAPHeader(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context, SOAPConstants soapConsts) throws AxisFault {
        super(namespace, localPart, prefix, attributes, context);
        this.soapConstants = soapConsts != null ? soapConsts : Constants.DEFAULT_SOAP_VERSION;
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

    public javax.xml.soap.SOAPHeaderElement addHeaderElement(Name name) throws SOAPException {
        SOAPHeaderElement headerElement = new SOAPHeaderElement(name);
        this.addChildElement(headerElement);
        return headerElement;
    }

    private Vector findHeaderElements(String actor) {
        ArrayList<String> actors = new ArrayList<String>();
        actors.add(actor);
        return this.getHeadersByActor(actors);
    }

    public Iterator examineHeaderElements(String actor) {
        return this.findHeaderElements(actor).iterator();
    }

    public Iterator extractHeaderElements(String actor) {
        Vector results = this.findHeaderElements(actor);
        Iterator iterator = results.iterator();
        while (iterator.hasNext()) {
            ((SOAPHeaderElement)iterator.next()).detachNode();
        }
        return results.iterator();
    }

    public Iterator examineMustUnderstandHeaderElements(String actor) {
        if (actor == null) {
            return null;
        }
        Vector result = new Vector();
        List headers = this.getChildren();
        if (headers != null) {
            for (int i = 0; i < headers.size(); ++i) {
                String candidate;
                SOAPHeaderElement she = (SOAPHeaderElement)headers.get(i);
                if (!she.getMustUnderstand() || !actor.equals(candidate = she.getActor())) continue;
                result.add(headers.get(i));
            }
        }
        return result.iterator();
    }

    public Iterator examineAllHeaderElements() {
        return this.getChildElements();
    }

    public Iterator extractAllHeaderElements() {
        Vector result = new Vector();
        List headers = this.getChildren();
        if (headers != null) {
            for (int i = 0; i < headers.size(); ++i) {
                result.add(headers.get(i));
            }
            headers.clear();
        }
        return result.iterator();
    }

    Vector getHeaders() {
        this.initializeChildren();
        return new Vector(this.getChildren());
    }

    Vector getHeadersByActor(ArrayList actors) {
        Vector<SOAPHeaderElement> results = new Vector<SOAPHeaderElement>();
        List headers = this.getChildren();
        if (headers == null) {
            return results;
        }
        Iterator i = headers.iterator();
        SOAPConstants soapVer = this.getEnvelope().getSOAPConstants();
        boolean isSOAP12 = soapVer == SOAPConstants.SOAP12_CONSTANTS;
        String nextActor = soapVer.getNextRoleURI();
        while (i.hasNext()) {
            SOAPHeaderElement header = (SOAPHeaderElement)i.next();
            String actor = header.getActor();
            if (isSOAP12 && "http://www.w3.org/2003/05/soap-envelope/role/none".equals(actor) || actor != null && !nextActor.equals(actor) && (!isSOAP12 || !"http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver".equals(actor)) && (actors == null || !actors.contains(actor))) continue;
            results.add(header);
        }
        return results;
    }

    void addHeader(SOAPHeaderElement header) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("addHeader00"));
        }
        try {
            this.addChildElement(header);
        }
        catch (SOAPException ex) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
        }
    }

    void removeHeader(SOAPHeaderElement header) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("removeHeader00"));
        }
        this.removeChild(header);
    }

    SOAPHeaderElement getHeaderByName(String namespace, String localPart, boolean accessAllHeaders) {
        MessageContext mc;
        QName name = new QName(namespace, localPart);
        SOAPHeaderElement header = (SOAPHeaderElement)this.getChildElement(name);
        if (!accessAllHeaders && (mc = MessageContext.getCurrentContext()) != null && header != null) {
            String actor = header.getActor();
            String nextActor = this.getEnvelope().getSOAPConstants().getNextRoleURI();
            if (nextActor.equals(actor)) {
                return header;
            }
            SOAPService soapService = mc.getService();
            if (soapService != null) {
                ArrayList actors = mc.getService().getActors();
                if (!(actor == null || actors != null && actors.contains(actor))) {
                    header = null;
                }
            }
        }
        return header;
    }

    Enumeration getHeadersByName(String namespace, String localPart, boolean accessAllHeaders) {
        ArrayList actors = null;
        boolean firstTime = false;
        Vector<SOAPHeaderElement> v = new Vector<SOAPHeaderElement>();
        List headers = this.getChildren();
        if (headers == null) {
            return v.elements();
        }
        Iterator e = headers.iterator();
        String nextActor = this.getEnvelope().getSOAPConstants().getNextRoleURI();
        while (e.hasNext()) {
            SOAPHeaderElement header = (SOAPHeaderElement)e.next();
            if (!header.getNamespaceURI().equals(namespace) || !header.getName().equals(localPart)) continue;
            if (!accessAllHeaders) {
                String actor;
                if (firstTime) {
                    MessageContext mc = MessageContext.getCurrentContext();
                    if (mc != null && mc.getAxisEngine() != null) {
                        actors = mc.getAxisEngine().getActorURIs();
                    }
                    firstTime = false;
                }
                if ((actor = header.getActor()) != null && !nextActor.equals(actor) && (actors == null || !actors.contains(actor))) continue;
            }
            v.addElement(header);
        }
        return v.elements();
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        List headers = this.getChildren();
        if (headers == null) {
            return;
        }
        boolean oldPretty = context.getPretty();
        context.setPretty(true);
        if (log.isDebugEnabled()) {
            log.debug((Object)(headers.size() + " " + Messages.getMessage("headers00")));
        }
        if (!headers.isEmpty()) {
            context.startElement(new QName(this.soapConstants.getEnvelopeURI(), "Header"), null);
            Iterator enumeration = headers.iterator();
            while (enumeration.hasNext()) {
                ((NodeImpl)enumeration.next()).output(context);
            }
            context.endElement();
        }
        context.setPretty(oldPretty);
    }

    public void addChild(MessageElement element) throws SOAPException {
        if (!(element instanceof SOAPHeaderElement)) {
            throw new SOAPException(Messages.getMessage("badSOAPHeader00"));
        }
        element.setEnvelope(this.getEnvelope());
        super.addChild(element);
    }

    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPHeaderElement)) {
            throw new SOAPException(Messages.getMessage("badSOAPHeader00"));
        }
        SOAPElement child = super.addChildElement(element);
        this.setDirty(true);
        return child;
    }

    public SOAPElement addChildElement(Name name) throws SOAPException {
        SOAPHeaderElement child = new SOAPHeaderElement(name);
        this.addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        SOAPHeaderElement child = new SOAPHeaderElement(this.getNamespaceURI(), localName);
        this.addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
        SOAPHeaderElement child = new SOAPHeaderElement(this.getNamespaceURI(prefix), localName);
        child.setPrefix(prefix);
        this.addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
        SOAPHeaderElement child = new SOAPHeaderElement(uri, localName);
        child.setPrefix(prefix);
        child.addNamespaceDeclaration(prefix, uri);
        this.addChildElement(child);
        return child;
    }

    public Node appendChild(Node newChild) throws DOMException {
        SOAPHeaderElement headerElement = null;
        headerElement = newChild instanceof SOAPHeaderElement ? (SOAPHeaderElement)newChild : new SOAPHeaderElement((Element)newChild);
        try {
            this.addChildElement(headerElement);
        }
        catch (SOAPException e) {
            throw new DOMException(11, e.toString());
        }
        return headerElement;
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

