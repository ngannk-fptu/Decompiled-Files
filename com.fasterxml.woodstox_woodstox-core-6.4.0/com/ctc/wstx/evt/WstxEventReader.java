/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.XMLEventReader2
 *  org.codehaus.stax2.XMLStreamReader2
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.sr.StreamScanner;
import java.util.NoSuchElementException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamReader2;

public class WstxEventReader
implements XMLEventReader2,
XMLStreamConstants {
    protected static final int STATE_INITIAL = 1;
    protected static final int STATE_END_OF_INPUT = 2;
    protected static final int STATE_CONTENT = 3;
    protected static final int ERR_GETELEMTEXT_NOT_START_ELEM = 1;
    protected static final int ERR_GETELEMTEXT_NON_TEXT_EVENT = 2;
    protected static final int ERR_NEXTTAG_NON_WS_TEXT = 3;
    protected static final int ERR_NEXTTAG_WRONG_TYPE = 4;
    protected final XMLEventAllocator mAllocator;
    protected final XMLStreamReader2 mReader;
    protected XMLEvent mPeekedEvent = null;
    protected int mState = 1;
    protected int mPrePeekEvent = 7;
    protected final boolean mCfgMultiDocMode;

    public WstxEventReader(XMLEventAllocator a, XMLStreamReader2 r) {
        this.mAllocator = a;
        this.mReader = r;
        this.mCfgMultiDocMode = r instanceof StreamScanner && ((StreamScanner)r).getConfig().inputParsingModeDocuments();
    }

    public boolean isPropertySupported(String name) {
        return ((XMLStreamReader2)this.getStreamReader()).isPropertySupported(name);
    }

    public boolean setProperty(String name, Object value) {
        return ((XMLStreamReader2)this.getStreamReader()).setProperty(name, value);
    }

    protected String getErrorDesc(int errorType, int currEvent) {
        switch (errorType) {
            case 1: {
                return ErrorConsts.ERR_STATE_NOT_STELEM + ", got " + ErrorConsts.tokenTypeDesc(currEvent);
            }
            case 2: {
                return "Expected a text token, got " + ErrorConsts.tokenTypeDesc(currEvent);
            }
            case 3: {
                return "Only all-whitespace CHARACTERS/CDATA (or SPACE) allowed for nextTag(), got " + ErrorConsts.tokenTypeDesc(currEvent);
            }
            case 4: {
                return "Got " + ErrorConsts.tokenTypeDesc(currEvent) + ", instead of START_ELEMENT, END_ELEMENT or SPACE";
            }
        }
        return null;
    }

    public void close() throws XMLStreamException {
        this.mReader.close();
    }

    public String getElementText() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            return this.mReader.getElementText();
        }
        XMLEvent evt = this.mPeekedEvent;
        this.mPeekedEvent = null;
        if (this.mPrePeekEvent != 1) {
            this.reportProblem(this.findErrorDesc(1, this.mPrePeekEvent));
        }
        String str = null;
        StringBuffer sb = null;
        while (!evt.isEndElement()) {
            int type = evt.getEventType();
            if (type != 5 && type != 3) {
                if (!evt.isCharacters()) {
                    this.reportProblem(this.findErrorDesc(2, type));
                }
                String curr = evt.asCharacters().getData();
                if (str == null) {
                    str = curr;
                } else {
                    if (sb == null) {
                        sb = new StringBuffer(str.length() + curr.length());
                        sb.append(str);
                    }
                    sb.append(curr);
                }
            }
            evt = this.nextEvent();
        }
        if (sb != null) {
            return sb.toString();
        }
        return str == null ? "" : str;
    }

    public Object getProperty(String name) {
        return this.mReader.getProperty(name);
    }

    public boolean hasNext() {
        return this.mState != 2;
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.mState == 2) {
            this.throwEndOfInput();
        } else if (this.mState == 1) {
            this.mState = 3;
            return this.createStartDocumentEvent();
        }
        if (this.mPeekedEvent != null) {
            XMLEvent evt = this.mPeekedEvent;
            this.mPeekedEvent = null;
            if (evt.isEndDocument()) {
                this.updateStateEndDocument();
            }
            return evt;
        }
        return this.createNextEvent(true, this.mReader.next());
    }

    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException sex) {
            this.throwUnchecked(sex);
            return null;
        }
    }

    public XMLEvent nextTag() throws XMLStreamException {
        if (this.mPeekedEvent != null) {
            XMLEvent evt = this.mPeekedEvent;
            this.mPeekedEvent = null;
            int type = evt.getEventType();
            switch (type) {
                case 8: {
                    return null;
                }
                case 7: {
                    break;
                }
                case 6: {
                    break;
                }
                case 3: 
                case 5: {
                    break;
                }
                case 4: 
                case 12: {
                    if (((Characters)evt).isWhiteSpace()) break;
                    this.reportProblem(this.findErrorDesc(3, type));
                    break;
                }
                case 1: 
                case 2: {
                    return evt;
                }
                default: {
                    this.reportProblem(this.findErrorDesc(4, type));
                }
            }
        } else if (this.mState == 1) {
            this.mState = 3;
        }
        block14: while (true) {
            int next = this.mReader.next();
            switch (next) {
                case 8: {
                    return null;
                }
                case 3: 
                case 5: 
                case 6: {
                    continue block14;
                }
                case 4: 
                case 12: {
                    if (this.mReader.isWhiteSpace()) continue block14;
                    this.reportProblem(this.findErrorDesc(3, next));
                    continue block14;
                }
                case 1: 
                case 2: {
                    return this.createNextEvent(false, next);
                }
            }
            this.reportProblem(this.findErrorDesc(4, next));
        }
    }

    public XMLEvent peek() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            if (this.mState == 2) {
                return null;
            }
            if (this.mState == 1) {
                this.mPrePeekEvent = 7;
                this.mPeekedEvent = this.createStartDocumentEvent();
                this.mState = 3;
            } else {
                this.mPrePeekEvent = this.mReader.getEventType();
                this.mPeekedEvent = this.createNextEvent(false, this.mReader.next());
            }
        }
        return this.mPeekedEvent;
    }

    public void remove() {
        throw new UnsupportedOperationException("Can not remove events from XMLEventReader.");
    }

    protected void updateStateEndDocument() throws XMLStreamException {
        if (this.mCfgMultiDocMode && this.mReader.hasNext()) {
            int next = this.mReader.next();
            if (next == 7) {
                this.mPrePeekEvent = 7;
                this.mPeekedEvent = this.createStartDocumentEvent();
                this.mState = 3;
                return;
            }
            this.reportProblem("Unexpected token (" + ErrorConsts.tokenTypeDesc(next) + ") after END_DOCUMENT in multi-document mode, XMLStreamReader.hasNext() returning true");
        }
        this.mState = 2;
    }

    public boolean hasNextEvent() throws XMLStreamException {
        return this.mState != 2;
    }

    protected XMLEvent createNextEvent(boolean checkEOD, int type) throws XMLStreamException {
        try {
            XMLEvent evt = this.mAllocator.allocate((XMLStreamReader)this.mReader);
            if (checkEOD && type == 8) {
                this.updateStateEndDocument();
            }
            return evt;
        }
        catch (RuntimeException rex) {
            throw this._checkUnwrap(rex);
        }
    }

    protected XMLStreamException _checkUnwrap(RuntimeException rex) {
        for (Throwable t = rex.getCause(); t != null; t = t.getCause()) {
            if (!(t instanceof XMLStreamException)) continue;
            return (XMLStreamException)t;
        }
        throw rex;
    }

    protected XMLEvent createStartDocumentEvent() throws XMLStreamException {
        XMLEvent start = this.mAllocator.allocate((XMLStreamReader)this.mReader);
        return start;
    }

    protected void throwEndOfInput() {
        throw new NoSuchElementException();
    }

    protected void throwUnchecked(XMLStreamException sex) {
        Throwable t;
        Throwable throwable = t = sex.getNestedException() == null ? sex : sex.getNestedException();
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        throw new RuntimeException("[was " + t.getClass() + "] " + t.getMessage(), t);
    }

    protected void reportProblem(String msg) throws XMLStreamException {
        this.reportProblem(msg, this.mReader.getLocation());
    }

    protected void reportProblem(String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            throw new WstxParsingException(msg);
        }
        throw new WstxParsingException(msg, loc);
    }

    protected XMLStreamReader getStreamReader() {
        return this.mReader;
    }

    protected final String findErrorDesc(int errorType, int currEvent) {
        String msg = this.getErrorDesc(errorType, currEvent);
        if (msg != null) {
            return msg;
        }
        switch (errorType) {
            case 1: {
                return "Current state not START_ELEMENT when calling getElementText()";
            }
            case 2: {
                return "Expected a text token";
            }
            case 3: {
                return "Only all-whitespace CHARACTERS/CDATA (or SPACE) allowed for nextTag()";
            }
            case 4: {
                return "Should only encounter START_ELEMENT/END_ELEMENT, SPACE, or all-white-space CHARACTERS";
            }
        }
        return "Internal error (unrecognized error type: " + errorType + ")";
    }
}

