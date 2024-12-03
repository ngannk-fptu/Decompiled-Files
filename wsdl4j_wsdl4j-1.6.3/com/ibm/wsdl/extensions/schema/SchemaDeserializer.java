/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.schema;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.util.xml.DOMUtils;
import com.ibm.wsdl.util.xml.QNameUtils;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SchemaDeserializer
implements ExtensionDeserializer,
Serializable {
    public static final long serialVersionUID = 1L;
    private final Map allReferencedSchemas = new Hashtable();
    private static ThreadLocal wsdlLocator = new ThreadLocal();

    public static void setLocator(WSDLLocator loc) {
        wsdlLocator.set(loc);
    }

    public ExtensibilityElement unmarshall(Class parentType, QName elementType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        Schema schema = (Schema)extReg.createExtension(parentType, elementType);
        schema.setElementType(elementType);
        schema.setElement(el);
        schema.setDocumentBaseURI(def.getDocumentBaseURI());
        Element tempEl = DOMUtils.getFirstChildElement(el);
        while (tempEl != null) {
            QName tempElType = QNameUtils.newQName(tempEl);
            SchemaReference sr = null;
            String locationURI = null;
            if (SchemaConstants.XSD_IMPORT_QNAME_LIST.contains(tempElType)) {
                SchemaImport im = schema.createImport();
                im.setId(DOMUtils.getAttribute(tempEl, "id"));
                im.setNamespaceURI(DOMUtils.getAttribute(tempEl, "namespace"));
                locationURI = DOMUtils.getAttribute(tempEl, "schemaLocation");
                im.setSchemaLocationURI(locationURI);
                schema.addImport(im);
            } else if (SchemaConstants.XSD_INCLUDE_QNAME_LIST.contains(tempElType)) {
                sr = schema.createInclude();
                sr.setId(DOMUtils.getAttribute(tempEl, "id"));
                locationURI = DOMUtils.getAttribute(tempEl, "schemaLocation");
                sr.setSchemaLocationURI(locationURI);
                schema.addInclude(sr);
            } else if (SchemaConstants.XSD_REDEFINE_QNAME_LIST.contains(tempElType)) {
                sr = schema.createRedefine();
                sr.setId(DOMUtils.getAttribute(tempEl, "id"));
                locationURI = DOMUtils.getAttribute(tempEl, "schemaLocation");
                sr.setSchemaLocationURI(locationURI);
                schema.addRedefine(sr);
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        return schema;
    }
}

