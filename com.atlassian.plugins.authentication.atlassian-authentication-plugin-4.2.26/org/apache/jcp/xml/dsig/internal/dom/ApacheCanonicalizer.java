/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.dom.ApacheData;
import org.apache.jcp.xml.dsig.internal.dom.ApacheNodeSetData;
import org.apache.jcp.xml.dsig.internal.dom.ApacheOctetStreamData;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.Utils;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ApacheCanonicalizer
extends TransformService {
    private static final Logger LOG;
    protected Canonicalizer canonicalizer;
    private Transform apacheTransform;
    protected String inclusiveNamespaces;
    protected C14NMethodParameterSpec params;
    protected Document ownerDoc;
    protected Element transformElem;

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    @Override
    public void init(XMLStructure parent, XMLCryptoContext context) throws InvalidAlgorithmParameterException {
        if (context != null && !(context instanceof DOMCryptoContext)) {
            throw new ClassCastException("context must be of type DOMCryptoContext");
        }
        if (parent == null) {
            throw new NullPointerException();
        }
        if (!(parent instanceof DOMStructure)) {
            throw new ClassCastException("parent must be of type DOMStructure");
        }
        this.transformElem = (Element)((DOMStructure)parent).getNode();
        this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
    }

    @Override
    public void marshalParams(XMLStructure parent, XMLCryptoContext context) throws MarshalException {
        if (context != null && !(context instanceof DOMCryptoContext)) {
            throw new ClassCastException("context must be of type DOMCryptoContext");
        }
        if (parent == null) {
            throw new NullPointerException();
        }
        if (!(parent instanceof DOMStructure)) {
            throw new ClassCastException("parent must be of type DOMStructure");
        }
        this.transformElem = (Element)((DOMStructure)parent).getNode();
        this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
    }

    public Data canonicalize(Data data, XMLCryptoContext xc) throws TransformException {
        return this.canonicalize(data, xc, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Data canonicalize(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        if (this.canonicalizer == null) {
            try {
                this.canonicalizer = Canonicalizer.getInstance(this.getAlgorithm());
                LOG.debug("Created canonicalizer for algorithm: {}", (Object)this.getAlgorithm());
            }
            catch (InvalidCanonicalizerException ice) {
                throw new TransformException("Couldn't find Canonicalizer for: " + this.getAlgorithm() + ": " + ice.getMessage(), ice);
            }
        }
        boolean isByteArrayOutputStream = os == null;
        OutputStream writer = isByteArrayOutputStream ? new ByteArrayOutputStream() : os;
        try {
            boolean secVal = Utils.secureValidation(xc);
            Set<Node> nodeSet = null;
            if (data instanceof ApacheData) {
                XMLSignatureInput in = ((ApacheData)data).getXMLSignatureInput();
                if (in.isElement()) {
                    if (this.inclusiveNamespaces != null) {
                        this.canonicalizer.canonicalizeSubtree(in.getSubNode(), this.inclusiveNamespaces, writer);
                        return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
                    }
                    this.canonicalizer.canonicalizeSubtree(in.getSubNode(), writer);
                    return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
                }
                if (!in.isNodeSet()) {
                    this.canonicalizer.canonicalize(Utils.readBytesFromStream(in.getOctetStream()), writer, secVal);
                    return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
                }
                nodeSet = in.getNodeSet();
            } else {
                if (data instanceof DOMSubTreeData) {
                    DOMSubTreeData subTree = (DOMSubTreeData)data;
                    if (this.inclusiveNamespaces != null) {
                        this.canonicalizer.canonicalizeSubtree(subTree.getRoot(), this.inclusiveNamespaces, writer);
                        return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
                    }
                    this.canonicalizer.canonicalizeSubtree(subTree.getRoot(), writer);
                    return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
                }
                if (!(data instanceof NodeSetData)) {
                    this.canonicalizer.canonicalize(Utils.readBytesFromStream(((OctetStreamData)data).getOctetStream()), writer, secVal);
                    return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
                }
                NodeSetData nsd = (NodeSetData)data;
                Set<Node> ns = Utils.toNodeSet(nsd.iterator());
                nodeSet = ns;
                LOG.debug("Canonicalizing {} nodes", (Object)nodeSet.size());
            }
            if (this.inclusiveNamespaces != null) {
                this.canonicalizer.canonicalizeXPathNodeSet(nodeSet, this.inclusiveNamespaces, writer);
                return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
            }
            this.canonicalizer.canonicalizeXPathNodeSet(nodeSet, writer);
            return new OctetStreamData(new ByteArrayInputStream(this.getC14nBytes(writer, isByteArrayOutputStream)));
        }
        catch (Exception e) {
            throw new TransformException(e);
        }
    }

    private byte[] getC14nBytes(OutputStream outputStream, boolean isByteArrayOutputStream) {
        if (isByteArrayOutputStream) {
            return ((ByteArrayOutputStream)outputStream).toByteArray();
        }
        return null;
    }

    @Override
    public Data transform(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        XMLSignatureInput in;
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        if (os == null) {
            throw new NullPointerException("output stream must not be null");
        }
        if (this.ownerDoc == null) {
            throw new TransformException("transform must be marshalled");
        }
        if (this.apacheTransform == null) {
            try {
                this.apacheTransform = new Transform(this.ownerDoc, this.getAlgorithm(), this.transformElem.getChildNodes());
                this.apacheTransform.setElement(this.transformElem, xc.getBaseURI());
                LOG.debug("Created transform for algorithm: {}", (Object)this.getAlgorithm());
            }
            catch (Exception ex) {
                throw new TransformException("Couldn't find Transform for: " + this.getAlgorithm(), ex);
            }
        }
        if (data instanceof ApacheData) {
            LOG.debug("ApacheData = true");
            in = ((ApacheData)data).getXMLSignatureInput();
        } else if (data instanceof NodeSetData) {
            LOG.debug("isNodeSet() = true");
            if (data instanceof DOMSubTreeData) {
                DOMSubTreeData subTree = (DOMSubTreeData)data;
                in = new XMLSignatureInput(subTree.getRoot());
                in.setExcludeComments(subTree.excludeComments());
            } else {
                Set<Node> nodeSet = Utils.toNodeSet(((NodeSetData)data).iterator());
                in = new XMLSignatureInput(nodeSet);
            }
        } else {
            LOG.debug("isNodeSet() = false");
            try {
                in = new XMLSignatureInput(((OctetStreamData)data).getOctetStream());
            }
            catch (Exception ex) {
                throw new TransformException(ex);
            }
        }
        boolean secVal = Utils.secureValidation(xc);
        in.setSecureValidation(secVal);
        try {
            in = this.apacheTransform.performTransform(in, os, secVal);
            if (in.isOctetStream()) {
                return new ApacheOctetStreamData(in);
            }
            return new ApacheNodeSetData(in);
        }
        catch (Exception ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public final boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        }
        return false;
    }

    static {
        Init.init();
        LOG = LoggerFactory.getLogger(ApacheCanonicalizer.class);
    }
}

