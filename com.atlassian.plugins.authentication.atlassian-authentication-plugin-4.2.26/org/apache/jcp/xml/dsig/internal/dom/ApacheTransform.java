/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jcp.xml.dsig.internal.dom;

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
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.apache.jcp.xml.dsig.internal.dom.ApacheData;
import org.apache.jcp.xml.dsig.internal.dom.ApacheNodeSetData;
import org.apache.jcp.xml.dsig.internal.dom.ApacheOctetStreamData;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.Utils;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ApacheTransform
extends TransformService {
    private static final Logger LOG;
    private Transform transform;
    protected Document ownerDoc;
    protected Element transformElem;
    protected TransformParameterSpec params;

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

    @Override
    public Data transform(Data data, XMLCryptoContext xc) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        return this.transformIt(data, xc, null);
    }

    @Override
    public Data transform(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        if (os == null) {
            throw new NullPointerException("output stream must not be null");
        }
        return this.transformIt(data, xc, os);
    }

    private Data transformIt(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        XMLSignatureInput in;
        String algorithm;
        if (this.ownerDoc == null) {
            throw new TransformException("transform must be marshalled");
        }
        if (this.transform == null) {
            try {
                this.transform = new Transform(this.ownerDoc, this.getAlgorithm(), this.transformElem.getChildNodes());
                this.transform.setElement(this.transformElem, xc.getBaseURI());
                LOG.debug("Created transform for algorithm: {}", (Object)this.getAlgorithm());
            }
            catch (Exception ex) {
                throw new TransformException("Couldn't find Transform for: " + this.getAlgorithm(), ex);
            }
        }
        if (Utils.secureValidation(xc) && "http://www.w3.org/TR/1999/REC-xslt-19991116".equals(algorithm = this.getAlgorithm())) {
            throw new TransformException("Transform " + algorithm + " is forbidden when secure validation is enabled");
        }
        if (data instanceof ApacheData) {
            LOG.debug("ApacheData = true");
            in = ((ApacheData)data).getXMLSignatureInput();
        } else if (data instanceof NodeSetData) {
            LOG.debug("isNodeSet() = true");
            if (data instanceof DOMSubTreeData) {
                LOG.debug("DOMSubTreeData = true");
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
            if (os != null) {
                if (!(in = this.transform.performTransform(in, os, secVal)).isNodeSet() && !in.isElement()) {
                    return null;
                }
            } else {
                in = this.transform.performTransform(in, secVal);
            }
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
        LOG = LoggerFactory.getLogger(ApacheTransform.class);
    }
}

