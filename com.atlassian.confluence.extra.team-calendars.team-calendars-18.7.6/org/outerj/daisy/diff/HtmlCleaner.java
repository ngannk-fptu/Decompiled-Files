/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff;

import java.io.IOException;
import org.outerj.daisy.diff.XslFilter;
import org.outerj.daisy.diff.helper.NekoHtmlParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlCleaner {
    private NekoHtmlParser parser = new NekoHtmlParser();
    private XslFilter filter = new XslFilter();

    public void cleanAndParse(InputSource source, ContentHandler consumer) throws IOException, SAXException {
        ContentHandler cleanupFilter = this.filter.xsl(consumer, "org/outerj/daisy/diff/cleanup.xsl");
        this.parser.parse(source, cleanupFilter);
    }
}

