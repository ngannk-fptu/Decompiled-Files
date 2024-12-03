/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCListener;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.DSCParserConstants;
import org.apache.xmlgraphics.ps.dsc.NestedDocumentHandler;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;

public class DefaultNestedDocumentHandler
implements DSCParserConstants,
NestedDocumentHandler,
DSCListener {
    private PSGenerator gen;

    public DefaultNestedDocumentHandler(PSGenerator gen) {
        this.gen = gen;
    }

    @Override
    public void handle(DSCEvent event, DSCParser parser) throws IOException, DSCException {
        this.processEvent(event, parser);
    }

    @Override
    public void processEvent(DSCEvent event, DSCParser parser) throws IOException, DSCException {
        if (event.isDSCComment()) {
            DSCComment comment = event.asDSCComment();
            if ("BeginDocument".equals(comment.getName())) {
                if (this.gen != null) {
                    comment.generate(this.gen);
                }
                boolean checkEOF = parser.isCheckEOF();
                parser.setCheckEOF(false);
                parser.setListenersDisabled(true);
                comment = parser.nextDSCComment("EndDocument", this.gen);
                if (comment == null) {
                    throw new DSCException("File is not DSC-compliant: Didn't find an EndDocument");
                }
                if (this.gen != null) {
                    comment.generate(this.gen);
                }
                parser.setCheckEOF(checkEOF);
                parser.setListenersDisabled(false);
                parser.next();
            } else if ("BeginData".equals(comment.getName())) {
                if (this.gen != null) {
                    comment.generate(this.gen);
                }
                boolean checkEOF = parser.isCheckEOF();
                parser.setCheckEOF(false);
                parser.setListenersDisabled(true);
                comment = parser.nextDSCComment("EndData", this.gen);
                if (comment == null) {
                    throw new DSCException("File is not DSC-compliant: Didn't find an EndData");
                }
                if (this.gen != null) {
                    comment.generate(this.gen);
                }
                parser.setCheckEOF(checkEOF);
                parser.setListenersDisabled(false);
                parser.next();
            }
        }
    }
}

