/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import com.sun.xml.messaging.saaj.util.SAAJUtil;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

public class ParserPool {
    private final BlockingQueue<SAXParser> queue;
    private SAXParserFactory factory;

    public ParserPool(int capacity) {
        this.queue = new ArrayBlockingQueue<SAXParser>(capacity);
        this.factory = SAXParserFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl", SAAJUtil.getSystemClassLoader());
        try {
            this.factory.setFeature("jdk.xml.resetSymbolTable", true);
        }
        catch (ParserConfigurationException | SAXException exception) {
            // empty catch block
        }
        this.factory.setNamespaceAware(true);
        for (int i = 0; i < capacity; ++i) {
            try {
                this.queue.put(this.factory.newSAXParser());
                continue;
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
            catch (ParserConfigurationException ex) {
                throw new RuntimeException(ex);
            }
            catch (SAXException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public SAXParser get() throws ParserConfigurationException, SAXException {
        try {
            return this.queue.take();
        }
        catch (InterruptedException ex) {
            throw new SAXException(ex);
        }
    }

    public boolean put(SAXParser parser) {
        return this.queue.offer(parser);
    }

    public void returnParser(SAXParser saxParser) {
        saxParser.reset();
        this.put(saxParser);
    }
}

