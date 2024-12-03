/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.encryption.AbstractSerializer;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public class TransformSerializer
extends AbstractSerializer {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public TransformSerializer(boolean secureValidation) throws InvalidCanonicalizerException, TransformerConfigurationException {
        this("http://santuario.apache.org/c14n/physical", secureValidation);
    }

    public TransformSerializer(String canonAlg, boolean secureValidation) throws TransformerConfigurationException, InvalidCanonicalizerException {
        super(canonAlg, secureValidation);
        this.transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
        if (secureValidation) {
            try {
                this.transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                this.transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    @Override
    public Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException, IOException {
        byte[] fragment = TransformSerializer.createContext(source, ctx);
        try (ByteArrayInputStream is = new ByteArrayInputStream(fragment);){
            Node node = this.deserialize(ctx, new StreamSource(is));
            return node;
        }
    }

    private Node deserialize(Node ctx, Source source) throws XMLEncryptionException {
        try {
            Document contextDocument = null;
            contextDocument = 9 == ctx.getNodeType() ? (Document)ctx : ctx.getOwnerDocument();
            Transformer transformer = this.transformerFactory.newTransformer();
            DOMResult res = new DOMResult();
            DocumentFragment placeholder = contextDocument.createDocumentFragment();
            res.setNode(placeholder);
            transformer.transform(source, res);
            Node dummyChild = placeholder.getFirstChild();
            Node child = dummyChild.getFirstChild();
            if (child != null && child.getNextSibling() == null) {
                return child;
            }
            DocumentFragment docfrag = contextDocument.createDocumentFragment();
            while (child != null) {
                dummyChild.removeChild(child);
                docfrag.appendChild(child);
                child = dummyChild.getFirstChild();
            }
            return docfrag;
        }
        catch (Exception e) {
            throw new XMLEncryptionException(e);
        }
    }
}

