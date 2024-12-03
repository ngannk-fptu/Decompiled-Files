/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.KeyInfoContent;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Element;

public class PGPData
extends SignatureElementProxy
implements KeyInfoContent {
    public PGPData(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override
    public String getBaseLocalName() {
        return "PGPData";
    }
}

