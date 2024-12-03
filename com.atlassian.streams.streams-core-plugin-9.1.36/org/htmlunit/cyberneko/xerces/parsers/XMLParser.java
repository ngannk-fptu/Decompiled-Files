/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.parsers;

import java.io.IOException;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParserConfiguration;

public abstract class XMLParser {
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/error-handler"};
    protected final XMLParserConfiguration fConfiguration;

    protected XMLParser(XMLParserConfiguration config) {
        this.fConfiguration = config;
        this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    }

    public void parse(XMLInputSource inputSource) throws XNIException, IOException {
        this.reset();
        this.fConfiguration.parse(inputSource);
    }

    protected void reset() throws XNIException {
    }
}

