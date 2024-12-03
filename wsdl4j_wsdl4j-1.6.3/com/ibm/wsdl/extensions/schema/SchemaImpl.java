/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.schema;

import com.ibm.wsdl.extensions.schema.SchemaImportImpl;
import com.ibm.wsdl.extensions.schema.SchemaReferenceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class SchemaImpl
implements Schema {
    protected QName elementType = null;
    protected Boolean required = null;
    protected Element element = null;
    public static final long serialVersionUID = 1L;
    private Map imports = new HashMap();
    private List includes = new Vector();
    private List redefines = new Vector();
    private String documentBaseURI = null;

    public Map getImports() {
        return this.imports;
    }

    public SchemaImport createImport() {
        return new SchemaImportImpl();
    }

    public void addImport(SchemaImport importSchema) {
        String namespaceURI = importSchema.getNamespaceURI();
        Vector<SchemaImport> importList = (Vector<SchemaImport>)this.imports.get(namespaceURI);
        if (importList == null) {
            importList = new Vector<SchemaImport>();
            this.imports.put(namespaceURI, importList);
        }
        importList.add(importSchema);
    }

    public List getIncludes() {
        return this.includes;
    }

    public SchemaReference createInclude() {
        return new SchemaReferenceImpl();
    }

    public void addInclude(SchemaReference includeSchema) {
        this.includes.add(includeSchema);
    }

    public List getRedefines() {
        return this.redefines;
    }

    public SchemaReference createRedefine() {
        return new SchemaReferenceImpl();
    }

    public void addRedefine(SchemaReference redefineSchema) {
        this.redefines.add(redefineSchema);
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SchemaExtensibilityElement (" + this.elementType + "):");
        strBuf.append("\nrequired=" + this.required);
        if (this.element != null) {
            strBuf.append("\nelement=" + this.element);
        }
        return strBuf.toString();
    }

    public void setElementType(QName elementType) {
        this.elementType = elementType;
    }

    public QName getElementType() {
        return this.elementType;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRequired() {
        return this.required;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }

    public void setDocumentBaseURI(String documentBaseURI) {
        this.documentBaseURI = documentBaseURI;
    }

    public String getDocumentBaseURI() {
        return this.documentBaseURI;
    }
}

