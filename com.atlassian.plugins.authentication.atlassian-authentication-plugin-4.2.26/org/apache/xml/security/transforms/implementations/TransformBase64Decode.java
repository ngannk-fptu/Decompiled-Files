/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TransformBase64Decode
extends TransformSpi {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#base64";
    }

    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws IOException, CanonicalizationException, TransformationException {
        if (input.isElement()) {
            Node el = input.getSubNode();
            if (input.getSubNode().getNodeType() == 3) {
                el = el.getParentNode();
            }
            StringBuilder sb = new StringBuilder();
            this.traverseElement((Element)el, sb);
            if (os == null) {
                byte[] decodedBytes = XMLUtils.decode(sb.toString());
                XMLSignatureInput output = new XMLSignatureInput(decodedBytes);
                output.setSecureValidation(secureValidation);
                return output;
            }
            byte[] bytes = XMLUtils.decode(sb.toString());
            os.write(bytes);
            XMLSignatureInput output = new XMLSignatureInput((byte[])null);
            output.setSecureValidation(secureValidation);
            output.setOutputStream(os);
            return output;
        }
        if (input.isOctetStream() || input.isNodeSet()) {
            if (os == null) {
                byte[] base64Bytes = input.getBytes();
                byte[] decodedBytes = XMLUtils.decode(base64Bytes);
                XMLSignatureInput output = new XMLSignatureInput(decodedBytes);
                output.setSecureValidation(secureValidation);
                return output;
            }
            if (input.isByteArray() || input.isNodeSet()) {
                byte[] bytes = XMLUtils.decode(input.getBytes());
                os.write(bytes);
            } else {
                byte[] inputBytes = JavaUtils.getBytesFromStream(input.getOctetStreamReal());
                byte[] bytes = XMLUtils.decode(inputBytes);
                os.write(bytes);
            }
            XMLSignatureInput output = new XMLSignatureInput((byte[])null);
            output.setSecureValidation(secureValidation);
            output.setOutputStream(os);
            return output;
        }
        throw new TransformationException("empty", new Object[]{"Unrecognized XMLSignatureInput state"});
    }

    private void traverseElement(Element node, StringBuilder sb) {
        for (Node sibling = node.getFirstChild(); sibling != null; sibling = sibling.getNextSibling()) {
            if (1 == sibling.getNodeType()) {
                this.traverseElement((Element)sibling, sb);
                continue;
            }
            if (3 != sibling.getNodeType()) continue;
            sb.append(((Text)sibling).getData());
        }
    }
}

