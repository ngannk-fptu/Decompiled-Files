/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.DOMParser
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 */
package net.sourceforge.htmlunit.cyberneko.parsers;

import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DOMParser
extends org.apache.xerces.parsers.DOMParser {
    public DOMParser() {
        super((XMLParserConfiguration)new HTMLConfiguration());
        try {
            this.setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.apache.html.dom.HTMLDocumentImpl");
        }
        catch (SAXNotRecognizedException e) {
            throw new RuntimeException("http://apache.org/xml/properties/dom/document-class-name property not recognized");
        }
        catch (SAXNotSupportedException e) {
            throw new RuntimeException("http://apache.org/xml/properties/dom/document-class-name property not supported");
        }
    }
}

