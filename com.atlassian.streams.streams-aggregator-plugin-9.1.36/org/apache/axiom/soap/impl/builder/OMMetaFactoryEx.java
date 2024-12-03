/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPMessage;

public interface OMMetaFactoryEx
extends OMMetaFactory {
    public SOAPMessage createSOAPMessage(OMXMLParserWrapper var1);
}

