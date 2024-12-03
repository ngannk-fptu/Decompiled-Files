/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.builder;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

public abstract class SOAPBuilderHelper {
    protected final SOAPFactoryEx factory;
    protected final StAXSOAPModelBuilder builder;
    protected XMLStreamReader parser;

    protected SOAPBuilderHelper(StAXSOAPModelBuilder builder, SOAPFactoryEx factory) {
        this.builder = builder;
        this.factory = factory;
    }

    public abstract OMElement handleEvent(XMLStreamReader var1, OMElement var2, int var3) throws SOAPProcessingException;
}

