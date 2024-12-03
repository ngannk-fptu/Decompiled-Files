/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.w3c.dom.Element;

public class TransformC14N
extends TransformSpi {
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws CanonicalizationException {
        Canonicalizer20010315 c14n = this.getCanonicalizer();
        if (os == null && (input.isOctetStream() || input.isElement() || input.isNodeSet())) {
            try (ByteArrayOutputStream writer = new ByteArrayOutputStream();){
                c14n.engineCanonicalize(input, (OutputStream)writer, secureValidation);
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
        c14n.engineCanonicalize(input, os, secureValidation);
        XMLSignatureInput output = new XMLSignatureInput((byte[])null);
        output.setSecureValidation(secureValidation);
        output.setOutputStream(os);
        return output;
    }

    protected Canonicalizer20010315 getCanonicalizer() {
        return new Canonicalizer20010315OmitComments();
    }
}

