/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

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

public abstract class Stax2EventReaderImpl
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
    private XMLEvent mPeekedEvent = null;
    protected int mState = 1;
    protected int mPrePeekEvent = 7;

    protected Stax2EventReaderImpl(XMLEventAllocator xMLEventAllocator, XMLStreamReader2 xMLStreamReader2) {
        this.mAllocator = xMLEventAllocator;
        this.mReader = xMLStreamReader2;
    }

    public abstract boolean isPropertySupported(String var1);

    public abstract boolean setProperty(String var1, Object var2);

    protected abstract String getErrorDesc(int var1, int var2);

    public void close() throws XMLStreamException {
        this.mReader.close();
    }

    public String getElementText() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            return this.mReader.getElementText();
        }
        XMLEvent xMLEvent = this.mPeekedEvent;
        this.mPeekedEvent = null;
        if (this.mPrePeekEvent != 1) {
            this.reportProblem(this.findErrorDesc(1, this.mPrePeekEvent));
        }
        String string = null;
        StringBuffer stringBuffer = null;
        while (!xMLEvent.isEndElement()) {
            int n = xMLEvent.getEventType();
            if (n != 5 && n != 3) {
                if (!xMLEvent.isCharacters()) {
                    this.reportProblem(this.findErrorDesc(2, n));
                }
                String string2 = xMLEvent.asCharacters().getData();
                if (string == null) {
                    string = string2;
                } else {
                    if (stringBuffer == null) {
                        stringBuffer = new StringBuffer(string.length() + string2.length());
                        stringBuffer.append(string);
                    }
                    stringBuffer.append(string2);
                }
            }
            xMLEvent = this.nextEvent();
        }
        if (stringBuffer != null) {
            return stringBuffer.toString();
        }
        return string == null ? "" : string;
    }

    public Object getProperty(String string) {
        return this.mReader.getProperty(string);
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
            XMLEvent xMLEvent = this.mPeekedEvent;
            this.mPeekedEvent = null;
            if (xMLEvent.isEndDocument()) {
                this.mState = 2;
            }
            return xMLEvent;
        }
        return this.createNextEvent(true, this.mReader.next());
    }

    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException xMLStreamException) {
            this.throwUnchecked(xMLStreamException);
            return null;
        }
    }

    public XMLEvent nextTag() throws XMLStreamException {
        if (this.mPeekedEvent != null) {
            XMLEvent xMLEvent = this.mPeekedEvent;
            this.mPeekedEvent = null;
            int n = xMLEvent.getEventType();
            switch (n) {
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
                    if (((Characters)xMLEvent).isWhiteSpace()) break;
                    this.reportProblem(this.findErrorDesc(3, n));
                    break;
                }
                case 1: 
                case 2: {
                    return xMLEvent;
                }
                default: {
                    this.reportProblem(this.findErrorDesc(4, n));
                }
            }
        } else if (this.mState == 1) {
            this.mState = 3;
        }
        block14: while (true) {
            int n = this.mReader.next();
            switch (n) {
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
                    this.reportProblem(this.findErrorDesc(3, n));
                    continue block14;
                }
                case 1: 
                case 2: {
                    return this.createNextEvent(false, n);
                }
            }
            this.reportProblem(this.findErrorDesc(4, n));
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

    public boolean hasNextEvent() throws XMLStreamException {
        return this.mState != 2;
    }

    protected XMLEvent createNextEvent(boolean bl, int n) throws XMLStreamException {
        try {
            XMLEvent xMLEvent = this.mAllocator.allocate(this.mReader);
            if (bl && n == 8) {
                this.mState = 2;
            }
            return xMLEvent;
        }
        catch (RuntimeException runtimeException) {
            for (Throwable throwable = runtimeException.getCause(); throwable != null; throwable = throwable.getCause()) {
                if (!(throwable instanceof XMLStreamException)) continue;
                throw (XMLStreamException)throwable;
            }
            throw runtimeException;
        }
    }

    protected XMLEvent createStartDocumentEvent() throws XMLStreamException {
        XMLEvent xMLEvent = this.mAllocator.allocate(this.mReader);
        return xMLEvent;
    }

    private void throwEndOfInput() {
        throw new NoSuchElementException();
    }

    protected void throwUnchecked(XMLStreamException xMLStreamException) {
        Throwable throwable;
        Throwable throwable2 = throwable = xMLStreamException.getNestedException() == null ? xMLStreamException : xMLStreamException.getNestedException();
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        throw new RuntimeException("[was " + throwable.getClass() + "] " + throwable.getMessage(), throwable);
    }

    protected void reportProblem(String string) throws XMLStreamException {
        this.reportProblem(string, this.mReader.getLocation());
    }

    protected void reportProblem(String string, Location location) throws XMLStreamException {
        if (location == null) {
            throw new XMLStreamException(string);
        }
        throw new XMLStreamException(string, location);
    }

    protected XMLStreamReader getStreamReader() {
        return this.mReader;
    }

    private final String findErrorDesc(int n, int n2) {
        String string = this.getErrorDesc(n, n2);
        if (string != null) {
            return string;
        }
        switch (n) {
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
        return "Internal error (unrecognized error type: " + n + ")";
    }
}

