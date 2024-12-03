/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import com.sun.xml.ws.policy.sourcemodel.PolicyReferenceData;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModelContext;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.ws.policy.spi.PrefixMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javax.xml.namespace.QName;

public class PolicySourceModel
implements Cloneable {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySourceModel.class);
    private static final Map<String, String> DEFAULT_NAMESPACE_TO_PREFIX = new HashMap<String, String>();
    private final Map<String, String> namespaceToPrefix = new HashMap<String, String>(DEFAULT_NAMESPACE_TO_PREFIX);
    private ModelNode rootNode;
    private final String policyId;
    private final String policyName;
    private final NamespaceVersion nsVersion;
    private final List<ModelNode> references = new LinkedList<ModelNode>();
    private boolean expanded = false;

    public static PolicySourceModel createPolicySourceModel(NamespaceVersion nsVersion) {
        return new PolicySourceModel(nsVersion);
    }

    public static PolicySourceModel createPolicySourceModel(NamespaceVersion nsVersion, String policyId, String policyName) {
        return new PolicySourceModel(nsVersion, policyId, policyName);
    }

    private PolicySourceModel(NamespaceVersion nsVersion) {
        this(nsVersion, null, null);
    }

    private PolicySourceModel(NamespaceVersion nsVersion, String policyId, String policyName) {
        this(nsVersion, policyId, policyName, null);
    }

    protected PolicySourceModel(NamespaceVersion nsVersion, String policyId, String policyName, Collection<PrefixMapper> prefixMappers) {
        this.rootNode = ModelNode.createRootPolicyNode(this);
        this.nsVersion = nsVersion;
        this.policyId = policyId;
        this.policyName = policyName;
        if (prefixMappers != null) {
            for (PrefixMapper prefixMapper : prefixMappers) {
                this.namespaceToPrefix.putAll(prefixMapper.getPrefixMap());
            }
        }
    }

    public ModelNode getRootNode() {
        return this.rootNode;
    }

    public String getPolicyName() {
        return this.policyName;
    }

    public String getPolicyId() {
        return this.policyId;
    }

    public NamespaceVersion getNamespaceVersion() {
        return this.nsVersion;
    }

    Map<String, String> getNamespaceToPrefixMapping() throws PolicyException {
        HashMap<String, String> nsToPrefixMap = new HashMap<String, String>();
        Collection<String> namespaces = this.getUsedNamespaces();
        for (String namespace : namespaces) {
            String prefix = this.getDefaultPrefix(namespace);
            if (prefix == null) continue;
            nsToPrefixMap.put(namespace, prefix);
        }
        return nsToPrefixMap;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PolicySourceModel)) {
            return false;
        }
        boolean result = true;
        PolicySourceModel that = (PolicySourceModel)obj;
        boolean bl = result && (this.policyId == null ? that.policyId == null : this.policyId.equals(that.policyId)) ? true : (result = false);
        result = result && (this.policyName == null ? that.policyName == null : this.policyName.equals(that.policyName));
        result = result && this.rootNode.equals(that.rootNode);
        return result;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.policyId == null ? 0 : this.policyId.hashCode());
        result = 37 * result + (this.policyName == null ? 0 : this.policyName.hashCode());
        result = 37 * result + this.rootNode.hashCode();
        return result;
    }

    public String toString() {
        String innerIndent = PolicyUtils.Text.createIndent(1);
        StringBuffer buffer = new StringBuffer(60);
        buffer.append("Policy source model {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("policy id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("policy name = '").append(this.policyName).append('\'').append(PolicyUtils.Text.NEW_LINE);
        this.rootNode.toString(1, buffer).append(PolicyUtils.Text.NEW_LINE).append('}');
        return buffer.toString();
    }

    protected PolicySourceModel clone() throws CloneNotSupportedException {
        PolicySourceModel clone = (PolicySourceModel)super.clone();
        clone.rootNode = this.rootNode.clone();
        try {
            clone.rootNode.setParentModel(clone);
        }
        catch (IllegalAccessException e) {
            throw (CloneNotSupportedException)LOGGER.logSevereException(new CloneNotSupportedException(LocalizationMessages.WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT()), e);
        }
        return clone;
    }

    public boolean containsPolicyReferences() {
        return !this.references.isEmpty();
    }

    private boolean isExpanded() {
        return this.references.isEmpty() || this.expanded;
    }

    public synchronized void expand(PolicySourceModelContext context) throws PolicyException {
        if (!this.isExpanded()) {
            for (ModelNode reference : this.references) {
                PolicyReferenceData refData = reference.getPolicyReferenceData();
                String digest = refData.getDigest();
                PolicySourceModel referencedModel = digest == null ? context.retrieveModel(refData.getReferencedModelUri()) : context.retrieveModel(refData.getReferencedModelUri(), refData.getDigestAlgorithmUri(), digest);
                reference.setReferencedModel(referencedModel);
            }
            this.expanded = true;
        }
    }

    void addNewPolicyReference(ModelNode node) {
        if (node.getType() != ModelNode.Type.POLICY_REFERENCE) {
            throw new IllegalArgumentException(LocalizationMessages.WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF((Object)node.getType()));
        }
        this.references.add(node);
    }

    private Collection<String> getUsedNamespaces() throws PolicyException {
        ModelNode processedNode;
        HashSet<String> namespaces = new HashSet<String>();
        namespaces.add(this.getNamespaceVersion().toString());
        if (this.policyId != null) {
            namespaces.add("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        }
        LinkedList<ModelNode> nodesToBeProcessed = new LinkedList<ModelNode>();
        nodesToBeProcessed.add(this.rootNode);
        while ((processedNode = (ModelNode)nodesToBeProcessed.poll()) != null) {
            for (ModelNode child : processedNode.getChildren()) {
                if (child.hasChildren() && !nodesToBeProcessed.offer(child)) {
                    throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0081_UNABLE_TO_INSERT_CHILD(nodesToBeProcessed, child)));
                }
                if (!child.isDomainSpecific()) continue;
                AssertionData nodeData = child.getNodeData();
                namespaces.add(nodeData.getName().getNamespaceURI());
                if (nodeData.isPrivateAttributeSet()) {
                    namespaces.add("http://java.sun.com/xml/ns/wsit/policy");
                }
                for (Map.Entry<QName, String> attribute : nodeData.getAttributesSet()) {
                    namespaces.add(attribute.getKey().getNamespaceURI());
                }
            }
        }
        return namespaces;
    }

    private String getDefaultPrefix(String namespace) {
        return this.namespaceToPrefix.get(namespace);
    }

    static {
        for (PrefixMapper mapper : ServiceLoader.load(PrefixMapper.class)) {
            DEFAULT_NAMESPACE_TO_PREFIX.putAll(mapper.getPrefixMap());
        }
        for (NamespaceVersion version : NamespaceVersion.values()) {
            DEFAULT_NAMESPACE_TO_PREFIX.put(version.toString(), version.getDefaultNamespacePrefix());
        }
        DEFAULT_NAMESPACE_TO_PREFIX.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
        DEFAULT_NAMESPACE_TO_PREFIX.put("http://java.sun.com/xml/ns/wsit/policy", "sunwsp");
    }
}

