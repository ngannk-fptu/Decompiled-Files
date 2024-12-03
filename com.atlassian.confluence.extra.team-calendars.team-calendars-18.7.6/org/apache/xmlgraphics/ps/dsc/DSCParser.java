/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.ps.dsc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.DSCCommentFactory;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCFilter;
import org.apache.xmlgraphics.ps.dsc.DSCHandler;
import org.apache.xmlgraphics.ps.dsc.DSCListener;
import org.apache.xmlgraphics.ps.dsc.DSCParserConstants;
import org.apache.xmlgraphics.ps.dsc.FilteringEventListener;
import org.apache.xmlgraphics.ps.dsc.NestedDocumentHandler;
import org.apache.xmlgraphics.ps.dsc.events.DSCAtend;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;
import org.apache.xmlgraphics.ps.dsc.events.DSCHeaderComment;
import org.apache.xmlgraphics.ps.dsc.events.PostScriptComment;
import org.apache.xmlgraphics.ps.dsc.events.PostScriptLine;
import org.apache.xmlgraphics.ps.dsc.events.UnparsedDSCComment;
import org.apache.xmlgraphics.ps.dsc.tools.DSCTools;

public class DSCParser
implements DSCParserConstants {
    private static final Log LOG = LogFactory.getLog(DSCParser.class);
    private InputStream in;
    private BufferedReader reader;
    private boolean eofFound;
    private boolean checkEOF = true;
    private DSCEvent currentEvent;
    private DSCEvent nextEvent;
    private DSCListener nestedDocumentHandler;
    private DSCListener filterListener;
    private List listeners;
    private boolean listenersDisabled;

    public DSCParser(InputStream in) throws IOException, DSCException {
        this.in = in.markSupported() ? in : new BufferedInputStream(this.in);
        String encoding = "US-ASCII";
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.in, encoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Incompatible VM! " + e.getMessage());
        }
        this.parseNext();
    }

    public InputStream getInputStream() {
        return this.in;
    }

    protected void warn(String msg) {
        LOG.warn((Object)msg);
    }

    protected String readLine() throws IOException, DSCException {
        String line = this.reader.readLine();
        this.checkLine(line);
        return line;
    }

    private void checkLine(String line) throws DSCException {
        if (line == null) {
            if (!this.eofFound) {
                throw new DSCException("%%EOF not found. File is not well-formed.");
            }
        } else if (line.length() > 255) {
            this.warn("Line longer than 255 characters. This file is not fully PostScript conforming.");
        }
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    private DSCComment parseDSCLine(String line) throws IOException, DSCException {
        String name;
        int colon = line.indexOf(58);
        StringBuilder value = new StringBuilder();
        if (colon > 0) {
            name = line.substring(2, colon);
            int startOfValue = colon + 1;
            if (startOfValue < line.length()) {
                if (this.isWhitespace(line.charAt(startOfValue))) {
                    ++startOfValue;
                }
                if ((value = new StringBuilder(line.substring(startOfValue).trim())).toString().equals(DSCConstants.ATEND.toString())) {
                    return new DSCAtend(name);
                }
            }
            while (true) {
                this.reader.mark(512);
                String nextLine = this.readLine();
                if (nextLine == null || !nextLine.startsWith("%%+")) break;
                value.append(nextLine.substring(3));
            }
        } else {
            String name2 = line.substring(2);
            return this.parseDSCComment(name2, null);
        }
        this.reader.reset();
        return this.parseDSCComment(name, value.toString());
    }

    private DSCComment parseDSCComment(String name, String value) {
        DSCComment parsed = DSCCommentFactory.createDSCCommentFor(name);
        if (parsed != null) {
            try {
                parsed.parseValue(value);
                return parsed;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        UnparsedDSCComment unparsed = new UnparsedDSCComment(name);
        unparsed.parseValue(value);
        return unparsed;
    }

    public void parse(DSCHandler handler) throws IOException, DSCException {
        DSCHeaderComment header = DSCTools.checkAndSkipDSC30Header(this);
        handler.startDocument("%!" + header.getComment());
        block7: while (this.hasNext()) {
            DSCEvent event = this.nextEvent();
            switch (event.getEventType()) {
                case 0: {
                    handler.startDocument("%!" + ((DSCHeaderComment)event).getComment());
                    continue block7;
                }
                case 1: {
                    handler.handleDSCComment(event.asDSCComment());
                    continue block7;
                }
                case 2: {
                    handler.comment(((PostScriptComment)event).getComment());
                    continue block7;
                }
                case 3: {
                    handler.line(this.getLine());
                    continue block7;
                }
                case 4: {
                    handler.endDocument();
                    continue block7;
                }
            }
            throw new IllegalStateException("Illegal event type: " + event.getEventType());
        }
    }

    public boolean hasNext() {
        return this.nextEvent != null;
    }

    public int next() throws IOException, DSCException {
        if (this.hasNext()) {
            this.currentEvent = this.nextEvent;
            this.parseNext();
            this.processListeners();
            return this.currentEvent.getEventType();
        }
        throw new NoSuchElementException("There are no more events");
    }

    private void processListeners() throws IOException, DSCException {
        if (this.isListenersDisabled()) {
            return;
        }
        if (this.filterListener != null) {
            this.filterListener.processEvent(this.currentEvent, this);
        }
        if (this.listeners != null) {
            for (Object listener : this.listeners) {
                ((DSCListener)listener).processEvent(this.currentEvent, this);
            }
        }
    }

    public DSCEvent nextEvent() throws IOException, DSCException {
        this.next();
        return this.getCurrentEvent();
    }

    public DSCEvent getCurrentEvent() {
        return this.currentEvent;
    }

    public DSCEvent peek() {
        return this.nextEvent;
    }

    protected void parseNext() throws IOException, DSCException {
        String line = this.readLine();
        if (line != null) {
            if (this.isCheckEOF() && this.eofFound && line.length() > 0) {
                throw new DSCException("Content found after EOF");
            }
            if (line.startsWith("%%")) {
                DSCComment comment = this.parseDSCLine(line);
                if (comment.getEventType() == 4) {
                    this.eofFound = true;
                }
                this.nextEvent = comment;
            } else {
                this.nextEvent = line.startsWith("%!") ? new DSCHeaderComment(line.substring(2)) : (line.startsWith("%") ? new PostScriptComment(line.substring(1)) : new PostScriptLine(line));
            }
        } else {
            this.nextEvent = null;
        }
    }

    public String getLine() {
        if (this.currentEvent.getEventType() == 3) {
            return ((PostScriptLine)this.currentEvent).getLine();
        }
        throw new IllegalStateException("Current event is not a PostScript line");
    }

    public DSCComment nextDSCComment(String name) throws IOException, DSCException {
        return this.nextDSCComment(name, null);
    }

    public DSCComment nextDSCComment(String name, PSGenerator gen) throws IOException, DSCException {
        while (this.hasNext()) {
            DSCComment comment;
            DSCEvent event = this.nextEvent();
            if (event.isDSCComment() && name.equals((comment = event.asDSCComment()).getName())) {
                return comment;
            }
            if (gen == null) continue;
            event.generate(gen);
        }
        return null;
    }

    public PostScriptComment nextPSComment(String prefix, PSGenerator gen) throws IOException, DSCException {
        while (this.hasNext()) {
            PostScriptComment comment;
            DSCEvent event = this.nextEvent();
            if (event.isComment() && (comment = (PostScriptComment)event).getComment().startsWith(prefix)) {
                return comment;
            }
            if (gen == null) continue;
            event.generate(gen);
        }
        return null;
    }

    public void setFilter(DSCFilter filter) {
        this.filterListener = filter != null ? new FilteringEventListener(filter) : null;
    }

    public void addListener(DSCListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener must not be null");
        }
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(listener);
    }

    public void removeListener(DSCListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void setListenersDisabled(boolean value) {
        this.listenersDisabled = value;
    }

    public boolean isListenersDisabled() {
        return this.listenersDisabled;
    }

    public void setNestedDocumentHandler(NestedDocumentHandler handler) {
        if (handler == null) {
            this.removeListener(this.nestedDocumentHandler);
        } else {
            MyDSCListener l = new MyDSCListener();
            l.handler = handler;
            this.addListener(l);
        }
    }

    public void setCheckEOF(boolean value) {
        this.checkEOF = value;
    }

    public boolean isCheckEOF() {
        return this.checkEOF;
    }

    static class MyDSCListener
    implements DSCListener {
        private NestedDocumentHandler handler;

        MyDSCListener() {
        }

        @Override
        public void processEvent(DSCEvent event, DSCParser parser) throws IOException, DSCException {
            this.handler.handle(event, parser);
        }
    }
}

