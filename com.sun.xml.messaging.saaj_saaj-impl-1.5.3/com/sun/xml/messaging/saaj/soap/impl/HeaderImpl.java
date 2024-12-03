/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.Node
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;

public abstract class HeaderImpl
extends ElementImpl
implements SOAPHeader {
    protected static final boolean MUST_UNDERSTAND_ONLY = false;

    protected HeaderImpl(SOAPDocumentImpl ownerDoc, NameImpl name) {
        super(ownerDoc, name);
    }

    public HeaderImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    protected abstract SOAPHeaderElement createHeaderElement(Name var1) throws SOAPException;

    protected abstract SOAPHeaderElement createHeaderElement(QName var1) throws SOAPException;

    protected abstract NameImpl getNotUnderstoodName();

    protected abstract NameImpl getUpgradeName();

    protected abstract NameImpl getSupportedEnvelopeName();

    public SOAPHeaderElement addHeaderElement(Name name) throws SOAPException {
        String uri;
        SOAPElement newHeaderElement = ElementFactory.createNamedElement(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name.getLocalName(), name.getPrefix(), name.getURI());
        if (newHeaderElement == null || !(newHeaderElement instanceof SOAPHeaderElement)) {
            newHeaderElement = this.createHeaderElement(name);
        }
        if ((uri = newHeaderElement.getElementQName().getNamespaceURI()) == null || "".equals(uri)) {
            log.severe("SAAJ0131.impl.header.elems.ns.qualified");
            throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
        }
        this.addNode((org.w3c.dom.Node)newHeaderElement);
        return (SOAPHeaderElement)newHeaderElement;
    }

    public SOAPHeaderElement addHeaderElement(QName name) throws SOAPException {
        String uri;
        SOAPElement newHeaderElement = ElementFactory.createNamedElement(((SOAPDocument)((Object)this.getOwnerDocument())).getDocument(), name.getLocalPart(), name.getPrefix(), name.getNamespaceURI());
        if (newHeaderElement == null || !(newHeaderElement instanceof SOAPHeaderElement)) {
            newHeaderElement = this.createHeaderElement(name);
        }
        if ((uri = newHeaderElement.getElementQName().getNamespaceURI()) == null || "".equals(uri)) {
            log.severe("SAAJ0131.impl.header.elems.ns.qualified");
            throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
        }
        this.addNode((org.w3c.dom.Node)newHeaderElement);
        return (SOAPHeaderElement)newHeaderElement;
    }

    @Override
    protected SOAPElement addElement(Name name) throws SOAPException {
        return this.addHeaderElement(name);
    }

    @Override
    protected SOAPElement addElement(QName name) throws SOAPException {
        return this.addHeaderElement(name);
    }

    public Iterator<SOAPHeaderElement> examineHeaderElements(String actor) {
        return this.getHeaderElementsForActor(actor, false, false);
    }

    public Iterator<SOAPHeaderElement> extractHeaderElements(String actor) {
        return this.getHeaderElementsForActor(actor, true, false);
    }

    protected Iterator<SOAPHeaderElement> getHeaderElementsForActor(String actor, boolean detach, boolean mustUnderstand) {
        if (actor == null || actor.equals("")) {
            log.severe("SAAJ0132.impl.invalid.value.for.actor.or.role");
            throw new IllegalArgumentException("Invalid value for actor or role");
        }
        return this.getHeaderElements(actor, detach, mustUnderstand);
    }

    protected Iterator<SOAPHeaderElement> getHeaderElements(String actor, boolean detach, boolean mustUnderstand) {
        ArrayList<HeaderElementImpl> elementList = new ArrayList<HeaderElementImpl>();
        Iterator<Node> eachChild = this.getChildElements();
        org.w3c.dom.Node currentChild = (org.w3c.dom.Node)this.iterate(eachChild);
        while (currentChild != null) {
            if (!(currentChild instanceof SOAPHeaderElement)) {
                currentChild = (org.w3c.dom.Node)this.iterate(eachChild);
                continue;
            }
            HeaderElementImpl currentElement = (HeaderElementImpl)((Object)currentChild);
            currentChild = (org.w3c.dom.Node)this.iterate(eachChild);
            boolean isMustUnderstandMatching = !mustUnderstand || currentElement.getMustUnderstand();
            boolean doAdd = false;
            if (actor == null && isMustUnderstandMatching) {
                doAdd = true;
            } else {
                String currentActor = currentElement.getActorOrRole();
                if (currentActor == null) {
                    currentActor = "";
                }
                if (currentActor.equalsIgnoreCase(actor) && isMustUnderstandMatching) {
                    doAdd = true;
                }
            }
            if (!doAdd) continue;
            elementList.add(currentElement);
            if (!detach) continue;
            currentElement.detachNode();
        }
        return elementList.listIterator();
    }

    private <T> T iterate(Iterator<T> each) {
        return each.hasNext() ? (T)each.next() : null;
    }

    @Override
    public void setParentElement(SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPEnvelope)) {
            log.severe("SAAJ0133.impl.header.parent.mustbe.envelope");
            throw new SOAPException("Parent of SOAPHeader has to be a SOAPEnvelope");
        }
        super.setParentElement(element);
    }

    @Override
    public SOAPElement addChildElement(String localName) throws SOAPException {
        SOAPElement element = super.addChildElement(localName);
        String uri = element.getElementName().getURI();
        if (uri == null || "".equals(uri)) {
            log.severe("SAAJ0134.impl.header.elems.ns.qualified");
            throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
        }
        return element;
    }

    public Iterator<SOAPHeaderElement> examineAllHeaderElements() {
        return this.getHeaderElements(null, false, false);
    }

    public Iterator<SOAPHeaderElement> examineMustUnderstandHeaderElements(String actor) {
        return this.getHeaderElements(actor, false, true);
    }

    public Iterator<SOAPHeaderElement> extractAllHeaderElements() {
        return this.getHeaderElements(null, true, false);
    }

    public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSoapUris) throws SOAPException {
        if (supportedSoapUris == null) {
            log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
            throw new SOAPException("Argument cannot be null; iterator of supportedURIs cannot be null");
        }
        if (!supportedSoapUris.hasNext()) {
            log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
            throw new SOAPException("List of supported URIs cannot be empty");
        }
        NameImpl upgradeName = this.getUpgradeName();
        SOAPHeaderElement upgradeHeaderElement = (SOAPHeaderElement)this.addChildElement(upgradeName);
        NameImpl supportedEnvelopeName = this.getSupportedEnvelopeName();
        int i = 0;
        while (supportedSoapUris.hasNext()) {
            SOAPElement subElement = upgradeHeaderElement.addChildElement((Name)supportedEnvelopeName);
            String ns = "ns" + Integer.toString(i);
            subElement.addAttribute((Name)NameImpl.createFromUnqualifiedName("qname"), ns + ":Envelope");
            subElement.addNamespaceDeclaration(ns, (String)supportedSoapUris.next());
            ++i;
        }
        return upgradeHeaderElement;
    }

    public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri) throws SOAPException {
        return this.addUpgradeHeaderElement(new String[]{supportedSoapUri});
    }

    public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) throws SOAPException {
        if (supportedSoapUris == null) {
            log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
            throw new SOAPException("Argument cannot be null; array of supportedURIs cannot be null");
        }
        if (supportedSoapUris.length == 0) {
            log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
            throw new SOAPException("List of supported URIs cannot be empty");
        }
        NameImpl upgradeName = this.getUpgradeName();
        SOAPHeaderElement upgradeHeaderElement = (SOAPHeaderElement)this.addChildElement(upgradeName);
        NameImpl supportedEnvelopeName = this.getSupportedEnvelopeName();
        for (int i = 0; i < supportedSoapUris.length; ++i) {
            SOAPElement subElement = upgradeHeaderElement.addChildElement((Name)supportedEnvelopeName);
            String ns = "ns" + Integer.toString(i);
            subElement.addAttribute((Name)NameImpl.createFromUnqualifiedName("qname"), ns + ":Envelope");
            subElement.addNamespaceDeclaration(ns, supportedSoapUris[i]);
        }
        return upgradeHeaderElement;
    }

    @Override
    protected SOAPElement convertToSoapElement(Element element) {
        SOAPHeaderElement headerElement;
        org.w3c.dom.Node soapNode = this.getSoapDocument().findIfPresent(element);
        if (soapNode instanceof SOAPHeaderElement) {
            return (SOAPElement)soapNode;
        }
        try {
            headerElement = this.createHeaderElement(NameImpl.copyElementName(element));
        }
        catch (SOAPException e) {
            throw new ClassCastException("Could not convert Element to SOAPHeaderElement: " + e.getMessage());
        }
        return this.replaceElementWithSOAPElement(element, (ElementImpl)headerElement);
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }
}

