/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.wsaddressing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public final class W3CEndpointReferenceBuilder {
    private String address;
    private List<Element> referenceParameters = new ArrayList<Element>();
    private List<Element> metadata = new ArrayList<Element>();
    private QName interfaceName;
    private QName serviceName;
    private QName endpointName;
    private String wsdlDocumentLocation;
    private Map<QName, String> attributes = new HashMap<QName, String>();
    private List<Element> elements = new ArrayList<Element>();

    public W3CEndpointReferenceBuilder address(String address) {
        this.address = address;
        return this;
    }

    public W3CEndpointReferenceBuilder interfaceName(QName interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public W3CEndpointReferenceBuilder serviceName(QName serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public W3CEndpointReferenceBuilder endpointName(QName endpointName) {
        if (this.serviceName == null) {
            throw new IllegalStateException("The W3CEndpointReferenceBuilder's serviceName must be set before setting the endpointName: " + endpointName);
        }
        this.endpointName = endpointName;
        return this;
    }

    public W3CEndpointReferenceBuilder wsdlDocumentLocation(String wsdlDocumentLocation) {
        this.wsdlDocumentLocation = wsdlDocumentLocation;
        return this;
    }

    public W3CEndpointReferenceBuilder referenceParameter(Element referenceParameter) {
        if (referenceParameter == null) {
            throw new IllegalArgumentException("The referenceParameter cannot be null.");
        }
        this.referenceParameters.add(referenceParameter);
        return this;
    }

    public W3CEndpointReferenceBuilder metadata(Element metadataElement) {
        if (metadataElement == null) {
            throw new IllegalArgumentException("The metadataElement cannot be null.");
        }
        this.metadata.add(metadataElement);
        return this;
    }

    public W3CEndpointReferenceBuilder element(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("The extension element cannot be null.");
        }
        this.elements.add(element);
        return this;
    }

    public W3CEndpointReferenceBuilder attribute(QName name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("The extension attribute name or value cannot be null.");
        }
        this.attributes.put(name, value);
        return this;
    }

    public W3CEndpointReference build() {
        if (this.elements.isEmpty() && this.attributes.isEmpty() && this.interfaceName == null) {
            return Provider.provider().createW3CEndpointReference(this.address, this.serviceName, this.endpointName, this.metadata, this.wsdlDocumentLocation, this.referenceParameters);
        }
        return Provider.provider().createW3CEndpointReference(this.address, this.interfaceName, this.serviceName, this.endpointName, this.metadata, this.wsdlDocumentLocation, this.referenceParameters, this.elements, this.attributes);
    }
}

