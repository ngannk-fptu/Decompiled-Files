/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

public class WSDDDocumentation
extends WSDDElement {
    private String value;

    protected QName getElementName() {
        return WSDDConstants.QNAME_DOC;
    }

    public WSDDDocumentation(String value) {
        this.value = value;
    }

    public WSDDDocumentation(Element e) throws WSDDException {
        super(e);
        this.value = XMLUtils.getChildCharacterData(e);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        context.startElement(QNAME_DOC, null);
        context.writeSafeString(this.value);
        context.endElement();
    }
}

