/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCFilter;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.DSCParserConstants;
import org.apache.xmlgraphics.ps.dsc.DefaultNestedDocumentHandler;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPage;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPages;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;
import org.apache.xmlgraphics.ps.dsc.events.DSCHeaderComment;
import org.apache.xmlgraphics.ps.dsc.tools.DSCTools;

public final class PageExtractor
implements DSCParserConstants {
    private PageExtractor() {
    }

    public static void extractPages(InputStream in, OutputStream out, int from, int to) throws IOException, DSCException {
        if (from <= 0) {
            throw new IllegalArgumentException("'from' page number must be 1 or higher");
        }
        if (to < from) {
            throw new IllegalArgumentException("'to' page number must be equal or larger than the 'from' page number");
        }
        DSCParser parser = new DSCParser(in);
        PSGenerator gen = new PSGenerator(out);
        parser.addListener(new DefaultNestedDocumentHandler(gen));
        int pageCount = 0;
        DSCHeaderComment header = DSCTools.checkAndSkipDSC30Header(parser);
        header.generate(gen);
        DSCCommentPages pages = new DSCCommentPages(to - from + 1);
        pages.generate(gen);
        parser.setFilter(new DSCFilter(){

            @Override
            public boolean accept(DSCEvent event) {
                if (event.isDSCComment()) {
                    return !event.asDSCComment().getName().equals("Pages");
                }
                return true;
            }
        });
        DSCComment pageOrTrailer = parser.nextDSCComment("Page", gen);
        if (pageOrTrailer == null) {
            throw new DSCException("Page expected, but none found");
        }
        parser.setFilter(null);
        do {
            DSCCommentPage page;
            boolean validPage;
            boolean bl = validPage = (page = (DSCCommentPage)pageOrTrailer).getPagePosition() >= from && page.getPagePosition() <= to;
            if (validPage) {
                page.setPagePosition(page.getPagePosition() - from + 1);
                page.generate(gen);
                ++pageCount;
            }
            if ((pageOrTrailer = DSCTools.nextPageOrTrailer(parser, validPage ? gen : null)) != null) continue;
            throw new DSCException("File is not DSC-compliant: Unexpected end of file");
        } while ("Page".equals(pageOrTrailer.getName()));
        pageOrTrailer.generate(gen);
        while (parser.hasNext()) {
            DSCEvent event = parser.nextEvent();
            event.generate(gen);
        }
    }
}

