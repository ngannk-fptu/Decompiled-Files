/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.grammars;

import org.apache.xerces.xni.XMLResourceIdentifier;

public interface XMLGrammarDescription
extends XMLResourceIdentifier {
    public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String XML_DTD = "http://www.w3.org/TR/REC-xml";

    public String getGrammarType();
}

