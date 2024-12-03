/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public interface SOAPElement
extends Node,
Element {
    public SOAPElement addChildElement(Name var1) throws SOAPException;

    public SOAPElement addChildElement(QName var1) throws SOAPException;

    public SOAPElement addChildElement(String var1) throws SOAPException;

    public SOAPElement addChildElement(String var1, String var2) throws SOAPException;

    public SOAPElement addChildElement(String var1, String var2, String var3) throws SOAPException;

    public SOAPElement addChildElement(SOAPElement var1) throws SOAPException;

    public void removeContents();

    public SOAPElement addTextNode(String var1) throws SOAPException;

    public SOAPElement addAttribute(Name var1, String var2) throws SOAPException;

    public SOAPElement addAttribute(QName var1, String var2) throws SOAPException;

    public SOAPElement addNamespaceDeclaration(String var1, String var2) throws SOAPException;

    public String getAttributeValue(Name var1);

    public String getAttributeValue(QName var1);

    public Iterator<Name> getAllAttributes();

    public Iterator<QName> getAllAttributesAsQNames();

    public String getNamespaceURI(String var1);

    public Iterator<String> getNamespacePrefixes();

    public Iterator<String> getVisibleNamespacePrefixes();

    public QName createQName(String var1, String var2) throws SOAPException;

    public Name getElementName();

    public QName getElementQName();

    public SOAPElement setElementQName(QName var1) throws SOAPException;

    public boolean removeAttribute(Name var1);

    public boolean removeAttribute(QName var1);

    public boolean removeNamespaceDeclaration(String var1);

    public Iterator<Node> getChildElements();

    public Iterator<Node> getChildElements(Name var1);

    public Iterator<Node> getChildElements(QName var1);

    public void setEncodingStyle(String var1) throws SOAPException;

    public String getEncodingStyle();
}

