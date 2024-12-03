/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.transforms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.InvalidTransformException;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.implementations.TransformBase64Decode;
import org.apache.xml.security.transforms.implementations.TransformC14N;
import org.apache.xml.security.transforms.implementations.TransformC14N11;
import org.apache.xml.security.transforms.implementations.TransformC14N11_WithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusive;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusiveWithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NWithComments;
import org.apache.xml.security.transforms.implementations.TransformEnvelopedSignature;
import org.apache.xml.security.transforms.implementations.TransformXPath;
import org.apache.xml.security.transforms.implementations.TransformXPath2Filter;
import org.apache.xml.security.transforms.implementations.TransformXSLT;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Transform
extends SignatureElementProxy {
    private static final Logger LOG = LoggerFactory.getLogger(Transform.class);
    private static Map<String, TransformSpi> transformSpiHash = new ConcurrentHashMap<String, TransformSpi>();
    private final TransformSpi transformSpi;

    public Transform(Document doc, String algorithmURI) throws InvalidTransformException {
        this(doc, algorithmURI, (NodeList)null);
    }

    public Transform(Document doc, String algorithmURI, Element contextChild) throws InvalidTransformException {
        super(doc);
        this.setLocalAttribute("Algorithm", algorithmURI);
        this.transformSpi = this.initializeTransform(algorithmURI);
        if (contextChild != null) {
            HelperNodeList contextNodes = new HelperNodeList();
            XMLUtils.addReturnToElement(doc, contextNodes);
            contextNodes.appendChild(contextChild);
            XMLUtils.addReturnToElement(doc, contextNodes);
            int length = contextNodes.getLength();
            for (int i = 0; i < length; ++i) {
                this.appendSelf(contextNodes.item(i).cloneNode(true));
            }
            LOG.debug("The NodeList is {}", (Object)contextNodes);
        }
    }

    public Transform(Document doc, String algorithmURI, NodeList contextNodes) throws InvalidTransformException {
        super(doc);
        this.setLocalAttribute("Algorithm", algorithmURI);
        this.transformSpi = this.initializeTransform(algorithmURI);
        if (contextNodes != null) {
            int length = contextNodes.getLength();
            for (int i = 0; i < length; ++i) {
                this.appendSelf(contextNodes.item(i).cloneNode(true));
            }
            LOG.debug("The NodeList is {}", (Object)contextNodes);
        }
    }

    public Transform(Element element, String baseURI) throws InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, baseURI);
        String algorithmURI = element.getAttributeNS(null, "Algorithm");
        if (algorithmURI == null || algorithmURI.length() == 0) {
            Object[] exArgs = new Object[]{"Algorithm", "Transform"};
            throw new TransformationException("xml.WrongContent", exArgs);
        }
        this.transformSpi = this.initializeTransform(algorithmURI);
    }

    public static void register(String algorithmURI, String implementingClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, InvalidTransformException {
        JavaUtils.checkRegisterPermission();
        TransformSpi transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi != null) {
            Object[] exArgs = new Object[]{algorithmURI, transformSpi};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        Class<?> transformSpiClass = ClassLoaderUtils.loadClass(implementingClass, Transform.class);
        try {
            transformSpiHash.put(algorithmURI, (TransformSpi)JavaUtils.newInstanceWithEmptyConstructor(transformSpiClass));
        }
        catch (IllegalAccessException | InstantiationException ex) {
            Object[] exArgs = new Object[]{algorithmURI};
            throw new InvalidTransformException(ex, "signature.Transform.UnknownTransform", exArgs);
        }
    }

    public static void register(String algorithmURI, Class<? extends TransformSpi> implementingClass) throws AlgorithmAlreadyRegisteredException, InvalidTransformException {
        JavaUtils.checkRegisterPermission();
        TransformSpi transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi != null) {
            Object[] exArgs = new Object[]{algorithmURI, transformSpi};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        try {
            transformSpiHash.put(algorithmURI, JavaUtils.newInstanceWithEmptyConstructor(implementingClass));
        }
        catch (IllegalAccessException | InstantiationException ex) {
            Object[] exArgs = new Object[]{algorithmURI};
            throw new InvalidTransformException(ex, "signature.Transform.UnknownTransform", exArgs);
        }
    }

    public static void registerDefaultAlgorithms() {
        transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#base64", new TransformBase64Decode());
        transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", new TransformC14N());
        transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", new TransformC14NWithComments());
        transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11", new TransformC14N11());
        transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", new TransformC14N11_WithComments());
        transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", new TransformC14NExclusive());
        transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", new TransformC14NExclusiveWithComments());
        transformSpiHash.put("http://www.w3.org/TR/1999/REC-xpath-19991116", new TransformXPath());
        transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#enveloped-signature", new TransformEnvelopedSignature());
        transformSpiHash.put("http://www.w3.org/TR/1999/REC-xslt-19991116", new TransformXSLT());
        transformSpiHash.put("http://www.w3.org/2002/06/xmldsig-filter2", new TransformXPath2Filter());
    }

    public String getURI() {
        return this.getLocalAttribute("Algorithm");
    }

    public XMLSignatureInput performTransform(XMLSignatureInput input, boolean secureValidation) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
        return this.performTransform(input, null, secureValidation);
    }

    public XMLSignatureInput performTransform(XMLSignatureInput input, OutputStream os, boolean secureValidation) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
        XMLSignatureInput result = null;
        try {
            result = this.transformSpi.enginePerformTransform(input, os, this.getElement(), this.baseURI, secureValidation);
        }
        catch (ParserConfigurationException ex) {
            Object[] exArgs = new Object[]{this.getURI(), "ParserConfigurationException"};
            throw new CanonicalizationException(ex, "signature.Transform.ErrorDuringTransform", exArgs);
        }
        catch (SAXException ex) {
            Object[] exArgs = new Object[]{this.getURI(), "SAXException"};
            throw new CanonicalizationException(ex, "signature.Transform.ErrorDuringTransform", exArgs);
        }
        return result;
    }

    @Override
    public String getBaseLocalName() {
        return "Transform";
    }

    private TransformSpi initializeTransform(String algorithmURI) throws InvalidTransformException {
        TransformSpi newTransformSpi = transformSpiHash.get(algorithmURI);
        if (newTransformSpi == null) {
            Object[] exArgs = new Object[]{algorithmURI};
            throw new InvalidTransformException("signature.Transform.UnknownTransform", exArgs);
        }
        LOG.debug("Create URI \"{}\" class \"{}\"", (Object)algorithmURI, newTransformSpi.getClass());
        return newTransformSpi;
    }
}

