/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  org.apache.xerces.parsers.DOMParser
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 *  org.cyberneko.html.HTMLConfiguration
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.html.ConfluenceHtmlToXmlFilter;
import com.atlassian.confluence.extra.flyingpdf.html.HtmlToDomParser;
import com.atlassian.confluence.extra.flyingpdf.html.LinkFixer;
import com.atlassian.confluence.importexport.ImportExportException;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class HtmlConverterUtils {
    public static final String STYLECOLLECTOR_KEY = "http://atlassian.com/html/properties/stylecollector";
    public static final String LINKFIXER_KEY = "http://atlassian.com/html/properties/linkfixer";

    public static HtmlToDomParser getHtmlToXhtmlParser(LinkFixer linkFixer) throws ImportExportException {
        HTMLConfiguration config = new HTMLConfiguration();
        config.addRecognizedProperties(new String[]{STYLECOLLECTOR_KEY, LINKFIXER_KEY});
        DOMParser parser = new DOMParser((XMLParserConfiguration)config);
        StringBuffer styleCollector = new StringBuffer();
        try {
            parser.setProperty("http://cyberneko.org/html/properties/filters", (Object)new XMLDocumentFilter[]{new ConfluenceHtmlToXmlFilter()});
            parser.setFeature("http://cyberneko.org/html/features/override-doctype", true);
            parser.setProperty("http://cyberneko.org/html/properties/doctype/pubid", (Object)"-//W3C//DTD XHTML 1.0 Transitional//EN");
            parser.setProperty("http://cyberneko.org/html/properties/doctype/sysid", (Object)"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", (Object)"UTF-8");
            parser.setProperty(STYLECOLLECTOR_KEY, (Object)styleCollector);
            parser.setProperty(LINKFIXER_KEY, (Object)linkFixer);
        }
        catch (SAXNotRecognizedException ex) {
            throw new ImportExportException((Throwable)ex);
        }
        catch (SAXNotSupportedException ex) {
            throw new ImportExportException((Throwable)ex);
        }
        return new HtmlToDomParser(parser);
    }
}

