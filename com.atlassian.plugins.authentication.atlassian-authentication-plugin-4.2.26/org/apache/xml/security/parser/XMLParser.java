/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.parser;

import java.io.InputStream;
import org.apache.xml.security.parser.XMLParserException;
import org.w3c.dom.Document;

public interface XMLParser {
    public Document parse(InputStream var1, boolean var2) throws XMLParserException;
}

