/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public abstract class AbstractOutputProcessor
implements OutputProcessor {
    protected XMLSecurityProperties securityProperties;
    protected XMLSecurityConstants.Action action;
    protected int actionOrder = -1;
    private XMLSecurityConstants.Phase phase = XMLSecurityConstants.Phase.PROCESSING;
    private Set<Class<? extends OutputProcessor>> beforeProcessors;
    private Set<Class<? extends OutputProcessor>> afterProcessors;

    protected AbstractOutputProcessor() throws XMLSecurityException {
    }

    @Override
    public void setXMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        this.securityProperties = xmlSecurityProperties;
    }

    @Override
    public void setAction(XMLSecurityConstants.Action action, int actionOrder) {
        this.action = action;
        this.actionOrder = actionOrder;
    }

    @Override
    public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        outputProcessorChain.addProcessor(this);
    }

    @Override
    public XMLSecurityConstants.Phase getPhase() {
        return this.phase;
    }

    public void setPhase(XMLSecurityConstants.Phase phase) {
        this.phase = phase;
    }

    @Override
    public void addBeforeProcessor(Class<? extends OutputProcessor> processor) {
        if (this.beforeProcessors == null) {
            this.beforeProcessors = new HashSet<Class<? extends OutputProcessor>>();
        }
        this.beforeProcessors.add(processor);
    }

    @Override
    public Set<Class<? extends OutputProcessor>> getBeforeProcessors() {
        if (this.beforeProcessors == null) {
            return Collections.emptySet();
        }
        return this.beforeProcessors;
    }

    @Override
    public void addAfterProcessor(Class<? extends OutputProcessor> processor) {
        if (this.afterProcessors == null) {
            this.afterProcessors = new HashSet<Class<? extends OutputProcessor>>();
        }
        this.afterProcessors.add(processor);
    }

    @Override
    public Set<Class<? extends OutputProcessor>> getAfterProcessors() {
        if (this.afterProcessors == null) {
            return Collections.emptySet();
        }
        return this.afterProcessors;
    }

    public XMLSecurityProperties getSecurityProperties() {
        return this.securityProperties;
    }

    @Override
    public XMLSecurityConstants.Action getAction() {
        return this.action;
    }

    @Override
    public int getActionOrder() {
        return this.actionOrder;
    }

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        outputProcessorChain.doFinal();
    }

    public XMLSecStartElement addAttributes(XMLSecStartElement xmlSecStartElement, List<XMLSecAttribute> attributeList) throws XMLStreamException {
        List<XMLSecNamespace> declaredNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
        for (int i = 0; i < attributeList.size(); ++i) {
            XMLSecAttribute xmlSecAttribute = attributeList.get(i);
            xmlSecStartElement.addAttribute(xmlSecAttribute);
            QName attributeName = xmlSecAttribute.getName();
            if (attributeName.getNamespaceURI() == null || attributeName.getNamespaceURI().length() == 0 || declaredNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) continue;
            xmlSecStartElement.addNamespace(xmlSecAttribute.getAttributeNamespace());
        }
        return xmlSecStartElement;
    }

    public void createStartElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element, List<XMLSecNamespace> namespaces, List<XMLSecAttribute> attributes) throws XMLStreamException, XMLSecurityException {
        XMLSecStartElement xmlSecStartElement = XMLSecEventFactory.createXmlSecStartElement(element, attributes, namespaces);
        this.outputAsEvent(outputProcessorChain, xmlSecStartElement);
    }

    public XMLSecStartElement createStartElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element, boolean outputLocalNs, List<XMLSecAttribute> attributes) throws XMLStreamException, XMLSecurityException {
        List<XMLSecNamespace> comparableNamespaces = Collections.emptyList();
        if (outputLocalNs) {
            comparableNamespaces = new ArrayList(2);
            comparableNamespaces.add(XMLSecEventFactory.createXMLSecNamespace(element.getPrefix(), element.getNamespaceURI()));
        }
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); ++i) {
                XMLSecAttribute xmlSecAttribute = attributes.get(i);
                QName attributeName = xmlSecAttribute.getName();
                String attributeNamePrefix = attributeName.getPrefix();
                if (attributeNamePrefix != null && attributeNamePrefix.isEmpty() || comparableNamespaces.contains(xmlSecAttribute.getAttributeNamespace())) continue;
                if (comparableNamespaces == Collections.emptyList()) {
                    comparableNamespaces = new ArrayList<XMLSecNamespace>(1);
                }
                comparableNamespaces.add(xmlSecAttribute.getAttributeNamespace());
            }
        }
        XMLSecStartElement xmlSecStartElement = XMLSecEventFactory.createXmlSecStartElement(element, attributes, comparableNamespaces);
        this.outputAsEvent(outputProcessorChain, xmlSecStartElement);
        return xmlSecStartElement;
    }

    public XMLSecEndElement createEndElement(QName element) {
        return XMLSecEventFactory.createXmlSecEndElement(element);
    }

    public void createEndElementAndOutputAsEvent(OutputProcessorChain outputProcessorChain, QName element) throws XMLStreamException, XMLSecurityException {
        this.outputAsEvent(outputProcessorChain, this.createEndElement(element));
    }

    public void createCharactersAndOutputAsEvent(OutputProcessorChain outputProcessorChain, String characters) throws XMLStreamException, XMLSecurityException {
        this.outputAsEvent(outputProcessorChain, this.createCharacters(characters));
    }

    public void createCharactersAndOutputAsEvent(OutputProcessorChain outputProcessorChain, char[] text) throws XMLStreamException, XMLSecurityException {
        this.outputAsEvent(outputProcessorChain, this.createCharacters(text));
    }

    public XMLSecCharacters createCharacters(String characters) {
        return XMLSecEventFactory.createXmlSecCharacters(characters);
    }

    public XMLSecCharacters createCharacters(char[] text) {
        return XMLSecEventFactory.createXmlSecCharacters(text);
    }

    public XMLSecAttribute createAttribute(QName attribute, String attributeValue) {
        return XMLSecEventFactory.createXMLSecAttribute(attribute, attributeValue);
    }

    public XMLSecNamespace createNamespace(String prefix, String uri) {
        return XMLSecEventFactory.createXMLSecNamespace(prefix, uri);
    }

    protected void outputAsEvent(OutputProcessorChain outputProcessorChain, XMLSecEvent xmlSecEvent) throws XMLStreamException, XMLSecurityException {
        outputProcessorChain.reset();
        outputProcessorChain.processEvent(xmlSecEvent);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement, OutputProcessorChain outputProcessorChain, String dynamicParts) {
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(dynamicParts);
        return this.securePartMatches(xmlSecStartElement, dynamicSecureParts);
    }

    protected SecurePart securePartMatches(XMLSecStartElement xmlSecStartElement, Map<Object, SecurePart> secureParts) {
        Attribute attribute;
        SecurePart securePart = null;
        if (secureParts != null && (securePart = secureParts.get(xmlSecStartElement.getName())) == null && (attribute = xmlSecStartElement.getAttributeByName(this.securityProperties.getIdAttributeNS())) != null) {
            securePart = secureParts.get(attribute.getValue());
        }
        return securePart;
    }

    protected void outputDOMElement(Element element, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        NamedNodeMap namedNodeMap = element.getAttributes();
        ArrayList<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(namedNodeMap.getLength());
        ArrayList<XMLSecNamespace> namespaces = new ArrayList<XMLSecNamespace>(namedNodeMap.getLength());
        for (int i = 0; i < namedNodeMap.getLength(); ++i) {
            Attr attribute = (Attr)namedNodeMap.item(i);
            if (attribute.getPrefix() == null) {
                attributes.add(this.createAttribute(new QName(attribute.getNamespaceURI(), attribute.getLocalName()), attribute.getValue()));
                continue;
            }
            if ("xmlns".equals(attribute.getPrefix()) || "xmlns".equals(attribute.getLocalName())) {
                namespaces.add(this.createNamespace(attribute.getLocalName(), attribute.getValue()));
                continue;
            }
            attributes.add(this.createAttribute(new QName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix()), attribute.getValue()));
        }
        QName elementName = new QName(element.getNamespaceURI(), element.getLocalName(), element.getPrefix());
        this.createStartElementAndOutputAsEvent(outputProcessorChain, elementName, namespaces, attributes);
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (1 == childNode.getNodeType()) {
                this.outputDOMElement((Element)childNode, outputProcessorChain);
                continue;
            }
            if (3 != childNode.getNodeType()) continue;
            this.createCharactersAndOutputAsEvent(outputProcessorChain, ((Text)childNode).getData());
        }
        this.createEndElementAndOutputAsEvent(outputProcessorChain, elementName);
    }
}

