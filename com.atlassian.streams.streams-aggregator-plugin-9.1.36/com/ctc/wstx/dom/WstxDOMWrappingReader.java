/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dom;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.exc.WstxParsingException;
import java.util.Collections;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import org.codehaus.stax2.ri.dom.DOMWrappingReader;

public class WstxDOMWrappingReader
extends DOMWrappingReader {
    protected final ReaderConfig mConfig;

    protected WstxDOMWrappingReader(DOMSource src, ReaderConfig cfg) throws XMLStreamException {
        super(src, cfg.willSupportNamespaces(), cfg.willCoalesceText());
        this.mConfig = cfg;
        if (cfg.hasInternNamesBeenEnabled()) {
            this.setInternNames(true);
        }
        if (cfg.hasInternNsURIsBeenEnabled()) {
            this.setInternNsURIs(true);
        }
    }

    public static WstxDOMWrappingReader createFrom(DOMSource src, ReaderConfig cfg) throws XMLStreamException {
        return new WstxDOMWrappingReader(src, cfg);
    }

    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    public Object getProperty(String name) {
        if (name.equals("javax.xml.stream.entities")) {
            return Collections.EMPTY_LIST;
        }
        if (name.equals("javax.xml.stream.notations")) {
            return Collections.EMPTY_LIST;
        }
        return this.mConfig.getProperty(name);
    }

    public boolean setProperty(String name, Object value) {
        return this.mConfig.setProperty(name, value);
    }

    protected void throwStreamException(String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            throw new WstxParsingException(msg);
        }
        throw new WstxParsingException(msg, loc);
    }
}

