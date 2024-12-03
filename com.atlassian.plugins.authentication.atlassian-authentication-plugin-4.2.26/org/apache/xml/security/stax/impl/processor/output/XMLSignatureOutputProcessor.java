/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.processor.output;

import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.processor.output.AbstractSignatureOutputProcessor;
import org.apache.xml.security.stax.impl.processor.output.XMLSignatureEndingOutputProcessor;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLSignatureOutputProcessor
extends AbstractSignatureOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger(XMLSignatureOutputProcessor.class);

    @Override
    public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        super.init(outputProcessorChain);
        XMLSignatureEndingOutputProcessor signatureEndingOutputProcessor = new XMLSignatureEndingOutputProcessor(this);
        signatureEndingOutputProcessor.setXMLSecurityProperties(this.getSecurityProperties());
        signatureEndingOutputProcessor.setAction(this.getAction(), this.getActionOrder());
        signatureEndingOutputProcessor.init(outputProcessorChain);
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (xmlSecEvent.getEventType() == 1) {
            SecurePart securePart;
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
            if (this.getActiveInternalSignatureOutputProcessor() == null && (securePart = this.securePartMatches(xmlSecStartElement, outputProcessorChain, "signatureParts")) != null) {
                LOG.debug("Matched securePart for signature");
                AbstractSignatureOutputProcessor.InternalSignatureOutputProcessor internalSignatureOutputProcessor = null;
                SignaturePartDef signaturePartDef = new SignaturePartDef();
                signaturePartDef.setSecurePart(securePart);
                signaturePartDef.setTransforms(securePart.getTransforms());
                if (signaturePartDef.getTransforms() == null) {
                    signaturePartDef.setTransforms(new String[]{"http://www.w3.org/2001/10/xml-exc-c14n#"});
                }
                signaturePartDef.setExcludeVisibleC14Nprefixes(true);
                signaturePartDef.setDigestAlgo(securePart.getDigestMethod());
                if (signaturePartDef.getDigestAlgo() == null) {
                    signaturePartDef.setDigestAlgo(this.getSecurityProperties().getSignatureDigestAlgorithm());
                }
                if (this.securityProperties.isSignatureGenerateIds()) {
                    if (securePart.getIdToSecure() == null) {
                        signaturePartDef.setGenerateXPointer(securePart.isGenerateXPointer());
                        signaturePartDef.setSigRefId(IDGenerator.generateID(null));
                        Attribute attribute = xmlSecStartElement.getAttributeByName(this.securityProperties.getIdAttributeNS());
                        if (attribute != null) {
                            signaturePartDef.setSigRefId(attribute.getValue());
                        } else {
                            ArrayList<XMLSecAttribute> attributeList = new ArrayList<XMLSecAttribute>(1);
                            attributeList.add(this.createAttribute(this.securityProperties.getIdAttributeNS(), signaturePartDef.getSigRefId()));
                            xmlSecEvent = this.addAttributes(xmlSecStartElement, attributeList);
                        }
                    } else {
                        signaturePartDef.setSigRefId(securePart.getIdToSecure());
                    }
                }
                this.getSignaturePartDefList().add(signaturePartDef);
                internalSignatureOutputProcessor = new AbstractSignatureOutputProcessor.InternalSignatureOutputProcessor(signaturePartDef, xmlSecStartElement);
                internalSignatureOutputProcessor.setXMLSecurityProperties(this.getSecurityProperties());
                internalSignatureOutputProcessor.setAction(this.getAction(), this.getActionOrder());
                internalSignatureOutputProcessor.addAfterProcessor(XMLSignatureOutputProcessor.class);
                internalSignatureOutputProcessor.addBeforeProcessor(XMLSignatureEndingOutputProcessor.class);
                internalSignatureOutputProcessor.init(outputProcessorChain);
                this.setActiveInternalSignatureOutputProcessor(internalSignatureOutputProcessor);
            }
        }
        outputProcessorChain.processEvent(xmlSecEvent);
    }
}

