/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.Import;

public class ImportImpl
extends AbstractWSDLElement
implements Import {
    protected String namespaceURI = null;
    protected String locationURI = null;
    protected Definition definition = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.IMPORT_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setLocationURI(String locationURI) {
        this.locationURI = locationURI;
    }

    public String getLocationURI() {
        return this.locationURI;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public Definition getDefinition() {
        return this.definition;
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Import:");
        if (this.namespaceURI != null) {
            strBuf.append("\nnamespaceURI=" + this.namespaceURI);
        }
        if (this.locationURI != null) {
            strBuf.append("\nlocationURI=" + this.locationURI);
        }
        if (this.definition != null) {
            strBuf.append("\ndefinition=" + this.definition.getDocumentBaseURI());
            strBuf.append("\ndefinition namespaceURI=" + this.definition.getTargetNamespace());
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }
}

