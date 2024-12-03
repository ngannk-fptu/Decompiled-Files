/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.LazyEnvelopeSource;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.ContextClassloaderLocal;
import com.sun.xml.messaging.saaj.soap.Envelope;
import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.messaging.saaj.soap.SOAPVersionMismatchException;
import com.sun.xml.messaging.saaj.soap.StaxLazySourceBridge;
import com.sun.xml.messaging.saaj.soap.StaxReaderBridge;
import com.sun.xml.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.messaging.saaj.util.ParserPool;
import com.sun.xml.messaging.saaj.util.RejectDoctypeSaxFilter;
import com.sun.xml.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class EnvelopeFactory {
    private static final String SAX_PARSER_POOL_SIZE_PROP_NAME = "com.sun.xml.messaging.saaj.soap.saxParserPoolSize";
    private static final int DEFAULT_SAX_PARSER_POOL_SIZE = 5;
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap", "com.sun.xml.messaging.saaj.soap.LocalStrings");
    private static ContextClassloaderLocal<ParserPool> parserPool = new ContextClassloaderLocal<ParserPool>(){

        @Override
        protected ParserPool initialValue() throws Exception {
            Integer poolSize = AccessController.doPrivileged(new PrivilegedAction<Integer>(){

                @Override
                public Integer run() {
                    try {
                        return Integer.getInteger(EnvelopeFactory.SAX_PARSER_POOL_SIZE_PROP_NAME, 5);
                    }
                    catch (SecurityException se) {
                        return 5;
                    }
                }
            });
            return new ParserPool(poolSize);
        }
    };
    private static XMLInputFactory xmlInputFactory = null;

    public static Envelope createEnvelope(Source src, SOAPPartImpl soapPart) throws SOAPException {
        if (src instanceof JAXMStreamSource) {
            try {
                if (!SOAPPartImpl.lazyContentLength) {
                    ((JAXMStreamSource)src).reset();
                }
            }
            catch (IOException ioe) {
                log.severe("SAAJ0515.source.reset.exception");
                throw new SOAPExceptionImpl(ioe);
            }
        }
        if (src instanceof LazyEnvelopeSource) {
            return EnvelopeFactory.lazy((LazyEnvelopeSource)src, soapPart);
        }
        if (soapPart.message.isLazySoapBodyParsing()) {
            return EnvelopeFactory.parseEnvelopeStax(src, soapPart);
        }
        return EnvelopeFactory.parseEnvelopeSax(src, soapPart);
    }

    private static Envelope lazy(LazyEnvelopeSource src, SOAPPartImpl soapPart) throws SOAPException {
        try {
            StaxLazySourceBridge staxBridge = new StaxLazySourceBridge(src, soapPart);
            staxBridge.bridgeEnvelopeAndHeaders();
            Envelope env = (Envelope)soapPart.getEnvelope();
            env.setStaxBridge(staxBridge);
            return env;
        }
        catch (XMLStreamException e) {
            throw new SOAPException((Throwable)e);
        }
    }

    private static Envelope parseEnvelopeStax(Source src, SOAPPartImpl soapPart) throws SOAPException {
        XMLStreamReader streamReader = null;
        if (src instanceof StAXSource) {
            streamReader = ((StAXSource)src).getXMLStreamReader();
        }
        try {
            if (streamReader == null) {
                if (xmlInputFactory == null) {
                    xmlInputFactory = XMLInputFactory.newInstance();
                }
                streamReader = xmlInputFactory.createXMLStreamReader(src);
            }
            StaxReaderBridge readerWriterBridge = new StaxReaderBridge(streamReader, soapPart);
            readerWriterBridge.bridgeEnvelopeAndHeaders();
            Envelope env = (Envelope)soapPart.getEnvelope();
            env.setStaxBridge(readerWriterBridge);
            return env;
        }
        catch (Exception e) {
            throw new SOAPException((Throwable)e);
        }
    }

    private static Envelope parseEnvelopeSax(Source src, SOAPPartImpl soapPart) throws SOAPException {
        SAXParser saxParser = null;
        ParserPool underlyingParserPool = parserPool.get();
        try {
            if (src instanceof StreamSource) {
                RejectDoctypeSaxFilter rejectFilter;
                try {
                    saxParser = underlyingParserPool.get();
                }
                catch (Exception e) {
                    log.severe("SAAJ0601.util.newSAXParser.exception");
                    throw new SOAPExceptionImpl("Couldn't get a SAX parser while constructing a envelope", e);
                }
                InputSource is = SAXSource.sourceToInputSource(src);
                if (is.getEncoding() == null && soapPart.getSourceCharsetEncoding() != null) {
                    is.setEncoding(soapPart.getSourceCharsetEncoding());
                }
                try {
                    rejectFilter = new RejectDoctypeSaxFilter(saxParser);
                }
                catch (Exception ex) {
                    log.severe("SAAJ0510.soap.cannot.create.envelope");
                    throw new SOAPExceptionImpl("Unable to create envelope from given source: ", ex);
                }
                src = new SAXSource(rejectFilter, is);
            }
            try {
                Envelope env;
                Transformer transformer = EfficientStreamingTransformer.newTransformer();
                DOMResult result = new DOMResult((Node)((Object)soapPart));
                transformer.transform(src, result);
                Envelope envelope = env = (Envelope)soapPart.getEnvelope();
                return envelope;
            }
            catch (Exception ex) {
                if (ex instanceof SOAPVersionMismatchException) {
                    throw (SOAPVersionMismatchException)((Object)ex);
                }
                log.severe("SAAJ0511.soap.cannot.create.envelope");
                throw new SOAPExceptionImpl("Unable to create envelope from given source: ", ex);
            }
        }
        finally {
            if (saxParser != null) {
                underlyingParserPool.returnParser(saxParser);
            }
        }
    }
}

