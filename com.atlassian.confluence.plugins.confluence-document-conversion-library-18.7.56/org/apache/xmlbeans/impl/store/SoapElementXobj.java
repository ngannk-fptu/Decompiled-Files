/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.Node;
import org.apache.xmlbeans.impl.soap.SOAPElement;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.ElementXobj;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapElementXobj
extends ElementXobj
implements SOAPElement,
Node {
    SoapElementXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapElementXobj(l, this._name);
    }

    @Override
    public void detachNode() {
        DomImpl._soapNode_detachNode(this);
    }

    @Override
    public void recycleNode() {
        DomImpl._soapNode_recycleNode(this);
    }

    @Override
    public String getValue() {
        return DomImpl._soapNode_getValue(this);
    }

    @Override
    public void setValue(String value) {
        DomImpl._soapNode_setValue(this, value);
    }

    @Override
    public SOAPElement getParentElement() {
        return DomImpl._soapNode_getParentElement(this);
    }

    @Override
    public void setParentElement(SOAPElement p) {
        DomImpl._soapNode_setParentElement(this, p);
    }

    @Override
    public void removeContents() {
        DomImpl._soapElement_removeContents(this);
    }

    @Override
    public String getEncodingStyle() {
        return DomImpl._soapElement_getEncodingStyle(this);
    }

    @Override
    public void setEncodingStyle(String encodingStyle) {
        DomImpl._soapElement_setEncodingStyle(this, encodingStyle);
    }

    @Override
    public boolean removeNamespaceDeclaration(String prefix) {
        return DomImpl._soapElement_removeNamespaceDeclaration(this, prefix);
    }

    @Override
    public Iterator<Name> getAllAttributes() {
        return DomImpl._soapElement_getAllAttributes(this);
    }

    @Override
    public Iterator<SOAPElement> getChildElements() {
        return DomImpl._soapElement_getChildElements(this);
    }

    @Override
    public Iterator<String> getNamespacePrefixes() {
        return DomImpl._soapElement_getNamespacePrefixes(this);
    }

    @Override
    public SOAPElement addAttribute(Name name, String value) throws SOAPException {
        return DomImpl._soapElement_addAttribute(this, name, value);
    }

    @Override
    public SOAPElement addChildElement(SOAPElement oldChild) throws SOAPException {
        return DomImpl._soapElement_addChildElement((DomImpl.Dom)this, oldChild);
    }

    @Override
    public SOAPElement addChildElement(Name name) throws SOAPException {
        return DomImpl._soapElement_addChildElement((DomImpl.Dom)this, name);
    }

    @Override
    public SOAPElement addChildElement(String localName) throws SOAPException {
        return DomImpl._soapElement_addChildElement((DomImpl.Dom)this, localName);
    }

    @Override
    public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
        return DomImpl._soapElement_addChildElement(this, localName, prefix);
    }

    @Override
    public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
        return DomImpl._soapElement_addChildElement(this, localName, prefix, uri);
    }

    @Override
    public SOAPElement addNamespaceDeclaration(String prefix, String uri) {
        return DomImpl._soapElement_addNamespaceDeclaration(this, prefix, uri);
    }

    @Override
    public SOAPElement addTextNode(String data) {
        return DomImpl._soapElement_addTextNode(this, data);
    }

    @Override
    public String getAttributeValue(Name name) {
        return DomImpl._soapElement_getAttributeValue(this, name);
    }

    @Override
    public Iterator<SOAPElement> getChildElements(Name name) {
        return DomImpl._soapElement_getChildElements(this, name);
    }

    @Override
    public Name getElementName() {
        return DomImpl._soapElement_getElementName(this);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return DomImpl._soapElement_getNamespaceURI(this, prefix);
    }

    @Override
    public Iterator<String> getVisibleNamespacePrefixes() {
        return DomImpl._soapElement_getVisibleNamespacePrefixes(this);
    }

    @Override
    public boolean removeAttribute(Name name) {
        return DomImpl._soapElement_removeAttribute(this, name);
    }
}

