/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 */
package software.amazon.awssdk.protocols.query.unmarshall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkProtectedApi
public final class XmlElement {
    private static final XmlElement EMPTY = XmlElement.builder().elementName("eof").build();
    private final String elementName;
    private final HashMap<String, List<XmlElement>> childrenByElement;
    private final List<XmlElement> children;
    private final String textContent;
    private final Map<String, String> attributes;

    private XmlElement(Builder builder) {
        this.elementName = builder.elementName;
        this.childrenByElement = new HashMap(builder.childrenByElement);
        this.children = Collections.unmodifiableList(new ArrayList(builder.children));
        this.textContent = builder.textContent;
        this.attributes = Collections.unmodifiableMap(new HashMap(builder.attributes));
    }

    public String elementName() {
        return this.elementName;
    }

    public List<XmlElement> children() {
        return this.children;
    }

    public XmlElement getFirstChild() {
        return this.children.isEmpty() ? null : this.children.get(0);
    }

    public List<XmlElement> getElementsByName(String tagName) {
        return this.childrenByElement.getOrDefault(tagName, Collections.emptyList());
    }

    public XmlElement getElementByName(String tagName) {
        List<XmlElement> elementsByName = this.getElementsByName(tagName);
        if (elementsByName.size() > 1) {
            throw SdkClientException.create((String)String.format("Did not expect more than one element with the name %s in the XML event %s", tagName, this.elementName));
        }
        return elementsByName.size() == 1 ? elementsByName.get(0) : null;
    }

    public Optional<XmlElement> getOptionalElementByName(String tagName) {
        return Optional.ofNullable(this.getElementByName(tagName));
    }

    public String textContent() {
        return this.textContent;
    }

    public Optional<String> getOptionalAttributeByName(String attribute) {
        return Optional.ofNullable(this.attributes.get(attribute));
    }

    public Map<String, String> attributes() {
        return this.attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static XmlElement empty() {
        return EMPTY;
    }

    public static final class Builder {
        private String elementName;
        private final Map<String, List<XmlElement>> childrenByElement = new HashMap<String, List<XmlElement>>();
        private final List<XmlElement> children = new ArrayList<XmlElement>();
        private String textContent = "";
        private Map<String, String> attributes = new HashMap<String, String>();

        private Builder() {
        }

        public Builder elementName(String elementName) {
            this.elementName = elementName;
            return this;
        }

        public Builder addChildElement(XmlElement childElement) {
            this.childrenByElement.computeIfAbsent(childElement.elementName(), s -> new ArrayList());
            this.childrenByElement.get(childElement.elementName()).add(childElement);
            this.children.add(childElement);
            return this;
        }

        public Builder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public XmlElement build() {
            return new XmlElement(this);
        }
    }
}

