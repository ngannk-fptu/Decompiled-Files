/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax.debug;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.wrapper.WrappingXMLInputFactory;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnclosedReaderDetector
extends WrappingXMLInputFactory {
    static final Log log = LogFactory.getLog(UnclosedReaderDetector.class);

    public UnclosedReaderDetector(XMLInputFactory parent) {
        super(parent);
    }

    protected XMLStreamReader wrap(XMLStreamReader reader) {
        return new StreamReaderWrapper(reader);
    }

    private static class StreamReaderWrapper
    extends XMLStreamReaderWrapper {
        private final Throwable stackTrace = new Throwable();
        private boolean isClosed;

        public StreamReaderWrapper(XMLStreamReader parent) {
            super(parent);
        }

        public void close() throws XMLStreamException {
            super.close();
            this.isClosed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.isClosed) {
                log.warn((Object)"Detected unclosed XMLStreamReader.", this.stackTrace);
            }
        }
    }
}

