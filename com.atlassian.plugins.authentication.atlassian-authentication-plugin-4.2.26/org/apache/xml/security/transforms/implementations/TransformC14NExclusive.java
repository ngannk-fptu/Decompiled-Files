/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformC14NExclusive
extends TransformSpi {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws CanonicalizationException {
        try {
            String inclusiveNamespaces = null;
            if (this.length(transformElement, "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
                Element inclusiveElement = XMLUtils.selectNode(transformElement.getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
                inclusiveNamespaces = new InclusiveNamespaces(inclusiveElement, baseURI).getInclusiveNamespaces();
            }
            Canonicalizer20010315Excl c14n = this.getCanonicalizer();
            if (os == null && (input.isOctetStream() || input.isElement() || input.isNodeSet())) {
                try (ByteArrayOutputStream writer = new ByteArrayOutputStream();){
                    c14n.engineCanonicalize(input, inclusiveNamespaces, writer, secureValidation);
                    writer.flush();
                    XMLSignatureInput output = new XMLSignatureInput(writer.toByteArray());
                    output.setSecureValidation(secureValidation);
                    XMLSignatureInput xMLSignatureInput = output;
                    return xMLSignatureInput;
                }
                catch (IOException ex) {
                    throw new CanonicalizationException("empty", new Object[]{ex.getMessage()});
                }
            }
            c14n.engineCanonicalize(input, inclusiveNamespaces, os, secureValidation);
            XMLSignatureInput output = new XMLSignatureInput((byte[])null);
            output.setSecureValidation(secureValidation);
            output.setOutputStream(os);
            return output;
        }
        catch (XMLSecurityException ex) {
            throw new CanonicalizationException(ex);
        }
    }

    protected Canonicalizer20010315Excl getCanonicalizer() {
        return new Canonicalizer20010315ExclOmitComments();
    }

    private int length(Element element, String namespace, String localname) {
        int number = 0;
        for (Node sibling = element.getFirstChild(); sibling != null; sibling = sibling.getNextSibling()) {
            if (!localname.equals(sibling.getLocalName()) || !namespace.equals(sibling.getNamespaceURI())) continue;
            ++number;
        }
        return number;
    }
}

