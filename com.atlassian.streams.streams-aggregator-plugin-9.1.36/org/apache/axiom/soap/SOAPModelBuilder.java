/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPEnvelope;

public interface SOAPModelBuilder
extends OMXMLParserWrapper {
    public SOAPEnvelope getSOAPEnvelope();
}

