/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.builder;

import java.util.Vector;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.exception.OMBuilderException;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.SOAPBuilderHelper;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

public class SOAP12BuilderHelper
extends SOAPBuilderHelper {
    private boolean codePresent = false;
    private boolean reasonPresent = false;
    private boolean nodePresent = false;
    private boolean rolePresent = false;
    private boolean detailPresent = false;
    private boolean subcodeValuePresent = false;
    private boolean subSubcodePresent = false;
    private boolean valuePresent = false;
    private boolean subcodePresent = false;
    private boolean codeprocessing = false;
    private boolean subCodeProcessing = false;
    private boolean reasonProcessing = false;
    private boolean processingDetailElements = false;
    private Vector detailElementNames;

    public SOAP12BuilderHelper(StAXSOAPModelBuilder builder, SOAPFactoryEx factory) {
        super(builder, factory);
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public OMElement handleEvent(XMLStreamReader parser, OMElement parent, int elementLevel) throws SOAPProcessingException {
        void var4_18;
        this.parser = parser;
        Object var4_4 = null;
        if (elementLevel == 4) {
            if (parser.getLocalName().equals("Code")) {
                if (this.codePresent) {
                    throw new OMBuilderException("Multiple Code element encountered");
                }
                SOAPFaultCode sOAPFaultCode = this.factory.createSOAPFaultCode((SOAPFault)parent, this.builder);
                this.codePresent = true;
                this.codeprocessing = true;
                return var4_18;
            }
            if (parser.getLocalName().equals("Reason")) {
                if (!this.codeprocessing && !this.subCodeProcessing) {
                    if (!this.codePresent) throw new OMBuilderException("Wrong element order encountred at " + parser.getLocalName());
                    if (this.reasonPresent) {
                        throw new OMBuilderException("Multiple Reason Element encountered");
                    }
                    SOAPFaultReason sOAPFaultReason = this.factory.createSOAPFaultReason((SOAPFault)parent, this.builder);
                    this.reasonPresent = true;
                    this.reasonProcessing = true;
                    return var4_18;
                }
                if (!this.codeprocessing) throw new OMBuilderException("A subcode doesn't have a Value");
                throw new OMBuilderException("Code doesn't have a value");
            }
            if (parser.getLocalName().equals("Node")) {
                if (this.reasonProcessing) throw new OMBuilderException("Reason element Should have a text");
                if (!this.reasonPresent) throw new OMBuilderException("wrong element order encountered at " + parser.getLocalName());
                if (this.rolePresent) throw new OMBuilderException("wrong element order encountered at " + parser.getLocalName());
                if (this.detailPresent) throw new OMBuilderException("wrong element order encountered at " + parser.getLocalName());
                if (this.nodePresent) {
                    throw new OMBuilderException("Multiple Node element encountered");
                }
                SOAPFaultNode sOAPFaultNode = this.factory.createSOAPFaultNode((SOAPFault)parent, this.builder);
                this.nodePresent = true;
                return var4_18;
            }
            if (parser.getLocalName().equals("Role")) {
                if (this.reasonProcessing) throw new OMBuilderException("Reason element should have a text");
                if (!this.reasonPresent) throw new OMBuilderException("Wrong element order encountered at " + parser.getLocalName());
                if (this.detailPresent) throw new OMBuilderException("Wrong element order encountered at " + parser.getLocalName());
                if (this.rolePresent) {
                    throw new OMBuilderException("Multiple Role element encountered");
                }
                SOAPFaultRole sOAPFaultRole = this.factory.createSOAPFaultRole((SOAPFault)parent, this.builder);
                this.rolePresent = true;
                return var4_18;
            }
            if (!parser.getLocalName().equals("Detail")) throw new OMBuilderException(parser.getLocalName() + " unsupported element in SOAPFault element");
            if (this.reasonProcessing) throw new OMBuilderException("Reason element should have a text");
            if (!this.reasonPresent) throw new OMBuilderException("wrong element order encountered at " + parser.getLocalName());
            if (this.detailPresent) {
                throw new OMBuilderException("Multiple detail element encountered");
            }
            SOAPFaultDetail sOAPFaultDetail = this.factory.createSOAPFaultDetail((SOAPFault)parent, this.builder);
            this.detailPresent = true;
            return var4_18;
        }
        if (elementLevel == 5) {
            if (parent.getLocalName().equals("Code")) {
                if (parser.getLocalName().equals("Value")) {
                    if (this.valuePresent) throw new OMBuilderException("Multiple value Encountered in code element");
                    SOAPFaultValue sOAPFaultValue = this.factory.createSOAPFaultValue((SOAPFaultCode)parent, (OMXMLParserWrapper)this.builder);
                    this.valuePresent = true;
                    this.codeprocessing = false;
                    return var4_18;
                }
                if (!parser.getLocalName().equals("Subcode")) throw new OMBuilderException(parser.getLocalName() + " is not supported inside the code element");
                if (this.subcodePresent) throw new OMBuilderException("multiple subcode Encountered in code element");
                if (!this.valuePresent) throw new OMBuilderException("Value should present before the subcode");
                SOAPFaultSubCode sOAPFaultSubCode = this.factory.createSOAPFaultSubCode((SOAPFaultCode)parent, (OMXMLParserWrapper)this.builder);
                this.subcodePresent = true;
                this.subCodeProcessing = true;
                return var4_18;
            }
            if (parent.getLocalName().equals("Reason")) {
                if (!parser.getLocalName().equals("Text")) throw new OMBuilderException(parser.getLocalName() + " is not supported inside the reason");
                SOAPFaultText sOAPFaultText = this.factory.createSOAPFaultText((SOAPFaultReason)parent, this.builder);
                ((OMNodeEx)((Object)sOAPFaultText)).setComplete(false);
                this.reasonProcessing = false;
                return var4_18;
            }
            if (!parent.getLocalName().equals("Detail")) throw new OMBuilderException(parent.getLocalName() + " should not have child element");
            OMElement oMElement = this.factory.createOMElement(parser.getLocalName(), parent, this.builder);
            this.processingDetailElements = true;
            this.detailElementNames = new Vector();
            this.detailElementNames.add(parser.getLocalName());
            return var4_18;
        }
        if (elementLevel <= 5) return var4_18;
        if (parent.getLocalName().equals("Subcode")) {
            if (!parser.getLocalName().equals("Value")) {
                if (!parser.getLocalName().equals("Subcode")) throw new OMBuilderException(parser.getLocalName() + " is not supported inside the subCode element");
                if (!this.subcodeValuePresent) throw new OMBuilderException("Value should present before the subcode");
                if (this.subSubcodePresent) throw new OMBuilderException("multiple subcode encountered");
                SOAPFaultSubCode sOAPFaultSubCode = this.factory.createSOAPFaultSubCode((SOAPFaultSubCode)parent, (OMXMLParserWrapper)this.builder);
                this.subcodeValuePresent = false;
                this.subSubcodePresent = true;
                this.subCodeProcessing = true;
                return var4_18;
            }
            if (this.subcodeValuePresent) {
                throw new OMBuilderException("multiple subCode value encountered");
            }
            SOAPFaultValue sOAPFaultValue = this.factory.createSOAPFaultValue((SOAPFaultSubCode)parent, (OMXMLParserWrapper)this.builder);
            this.subcodeValuePresent = true;
            this.subSubcodePresent = false;
            this.subCodeProcessing = false;
            return var4_18;
        }
        if (!this.processingDetailElements) throw new OMBuilderException(parent.getLocalName() + " should not have child at element level " + elementLevel);
        int detailElementLevel = 0;
        boolean localNameExist = false;
        int i = 0;
        while (true) {
            if (i >= this.detailElementNames.size()) {
                if (!localNameExist) return var4_18;
                this.detailElementNames.setSize(detailElementLevel);
                OMElement oMElement = this.factory.createOMElement(parser.getLocalName(), parent, this.builder);
                this.detailElementNames.add(parser.getLocalName());
                return var4_18;
            }
            if (parent.getLocalName().equals(this.detailElementNames.get(i))) {
                localNameExist = true;
                detailElementLevel = i + 1;
            }
            ++i;
        }
    }
}

