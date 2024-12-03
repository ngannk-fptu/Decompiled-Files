/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.PolicyConstants;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public final class AssertionData
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 4416256070795526315L;
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionData.class);
    private final QName name;
    private final String value;
    private final Map<QName, String> attributes;
    private ModelNode.Type type;
    private boolean optional;
    private boolean ignorable;

    public static AssertionData createAssertionData(QName name) throws IllegalArgumentException {
        return new AssertionData(name, null, null, ModelNode.Type.ASSERTION, false, false);
    }

    public static AssertionData createAssertionParameterData(QName name) throws IllegalArgumentException {
        return new AssertionData(name, null, null, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
    }

    public static AssertionData createAssertionData(QName name, String value, Map<QName, String> attributes, boolean optional, boolean ignorable) throws IllegalArgumentException {
        return new AssertionData(name, value, attributes, ModelNode.Type.ASSERTION, optional, ignorable);
    }

    public static AssertionData createAssertionParameterData(QName name, String value, Map<QName, String> attributes) throws IllegalArgumentException {
        return new AssertionData(name, value, attributes, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
    }

    AssertionData(QName name, String value, Map<QName, String> attributes, ModelNode.Type type, boolean optional, boolean ignorable) throws IllegalArgumentException {
        this.name = name;
        this.value = value;
        this.optional = optional;
        this.ignorable = ignorable;
        this.attributes = new HashMap<QName, String>();
        if (attributes != null && !attributes.isEmpty()) {
            this.attributes.putAll(attributes);
        }
        this.setModelNodeType(type);
    }

    private void setModelNodeType(ModelNode.Type type) throws IllegalArgumentException {
        if (type != ModelNode.Type.ASSERTION && type != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE((Object)type, (Object)ModelNode.Type.ASSERTION, (Object)ModelNode.Type.ASSERTION_PARAMETER_NODE)));
        }
        this.type = type;
    }

    AssertionData(AssertionData data) {
        this.name = data.name;
        this.value = data.value;
        this.attributes = new HashMap<QName, String>();
        if (!data.attributes.isEmpty()) {
            this.attributes.putAll(data.attributes);
        }
        this.type = data.type;
    }

    protected AssertionData clone() throws CloneNotSupportedException {
        return (AssertionData)super.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsAttribute(QName name) {
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            return this.attributes.containsKey(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssertionData)) {
            return false;
        }
        boolean result = true;
        AssertionData that = (AssertionData)obj;
        boolean bl = result = result && this.name.equals(that.name);
        result = result && (this.value == null ? that.value == null : this.value.equals(that.value));
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            result = result && this.attributes.equals(that.attributes);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getAttributeValue(QName name) {
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            return this.attributes.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<QName, String> getAttributes() {
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            return new HashMap<QName, String>(this.attributes);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<Map.Entry<QName, String>> getAttributesSet() {
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            return new HashSet<Map.Entry<QName, String>>(this.attributes.entrySet());
        }
    }

    public QName getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.name.hashCode();
        result = 37 * result + (this.value == null ? 0 : this.value.hashCode());
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            result = 37 * result + this.attributes.hashCode();
        }
        return result;
    }

    public boolean isPrivateAttributeSet() {
        return "private".equals(this.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String removeAttribute(QName name) {
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            return this.attributes.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAttribute(QName name, String value) {
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            this.attributes.put(name, value);
        }
    }

    public void setOptionalAttribute(boolean value) {
        this.optional = value;
    }

    public boolean isOptionalAttributeSet() {
        return this.optional;
    }

    public void setIgnorableAttribute(boolean value) {
        this.ignorable = value;
    }

    public boolean isIgnorableAttributeSet() {
        return this.ignorable;
    }

    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StringBuffer toString(int indentLevel, StringBuffer buffer) {
        String indent = PolicyUtils.Text.createIndent(indentLevel);
        String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        String innerDoubleIndent = PolicyUtils.Text.createIndent(indentLevel + 2);
        buffer.append(indent);
        if (this.type == ModelNode.Type.ASSERTION) {
            buffer.append("assertion data {");
        } else {
            buffer.append("assertion parameter data {");
        }
        buffer.append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("namespace = '").append(this.name.getNamespaceURI()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("prefix = '").append(this.name.getPrefix()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("local name = '").append(this.name.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("value = '").append(this.value).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("optional = '").append(this.optional).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("ignorable = '").append(this.ignorable).append('\'').append(PolicyUtils.Text.NEW_LINE);
        Map<QName, String> map = this.attributes;
        synchronized (map) {
            if (this.attributes.isEmpty()) {
                buffer.append(innerIndent).append("no attributes");
            } else {
                buffer.append(innerIndent).append("attributes {").append(PolicyUtils.Text.NEW_LINE);
                for (Map.Entry<QName, String> entry : this.attributes.entrySet()) {
                    QName aName = entry.getKey();
                    buffer.append(innerDoubleIndent).append("name = '").append(aName.getNamespaceURI()).append(':').append(aName.getLocalPart());
                    buffer.append("', value = '").append(entry.getValue()).append('\'').append(PolicyUtils.Text.NEW_LINE);
                }
                buffer.append(innerIndent).append('}');
            }
        }
        buffer.append(PolicyUtils.Text.NEW_LINE).append(indent).append('}');
        return buffer;
    }

    public ModelNode.Type getNodeType() {
        return this.type;
    }
}

