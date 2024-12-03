/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public interface ValidationContext {
    public String getXmlVersion();

    public QName getCurrentElementName();

    public String getNamespaceURI(String var1);

    public int getAttributeCount();

    public String getAttributeLocalName(int var1);

    public String getAttributeNamespace(int var1);

    public String getAttributePrefix(int var1);

    public String getAttributeValue(int var1);

    public String getAttributeValue(String var1, String var2);

    public String getAttributeType(int var1);

    public int findAttributeIndex(String var1, String var2);

    public boolean isNotationDeclared(String var1);

    public boolean isUnparsedEntityDeclared(String var1);

    public String getBaseUri();

    public Location getValidationLocation();

    public void reportProblem(XMLValidationProblem var1) throws XMLStreamException;

    public int addDefaultAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;
}

