/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxParsingException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventAllocator;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2EventReaderImpl;

public class WstxEventReader
extends Stax2EventReaderImpl {
    public WstxEventReader(XMLEventAllocator a, XMLStreamReader2 r) {
        super(a, r);
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

    public boolean isPropertySupported(String name) {
        return ((XMLStreamReader2)this.getStreamReader()).isPropertySupported(name);
    }

    public boolean setProperty(String name, Object value) {
        return ((XMLStreamReader2)this.getStreamReader()).setProperty(name, value);
    }

    protected void reportProblem(String msg, Location loc) throws XMLStreamException {
        throw new WstxParsingException(msg, loc);
    }
}

