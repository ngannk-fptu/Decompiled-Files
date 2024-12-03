/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xhtml.parsing.SelfClosingTagPreservingHTMLTagBalancer
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.owasp.validator.html.scan.CustomSAXParser
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConfluenceXhtmlCyberNekoWriter;
import com.atlassian.confluence.content.render.xhtml.IllegalAttributeFilter;
import com.atlassian.confluence.content.render.xhtml.ScriptWrappingFilter;
import com.atlassian.confluence.content.render.xhtml.XmlAttributeEncodingFilter;
import com.atlassian.xhtml.parsing.SelfClosingTagPreservingHTMLTagBalancer;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.owasp.validator.html.scan.CustomSAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class CybernekoHtmlToXmlConverter {
    CybernekoHtmlToXmlConverter() {
    }

    public String convert(String unclean) {
        StringWriter result = new StringWriter();
        CustomSAXParser parser = new CustomSAXParser();
        InputSource inputSource = new InputSource(new StringReader(unclean));
        try {
            parser.setProperty("http://cyberneko.org/html/properties/filters", (Object)new XMLDocumentFilter[]{new SelfClosingTagPreservingHTMLTagBalancer(parser.getHtmlConfiguration()), new ScriptWrappingFilter(), new IllegalAttributeFilter(), new XmlAttributeEncodingFilter(), new ConfluenceXhtmlCyberNekoWriter(result, "UTF-8")});
            parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
            parser.parse(inputSource);
        }
        catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }
}

