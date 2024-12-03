/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.PolicyConstants;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelUnmarshaller;
import com.sun.xml.ws.policy.sourcemodel.PolicyReferenceData;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XmlPolicyModelUnmarshaller
extends PolicyModelUnmarshaller {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelUnmarshaller.class);

    protected XmlPolicyModelUnmarshaller() {
    }

    @Override
    public PolicySourceModel unmarshalModel(Object storage) throws PolicyException {
        XMLEventReader reader = this.createXMLEventReader(storage);
        PolicySourceModel model = null;
        block7: while (reader.hasNext()) {
            try {
                XMLEvent event = reader.peek();
                switch (event.getEventType()) {
                    case 5: 
                    case 7: {
                        reader.nextEvent();
                        break;
                    }
                    case 4: {
                        this.processCharacters(ModelNode.Type.POLICY, event.asCharacters(), null);
                        reader.nextEvent();
                        break;
                    }
                    case 1: {
                        if (NamespaceVersion.resolveAsToken(event.asStartElement().getName()) == XmlToken.Policy) {
                            StartElement rootElement = reader.nextEvent().asStartElement();
                            model = this.initializeNewModel(rootElement);
                            this.unmarshalNodeContent(model.getNamespaceVersion(), model.getRootNode(), rootElement.getName(), reader);
                            break block7;
                        }
                        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
                    }
                    default: {
                        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
                    }
                }
            }
            catch (XMLStreamException e) {
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), e));
            }
        }
        return model;
    }

    protected PolicySourceModel createSourceModel(NamespaceVersion nsVersion, String id, String name) {
        return PolicySourceModel.createPolicySourceModel(nsVersion, id, name);
    }

    private PolicySourceModel initializeNewModel(StartElement element) throws PolicyException, XMLStreamException {
        NamespaceVersion nsVersion = NamespaceVersion.resolveVersion(element.getName().getNamespaceURI());
        Attribute policyName = this.getAttributeByName(element, nsVersion.asQName(XmlToken.Name));
        Attribute xmlId = this.getAttributeByName(element, PolicyConstants.XML_ID);
        Attribute policyId = this.getAttributeByName(element, PolicyConstants.WSU_ID);
        if (policyId == null) {
            policyId = xmlId;
        } else if (xmlId != null) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED()));
        }
        PolicySourceModel model = this.createSourceModel(nsVersion, policyId == null ? null : policyId.getValue(), policyName == null ? null : policyName.getValue());
        return model;
    }

    private ModelNode addNewChildNode(NamespaceVersion nsVersion, ModelNode parentNode, StartElement childElement) throws PolicyException {
        ModelNode childNode;
        QName childElementName = childElement.getName();
        if (parentNode.getType() == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            childNode = parentNode.createChildAssertionParameterNode();
        } else {
            XmlToken token = NamespaceVersion.resolveAsToken(childElementName);
            switch (token) {
                case Policy: {
                    childNode = parentNode.createChildPolicyNode();
                    break;
                }
                case All: {
                    childNode = parentNode.createChildAllNode();
                    break;
                }
                case ExactlyOne: {
                    childNode = parentNode.createChildExactlyOneNode();
                    break;
                }
                case PolicyReference: {
                    Attribute uri = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.Uri));
                    if (uri == null) {
                        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND()));
                    }
                    try {
                        PolicyReferenceData refData;
                        URI reference = new URI(uri.getValue());
                        Attribute digest = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.Digest));
                        if (digest == null) {
                            refData = new PolicyReferenceData(reference);
                        } else {
                            Attribute digestAlgorithm = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.DigestAlgorithm));
                            URI algorithmRef = null;
                            if (digestAlgorithm != null) {
                                algorithmRef = new URI(digestAlgorithm.getValue());
                            }
                            refData = new PolicyReferenceData(reference, digest.getValue(), algorithmRef);
                        }
                        childNode = parentNode.createChildPolicyReferenceNode(refData);
                        break;
                    }
                    catch (URISyntaxException e) {
                        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI(), e));
                    }
                }
                default: {
                    childNode = parentNode.isDomainSpecific() ? parentNode.createChildAssertionParameterNode() : parentNode.createChildAssertionNode();
                }
            }
        }
        return childNode;
    }

    private void parseAssertionData(NamespaceVersion nsVersion, String value, ModelNode childNode, StartElement childElement) throws IllegalArgumentException, PolicyException {
        String visibilityValue;
        HashMap<QName, String> attributeMap = new HashMap<QName, String>();
        boolean optional = false;
        boolean ignorable = false;
        Iterator<Attribute> iterator = childElement.getAttributes();
        while (iterator.hasNext()) {
            Attribute nextAttribute = iterator.next();
            QName name = nextAttribute.getName();
            if (attributeMap.containsKey(name)) {
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(nextAttribute.getName(), childElement.getName())));
            }
            if (nsVersion.asQName(XmlToken.Optional).equals(name)) {
                optional = this.parseBooleanValue(nextAttribute.getValue());
                continue;
            }
            if (nsVersion.asQName(XmlToken.Ignorable).equals(name)) {
                ignorable = this.parseBooleanValue(nextAttribute.getValue());
                continue;
            }
            attributeMap.put(name, nextAttribute.getValue());
        }
        AssertionData nodeData = new AssertionData(childElement.getName(), value, attributeMap, childNode.getType(), optional, ignorable);
        if (nodeData.containsAttribute(PolicyConstants.VISIBILITY_ATTRIBUTE) && !"private".equals(visibilityValue = nodeData.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE))) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(visibilityValue)));
        }
        childNode.setOrReplaceNodeData(nodeData);
    }

    private Attribute getAttributeByName(StartElement element, QName attributeName) {
        Attribute attribute = element.getAttributeByName(attributeName);
        if (attribute == null) {
            String localAttributeName = attributeName.getLocalPart();
            Iterator<Attribute> iterator = element.getAttributes();
            while (iterator.hasNext()) {
                Attribute nextAttribute = iterator.next();
                QName aName = nextAttribute.getName();
                boolean attributeFoundByWorkaround = aName.equals(attributeName) || aName.getLocalPart().equals(localAttributeName) && (aName.getPrefix() == null || "".equals(aName.getPrefix()));
                if (!attributeFoundByWorkaround) continue;
                attribute = nextAttribute;
                break;
            }
        }
        return attribute;
    }

    private String unmarshalNodeContent(NamespaceVersion nsVersion, ModelNode node, QName nodeElementName, XMLEventReader reader) throws PolicyException {
        StringBuilder valueBuffer = null;
        block8: while (reader.hasNext()) {
            try {
                XMLEvent xmlParserEvent = reader.nextEvent();
                switch (xmlParserEvent.getEventType()) {
                    case 5: {
                        break;
                    }
                    case 4: {
                        valueBuffer = this.processCharacters(node.getType(), xmlParserEvent.asCharacters(), valueBuffer);
                        break;
                    }
                    case 2: {
                        this.checkEndTagName(nodeElementName, xmlParserEvent.asEndElement());
                        break block8;
                    }
                    case 1: {
                        StartElement childElement = xmlParserEvent.asStartElement();
                        ModelNode childNode = this.addNewChildNode(nsVersion, node, childElement);
                        String value = this.unmarshalNodeContent(nsVersion, childNode, childElement.getName(), reader);
                        if (!childNode.isDomainSpecific()) continue block8;
                        this.parseAssertionData(nsVersion, value, childNode, childElement);
                        break;
                    }
                    default: {
                        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED()));
                    }
                }
            }
            catch (XMLStreamException e) {
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), e));
            }
        }
        return valueBuffer == null ? null : valueBuffer.toString().trim();
    }

    private XMLEventReader createXMLEventReader(Object storage) throws PolicyException {
        if (storage instanceof XMLEventReader) {
            return (XMLEventReader)storage;
        }
        if (!(storage instanceof Reader)) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(storage.getClass().getName())));
        }
        try {
            return XMLInputFactory.newInstance().createXMLEventReader((Reader)storage);
        }
        catch (XMLStreamException e) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE(), e));
        }
    }

    private void checkEndTagName(QName expected, EndElement element) throws PolicyException {
        QName actual = element.getName();
        if (!expected.equals(actual)) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(expected, actual)));
        }
    }

    private StringBuilder processCharacters(ModelNode.Type currentNodeType, Characters characters, StringBuilder currentValueBuffer) throws PolicyException {
        if (characters.isWhiteSpace()) {
            return currentValueBuffer;
        }
        StringBuilder buffer = currentValueBuffer == null ? new StringBuilder() : currentValueBuffer;
        String data = characters.getData();
        if (currentNodeType == ModelNode.Type.ASSERTION || currentNodeType == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            return buffer.append(data);
        }
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE((Object)currentNodeType, data)));
    }

    private boolean parseBooleanValue(String value) throws PolicyException {
        if ("true".equals(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equals(value) || "0".equals(value)) {
            return false;
        }
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0095_INVALID_BOOLEAN_VALUE(value)));
    }
}

