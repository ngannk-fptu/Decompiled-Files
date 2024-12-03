/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public interface SOAPElement
extends Node,
Element {
    public SOAPElement addChildElement(Name var1) throws SOAPException;

    public SOAPElement addChildElement(String var1) throws SOAPException;

    public SOAPElement addChildElement(String var1, String var2) throws SOAPException;

    public SOAPElement addChildElement(String var1, String var2, String var3) throws SOAPException;

    public SOAPElement addChildElement(SOAPElement var1) throws SOAPException;

    public SOAPElement addTextNode(String var1) throws SOAPException;

    public SOAPElement addAttribute(Name var1, String var2) throws SOAPException;

    public SOAPElement addNamespaceDeclaration(String var1, String var2) throws SOAPException;

    public String getAttributeValue(Name var1);

    public Iterator getAllAttributes();

    public String getNamespaceURI(String var1);

    public Iterator getNamespacePrefixes();

    public Name getElementName();

    public boolean removeAttribute(Name var1);

    public boolean removeNamespaceDeclaration(String var1);

    public Iterator getChildElements();

    public Iterator getChildElements(Name var1);

    public void setEncodingStyle(String var1) throws SOAPException;

    public String getEncodingStyle();

    public void removeContents();

    public Iterator getVisibleNamespacePrefixes();
}

