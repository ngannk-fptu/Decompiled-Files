/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.apache.jcp.xml.dsig.internal.dom.ApacheData;
import org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMStructure;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.apache.jcp.xml.dsig.internal.dom.DOMTransform;
import org.apache.jcp.xml.dsig.internal.dom.DOMURIDereferencer;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.Utils;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMReference
extends DOMStructure
implements Reference,
DOMURIReference {
    public static final int MAXIMUM_TRANSFORM_COUNT = 5;
    private static boolean useC14N11 = AccessController.doPrivileged(() -> Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
    private static final Logger LOG = LoggerFactory.getLogger(DOMReference.class);
    private final DigestMethod digestMethod;
    private final String id;
    private final List<Transform> transforms;
    private List<Transform> allTransforms;
    private final Data appliedTransformData;
    private Attr here;
    private final String uri;
    private final String type;
    private byte[] digestValue;
    private byte[] calcDigestValue;
    private Element refElem;
    private boolean digested = false;
    private boolean validated = false;
    private boolean validationStatus;
    private Data derefData;
    private InputStream dis;
    private MessageDigest md;
    private Provider provider;

    public DOMReference(String uri, String type, DigestMethod dm, List<? extends Transform> transforms, String id, Provider provider) {
        this(uri, type, dm, null, null, transforms, id, null, provider);
    }

    public DOMReference(String uri, String type, DigestMethod dm, List<? extends Transform> appliedTransforms, Data result, List<? extends Transform> transforms, String id, Provider provider) {
        this(uri, type, dm, appliedTransforms, result, transforms, id, null, provider);
    }

    public DOMReference(String uri, String type, DigestMethod dm, List<? extends Transform> appliedTransforms, Data result, List<? extends Transform> transforms, String id, byte[] digestValue, Provider provider) {
        int i;
        int size;
        if (dm == null) {
            throw new NullPointerException("DigestMethod must be non-null");
        }
        if (appliedTransforms == null) {
            this.allTransforms = new ArrayList<Transform>();
        } else {
            this.allTransforms = new ArrayList<Transform>(appliedTransforms);
            size = this.allTransforms.size();
            for (i = 0; i < size; ++i) {
                if (this.allTransforms.get(i) instanceof Transform) continue;
                throw new ClassCastException("appliedTransforms[" + i + "] is not a valid type");
            }
        }
        if (transforms == null) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = new ArrayList<Transform>(transforms);
            size = this.transforms.size();
            for (i = 0; i < size; ++i) {
                if (this.transforms.get(i) instanceof Transform) continue;
                throw new ClassCastException("transforms[" + i + "] is not a valid type");
            }
            this.allTransforms.addAll(this.transforms);
        }
        this.digestMethod = dm;
        this.uri = uri;
        if (uri != null && !uri.isEmpty()) {
            try {
                new URI(uri);
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        this.type = type;
        this.id = id;
        if (digestValue != null) {
            this.digestValue = (byte[])digestValue.clone();
            this.digested = true;
        }
        this.appliedTransformData = result;
        this.provider = provider;
    }

    public DOMReference(Element refElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        boolean secVal = Utils.secureValidation(context);
        Element nextSibling = DOMUtils.getFirstChildElement(refElem);
        ArrayList<Transform> newTransforms = new ArrayList<Transform>(5);
        if ("Transforms".equals(nextSibling.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(nextSibling.getNamespaceURI())) {
            Element transformElem = DOMUtils.getFirstChildElement(nextSibling, "Transform", "http://www.w3.org/2000/09/xmldsig#");
            newTransforms.add(new DOMTransform(transformElem, context, provider));
            transformElem = DOMUtils.getNextSiblingElement(transformElem);
            while (transformElem != null) {
                String localName = transformElem.getLocalName();
                String namespace = transformElem.getNamespaceURI();
                if (!"Transform".equals(localName) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    throw new MarshalException("Invalid element name: " + localName + ", expected Transform");
                }
                newTransforms.add(new DOMTransform(transformElem, context, provider));
                if (secVal && newTransforms.size() > 5) {
                    String error = "A maxiumum of 5 transforms per Reference are allowed with secure validation";
                    throw new MarshalException(error);
                }
                transformElem = DOMUtils.getNextSiblingElement(transformElem);
            }
            nextSibling = DOMUtils.getNextSiblingElement(nextSibling);
        }
        if (!"DigestMethod".equals(nextSibling.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(nextSibling.getNamespaceURI())) {
            throw new MarshalException("Invalid element name: " + nextSibling.getLocalName() + ", expected DigestMethod");
        }
        Element dmElem = nextSibling;
        this.digestMethod = DOMDigestMethod.unmarshal(dmElem);
        String digestMethodAlgorithm = this.digestMethod.getAlgorithm();
        if (secVal && "http://www.w3.org/2001/04/xmldsig-more#md5".equals(digestMethodAlgorithm)) {
            throw new MarshalException("It is forbidden to use algorithm " + this.digestMethod + " when secure validation is enabled");
        }
        Element dvElem = DOMUtils.getNextSiblingElement(dmElem, "DigestValue", "http://www.w3.org/2000/09/xmldsig#");
        String content = XMLUtils.getFullTextChildrenFromNode(dvElem);
        this.digestValue = XMLUtils.decode(content);
        if (DOMUtils.getNextSiblingElement(dvElem) != null) {
            throw new MarshalException("Unexpected element after DigestValue element");
        }
        this.uri = DOMUtils.getAttributeValue(refElem, "URI");
        Attr attr = refElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            refElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        this.type = DOMUtils.getAttributeValue(refElem, "Type");
        this.here = refElem.getAttributeNodeNS(null, "URI");
        this.refElem = refElem;
        this.transforms = newTransforms;
        this.allTransforms = this.transforms;
        this.appliedTransformData = null;
        this.provider = provider;
    }

    @Override
    public DigestMethod getDigestMethod() {
        return this.digestMethod;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public List<Transform> getTransforms() {
        return Collections.unmodifiableList(this.allTransforms);
    }

    @Override
    public byte[] getDigestValue() {
        return this.digestValue == null ? null : (byte[])this.digestValue.clone();
    }

    @Override
    public byte[] getCalculatedDigestValue() {
        return this.calcDigestValue == null ? null : (byte[])this.calcDigestValue.clone();
    }

    @Override
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        LOG.debug("Marshalling Reference");
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        this.refElem = DOMUtils.createElement(ownerDoc, "Reference", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttributeID(this.refElem, "Id", this.id);
        DOMUtils.setAttribute(this.refElem, "URI", this.uri);
        DOMUtils.setAttribute(this.refElem, "Type", this.type);
        if (!this.allTransforms.isEmpty()) {
            Element transformsElem = DOMUtils.createElement(ownerDoc, "Transforms", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            this.refElem.appendChild(transformsElem);
            for (Transform transform : this.allTransforms) {
                ((DOMStructure)((Object)transform)).marshal(transformsElem, dsPrefix, context);
            }
        }
        ((DOMDigestMethod)this.digestMethod).marshal(this.refElem, dsPrefix, context);
        LOG.debug("Adding digestValueElem");
        Element digestValueElem = DOMUtils.createElement(ownerDoc, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        if (this.digestValue != null) {
            digestValueElem.appendChild(ownerDoc.createTextNode(XMLUtils.encodeToString(this.digestValue)));
        }
        this.refElem.appendChild(digestValueElem);
        parent.appendChild(this.refElem);
        this.here = this.refElem.getAttributeNodeNS(null, "URI");
    }

    public void digest(XMLSignContext signContext) throws XMLSignatureException {
        Data data = null;
        data = this.appliedTransformData == null ? this.dereference(signContext) : this.appliedTransformData;
        this.digestValue = this.transform(data, signContext);
        String encodedDV = XMLUtils.encodeToString(this.digestValue);
        LOG.debug("Reference object uri = {}", (Object)this.uri);
        Element digestElem = DOMUtils.getLastChildElement(this.refElem);
        if (digestElem == null) {
            throw new XMLSignatureException("DigestValue element expected");
        }
        DOMUtils.removeAllChildren(digestElem);
        digestElem.appendChild(this.refElem.getOwnerDocument().createTextNode(encodedDV));
        this.digested = true;
        LOG.debug("Reference digesting completed");
    }

    @Override
    public boolean validate(XMLValidateContext validateContext) throws XMLSignatureException {
        if (validateContext == null) {
            throw new NullPointerException("validateContext cannot be null");
        }
        if (this.validated) {
            return this.validationStatus;
        }
        Data data = this.dereference(validateContext);
        this.calcDigestValue = this.transform(data, validateContext);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Expected digest: " + XMLUtils.encodeToString(this.digestValue));
            LOG.debug("Actual digest: " + XMLUtils.encodeToString(this.calcDigestValue));
        }
        this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
        this.validated = true;
        return this.validationStatus;
    }

    @Override
    public Data getDereferencedData() {
        return this.derefData;
    }

    @Override
    public InputStream getDigestInputStream() {
        return this.dis;
    }

    private Data dereference(XMLCryptoContext context) throws XMLSignatureException {
        Data data = null;
        URIDereferencer deref = context.getURIDereferencer();
        if (deref == null) {
            deref = DOMURIDereferencer.INSTANCE;
        }
        try {
            data = deref.dereference(this, context);
            LOG.debug("URIDereferencer class name: {}", (Object)deref.getClass().getName());
            LOG.debug("Data class name: {}", (Object)data.getClass().getName());
        }
        catch (URIReferenceException ure) {
            throw new XMLSignatureException(ure);
        }
        return data;
    }

    /*
     * Exception decompiling
     */
    private byte[] transform(Data dereferencedData, XMLCryptoContext context) throws XMLSignatureException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public Node getHere() {
        return this.here;
    }

    public boolean equals(Object o) {
        boolean urisEqual;
        boolean idsEqual;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        Reference oref = (Reference)o;
        boolean bl = this.id == null ? oref.getId() == null : (idsEqual = this.id.equals(oref.getId()));
        boolean bl2 = this.uri == null ? oref.getURI() == null : (urisEqual = this.uri.equals(oref.getURI()));
        boolean typesEqual = this.type == null ? oref.getType() == null : this.type.equals(oref.getType());
        boolean digestValuesEqual = Arrays.equals(this.digestValue, oref.getDigestValue());
        return this.digestMethod.equals(oref.getDigestMethod()) && idsEqual && urisEqual && typesEqual && this.allTransforms.equals(oref.getTransforms()) && digestValuesEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = 31 * result + this.id.hashCode();
        }
        if (this.uri != null) {
            result = 31 * result + this.uri.hashCode();
        }
        if (this.type != null) {
            result = 31 * result + this.type.hashCode();
        }
        if (this.digestValue != null) {
            result = 31 * result + Arrays.hashCode(this.digestValue);
        }
        result = 31 * result + this.digestMethod.hashCode();
        result = 31 * result + this.allTransforms.hashCode();
        return result;
    }

    boolean isDigested() {
        return this.digested;
    }

    private static Data copyDerefData(Data dereferencedData) {
        if (dereferencedData instanceof ApacheData) {
            ApacheData ad = (ApacheData)dereferencedData;
            XMLSignatureInput xsi = ad.getXMLSignatureInput();
            if (xsi.isNodeSet()) {
                try {
                    final Set<Node> s = xsi.getNodeSet();
                    return new NodeSetData(){

                        @Override
                        public Iterator<Node> iterator() {
                            return s.iterator();
                        }
                    };
                }
                catch (Exception e) {
                    LOG.warn("cannot cache dereferenced data: " + e);
                    return null;
                }
            }
            if (xsi.isElement()) {
                return new DOMSubTreeData(xsi.getSubNode(), xsi.isExcludeComments());
            }
            if (xsi.isOctetStream() || xsi.isByteArray()) {
                try {
                    return new OctetStreamData(xsi.getOctetStream(), xsi.getSourceURI(), xsi.getMIMEType());
                }
                catch (IOException ioe) {
                    LOG.warn("cannot cache dereferenced data: " + ioe);
                    return null;
                }
            }
        }
        return dereferencedData;
    }
}

