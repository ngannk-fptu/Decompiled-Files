/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  org.apache.xerces.parsers.DOMParser
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.importexport.ImportExportException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlToDomParser {
    private final DOMParser parser;
    private StringBuffer collectedStyles;

    public HtmlToDomParser(DOMParser parser) {
        this.parser = parser;
        try {
            this.collectedStyles = (StringBuffer)parser.getProperty("http://atlassian.com/html/properties/stylecollector");
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
    }

    public Document parse(String html) throws ImportExportException {
        return this.parse(new StringReader(html));
    }

    Document parse(Reader reader) throws ImportExportException {
        try {
            this.parser.parse(new InputSource(reader));
        }
        catch (SAXException ex) {
            throw new ImportExportException((Throwable)ex);
        }
        catch (IOException ex) {
            throw new ImportExportException((Throwable)ex);
        }
        Document dom = this.parser.getDocument();
        this.injectCollectedStyles(dom);
        return dom;
    }

    private void injectCollectedStyles(Document dom) {
        NodeList styleNodes;
        if (this.collectedStyles != null && this.collectedStyles.length() > 0 && (styleNodes = dom.getElementsByTagName("style")).getLength() > 0) {
            Node styleInHead = styleNodes.item(0);
            String styles = styleInHead.getTextContent();
            styleInHead.setTextContent(styles + "\r\n" + this.collectedStyles);
        }
    }
}

