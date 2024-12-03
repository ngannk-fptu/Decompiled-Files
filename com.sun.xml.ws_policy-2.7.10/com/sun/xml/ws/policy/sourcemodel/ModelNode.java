/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.sourcemodel.PolicyReferenceData;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public final class ModelNode
implements Iterable<ModelNode>,
Cloneable {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelNode.class);
    private LinkedList<ModelNode> children;
    private Collection<ModelNode> unmodifiableViewOnContent;
    private final Type type;
    private ModelNode parentNode;
    private PolicySourceModel parentModel;
    private PolicyReferenceData referenceData;
    private PolicySourceModel referencedModel;
    private AssertionData nodeData;

    static ModelNode createRootPolicyNode(PolicySourceModel model) throws IllegalArgumentException {
        if (model == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL()));
        }
        return new ModelNode(Type.POLICY, model);
    }

    private ModelNode(Type type, PolicySourceModel parentModel) {
        this.type = type;
        this.parentModel = parentModel;
        this.children = new LinkedList();
        this.unmodifiableViewOnContent = Collections.unmodifiableCollection(this.children);
    }

    private ModelNode(Type type, PolicySourceModel parentModel, AssertionData data) {
        this(type, parentModel);
        this.nodeData = data;
    }

    private ModelNode(PolicySourceModel parentModel, PolicyReferenceData data) {
        this(Type.POLICY_REFERENCE, parentModel);
        this.referenceData = data;
    }

    private void checkCreateChildOperationSupportForType(Type type) throws UnsupportedOperationException {
        if (!this.type.isChildTypeSupported(type)) {
            throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED((Object)type, (Object)this.type)));
        }
    }

    public ModelNode createChildPolicyNode() {
        this.checkCreateChildOperationSupportForType(Type.POLICY);
        ModelNode node = new ModelNode(Type.POLICY, this.parentModel);
        this.addChild(node);
        return node;
    }

    public ModelNode createChildAllNode() {
        this.checkCreateChildOperationSupportForType(Type.ALL);
        ModelNode node = new ModelNode(Type.ALL, this.parentModel);
        this.addChild(node);
        return node;
    }

    public ModelNode createChildExactlyOneNode() {
        this.checkCreateChildOperationSupportForType(Type.EXACTLY_ONE);
        ModelNode node = new ModelNode(Type.EXACTLY_ONE, this.parentModel);
        this.addChild(node);
        return node;
    }

    public ModelNode createChildAssertionNode() {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION);
        ModelNode node = new ModelNode(Type.ASSERTION, this.parentModel);
        this.addChild(node);
        return node;
    }

    public ModelNode createChildAssertionNode(AssertionData nodeData) {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION);
        ModelNode node = new ModelNode(Type.ASSERTION, this.parentModel, nodeData);
        this.addChild(node);
        return node;
    }

    public ModelNode createChildAssertionParameterNode() {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
        ModelNode node = new ModelNode(Type.ASSERTION_PARAMETER_NODE, this.parentModel);
        this.addChild(node);
        return node;
    }

    ModelNode createChildAssertionParameterNode(AssertionData nodeData) {
        this.checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
        ModelNode node = new ModelNode(Type.ASSERTION_PARAMETER_NODE, this.parentModel, nodeData);
        this.addChild(node);
        return node;
    }

    ModelNode createChildPolicyReferenceNode(PolicyReferenceData referenceData) {
        this.checkCreateChildOperationSupportForType(Type.POLICY_REFERENCE);
        ModelNode node = new ModelNode(this.parentModel, referenceData);
        this.parentModel.addNewPolicyReference(node);
        this.addChild(node);
        return node;
    }

    Collection<ModelNode> getChildren() {
        return this.unmodifiableViewOnContent;
    }

    void setParentModel(PolicySourceModel model) throws IllegalAccessException {
        if (this.parentNode != null) {
            throw (IllegalAccessException)LOGGER.logSevereException(new IllegalAccessException(LocalizationMessages.WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED()));
        }
        this.updateParentModelReference(model);
    }

    private void updateParentModelReference(PolicySourceModel model) {
        this.parentModel = model;
        for (ModelNode child : this.children) {
            child.updateParentModelReference(model);
        }
    }

    public PolicySourceModel getParentModel() {
        return this.parentModel;
    }

    public Type getType() {
        return this.type;
    }

    public ModelNode getParentNode() {
        return this.parentNode;
    }

    public AssertionData getNodeData() {
        return this.nodeData;
    }

    PolicyReferenceData getPolicyReferenceData() {
        return this.referenceData;
    }

    public AssertionData setOrReplaceNodeData(AssertionData newData) {
        if (!this.isDomainSpecific()) {
            throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE((Object)this.type)));
        }
        AssertionData oldData = this.nodeData;
        this.nodeData = newData;
        return oldData;
    }

    boolean isDomainSpecific() {
        return this.type == Type.ASSERTION || this.type == Type.ASSERTION_PARAMETER_NODE;
    }

    private boolean addChild(ModelNode child) {
        this.children.add(child);
        child.parentNode = this;
        return true;
    }

    void setReferencedModel(PolicySourceModel model) {
        if (this.type != Type.POLICY_REFERENCE) {
            throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE((Object)this.type)));
        }
        this.referencedModel = model;
    }

    PolicySourceModel getReferencedModel() {
        return this.referencedModel;
    }

    public int childrenSize() {
        return this.children.size();
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    @Override
    public Iterator<ModelNode> iterator() {
        return this.children.iterator();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ModelNode)) {
            return false;
        }
        boolean result = true;
        ModelNode that = (ModelNode)obj;
        boolean bl = result = result && this.type.equals((Object)that.type);
        boolean bl2 = result && (this.nodeData == null ? that.nodeData == null : this.nodeData.equals(that.nodeData)) ? true : (result = false);
        result = result && (this.children == null ? that.children == null : this.children.equals(that.children));
        return result;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.type.hashCode();
        result = 37 * result + (this.parentNode == null ? 0 : this.parentNode.hashCode());
        result = 37 * result + (this.nodeData == null ? 0 : this.nodeData.hashCode());
        result = 37 * result + this.children.hashCode();
        return result;
    }

    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }

    public StringBuffer toString(int indentLevel, StringBuffer buffer) {
        String indent = PolicyUtils.Text.createIndent(indentLevel);
        String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append((Object)this.type).append(" {").append(PolicyUtils.Text.NEW_LINE);
        if (this.type == Type.ASSERTION) {
            if (this.nodeData == null) {
                buffer.append(innerIndent).append("no assertion data set");
            } else {
                this.nodeData.toString(indentLevel + 1, buffer);
            }
            buffer.append(PolicyUtils.Text.NEW_LINE);
        } else if (this.type == Type.POLICY_REFERENCE) {
            if (this.referenceData == null) {
                buffer.append(innerIndent).append("no policy reference data set");
            } else {
                this.referenceData.toString(indentLevel + 1, buffer);
            }
            buffer.append(PolicyUtils.Text.NEW_LINE);
        } else if (this.type == Type.ASSERTION_PARAMETER_NODE) {
            if (this.nodeData == null) {
                buffer.append(innerIndent).append("no parameter data set");
            } else {
                this.nodeData.toString(indentLevel + 1, buffer);
            }
            buffer.append(PolicyUtils.Text.NEW_LINE);
        }
        if (this.children.size() > 0) {
            for (ModelNode child : this.children) {
                child.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
            }
        } else {
            buffer.append(innerIndent).append("no child nodes").append(PolicyUtils.Text.NEW_LINE);
        }
        buffer.append(indent).append('}');
        return buffer;
    }

    protected ModelNode clone() throws CloneNotSupportedException {
        ModelNode clone = (ModelNode)super.clone();
        if (this.nodeData != null) {
            clone.nodeData = this.nodeData.clone();
        }
        if (this.referencedModel != null) {
            clone.referencedModel = this.referencedModel.clone();
        }
        clone.children = new LinkedList();
        clone.unmodifiableViewOnContent = Collections.unmodifiableCollection(clone.children);
        for (ModelNode thisChild : this.children) {
            clone.addChild(thisChild.clone());
        }
        return clone;
    }

    PolicyReferenceData getReferenceData() {
        return this.referenceData;
    }

    public static enum Type {
        POLICY(XmlToken.Policy),
        ALL(XmlToken.All),
        EXACTLY_ONE(XmlToken.ExactlyOne),
        POLICY_REFERENCE(XmlToken.PolicyReference),
        ASSERTION(XmlToken.UNKNOWN),
        ASSERTION_PARAMETER_NODE(XmlToken.UNKNOWN);

        private XmlToken token;

        private Type(XmlToken token) {
            this.token = token;
        }

        public XmlToken getXmlToken() {
            return this.token;
        }

        private boolean isChildTypeSupported(Type childType) {
            switch (this) {
                case POLICY: 
                case ALL: 
                case EXACTLY_ONE: {
                    switch (childType) {
                        case ASSERTION_PARAMETER_NODE: {
                            return false;
                        }
                    }
                    return true;
                }
                case POLICY_REFERENCE: {
                    return false;
                }
                case ASSERTION: {
                    switch (childType) {
                        case ASSERTION_PARAMETER_NODE: 
                        case POLICY: 
                        case POLICY_REFERENCE: {
                            return true;
                        }
                    }
                    return false;
                }
                case ASSERTION_PARAMETER_NODE: {
                    switch (childType) {
                        case ASSERTION_PARAMETER_NODE: {
                            return true;
                        }
                    }
                    return false;
                }
            }
            throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN((Object)this)));
        }
    }
}

