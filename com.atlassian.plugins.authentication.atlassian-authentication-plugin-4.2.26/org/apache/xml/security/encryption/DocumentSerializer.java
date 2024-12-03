/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.encryption.AbstractSerializer;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocumentSerializer
extends AbstractSerializer {
    public DocumentSerializer(boolean secureValidation) throws InvalidCanonicalizerException {
        this("http://santuario.apache.org/c14n/physical", secureValidation);
    }

    public DocumentSerializer(String canonAlg, boolean secureValidation) throws InvalidCanonicalizerException {
        super(canonAlg, secureValidation);
    }

    @Override
    public Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException, IOException {
        byte[] fragment = DocumentSerializer.createContext(source, ctx);
        try (ByteArrayInputStream is = new ByteArrayInputStream(fragment);){
            Node node = this.deserialize(ctx, is);
            return node;
        }
    }

    private Node deserialize(Node ctx, InputStream inputStream) throws XMLEncryptionException {
        try {
            Document d = XMLUtils.read(inputStream, this.secureValidation);
            Document contextDocument = null;
            contextDocument = 9 == ctx.getNodeType() ? (Document)ctx : ctx.getOwnerDocument();
            Element fragElt = (Element)contextDocument.importNode(d.getDocumentElement(), true);
            DocumentFragment result = contextDocument.createDocumentFragment();
            Node child = fragElt.getFirstChild();
            while (child != null) {
                fragElt.removeChild(child);
                result.appendChild(child);
                child = fragElt.getFirstChild();
            }
            return result;
        }
        catch (XMLParserException pce) {
            throw new XMLEncryptionException(pce);
        }
    }
}

