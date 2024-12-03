/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.schema;

import com.ibm.wsdl.util.xml.DOM2Writer;
import java.io.PrintWriter;
import java.io.Serializable;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;

public class SchemaSerializer
implements ExtensionSerializer,
Serializable {
    public static final long serialVersionUID = 1L;

    public void marshall(Class parentType, QName elementType, ExtensibilityElement extension, PrintWriter pw, Definition def, ExtensionRegistry extReg) throws WSDLException {
        Schema schema = (Schema)extension;
        pw.print("    ");
        DOM2Writer.serializeAsXML(schema.getElement(), def.getNamespaces(), pw);
        pw.println();
    }
}

