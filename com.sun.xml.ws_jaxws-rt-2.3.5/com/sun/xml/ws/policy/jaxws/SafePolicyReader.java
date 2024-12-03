/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.ws.resources.PolicyMessages;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class SafePolicyReader {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(SafePolicyReader.class);
    private final Set<String> urlsRead = new HashSet<String>();
    private final Set<String> qualifiedPolicyUris = new HashSet<String>();

    public PolicyRecord readPolicyElement(XMLStreamReader reader, String baseUrl) {
        if (null == reader || !reader.isStartElement()) {
            return null;
        }
        StringBuffer elementCode = new StringBuffer();
        PolicyRecord policyRec = new PolicyRecord();
        QName elementName = reader.getName();
        int depth = 0;
        try {
            do {
                switch (reader.getEventType()) {
                    case 1: {
                        boolean insidePolicyReferenceAttr;
                        QName curName = reader.getName();
                        boolean bl = insidePolicyReferenceAttr = NamespaceVersion.resolveAsToken((QName)curName) == XmlToken.PolicyReference;
                        if (elementName.equals(curName)) {
                            ++depth;
                        }
                        StringBuffer xmlnsCode = new StringBuffer();
                        HashSet<String> tmpNsSet = new HashSet<String>();
                        if (null == curName.getPrefix() || "".equals(curName.getPrefix())) {
                            elementCode.append('<').append(curName.getLocalPart());
                            xmlnsCode.append(" xmlns=\"").append(curName.getNamespaceURI()).append('\"');
                        } else {
                            elementCode.append('<').append(curName.getPrefix()).append(':').append(curName.getLocalPart());
                            xmlnsCode.append(" xmlns:").append(curName.getPrefix()).append("=\"").append(curName.getNamespaceURI()).append('\"');
                            tmpNsSet.add(curName.getPrefix());
                        }
                        int attrCount = reader.getAttributeCount();
                        StringBuffer attrCode = new StringBuffer();
                        for (int i = 0; i < attrCount; ++i) {
                            boolean uriAttrFlg = false;
                            if (insidePolicyReferenceAttr && "URI".equals(reader.getAttributeName(i).getLocalPart())) {
                                uriAttrFlg = true;
                                if (null == policyRec.unresolvedURIs) {
                                    policyRec.unresolvedURIs = new HashSet<String>();
                                }
                                policyRec.unresolvedURIs.add(SafePolicyReader.relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl));
                            }
                            if ("xmlns".equals(reader.getAttributePrefix(i)) && tmpNsSet.contains(reader.getAttributeLocalName(i))) continue;
                            if (null == reader.getAttributePrefix(i) || "".equals(reader.getAttributePrefix(i))) {
                                attrCode.append(' ').append(reader.getAttributeLocalName(i)).append("=\"").append(uriAttrFlg ? SafePolicyReader.relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl) : reader.getAttributeValue(i)).append('\"');
                                continue;
                            }
                            attrCode.append(' ').append(reader.getAttributePrefix(i)).append(':').append(reader.getAttributeLocalName(i)).append("=\"").append(uriAttrFlg ? SafePolicyReader.relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl) : reader.getAttributeValue(i)).append('\"');
                            if (tmpNsSet.contains(reader.getAttributePrefix(i))) continue;
                            xmlnsCode.append(" xmlns:").append(reader.getAttributePrefix(i)).append("=\"").append(reader.getAttributeNamespace(i)).append('\"');
                            tmpNsSet.add(reader.getAttributePrefix(i));
                        }
                        elementCode.append(xmlnsCode).append(attrCode).append('>');
                        break;
                    }
                    case 2: {
                        QName curName = reader.getName();
                        if (elementName.equals(curName)) {
                            --depth;
                        }
                        elementCode.append("</").append("".equals(curName.getPrefix()) ? "" : curName.getPrefix() + ':').append(curName.getLocalPart()).append('>');
                        break;
                    }
                    case 4: {
                        elementCode.append(reader.getText());
                        break;
                    }
                    case 12: {
                        elementCode.append("<![CDATA[").append(reader.getText()).append("]]>");
                        break;
                    }
                    case 5: {
                        break;
                    }
                }
                if (!reader.hasNext() || depth <= 0) continue;
                reader.next();
            } while (8 != reader.getEventType() && depth > 0);
            policyRec.policyModel = ModelUnmarshaller.getUnmarshaller().unmarshalModel(new StringReader(elementCode.toString()));
            if (null != policyRec.policyModel.getPolicyId()) {
                policyRec.setUri(baseUrl + "#" + policyRec.policyModel.getPolicyId(), policyRec.policyModel.getPolicyId());
            } else if (policyRec.policyModel.getPolicyName() != null) {
                policyRec.setUri(policyRec.policyModel.getPolicyName(), policyRec.policyModel.getPolicyName());
            }
        }
        catch (Exception e) {
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(elementCode.toString()), (Throwable)e));
        }
        this.urlsRead.add(baseUrl);
        return policyRec;
    }

    public Set<String> getUrlsRead() {
        return this.urlsRead;
    }

    public String readPolicyReferenceElement(XMLStreamReader reader) {
        try {
            if (NamespaceVersion.resolveAsToken((QName)reader.getName()) == XmlToken.PolicyReference) {
                for (int i = 0; i < reader.getAttributeCount(); ++i) {
                    if (XmlToken.resolveToken((String)reader.getAttributeName(i).getLocalPart()) != XmlToken.Uri) continue;
                    String uriValue = reader.getAttributeValue(i);
                    reader.next();
                    return uriValue;
                }
            }
            reader.next();
            return null;
        }
        catch (XMLStreamException e) {
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE(), (Throwable)e));
        }
    }

    public static String relativeToAbsoluteUrl(String relativeUri, String baseUri) {
        if ('#' != relativeUri.charAt(0)) {
            return relativeUri;
        }
        return null == baseUri ? relativeUri : baseUri + relativeUri;
    }

    public final class PolicyRecord {
        PolicyRecord next;
        PolicySourceModel policyModel;
        Set<String> unresolvedURIs;
        private String uri;

        PolicyRecord() {
        }

        PolicyRecord insert(PolicyRecord insertedRec) {
            if (null == insertedRec.unresolvedURIs || insertedRec.unresolvedURIs.isEmpty()) {
                insertedRec.next = this;
                return insertedRec;
            }
            PolicyRecord head = this;
            PolicyRecord oneBeforeCurrent = null;
            PolicyRecord current = head;
            while (null != current.next) {
                if (null != current.unresolvedURIs && current.unresolvedURIs.contains(insertedRec.uri)) {
                    if (null == oneBeforeCurrent) {
                        insertedRec.next = current;
                        return insertedRec;
                    }
                    oneBeforeCurrent.next = insertedRec;
                    insertedRec.next = current;
                    return head;
                }
                if (insertedRec.unresolvedURIs.remove(current.uri) && insertedRec.unresolvedURIs.isEmpty()) {
                    insertedRec.next = current.next;
                    current.next = insertedRec;
                    return head;
                }
                oneBeforeCurrent = current;
                current = current.next;
            }
            insertedRec.next = null;
            current.next = insertedRec;
            return head;
        }

        public void setUri(String uri, String id) throws PolicyException {
            if (SafePolicyReader.this.qualifiedPolicyUris.contains(uri)) {
                throw (PolicyException)LOGGER.logSevereException((Throwable)new PolicyException(PolicyMessages.WSP_1020_DUPLICATE_ID(id)));
            }
            this.uri = uri;
            SafePolicyReader.this.qualifiedPolicyUris.add(uri);
        }

        public String getUri() {
            return this.uri;
        }

        public String toString() {
            String result = this.uri;
            if (null != this.next) {
                result = result + "->" + this.next.toString();
            }
            return result;
        }
    }
}

