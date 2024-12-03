/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformEnvelopedSignature
extends TransformSpi {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    }

    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws TransformationException {
        Node signatureElement = TransformEnvelopedSignature.searchSignatureElement(transformElement);
        input.setExcludeNode(signatureElement);
        try {
            input.addNodeFilter(new EnvelopedNodeFilter(signatureElement));
        }
        catch (IOException | XMLParserException ex) {
            throw new TransformationException(ex);
        }
        return input;
    }

    private static Node searchSignatureElement(Node signatureElement) throws TransformationException {
        boolean found = false;
        while (signatureElement != null && signatureElement.getNodeType() != 9) {
            Element el = (Element)signatureElement;
            if (el.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && el.getLocalName().equals("Signature")) {
                found = true;
                break;
            }
            signatureElement = signatureElement.getParentNode();
        }
        if (!found) {
            throw new TransformationException("transform.envelopedSignatureTransformNotInSignatureElement");
        }
        return signatureElement;
    }

    static class EnvelopedNodeFilter
    implements NodeFilter {
        private final Node exclude;

        EnvelopedNodeFilter(Node n) {
            this.exclude = n;
        }

        @Override
        public int isNodeIncludeDO(Node n, int level) {
            if (n == this.exclude) {
                return -1;
            }
            return 1;
        }

        @Override
        public int isNodeInclude(Node n) {
            if (n == this.exclude || XMLUtils.isDescendantOrSelf(this.exclude, n)) {
                return -1;
            }
            return 1;
        }
    }
}

