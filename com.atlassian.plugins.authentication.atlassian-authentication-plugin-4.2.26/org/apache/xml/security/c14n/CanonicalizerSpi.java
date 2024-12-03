/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class CanonicalizerSpi {
    public void engineCanonicalize(byte[] inputBytes, OutputStream writer, boolean secureValidation) throws XMLParserException, IOException, CanonicalizationException {
        Document document = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);){
            document = XMLUtils.read(bais, secureValidation);
        }
        this.engineCanonicalizeSubTree(document, writer);
    }

    public abstract String engineGetURI();

    public abstract void engineCanonicalizeXPathNodeSet(Set<Node> var1, OutputStream var2) throws CanonicalizationException;

    public abstract void engineCanonicalizeXPathNodeSet(Set<Node> var1, String var2, OutputStream var3) throws CanonicalizationException;

    public abstract void engineCanonicalizeSubTree(Node var1, OutputStream var2) throws CanonicalizationException;

    public abstract void engineCanonicalizeSubTree(Node var1, String var2, OutputStream var3) throws CanonicalizationException;

    public abstract void engineCanonicalizeSubTree(Node var1, String var2, boolean var3, OutputStream var4) throws CanonicalizationException;
}

