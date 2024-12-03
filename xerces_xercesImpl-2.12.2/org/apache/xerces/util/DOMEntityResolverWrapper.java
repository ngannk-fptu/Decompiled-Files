/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMEntityResolverWrapper
implements XMLEntityResolver {
    private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
    private static final String XSD_TYPE = "http://www.w3.org/2001/XMLSchema";
    protected LSResourceResolver fEntityResolver;

    public DOMEntityResolverWrapper() {
    }

    public DOMEntityResolverWrapper(LSResourceResolver lSResourceResolver) {
        this.setEntityResolver(lSResourceResolver);
    }

    public void setEntityResolver(LSResourceResolver lSResourceResolver) {
        this.fEntityResolver = lSResourceResolver;
    }

    public LSResourceResolver getEntityResolver() {
        return this.fEntityResolver;
    }

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier xMLResourceIdentifier) throws XNIException, IOException {
        if (this.fEntityResolver != null) {
            LSInput lSInput;
            LSInput lSInput2 = lSInput = xMLResourceIdentifier == null ? this.fEntityResolver.resolveResource(null, null, null, null, null) : this.fEntityResolver.resolveResource(this.getType(xMLResourceIdentifier), xMLResourceIdentifier.getNamespace(), xMLResourceIdentifier.getPublicId(), xMLResourceIdentifier.getLiteralSystemId(), xMLResourceIdentifier.getBaseSystemId());
            if (lSInput != null) {
                String string = lSInput.getPublicId();
                String string2 = lSInput.getSystemId();
                String string3 = lSInput.getBaseURI();
                InputStream inputStream = lSInput.getByteStream();
                Reader reader = lSInput.getCharacterStream();
                String string4 = lSInput.getEncoding();
                String string5 = lSInput.getStringData();
                XMLInputSource xMLInputSource = new XMLInputSource(string, string2, string3);
                if (reader != null) {
                    xMLInputSource.setCharacterStream(reader);
                } else if (inputStream != null) {
                    xMLInputSource.setByteStream(inputStream);
                } else if (string5 != null && string5.length() != 0) {
                    xMLInputSource.setCharacterStream(new StringReader(string5));
                }
                xMLInputSource.setEncoding(string4);
                return xMLInputSource;
            }
        }
        return null;
    }

    private String getType(XMLResourceIdentifier xMLResourceIdentifier) {
        XMLGrammarDescription xMLGrammarDescription;
        if (xMLResourceIdentifier instanceof XMLGrammarDescription && XSD_TYPE.equals((xMLGrammarDescription = (XMLGrammarDescription)xMLResourceIdentifier).getGrammarType())) {
            return XSD_TYPE;
        }
        return XML_TYPE;
    }
}

