/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.transforms.implementations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class TransformXSLT
extends TransformSpi {
    static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
    static final String defaultXSLTSpecNSprefix = "xslt";
    static final String XSLTSTYLESHEET = "stylesheet";
    private static final Logger LOG = LoggerFactory.getLogger(TransformXSLT.class);

    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/1999/REC-xslt-19991116";
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream baos, Element transformElement, String baseURI, boolean secureValidation) throws IOException, TransformationException {
        try {
            StreamResult outputTarget;
            StreamSource xmlSource;
            StreamSource stylesheet;
            Element xsltElement = XMLUtils.selectNode(transformElement.getFirstChild(), XSLTSpecNS, XSLTSTYLESHEET, 0);
            if (xsltElement == null) {
                xsltElement = XMLUtils.selectNode(transformElement.getFirstChild(), XSLTSpecNS, "transform", 0);
            }
            if (xsltElement == null) {
                Object[] exArgs = new Object[]{"xslt:stylesheet", "Transform"};
                throw new TransformationException("xml.WrongContent", exArgs);
            }
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            if (secureValidation) {
                try {
                    tFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                    tFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
                Transformer transformer = tFactory.newTransformer();
                DOMSource source = new DOMSource(xsltElement);
                StreamResult result = new StreamResult(os);
                transformer.transform(source, result);
                stylesheet = new StreamSource(new ByteArrayInputStream(os.toByteArray()));
            }
            Transformer transformer = tFactory.newTransformer(stylesheet);
            try {
                transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
            }
            catch (Exception e) {
                LOG.warn("Unable to set Xalan line-separator property: " + e.getMessage());
            }
            ByteArrayInputStream is = new ByteArrayInputStream(input.getBytes());
            Throwable throwable = null;
            try {
                xmlSource = new StreamSource(is);
                if (baos == null) {
                    XMLSignatureInput xMLSignatureInput;
                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    Throwable throwable2 = null;
                    try {
                        StreamResult outputTarget2 = new StreamResult(baos1);
                        transformer.transform(xmlSource, outputTarget2);
                        XMLSignatureInput output = new XMLSignatureInput(baos1.toByteArray());
                        output.setSecureValidation(secureValidation);
                        xMLSignatureInput = output;
                    }
                    catch (Throwable throwable3) {
                        try {
                            throwable2 = throwable3;
                            throw throwable3;
                        }
                        catch (Throwable throwable4) {
                            TransformXSLT.$closeResource(throwable2, baos1);
                            throw throwable4;
                        }
                    }
                    TransformXSLT.$closeResource(throwable2, baos1);
                    return xMLSignatureInput;
                }
                outputTarget = new StreamResult(baos);
            }
            catch (Throwable throwable5) {
                throwable = throwable5;
                throw throwable5;
            }
            transformer.transform(xmlSource, outputTarget);
            XMLSignatureInput output = new XMLSignatureInput((byte[])null);
            output.setSecureValidation(secureValidation);
            output.setOutputStream(baos);
            return output;
            finally {
                TransformXSLT.$closeResource(throwable, is);
            }
        }
        catch (TransformerException | XMLSecurityException ex) {
            throw new TransformationException(ex);
        }
    }
}

