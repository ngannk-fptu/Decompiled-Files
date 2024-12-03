/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlgraphics.ps.dsc.DSCHandler;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;

public class EventRecorder
implements DSCHandler {
    private List events = new ArrayList();

    public void replay(DSCHandler handler) throws IOException {
        for (Object obj : this.events) {
            if (obj instanceof PSLine) {
                handler.line(((PSLine)obj).getLine());
                continue;
            }
            if (obj instanceof PSComment) {
                handler.comment(((PSComment)obj).getComment());
                continue;
            }
            if (obj instanceof DSCComment) {
                handler.handleDSCComment((DSCComment)obj);
                continue;
            }
            throw new IllegalStateException("Unsupported class type");
        }
    }

    @Override
    public void comment(String comment) throws IOException {
        this.events.add(new PSComment(comment));
    }

    @Override
    public void handleDSCComment(DSCComment comment) throws IOException {
        this.events.add(comment);
    }

    @Override
    public void line(String line) throws IOException {
        this.events.add(new PSLine(line));
    }

    @Override
    public void startDocument(String header) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only used to handle parts of a document");
    }

    @Override
    public void endDocument() throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only used to handle parts of a document");
    }

    private static class PSLine {
        private String line;

        public PSLine(String line) {
            this.line = line;
        }

        public String getLine() {
            return this.line;
        }
    }

    private static class PSComment {
        private String comment;

        public PSComment(String comment) {
            this.comment = comment;
        }

        public String getComment() {
            return this.comment;
        }
    }
}

