/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;

public interface SOAPFactoryEx
extends SOAPFactory,
OMFactoryEx {
    public SOAPMessage createSOAPMessage(OMXMLParserWrapper var1);

    public SOAPEnvelope createSOAPEnvelope(SOAPMessage var1, OMXMLParserWrapper var2);

    public SOAPHeader createSOAPHeader(SOAPEnvelope var1, OMXMLParserWrapper var2);

    public SOAPHeaderBlock createSOAPHeaderBlock(String var1, SOAPHeader var2, OMXMLParserWrapper var3) throws SOAPProcessingException;

    public SOAPFault createSOAPFault(SOAPBody var1, OMXMLParserWrapper var2);

    public SOAPBody createSOAPBody(SOAPEnvelope var1, OMXMLParserWrapper var2);

    public SOAPFaultCode createSOAPFaultCode(SOAPFault var1, OMXMLParserWrapper var2);

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode var1, OMXMLParserWrapper var2);

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode var1, OMXMLParserWrapper var2);

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode var1, OMXMLParserWrapper var2);

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode var1, OMXMLParserWrapper var2);

    public SOAPFaultReason createSOAPFaultReason(SOAPFault var1, OMXMLParserWrapper var2);

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason var1, OMXMLParserWrapper var2);

    public SOAPFaultNode createSOAPFaultNode(SOAPFault var1, OMXMLParserWrapper var2);

    public SOAPFaultRole createSOAPFaultRole(SOAPFault var1, OMXMLParserWrapper var2);

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault var1, OMXMLParserWrapper var2);
}

