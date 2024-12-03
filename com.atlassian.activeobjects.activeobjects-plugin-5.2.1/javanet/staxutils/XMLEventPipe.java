/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javanet.staxutils.BaseXMLEventReader;
import javanet.staxutils.BaseXMLEventWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class XMLEventPipe {
    public static final int QUEUE_CAPACITY = 16;
    private List eventQueue = new LinkedList();
    private int capacity = 16;
    private boolean readEndClosed;
    private boolean writeEndClosed;
    private PipedXMLEventReader readEnd = new PipedXMLEventReader(this);
    private PipedXMLEventWriter writeEnd = new PipedXMLEventWriter(this);

    public XMLEventPipe() {
    }

    public XMLEventPipe(int capacity) {
        this.capacity = capacity;
    }

    public synchronized XMLEventReader getReadEnd() {
        if (this.readEnd == null) {
            this.readEnd = new PipedXMLEventReader(this);
        }
        return this.readEnd;
    }

    public synchronized XMLEventWriter getWriteEnd() {
        if (this.writeEnd == null) {
            this.writeEnd = new PipedXMLEventWriter(this);
        }
        return this.writeEnd;
    }

    private static final class PipedXMLEventReader
    extends BaseXMLEventReader {
        private XMLEventPipe pipe;

        public PipedXMLEventReader(XMLEventPipe pipe) {
            this.pipe = pipe;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized XMLEvent nextEvent() throws XMLStreamException {
            if (this.closed) {
                throw new XMLStreamException("Stream has been closed");
            }
            XMLEventPipe xMLEventPipe = this.pipe;
            synchronized (xMLEventPipe) {
                while (this.pipe.eventQueue.size() == 0) {
                    if (this.pipe.writeEndClosed) {
                        throw new NoSuchElementException("Stream has completed");
                    }
                    try {
                        this.pipe.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                boolean notify = this.pipe.capacity > 0 && this.pipe.eventQueue.size() >= this.pipe.capacity;
                XMLEvent nextEvent = (XMLEvent)this.pipe.eventQueue.remove(0);
                if (notify) {
                    this.pipe.notifyAll();
                }
                return nextEvent;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized boolean hasNext() {
            if (this.closed) {
                return false;
            }
            XMLEventPipe xMLEventPipe = this.pipe;
            synchronized (xMLEventPipe) {
                while (this.pipe.eventQueue.size() == 0 && !this.pipe.writeEndClosed) {
                    try {
                        this.pipe.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                return this.pipe.eventQueue.size() > 0;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized XMLEvent peek() throws XMLStreamException {
            if (this.closed) {
                return null;
            }
            XMLEventPipe xMLEventPipe = this.pipe;
            synchronized (xMLEventPipe) {
                while (this.pipe.eventQueue.size() == 0) {
                    if (this.pipe.writeEndClosed) {
                        return null;
                    }
                    try {
                        this.pipe.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                return (XMLEvent)this.pipe.eventQueue.get(0);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized void close() throws XMLStreamException {
            if (this.closed) {
                return;
            }
            XMLEventPipe xMLEventPipe = this.pipe;
            synchronized (xMLEventPipe) {
                this.pipe.readEndClosed = true;
                this.pipe.notifyAll();
            }
            super.close();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void finalize() {
            if (!this.closed) {
                XMLEventPipe xMLEventPipe = this.pipe;
                synchronized (xMLEventPipe) {
                    this.pipe.readEndClosed = true;
                    this.pipe.notifyAll();
                }
            }
        }
    }

    private static final class PipedXMLEventWriter
    extends BaseXMLEventWriter {
        private XMLEventPipe pipe;

        public PipedXMLEventWriter(XMLEventPipe pipe) {
            this.pipe = pipe;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized void close() throws XMLStreamException {
            super.close();
            XMLEventPipe xMLEventPipe = this.pipe;
            synchronized (xMLEventPipe) {
                if (this.pipe.readEndClosed) {
                    this.pipe.eventQueue.clear();
                }
                this.pipe.writeEndClosed = true;
                this.pipe.notifyAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void sendEvent(XMLEvent event) throws XMLStreamException {
            XMLEventPipe xMLEventPipe = this.pipe;
            synchronized (xMLEventPipe) {
                if (this.pipe.readEndClosed) {
                    return;
                }
                if (this.pipe.capacity > 0) {
                    while (this.pipe.eventQueue.size() >= this.pipe.capacity) {
                        try {
                            this.pipe.wait();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.pipe.eventQueue.add(event);
                if (this.pipe.eventQueue.size() == 1) {
                    this.pipe.notifyAll();
                }
                if (event.isEndDocument()) {
                    this.close();
                }
            }
        }
    }
}

