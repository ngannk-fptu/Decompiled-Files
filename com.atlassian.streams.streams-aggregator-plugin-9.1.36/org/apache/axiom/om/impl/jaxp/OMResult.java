/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.jaxp;

import javax.xml.transform.sax.SAXResult;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.SAXOMBuilder;

public class OMResult
extends SAXResult {
    private final SAXOMBuilder builder;

    public OMResult(OMFactory omFactory) {
        this.builder = new SAXOMBuilder(omFactory);
        this.setHandler(this.builder);
    }

    public OMResult() {
        this(OMAbstractFactory.getOMFactory());
    }

    public OMDocument getDocument() {
        return this.builder.getDocument();
    }

    public OMElement getRootElement() {
        return this.builder.getRootElement();
    }
}

