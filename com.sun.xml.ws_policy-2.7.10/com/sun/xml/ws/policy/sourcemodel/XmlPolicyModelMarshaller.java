/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TXW
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.output.StaxSerializer
 *  com.sun.xml.txw2.output.XmlSerializer
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.output.StaxSerializer;
import com.sun.xml.txw2.output.XmlSerializer;
import com.sun.xml.ws.policy.PolicyConstants;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

public final class XmlPolicyModelMarshaller
extends PolicyModelMarshaller {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelMarshaller.class);
    private final boolean marshallInvisible;

    XmlPolicyModelMarshaller(boolean marshallInvisible) {
        this.marshallInvisible = marshallInvisible;
    }

    @Override
    public void marshal(PolicySourceModel model, Object storage) throws PolicyException {
        if (storage instanceof StaxSerializer) {
            this.marshal(model, (StaxSerializer)storage);
        } else if (storage instanceof TypedXmlWriter) {
            this.marshal(model, (TypedXmlWriter)storage);
        } else if (storage instanceof XMLStreamWriter) {
            this.marshal(model, (XMLStreamWriter)storage);
        } else {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(storage.getClass().getName())));
        }
    }

    @Override
    public void marshal(Collection<PolicySourceModel> models, Object storage) throws PolicyException {
        for (PolicySourceModel model : models) {
            this.marshal(model, storage);
        }
    }

    private void marshal(PolicySourceModel model, StaxSerializer writer) throws PolicyException {
        TypedXmlWriter policy = TXW.create((QName)model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, (XmlSerializer)writer);
        this.marshalDefaultPrefixes(model, policy);
        XmlPolicyModelMarshaller.marshalPolicyAttributes(model, policy);
        this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
        policy.commit();
    }

    private void marshal(PolicySourceModel model, TypedXmlWriter writer) throws PolicyException {
        TypedXmlWriter policy = writer._element(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class);
        this.marshalDefaultPrefixes(model, policy);
        XmlPolicyModelMarshaller.marshalPolicyAttributes(model, policy);
        this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
    }

    private void marshal(PolicySourceModel model, XMLStreamWriter writer) throws PolicyException {
        StaxSerializer serializer = new StaxSerializer(writer);
        TypedXmlWriter policy = TXW.create((QName)model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, (XmlSerializer)serializer);
        this.marshalDefaultPrefixes(model, policy);
        XmlPolicyModelMarshaller.marshalPolicyAttributes(model, policy);
        this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
        policy.commit();
        serializer.flush();
    }

    private static void marshalPolicyAttributes(PolicySourceModel model, TypedXmlWriter writer) {
        String policyName;
        String policyId = model.getPolicyId();
        if (policyId != null) {
            writer._attribute(PolicyConstants.WSU_ID, (Object)policyId);
        }
        if ((policyName = model.getPolicyName()) != null) {
            writer._attribute(model.getNamespaceVersion().asQName(XmlToken.Name), (Object)policyName);
        }
    }

    private void marshal(NamespaceVersion nsVersion, ModelNode rootNode, TypedXmlWriter writer) {
        for (ModelNode node : rootNode) {
            AssertionData data = node.getNodeData();
            if (!this.marshallInvisible && data != null && data.isPrivateAttributeSet()) continue;
            TypedXmlWriter child = null;
            if (data == null) {
                child = writer._element(nsVersion.asQName(node.getType().getXmlToken()), TypedXmlWriter.class);
            } else {
                child = writer._element(data.getName(), TypedXmlWriter.class);
                String value = data.getValue();
                if (value != null) {
                    child._pcdata((Object)value);
                }
                if (data.isOptionalAttributeSet()) {
                    child._attribute(nsVersion.asQName(XmlToken.Optional), (Object)Boolean.TRUE);
                }
                if (data.isIgnorableAttributeSet()) {
                    child._attribute(nsVersion.asQName(XmlToken.Ignorable), (Object)Boolean.TRUE);
                }
                for (Map.Entry<QName, String> entry : data.getAttributesSet()) {
                    child._attribute(entry.getKey(), (Object)entry.getValue());
                }
            }
            this.marshal(nsVersion, node, child);
        }
    }

    private void marshalDefaultPrefixes(PolicySourceModel model, TypedXmlWriter writer) throws PolicyException {
        Map<String, String> nsMap = model.getNamespaceToPrefixMapping();
        if (!this.marshallInvisible && nsMap.containsKey("http://java.sun.com/xml/ns/wsit/policy")) {
            nsMap.remove("http://java.sun.com/xml/ns/wsit/policy");
        }
        for (Map.Entry<String, String> nsMappingEntry : nsMap.entrySet()) {
            writer._namespace(nsMappingEntry.getKey(), nsMappingEntry.getValue());
        }
    }
}

