/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.InputStream;
import java.util.Arrays;
import javax.xml.namespace.QName;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.utils.XMLReaderUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlRootExtractor {
    private static final ParseContext EMPTY_CONTEXT = new ParseContext();

    public QName extractRootElement(byte[] data) {
        while (true) {
            try {
                return this.extractRootElement(new ByteArrayInputStream(data), true);
            }
            catch (MalformedCharException e) {
                int newLen = data.length / 2;
                if (newLen % 2 == 1) {
                    --newLen;
                }
                if (newLen > 0) {
                    data = Arrays.copyOf(data, newLen);
                    continue;
                }
                return null;
            }
            break;
        }
    }

    public QName extractRootElement(InputStream stream) {
        return this.extractRootElement(stream, false);
    }

    private QName extractRootElement(InputStream stream, boolean throwMalformed) {
        ExtractorHandler handler;
        block3: {
            handler = new ExtractorHandler();
            try {
                XMLReaderUtils.parseSAX(new CloseShieldInputStream(stream), new OfflineContentHandler(handler), EMPTY_CONTEXT);
            }
            catch (SecurityException e) {
                throw e;
            }
            catch (Exception e) {
                if (!throwMalformed || !(e instanceof CharConversionException) && !(e.getCause() instanceof CharConversionException)) break block3;
                throw new MalformedCharException(e);
            }
        }
        return handler.rootElement;
    }

    private static class MalformedCharException
    extends RuntimeException {
        public MalformedCharException(Exception e) {
            super(e);
        }
    }

    private static class ExtractorHandler
    extends DefaultHandler {
        private QName rootElement = null;

        private ExtractorHandler() {
        }

        @Override
        public void startElement(String uri, String local, String name, Attributes attributes) throws SAXException {
            this.rootElement = new QName(uri, local);
            throw new SAXException("Aborting: root element received");
        }
    }
}

